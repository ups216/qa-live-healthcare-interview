# 设计文档：问诊用户账号密码登录（PATIENT-AUTH-001）

> 对应 PRD：`docs/PRD-patient-auth.md`
> 对应实现方案：`docs/IMPLEMENTATION_PLAN.md` §6

---

## 1. 设计目标与约束

| 类别 | 内容 |
|---|---|
| 目标 | 为问诊用户提供可持久、可复现的身份；以最小改动替换原有的「姓名+生日」弱验证 |
| 硬约束 1 | 密码必须 BCrypt 加盐存储，**任何对外响应不得包含 password** |
| 硬约束 2 | **不得引入完整 Spring Security**（会带默认登录页与 CSRF，干扰既有裸接口） |
| 硬约束 3 | 既有接口 `/api/test/cors`、`/actuator/health`、`/api/doctors/**` 不得回归 |
| 硬约束 4 | 持久层用 MyBatis XML；XML 放 `src/main/resources/mapper/` |

---

## 2. 数据模型

### 2.1 `t_patient`

```sql
CREATE TABLE IF NOT EXISTS t_patient (
    id          VARCHAR(32)  NOT NULL,
    username    VARCHAR(64)  NOT NULL COMMENT '登录用户名',
    password    VARCHAR(128) NOT NULL COMMENT 'BCrypt 密文',
    name        VARCHAR(64)  NOT NULL COMMENT '姓名',
    birthday    VARCHAR(16)           COMMENT '生日 YYYY-MM-DD',
    phone       VARCHAR(32)           COMMENT '手机号',
    gender      VARCHAR(8)            COMMENT '性别',
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_patient_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

**设计说明**

- `password VARCHAR(128)`：BCrypt 输出固定 60 字符，留足余量以便将来换算法。
- `username` 唯一索引：注册查重的**最终兜底**。`PatientService` 先查后插，
  但并发下仍可能同时通过检查，唯一索引会抛出 `DuplicateKeyException` 并被捕获转成 1001。
- `id` 由应用层生成（`pt-` + UUID 前 12 位 = 15 字符），不依赖数据库自增，便于未来分库分表。
  ⚠️ 曾因使用「`patient` + 完整 UUID」= 39 字符而超出 `VARCHAR(32)` 触发 `Data truncation`。

### 2.2 与既有数据的关系

`web/qa-web/src/data/patient-user.json` 中的 5 位历史患者（仅 `id/name/birthday/phone/gender`）
**不迁移**：它们没有凭证字段，批量补默认密码会制造弱口令账户。本期仅新注册用户可用。

---

## 3. 后端分层设计

```
Controller  PatientController  → 只做参数接收与 ApiResponse 包装
    ↓
Service     PatientService     → 业务规则：查重、加密、校验、签发 token
    ↓
Mapper      PatientMapper      → 纯 SQL（resources/mapper/PatientMapper.xml）
    ↓
DB          t_patient
```

跨层组件：

| 组件 | 位置 | 职责 |
|---|---|---|
| `PasswordEncoderConfig` | `config/` | 注册 `BCryptPasswordEncoder` Bean |
| `JwtUtil` | `util/` | 签发与解析 JWT |
| `JwtInterceptor` | `config/` | 白名单判定 + token 校验 + 写入 `patientId` |
| `WebConfig` | `config/` | 注册拦截器到 `/**` |
| `ApiResponse<T>` | `common/` | 统一响应 `{code, message, data, timestamp}` |
| `GlobalExceptionHandler` | `exception/` | `@RestControllerAdvice` 统一兜底 |

### 3.1 Entity 与 VO 分离（安全的关键结构保障）

```
entity/Patient  : id, username, password, name, birthday, phone, gender   ← 含密码，仅在 Service/Mapper 内流转
dto/PatientVO   : id, username, name, birthday, phone, gender             ← 不含密码，唯一对外的患者模型
```

把「不含密码」编码进类型，而不是靠每次手写时记得删字段 —— 这是防止密码泄漏的结构性保障。

### 3.2 错误码

| code | 枚举 | HTTP | 对外 message |
|---|---|---|---|
| 1001 | `PATIENT_USERNAME_EXISTS` | 400 | 用户名已存在 |
| 1002 | `PARAM_INVALID` | 400 | 参数校验失败：`<字段>` `<原因>` |
| 1003 | `PATIENT_NOT_FOUND` | 401 | 用户名或密码错误 |
| 1004 | `PATIENT_BAD_CREDENTIALS` | 401 | 用户名或密码错误 |
| 1005 | `TOKEN_MISSING` | 401 | 未登录或登录已过期 |
| 1006 | `TOKEN_INVALID` | 401 | token 无效或已过期 |
| 5000 | `SYSTEM_ERROR` | 500 | 服务器内部错误，请稍后重试 |

> 1003 与 1004 的**对外文案刻意相同**（防用户名枚举），内部 code 不同以便日志排查。

---

## 4. 核心流程时序

### 4.1 注册

```
Client                Controller            Service               Mapper        DB
  │ POST /register      │                     │                     │           │
  ├────────────────────►│ @Valid 校验         │                     │           │
  │                     ├─ 失败 → 400/1002 ───┼─────────────────────┼───────────┤
  │                     ├────────────────────►│ selectByUsername    │           │
  │                     │                     ├────────────────────►├──────────►│
  │                     │                     │◄────── 存在 → 1001 ─┤◄──────────┤
  │                     │                     │ passwordEncoder.encode(明文)     │
  │                     │                     ├────────────────────►├──────────►│ INSERT
  │                     │                     │◄─ DuplicateKey → 1001           │
  │◄──── 201 + PatientVO┤◄────────────────────┤                     │           │
```

### 4.2 登录并签发 token

```
Client              Controller          Service            JwtUtil
  │ POST /login       │                   │                   │
  ├──────────────────►│                   │                   │
  │                   ├──────────────────►│ selectByUsername  │
  │                   │                   │ 不存在 → 401/1003 │
  │                   │                   │ matches() false → 401/1004
  │                   │                   ├──────────────────►│ generateToken
  │                   │                   │◄────── JWT ───────┤
  │◄─ 200 {token, expiresIn, patient} ────┤◄──────────────────┤
```

### 4.3 鉴权拦截

```
任意请求 → JwtInterceptor.preHandle
   ├─ 命中白名单（/api/doctors/**, /api/patient/login, /api/patient/register,
   │              /api/test/**, /actuator/**） → 放行
   ├─ 无 Authorization 头 或 无 "Bearer " 前缀 → 401/1005
   ├─ parseClaims 抛异常（过期/签名错/格式非法）→ 401/1006
   └─ 成功 → request.setAttribute("patientId", subject) → 放行
```

**白名单为何包含 `/api/doctors/**`**：医生列表是公开信息，首页与医生页在匿名状态下也要能浏览；
若误拦，整站首屏会白屏。因此测试中用 T30/T31 做**反向验证**。

**token 解析结果如何传给 Controller**：拦截成功后写入 request attribute，
`PatientController.me()` 直接读取，**避免每个接口重复解析一遍 token**。

---

## 5. 前端设计

### 5.1 状态与存储

| 数据 | 位置 | 说明 |
|---|---|---|
| `token` | `localStorage['qa-live-token']` | 跨刷新保持登录态 |
| `currentPatient` | `store.state` | 内存态，供各视图消费 |
| 语言选择 | `localStorage['qa-live-locale']` | 与需求1 共用，key 互不冲突 |

### 5.2 登录态恢复的两条路径

```
① 路由守卫 beforeEach（首屏直开 /consultation）
     currentPatient 为空 → await store.restorePatient() → /patient/login?redirect=...

② App.vue onMounted（常规进入）
     await store.restorePatient()  →  再 await store.loadDoctors()
```

**为什么需要两条**：路由守卫保证「未登录不许进」；`App.vue` 保证「已登录时头部立刻显示用户名」。
`restorePatient()` 本身幂等（只读一次 `/api/patient/me`），重复调用的开销可接受。

### 5.3 axios 拦截器的解耦处理

`api/request.ts` 在收到 401 时**不直接跳转**，而是：

1. 清理本地 token；
2. `window.dispatchEvent(new Event('qa-live-auth-expired'))`。

**原因**：api 层若直接依赖 router，会形成 `api → router → store → api` 的循环引用，
且容易出现「拦截器跳转」与「路由守卫跳转」两处逻辑打架。

### 5.4 组件划分

```
views/PatientLogin.vue      ← 容器：Tabs 切换 + 成功后跳转（优先 redirect）
    ├── components/LoginForm.vue      ← 登录表单
    └── components/RegisterForm.vue   ← 注册表单（含确认密码一致性校验）
```

拆分的理由：登录与注册字段、校验规则、提交流程差异较大，
拆开后每个组件职责单一；注册成功后复用 `store.loginPatient()` 自动登录，省去用户再输一次密码。

---

## 6. 关键取舍记录

| # | 取舍 | 理由 |
|---|---|---|
| 1 | 只引 `spring-security-crypto`，不引完整 Security | 完整 Security 会带来默认登录页、默认鉴权、CSRF，直接挡住既有裸接口 |
| 2 | JWT 无状态，不做服务端 session | 无需额外存储，水平扩容无障碍；代价是 token 无法主动失效（可接受） |
| 3 | 注册成功返回 201 而非 200 | 符合 REST 语义（资源已创建） |
| 4 | 登录失败对外不区分「用户不存在」与「密码错误」 | 防止用户名枚举；内部保留不同 code 便于排查 |
| 5 | 历史患者数据不迁移 | 避免批量制造弱口令账户 |
| 6 | 医生登录仍走前端本地凭证比对 | 需求3 只要求「问诊用户」登录；且后端 VO 不下发 password。后续应新增 `/api/doctor/login` 彻底服务端化 |

---

## 7. 安全清单

- [x] 密码 BCrypt（cost 10，自动加盐）存储
- [x] 对外响应不含 password（Entity / VO 分离）
- [x] 登录失败提示不区分用户是否存在
- [x] SQL 全部预编译（`#{}`），无 `${}` 拼接用户输入
- [x] JWT 密钥 ≥256 bit，长度不足时在启动阶段即抛错
- [x] 拦截器异常统一转 401，不透出堆栈
- [ ] 生产环境启用 HTTPS（**演示环境为 HTTP，已知限制**）
- [ ] token 主动失效机制（登出黑名单）——本期未实现，可用短有效期缓解
