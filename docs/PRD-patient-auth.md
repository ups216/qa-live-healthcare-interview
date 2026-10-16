# Feature PRD: 问诊用户账号密码登录

**Feature ID**：PATIENT-AUTH-001
**Created Date**：2026-10-15
**Status**：COMPLETED
**Language**：zh
**Template**：`docs/PRD_TEMPLATE.md`

---

## 1. Overview

### 1.1 Feature Summary

为在线问诊平台的**问诊用户（患者）**提供标准的「用户名 + 密码」注册与登录能力，
以 JWT 维持登录态，替代原有的「姓名 + 生日」弱验证。

- **做什么**：新增患者注册、登录、获取当前用户三个接口；新增 `/patient/login` 登录注册页；登录态以 token 持久化到 `localStorage`，刷新不丢失。
- **为什么需要**：原实现存在明确的身份伪造问题 —— `web/qa-web/src/store/index.ts` 的 `verifyPatient()` 是「按姓名+生日查找，**找不到就自动建档**」，即任何人输入任意姓名生日都能立即获得账户；且登录态仅存于内存的 `currentPatient`，浏览器刷新即丢失，无法支撑真实业务。
- **谁受益**：问诊用户获得持久、可复现的身份；平台获得基础鉴权边界。

### 1.2 Objectives

- Objective 1：患者可通过用户名 + 密码注册账号并登录。
- Objective 2：登录态跨页面刷新保持，无需重复登录。
- Objective 3：未登录用户访问问诊页被引导至登录页，登录后回到原目标页。
- Objective 4：密码以 BCrypt 加盐存储，任何对外响应均不含密码字段。

### 1.3 Out of Scope（明确不做）

- **医生登录改造**：`DoctorLogin.vue` 与 `store.loginDoctor` 保持原样。
- **短信验证码 / 邮箱验证 / 第三方登录**：本期不实现。
- **找回密码 / 修改密码 / 注销账号**：本期不实现。
- **历史患者数据迁移**：`web/qa-web/src/data/patient-user.json` 的 5 位患者无凭证字段，本期**不迁移**（避免批量制造弱口令账户）。
- **问诊/提问接口的服务端化**：`question-list.json` 仍为前端 mock。
- **RBAC 权限模型**：本期只有「已登录 / 未登录」二态。

---

## 2. User Stories

### Story 1 — 新用户注册

**As a** 首次使用平台的问诊用户
**I want to** 用用户名和密码注册一个账号
**So that** 我可以在后续会话中凭同一身份问诊

**Acceptance Criteria**：

- [x] AC1 提交合法用户名（3~32 位）、密码（≥6 位）、姓名后可注册成功
- [x] AC2 用户名重复时返回明确错误，不覆盖既有账号
- [x] AC3 注册成功后数据库 `t_patient.password` 为 BCrypt 密文，不以任何形式返回明文
- [x] AC4 缺少必填字段时返回参数校验错误，不产生脏数据

### Story 2 — 老用户登录

**As a** 已注册的问诊用户
**I want to** 用用户名和密码登录
**So that** 我可以继续之前的问诊

**Acceptance Criteria**：

- [x] AC1 凭据正确时返回 JWT 与用户基本信息
- [x] AC2 密码错误时返回 401
- [x] AC3 用户不存在与密码错误对外返回**一致**提示，防止用户名枚举
- [x] AC4 登录成功后前端将 token 写入 `localStorage`

### Story 3 — 登录态保持

**As a** 已登录的问诊用户
**I want** 刷新浏览器后仍处于登录状态
**So that** 我不用每次都重新输入密码

**Acceptance Criteria**：

- [x] AC1 刷新页面后自动调用 `/api/patient/me` 恢复用户信息
- [x] AC2 token 过期或非法时自动清理本地 token 并要求重新登录

### Story 4 — 未登录访问拦截

**As a** 平台运营方
**I want** 未登录用户访问问诊页时被引导到登录页
**So that** 问诊行为可以关联到具体用户

**Acceptance Criteria**：

- [x] AC1 未登录访问 `/consultation` 重定向到 `/patient/login?redirect=<原路径>`
- [x] AC2 登录成功后跳回 `redirect` 指向的原路径
- [x] AC3 `/doctors`、`/`、`/about` 等公开页面无需登录即可访问

### Story 5 — 退出登录

**As a** 已登录的问诊用户
**I want** 能够安全退出
**So that** 在公共设备上不遗留登录态

**Acceptance Criteria**：

- [x] AC1 退出后清除本地 token，页面回到未登录态
- [x] AC2 退出后再次访问 `/consultation` 需重新登录

---

## 3. Functional Requirements

### Requirement 1 — 患者注册

- **ID**：REQ-001
- **Description**：`POST /api/patient/register`，接收 `{username, password, name, birthday?, phone?, gender?}`，校验通过后生成 id、BCrypt 加密密码、落库 `t_patient`。
- **Priority**：High
- **Related Stories**：Story 1
- **Acceptance**：Story 1 全部 AC 满足；SQL 查询可见密文密码

### Requirement 2 — 患者登录并签发令牌

- **ID**：REQ-002
- **Description**：`POST /api/patient/login`，校验凭据后签发 JWT（`sub` 为患者 id，`claim` 携带 username）。
- **Priority**：High
- **Related Stories**：Story 2
- **Acceptance**：返回 `{token, expiresIn, patient:{id,username,name}}`；错误时 401

### Requirement 3 — 当前用户信息

- **ID**：REQ-003
- **Description**：`GET /api/patient/me`，从 `Authorization: Bearer` 头解析 token 并返回用户档案。
- **Priority**：High
- **Related Stories**：Story 3
- **Acceptance**：有效 token → 200；缺失/过期/篡改 → 401

### Requirement 4 — 接口鉴权拦截

- **ID**：REQ-004
- **Description**：`JwtInterceptor` 拦截所有请求，命中白名单放行，其余要求有效 Bearer token。
- **Priority**：High
- **Related Stories**：Story 4
- **Acceptance**：`/api/patient/me` 无 token 返回 401；白名单接口仍可匿名访问

### Requirement 5 — 前端登录注册页

- **ID**：REQ-005
- **Description**：新增 `/patient/login`（`views/PatientLogin.vue`），含登录/注册两个 `a-tabs` Tab。
- **Priority**：High
- **Related Stories**：Story 1、Story 2
- **Acceptance**：注册与登录均可通过 UI 完成；表单校验齐全；错误有明确提示

### Requirement 6 — 前端路由守卫与 token 管理

- **ID**：REQ-006
- **Description**：`router.beforeEach` 对 `/consultation` 做登录校验；axios 拦截器注入 Bearer 并在 401 时清理登录态。
- **Priority**：Medium
- **Related Stories**：Story 3、Story 4、Story 5
- **Acceptance**：Story 3/4/5 全部 AC 满足

### Requirement 7 — 移除旧验证流程

- **ID**：REQ-007
- **Description**：`Consultation.vue` 移除「姓名+生日」验证表单，改为读取登录态。
- **Priority**：Medium
- **Related Stories**：Story 4
- **Acceptance**：旧表单不再出现；未登录时显示登录引导卡片

---

## 4. Non-Functional Requirements

### 4.1 Performance

- 登录接口 P95 < 300ms（BCrypt cost 10）
- `/api/patient/me` P95 < 100ms

### 4.2 Security

- **密码存储**：`BCryptPasswordEncoder`（cost 10，自动加盐）；禁止明文或 MD5/SHA 直接存储
- **令牌机制**：JJWT 0.12.7 HS256，密钥 ≥256 bit，有效期 24h
- **字段脱敏**：所有对外 DTO 均不含 `password`
- **防枚举**：登录失败对外统一返回「用户名或密码错误」，不区分用户是否存在
- **SQL 注入**：全部走 MyBatis `#{}` 预编译，禁止 `${}` 拼接用户输入
- **传输**：演示环境 HTTP；**生产须 HTTPS**（已知限制）

### 4.3 Scalability

- `t_patient.username` 唯一索引，注册查重采用「应用层查询 + DB 唯一约束」双保险
- JWT 无状态，服务端无 session 存储，水平扩容无阻碍

### 4.4 Reliability

- 注册幂等：同名重复注册不覆盖既有记录
- token 密钥/过期时间通过配置注入，便于轮转
- 拦截器异常统一转 401 并记录日志，不透出堆栈

---

## 5. Technical Requirements

### 5.1 Architecture Considerations

- 严格四层：`Controller → Service → Mapper → DB`
- `entity/Patient`（含密码）与 `dto/PatientVO`（不含密码）分离
- 前端不含任何本地密码校验逻辑，全部经后端接口判定

### 5.2 Dependencies

**内部**：数据表 `t_patient`；既有模块 `common/ApiResponse`、`exception/GlobalExceptionHandler`

**外部（版本已核对，见 `IMPLEMENTATION_PLAN.md` §2.4）**：

| 依赖 | 版本 |
|---|---|
| `spring-security-crypto` | Spring Boot 3.5.7 纳管 |
| `jjwt-api` / `jjwt-impl` / `jjwt-jackson` | 0.12.7 |
| `mybatis-spring-boot-starter` | 3.0.5（**不能用 4.x**，其依赖 Spring Boot 4） |
| `spring-boot-starter-validation` | Spring Boot 纳管 |
| `axios` | ^1.20.0 |

### 5.3 Constraints

- Spring Boot 3.5.7 + Java 17；**禁止引入完整 Spring Security**
- MyBatis XML 必须放 `src/main/resources/mapper/`
- 前端 TypeScript 严格模式（`noUnusedLocals` / `noUnusedParameters`）
- 既有接口 `/api/test/cors`、`/actuator/health`、`/api/doctors/**` 不得回归

---

## 6. Success Criteria

- [x] SC1 七项 Functional Requirements 全部实现并通过对应 Acceptance
- [x] SC2 `docs/API_TEST.md` 中本特性相关用例（T14~T31）全部通过
- [x] SC3 数据库 `t_patient` 的 `password` 均以 `$2a$` 开头且长度为 60
- [x] SC4 浏览器实测：注册 → 登录 → 刷新保持 → 退出 → 再访问被拦截，全链路可用
- [x] SC5 `npm run build` 与 `./mvnw test` 均通过

---

## 7. Task Breakdown

> 任务数：**10**（符合模板 ≤10 约束）

| # | 任务 | 产出文件 | 依赖 | 验收方式 | 状态 |
|---|---|---|---|---|---|
| 1 | `t_patient` 表（字段 + 唯一索引） | `db/init/01-schema.sql` | — | `SHOW CREATE TABLE` 含 `uk_patient_username` | ✅ |
| 2 | MyBatis 持久层：`Patient` 实体、`PatientMapper` 接口与 XML | `entity/Patient.java`、`mapper/PatientMapper.java`、`resources/mapper/PatientMapper.xml` | 1 | SQL 可执行 | ✅ |
| 3 | 密码与令牌基础设施：`PasswordEncoderConfig`（BCrypt）、`JwtUtil`（0.12.x API） | `config/PasswordEncoderConfig.java`、`util/JwtUtil.java` | — | 可签发并解析 token | ✅ |
| 4 | `PatientService`：注册查重 + 加密落库、登录校验 + 签发 token、`me` 查询 | `service/PatientService.java`、`dto/RegisterRequest.java`、`dto/LoginRequest.java`、`dto/LoginResponse.java`、`dto/PatientVO.java` | 2, 3 | REQ-001~003 的 AC | ✅ |
| 5 | `PatientController` 三个端点，统一 `ApiResponse` 包装与错误码 | `controller/PatientController.java` | 4 | curl 返回约定结构 | ✅ |
| 6 | 鉴权拦截：`JwtInterceptor` + `WebConfig` 注册白名单 | `config/JwtInterceptor.java`、`config/WebConfig.java` | 3 | REQ-004 的 AC（含白名单反向验证） | ✅ |
| 7 | 前端 API 层：`api/patient.ts`；`api/request.ts` 增加 Bearer 注入与 401 处理 | `api/patient.ts`、`api/request.ts` | 5, 6 | Network 面板可见 Authorization 头 | ✅ |
| 8 | 前端页面：`PatientLogin.vue`（登录/注册 Tabs，拆 `LoginForm.vue` / `RegisterForm.vue`）；移除 `Consultation.vue` 旧表单 | `views/PatientLogin.vue`、`components/LoginForm.vue`、`components/RegisterForm.vue`、`views/Consultation.vue` | 7 | REQ-005、REQ-007 的 AC | ✅ |
| 9 | 路由与状态：`router` 新增 `/patient/login` + `beforeEach` 守卫；`store` 增加登录态与恢复逻辑；`AppHeader` 增加用户入口 | `router/index.ts`、`store/index.ts`、`App.vue`、`components/AppHeader.vue` | 7 | REQ-006 的 AC | ✅ |
| 10 | 补充 curl 用例到 `API_TEST.md` 与 `scripts/api-test.sh`，执行后产出 `TEST_REPORT.md` | `docs/API_TEST.md`、`scripts/api-test.sh`、`docs/TEST_REPORT.md` | 5, 6 | 用例全通过且脚本可重复执行 | ✅ |

---

## 8. Implementation Notes

1. **JJWT 版本陷阱**：0.12.x 与 0.11.x API 完全不同。0.11 的 `Jwts.parserBuilder().setSigningKey()` 在 0.12 已移除，须改用 `Jwts.parser().verifyWith(key).build().parseSignedClaims()`。
2. **主键长度**：`t_patient.id` 为 `VARCHAR(32)`。早期实现用「`patient` + 完整 UUID」生成 39 字符导致 `Data truncation`，改为 `pt-` + UUID 前 12 位（共 15 字符）。
3. **统一响应**：注册成功返回 **201 Created**（非 200）；用户名重复返回 400 + code 1001。
4. **api 层不直接依赖 router**：401 时广播事件而非直接跳转，避免循环引用与多处重复跳转逻辑。
5. **恢复登录态的时机**：`App.vue` 的 `onMounted` 与路由守卫都会调用 `restorePatient()`，后者保证首屏直开 `/consultation` 时不被误判为未登录。
6. **数据库字符集**：initdb 脚本必须带 `SET NAMES utf8mb4;`，否则中文会被双重编码。

---

## 9. Risks and Mitigations

### Risk 1 — JWT 版本 API 混写导致编译失败

- **Impact**：High
- **Mitigation**：锁 0.12.7，三个 jjwt 依赖同版本；提示词中明确要求使用 `verifyWith` 写法

### Risk 2 — 引入完整 Spring Security 干扰既有裸接口

- **Impact**：High
- **Mitigation**：只引入 `spring-security-crypto`；白名单 + 反向验证 T30/T31 确保医生接口与 `/api/test/**` 仍匿名可访问

### Risk 3 — 主键超长导致注册 500

- **Impact**：Medium
- **Mitigation**：缩短 id 生成策略；T20 覆盖超长用户名场景（**已实际发生并修复**）

### Risk 4 — 登录态丢失

- **Impact**：Medium
- **Mitigation**：token 存 `localStorage`；`App.vue` 与路由守卫双重恢复路径

### Risk 5 — 密码泄漏到前端

- **Impact**：High
- **Mitigation**：Entity 与 VO 分离；T06a / T14c / T25c 三项断言持续校验响应不含 password

---

## 10. Appendix

### 10.1 References

- `web/qa-web/src/store/index.ts`（原 `verifyPatient` 自动建档逻辑，已被本特性替换）
- `web/qa-web/src/views/Consultation.vue`（原「姓名+生日」表单，已改为登录引导）
- `web/qa-web/src/views/DoctorLogin.vue`（本特性 UI 范式的参照）
- `docs/IMPLEMENTATION_PLAN.md` §6（实现方案对应章节）
- `docs/API_TEST.md`（测试用例）
- `docs/TEST_REPORT.md`（执行结果）

### 10.2 Glossary

- **问诊用户 / 患者**：使用平台向医生提问的终端用户，本特性的目标角色
- **Bearer token**：HTTP `Authorization: Bearer <JWT>` 形式的鉴权凭证
- **BCrypt cost**：BCrypt 的哈希轮次参数（2^cost），默认 10

### 10.3 API 契约清单

| 方法 | 路径 | 鉴权 | 请求体 | 响应 `data` | 主要错误码 |
|---|---|---|---|---|---|
| POST | `/api/patient/register` | 白名单 | `{username,password,name,birthday?,phone?,gender?}` | `{id,username,name,birthday,phone,gender}` | 1001, 1002 |
| POST | `/api/patient/login` | 白名单 | `{username,password}` | `{token,expiresIn,patient}` | 1003, 1004 |
| GET | `/api/patient/me` | **Bearer** | — | `{id,username,name,birthday,phone,gender}` | 1005, 1006 |

### 10.4 受影响文件清单

| 文件 | 动作 | 说明 |
|---|---|---|
| `db/init/01-schema.sql` | MODIFY | 新增 `t_patient` |
| `server/.../entity/Patient.java` | NEW | 实体（含密码，不对外） |
| `server/.../dto/PatientVO.java` | NEW | 脱敏响应模型 |
| `server/.../dto/RegisterRequest.java` | NEW | 注册入参 + 校验注解 |
| `server/.../dto/LoginRequest.java` | NEW | 登录入参 |
| `server/.../dto/LoginResponse.java` | NEW | 登录响应 |
| `server/.../mapper/PatientMapper.java` | NEW | Mapper 接口 |
| `server/.../resources/mapper/PatientMapper.xml` | NEW | XML SQL |
| `server/.../service/PatientService.java` | NEW | 业务层 |
| `server/.../controller/PatientController.java` | NEW | REST 接口 |
| `server/.../config/PasswordEncoderConfig.java` | NEW | BCrypt Bean |
| `server/.../config/JwtInterceptor.java` | NEW | 鉴权拦截 |
| `server/.../config/WebConfig.java` | NEW | 注册拦截器 |
| `server/.../util/JwtUtil.java` | NEW | 签发/解析 token |
| `server/.../pom.xml` | MODIFY | 新增依赖 |
| `server/.../application.properties` | MODIFY | JWT 配置 |
| `web/qa-web/src/api/patient.ts` | NEW | 患者接口 |
| `web/qa-web/src/api/request.ts` | NEW | axios 封装（Bearer/401） |
| `web/qa-web/src/views/PatientLogin.vue` | NEW | 登录注册页 |
| `web/qa-web/src/components/LoginForm.vue` | NEW | 登录表单 |
| `web/qa-web/src/components/RegisterForm.vue` | NEW | 注册表单 |
| `web/qa-web/src/router/index.ts` | MODIFY | 新路由 + 守卫 |
| `web/qa-web/src/store/index.ts` | MODIFY | 患者登录态 |
| `web/qa-web/src/views/Consultation.vue` | MODIFY | 移除旧验证表单 |
| `web/qa-web/src/components/AppHeader.vue` | MODIFY | 用户入口 |
| `web/qa-web/src/App.vue` | MODIFY | 恢复登录态 |
