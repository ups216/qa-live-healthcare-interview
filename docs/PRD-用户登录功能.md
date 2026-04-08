# PRD - 问诊用户登录功能

| 属性 | 内容 |
|------|------|
| **文档版本** | v1.0 |
| **产品名称** | QA Live Healthcare - 在线医疗问诊平台 |
| **需求编号** | FEAT-LOGIN-001 |
| **撰写日期** | 2026-04-08 |
| **状态** | 草案 |
| **作者** | 产品团队 |

---

## 1. 文档概述

### 1.1 背景

当前问诊（Consultation）页面仅通过姓名和生日验证患者身份，缺乏正式的账号体系。用户每次问诊都需重新输入身份信息，无法保留历史记录，也不利于后续扩展个人中心、消息通知等功能。为提升用户体验、增强数据安全性、支持产品长期演进，需要为问诊用户提供**用户名 + 密码**的账号注册与登录能力。

### 1.2 目标

| 目标项 | 描述 | 衡量指标 |
|--------|------|---------|
| 建立账号体系 | 为问诊用户提供完整的注册与登录流程 | 支持用户名+密码认证方式 |
| 安全性保障 | 用户密码加密存储，API 不暴露敏感信息 | BCrypt 加密；响应中不含 password 字段 |
| 状态管理 | 全局维护用户登录态，各页面共享 | Store 统一管理；Header 实时展示登录状态 |
| 体验优化 | 注册成功自动登录；登录后无缝进入问诊 | 登录/注册到问诊 ≤ 2 次跳转 |

### 1.3 范围

**包含：**
- 用户注册（用户名 + 密码 + 基本信息：姓名、性别、生日、手机号）
- 用户登录（用户名 + 密码）
- 登录状态管理（前端 Store 持久化 + 后端接口鉴权）
- 导航栏 Header 的登录态适配（显示欢迎信息 / 退出按钮）
- 退出登录功能

**不包含：**
- 第三方登录（微信 / 手机号验证码等）
- 密码找回 / 重置流程
- 用户资料编辑页
- 权限管理与角色 RBAC 细化
- 医生端登录改造（已有独立登录流程）

**依赖关系：**
- MySQL 数据库（通过 docker-compose 已就绪）
- 后端 Spring Boot JPA 基础设施（已搭建 Doctor 三层架构）
- 前端 Vue3 + Ant Design Vue + API 服务层（已搭建）

---

## 2. 角色定义

| 角色 | 说明 | 涉及场景 |
|------|------|----------|
| **问诊用户 (Patient)** | 使用平台进行医疗咨询的患者 | 注册账号、登录系统、发起问诊、查看历史记录 |
| **医生 (Doctor)** | 在线提供专业医疗建议 | （已有角色，本需求不涉及变更） |

---

## 3. 功能需求

### 3.1 用户注册

#### 功能概述
新用户通过填写注册表单创建平台账号。

#### 前置条件
- 后端 patients 表已创建
- 注册页面可正常访问
- 用户名未被其他用户占用

#### 详细规则

##### 3.1.1 注册表单提交

**输入参数：**

| 参数名 | 类型 | 必填 | 校验规则 | 说明 |
|--------|------|:----:|----------|------|
| username | string | 是 | 3-20 字符；仅允许字母(a-z, A-Z)、数字(0-9)、下划线(_)；首字符必须为字母或下划线 | 用于登录的唯一标识 |
| password | string | 是 | 6-20 字符；必须同时包含至少一个字母和一个数字 | 明文传输，后端 BCrypt 加密存储 |
| confirmPassword | string | 是 | 必须与 password 字段完全一致 | 仅前端校验 |
| name | string | 是 | 2-10 个中文字符 或 4-20 个英文字符 | 真实姓名 |
| gender | string | 是 | 枚举值："男" / "女" / "其他" | 性别 |
| birthday | string | 是 | 合法日期格式 (YYYY-MM-DD)；不能晚于当前日期 | 出生日期 |
| phone | string | 否 | 可选填；如填写需符合中国大陆手机号格式 (11位数字) | 联系方式 |

**处理逻辑：**

```
1. 前端实时校验各字段格式（输入时即时反馈错误）
2. 用户点击"注册"按钮 → 前端做最终表单校验
3. 校验通过 → POST /api/patients/register 发送请求
4. 后端接收请求：
   a. @Valid Bean Validation 二次校验参数
   b. 查询数据库确认 username 唯一性
   c. 使用 BCryptPasswordEncoder 加密 password
   d. 生成 UUID 作为患者 ID
   e. 写入 patients 表
   f. 返回 PatientDTO（不含密码）
5. 前端收到成功响应：
   a. 将返回的用户信息写入 store.currentPatientUser
   b. 标记 isPatientLoggedIn = true
   c. 跳转至首页或来源页
```

**输出结果：**

| 场景 | HTTP Code | 返回内容 |
|------|:---------:|----------|
| 注册成功 | 200 | `{"data": PatientDTO, "error": null}` |
| 用户名已存在 | 400 | `{"data": null, "error": "USERNAME_EXISTS", "message": "该用户名已被注册"}` |
| 参数校验失败 | 400 | `{"data": null, "error": "VALIDATION_ERROR", "message": "<具体字段错误信息>"}` |
| 服务器异常 | 500 | `{"data": null, "error": "INTERNAL_ERROR", "message": "注册失败，请稍后重试"}` |

#### UI / 交互要求

**页面入口路径：**
- 登录页底部「还没有账号？立即注册」链接
- Header 导航栏「患者登录」按钮旁的下拉菜单中的"注册"入口

**核心交互步骤：**

```
用户打开注册页面
  → 浏览表单字段和提示文字
  → 输入用户名（失焦时检查唯一性，异步调用 GET /api/patients/check-username?username=xxx）
  → 填写密码（强度指示器：弱/中/强）
  → 填写确认密码
  → 填写基本信息（姓名/性别/生日/手机号）
  → 点击【注册】按钮
    → 按钮 loading 状态，防止重复提交
    → 成功：Toast 提示"注册成功"，1.5s 后跳转首页
    → 失败：对应字段下方显示红色错误提示
```

**状态反馈：**
| 状态 | 视觉反馈 |
|------|---------|
| 加载中 | 注册按钮变为 loading spinner，文字变为"注册中..." |
| 注册成功 | 绿色 Toast "注册成功，欢迎加入！" |
| 用户名重复 | 用户名输入框红色边框 + 下方红字"该用户名已被使用" |
| 密码不一致 | 确认密码框红色边框 + 下方红字"两次输入的密码不一致" |
| 网络错误 | 全局 Error Modal "网络连接失败，请检查网络后重试" |

---

### 3.2 用户登录

#### 功能概述
已注册用户通过用户名和密码完成身份验证，建立会话。

#### 前置条件
- 用户已在系统中完成注册
- 登录页面可正常访问

#### 详细规则

##### 3.2.1 登录认证

**输入参数：**

| 参数名 | 类型 | 必填 | 校验规则 | 说明 |
|--------|------|:----:|----------|------|
| username | string | 是 | 非空，去除首尾空格 | 用户输入的用户名 |
| password | string | 是 | 非空 | 用户输入的密码 |

**处理逻辑：**

```
1. 用户在登录页输入用户名和密码
2. 点击"登录"按钮
3. POST /api/patients/login 发送 {username, password}
4. 后端处理：
   a. 根据 username 查询 patients 表
   b. 未找到用户 → 返回 401（统一提示"用户名或密码错误"，不泄露用户是否存在）
   c. 找到用户 → BCryptPasswordEncoder.matches(rawPassword, encodedPassword)
   d. 密码不匹配 → 返回 401（同上统一提示）
   e. 密码匹配 → 检查 is_active 是否为 true
   f. 账号被禁用 → 返回 403 "账号已被禁用"
   g. 认证通过 → 返回 PatientDTO
5. 前端收到成功响应：
   a. 写入 store.currentPatientUser
   b. localStorage 持久化登录标记（可选：存 token 或 user ID）
   c. 更新 Header 显示欢迎信息
   d. 跳转至目标页（优先来源页，其次首页）
```

**输出结果：**

| 场景 | HTTP Code | 返回内容 |
|------|:---------:|----------|
| 登录成功 | 200 | `{"data": PatientDTO, "error": null}` |
| 认证失败（用户不存在/密码错误） | 401 | `{"data": null, "error": "INVALID_CREDENTIALS", "message": "用户名或密码错误"}` |
| 账号禁用 | 403 | `{"data": null, "error": "ACCOUNT_DISABLED", "message": "账号已被禁用，请联系客服"}` |
| 参数缺失 | 400 | `{"data": null, "error": "VALIDATION_ERROR", "message": "请输入用户名和密码"}` |

##### 3.2.2 退出登录

**处理逻辑：**

```
1. 用户点击 Header 中的"退出"按钮
2. 确认弹窗："确定要退出登录吗？"
3. 确认后：
   a. POST /api/patients/logout（服务端清除 session/token，如有）
   b. 清除 store.currentPatientUser（置 null）
   c. 清除 localStorage 中的登录标记
   d. 更新 Header 回复未登录态
   e. 跳转回首页
```

**输出结果：**

| 场景 | HTTP Code | 返回内容 |
|------|:---------:|----------|
| 退出成功 | 200 | `{"data": null, "error": null, "message": "已退出登录"}` |

#### UI / 交互要求

**页面入口路径：**
- 直接访问 `/patient/login` 路由
- Header 导航栏「患者登录」按钮
- 问诊页未登录时的引导链接

**核心交互步骤：**

```
用户打开登录页
  → 看到用户名和密码两个输入框
  → 输入用户名和密码
  → 点击【登录】按钮
    → loading 状态
    → 成功：跳转目标页
    → 失败：表单上方红色横幅显示错误信息
  → 底部看到"还没有账号？[立即注册]"链接
  → 右下角看到"[医生入口]"链接（跳转医生登录页）
```

**状态反馈：**
| 状态 | 视觉反馈 |
|------|---------|
| 加载中 | 登录按钮 loading，文字"登录中..." |
| 登录失败 | 表单顶部 Alert 组件，红色背景，显示"用户名或密码错误" |
| 退出确认 | Popconfirm 气泡确认框："确定要退出登录吗？" |

---

### 3.3 登录状态管理

#### 功能概述
在前端 Store 和页面导航中全局维护用户的登录状态。

#### 详细规则

##### 3.3.1 Store 扩展

```typescript
// 新增类型定义
export interface PatientUser {
  id: string;
  username: string;
  name: string;
  gender: string;
  birthday: string;
  phone?: string;
}

// State 中新增字段
interface State {
  // ... 已有字段 ...
  currentPatientUser: PatientUser | null;  // 新增
}

// Store 中新增方法
logoutPatient(): void;          // 清除当前患者用户
isPatientLoggedIn(): boolean;   // computed 判断是否已登录
```

##### 3.3.2 页面影响矩阵

| 页面 | 未登录状态 | 已登录状态 |
|------|-----------|-----------|
| **AppHeader 导航栏** | 显示「患者登录」按钮（绿色） | 显示「欢迎，{name}」+ 退出按钮（Popconfirm） |
| **Consultation 问诊页** | 自动弹出登录引导模态框或重定向到登录页 | 正常显示，表单自动带入用户姓名等信息 |
| **Home 首页** | 正常显示 | 正常显示 |
| **Doctors 医生列表** | 正常显示 | 正常显示 |
| **Login 登录页** | 显示登录表单 | 如果已登录，提示"您已登录"并提供去问诊入口 |
| **Register 注册页** | 显示注册表单 | 如果已登录，提示"您已注册"并提供去问诊入口 |

---

## 4. 数据模型

### 4.1 数据库设计

新增 `patients` 表：

```sql
CREATE TABLE patients (
    id            VARCHAR(36)  NOT NULL COMMENT 'UUID主键',
    username      VARCHAR(50)  NOT NULL COMMENT '用户名（登录凭证）',
    password      VARCHAR(255) NOT NULL COMMENT 'BCrypt加密后的密码',
    name          VARCHAR(50)  NOT NULL COMMENT '真实姓名',
    gender        VARCHAR(10)  NOT NULL COMMENT '性别',
    birthday      DATE         NOT NULL COMMENT '出生日期',
    phone         VARCHAR(20)  DEFAULT NULL COMMENT '手机号（可选）',
    is_active     BOOLEAN      DEFAULT TRUE COMMENT '账号是否激活',
    created_at    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    PRIMARY KEY (id),
    UNIQUE KEY uk_patients_username (username),
    INDEX idx_patients_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='问诊用户表';
```

| 字段 | 类型 | 是否必填 | 默认值 | 说明 |
|------|------|:-------:|--------|------|
| id | VARCHAR(36) | 是 | AUTO(UUID) | 主键 |
| username | VARCHAR(50) | 是 | - | 唯一，登录标识 |
| password | VARCHAR(255) | 是 | - | BCrypt 哈希值，非明文 |
| name | VARCHAR(50) | 是 | - | 真实姓名 |
| gender | VARCHAR(10) | 是 | - | 男/女/其他 |
| birthday | DATE | 是 | - | 出生日期 |
| phone | VARCHAR(20) | 否 | NULL | 手机号 |
| is_active | BOOLEAN | 是 | true | 账号启用状态 |
| created_at | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | 是 | ON UPDATE | 更新时间 |

### 4.2 Entity 定义

```java
@Entity
@Table(name = "patients")
public class Patient {

    @Id
    private String id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(name = "password", nullable = false)
    private String password; // BCrypt encoded

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 10)
    private String gender;

    @Column(name = "birthday", nullable = false)
    private LocalDate birthday;

    @Column(length = 20)
    private String phone;

    @Column(name = "is_active")
    private Boolean isActive;

    // getters & setters...
}
```

### 4.3 DTO 定义

```java
// PatientDTO.java — API 返回给前端的用户信息（不含密码）
public class PatientDTO {
    private String id;
    private String username;
    private String name;
    private String gender;
    private String birthday;
    private String phone;
    private boolean active;

    // getters & setters...
}

// RegisterRequest.java — 注册请求体
public class RegisterRequest {
    @NotBlank @Size(min = 3, max = 20) @Pattern(regexp = "^[a-zA-Z_][a-zA-Z0-9_]*$")
    private String username;

    @NotBlank @Size(min = 6, max = 20) @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).+$")
    private String password;

    @NotBlank
    private String confirmPassword;

    @NotBlank @Size(min = 2, max = 10)
    private String name;

    @NotBlank
    private String gender;

    @NotNull @Past
    private LocalDate birthday;

    @Pattern(regexp = "^1[3-9]\\d{9}$")
    private String phone;

    // getters & setters...
}

// LoginRequest.java — 登录请求体
public class LoginRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    // getters & setters...
}
```

### 4.4 API 接口定义

#### POST `/api/patients/register` — 用户注册

**请求头：**
```
Content-Type: application/json
```

**请求体：**
```json
{
    "username": "zhangsan2026",
    "password": "Pass123456",
    "confirmPassword": "Pass123456",
    "name": "张三",
    "gender": "男",
    "birthday": "1990-05-15",
    "phone": "13800138000"
}
```

**成功响应 (200)：**
```json
{
    "data": {
        "id": "a1b2c3d4-e5f6-...",
        "username": "zhangsan2026",
        "name": "张三",
        "gender": "男",
        "birthday": "1990-05-15",
        "phone": "13800138000",
        "active": true
    },
    "total": 0,
    "error": null,
    "message": null
}
```

**错误响应：**
| HTTP Code | error 字段 | message 字段 | 触发场景 |
|-----------|-----------|-------------|---------|
| 400 | USERNAME_EXISTS | 该用户名已被注册 | 数据库中 username 已存在 |
| 400 | VALIDATION_ERROR | <具体字段错误> | Bean Validation 校验失败 |
| 500 | INTERNAL_ERROR | 注册失败，请稍后重试 | 服务器内部异常 |

---

#### POST `/api/patients/login` — 用户登录

**请求体：**
```json
{
    "username": "zhangsan2026",
    "password": "Pass123456"
}
```

**成功响应 (200)：**
```json
{
    "data": {
        "id": "a1b2c3d4-e5f6-...",
        "username": "zhangsan2026",
        "name": "张三",
        "gender": "男",
        "birthday": "1990-05-15",
        "phone": "13800138000",
        "active": true
    },
    "total": 0,
    "error": null,
    "message": null
}
```

**错误响应：**
| HTTP Code | error 字段 | message 字段 | 触发场景 |
|-----------|-----------|-------------|---------|
| 401 | INVALID_CREDENTIALS | 用户名或密码错误 | 用户不存在或密码不匹配 |
| 403 | ACCOUNT_DISABLED | 账号已被禁用 | is_active = false |
| 400 | VALIDATION_ERROR | 请输入用户名和密码 | 参数为空或格式错误 |

---

#### POST `/api/patients/logout` — 退出登录

**成功响应 (200)：**
```json
{
    "data": null,
    "total": 0,
    "error": null,
    "message": "已退出登录"
}
```

---

#### GET `/api/patients/check-username?username=xxx` — 检查用户名可用性（可选）

**成功响应 (200)：**
```json
{
    "data": {
        "available": true,
        "username": "zhangsan2026"
    },
    "error": null
}
```

---

## 5. 技术方案

### 5.1 架构设计

遵循现有 Spring MVC 三层架构（与 Doctor 模块保持一致）：

```
┌─────────────────────────────────────────────┐
│                  Frontend (Vue3)             │
│  ┌──────────┐ ┌──────────┐ ┌─────────────┐ │
│  │ LoginPage│ │RegisterPage│ │ AppHeader   │ │
│  └────┬─────┘ └────┬─────┘ └──────┬──────┘ │
│       └────────────┼────────────┘          │
│              api/patient.ts                 │
│                    │                        │
│              Store (currentPatientUser)     │
└────────────────────┼───────────────────────┘
                     │ HTTP (JSON)
                     ▼
┌─────────────────────────────────────────────┐
│           Backend (Spring Boot)              │
│  ┌──────────────────────────────────────┐   │
│  │      PatientController               │   │
│  │  /register  /login  /logout          │   │
│  └──────────────┬───────────────────────┘   │
│                 │                           │
│  ┌──────────────▼───────────────────────┐   │
│  │       PatientService                │   │
│  │  register() login() logout()         │   │
│  │  checkUsernameExists() encryptPwd()  │   │
│  └──────────────┬───────────────────────┘   │
│                 │                           │
│  ┌──────────────▼───────────────────────┐   │
│  │     PatientRepository (JPA)          │   │
│  │  findByUsername() existsByUsername()  │   │
│  │  save()                               │   │
│  └──────────────┬───────────────────────┘   │
│                 │                           │
│  ┌──────────────▼───────────────────────┐   │
│  │     MySQL (patients table)            │   │
│  └──────────────────────────────────────┘   │
└─────────────────────────────────────────────┘
```

### 5.2 技术选型

| 组件 | 选型方案 | 版本 | 选择理由 |
|------|---------|:----:|----------|
| 后端框架 | Spring Boot | 3.5.7 | 与现有项目一致 |
| 数据访问 | Spring Data JPA | - | 与 Doctor 模块保持一致的三层架构 |
| 密码加密 | BCryptPasswordEncoder | (Spring Security) | 行业标准单向哈希算法，内置盐值 |
| 参数校验 | Jakarta Bean Validation (@Valid) | 3.0 | 声明式校验，与 Controller 无缝集成 |
| 前端框架 | Vue 3 Composition API | 3.5 | 与现有项目一致 |
| UI 组件库 | Ant Design Vue | 4.2 | Form / Input / Button / Message 等组件完备 |
| API 封装 | 原生 fetch | - | 与 doctor.ts 保持一致的轻量方案 |

### 5.3 安全考虑

- **密码安全**：使用 BCrypt 单向哈希加密存储（强度 factor=10~12），不可逆向解密
- **防 SQL 注入**：全部使用 JPA Parameterized Query / 方法命名查询，杜绝拼接 SQL
- **防 XSS**：前端对用户输入做 HTML 转义（Vue 默认转义 {{}}）
- **敏感信息保护**：
  - API 响应 DTO 中绝不包含 password 字段
  - 日志中不打印密码明文
  - 错误信息不泄露用户是否存在（统一提示"用户名或密码错误"）
- **输入校验**：前后端双重校验（前端即时反馈 + 后端 @Valid 强校验）
- **暴力破解防护**：（v1.0 可选）连续失败次数限制

### 5.4 关键文件清单

| 文件路径 | 类型 | 说明 |
|---------|:----:|------|
| `server/.../entity/Patient.java` | 新建 | 患者 JPA 实体类 |
| `server/.../dto/PatientDTO.java` | 新建 | 患者数据传输对象（API 响应用） |
| `server/.../dto/RegisterRequest.java` | 新建 | 注册请求体 |
| `server/.../dto/LoginRequest.java` | 新建 | 登录请求体 |
| `server/.../repository/PatientRepository.java` | 新建 | JPA Repository 接口 |
| `server/.../service/PatientService.java` | 新建 | 患者业务逻辑（注册/登录/BCrypt） |
| `server/.../controller/PatientController.java` | 新建 | 患者 REST API 控制器 |
| `server/.../pom.xml` | 修改 | 添加 spring-boot-starter-security（BCrypt 依赖） |
| `web/qa-web/src/api/patient.ts` | 新建 | 前端 API 调用封装 |
| `web/qa-web/src/store/index.ts` | 修改 | 新增 PatientUser 类型和相关方法 |
| `web/qa-web/src/views/Login.vue` | 新建/修改 | 患者登录页 |
| `web/qa-web/src/views/Register.vue` | 新建 | 患者注册页 |
| `web/qa-web/src/components/AppHeader.vue` | 修改 | 适配患者登录态展示 |

---

## 6. 非功能需求

| 维度 | 要求 | 验证方法 |
|------|------|---------|
| **性能** | 注册/登录接口响应时间 < 500ms (P99) | Postman / 压测工具测量 |
| **安全性** | BCrypt 加密密码；API 不暴露 password；防止 SQL 注入与 XSS | 安全审计 / 代码审查 |
| **兼容性** | Chrome/Firefox/Safari/Edge 最新 2 个版本；移动端 Safari/Chrome 浏览器 | 多浏览器实测 |
| **可用性** | 错误提示清晰友好；表单实时校验即时反馈 | UX 走查 |
| **可扩展性** | Entity 设计预留扩展字段（邮箱、地址等）；Service 层便于添加第三方登录 | 架构评审 |
| **可维护性** | 代码风格与 Doctor 模块一致；关键方法含注释 | Code Review |

---

## 7. 页面原型 / 交互设计

### 7.1 登录页 (`/patient/login`)

```
┌──────────────────────────────────────────────────────┐
│  ┌─────────────────────────────────────────────────┐ │
│  │  QA Live Healthcare                      [Logo] │ │
│  ├─────────────────────────────────────────────────┤ │
│  │                                                 │ │
│  │          ┌───────────────────────────┐          │ │
│  │          │      患者账号登录          │          │ │
│  │          │                           │          │ │
│  │          │  ┌─────────────────────┐  │          │ │
│  │          │  │  请输入用户名          │  │          │ │
│  │          │  └─────────────────────┘  │          │ │
│  │          │  ┌─────────────────────┐  │          │ │
│  │          │  │  请输入密码            │  │          │ │
│  │          │  └─────────────────────┘  │          │ │
│  │          │                           │          │ │
│  │          │  [        登 录        ]  │          │ │
│  │          │                           │          │ │
│  │          │  还没有账号？[立即注册]     │          │ │
│  │          └───────────────────────────┘          │ │
│  │                                                 │ │
│  │                   [医生入口 →]                  │ │
│  └─────────────────────────────────────────────────┘ │
└──────────────────────────────────────────────────────┘
```

**说明：**
- 居中卡片式布局，最大宽度 400px
- Logo 区域复用 AppHeader 的品牌元素
- 「医生入口」右下角小字链接，跳转到 `/doctor/login`
- 登录失败时卡片顶部出现红色 Alert 横幅

### 7.2 注册页 (`/patient/register`)

```
┌──────────────────────────────────────────────────────┐
│  ┌─────────────────────────────────────────────────┐ │
│  │  QA Live Healthcare                      [Logo] │ │
│  ├─────────────────────────────────────────────────┤ │
│  │                                                 │ │
│  │          ┌───────────────────────────┐          │ │
│  │          │      创建患者账号          │          │ │
│  │          │                           │          │ │
│  │          │  用户名 *                  │          │ │
│  │          │  ┌─────────────────────┐  │          │ │
│  │          │  │  3-20位字母/数字/下划线│  │          │ │
│  │          │  └─────────────────────┘  │          │ │
│  │          │                           │          │ │
│  │          │  密   码 *                 │          │ │
│  │          │  ┌─────────────────────┐  │          │ │
│  │          │  │  ••••••••            │  │          │ │
│  │          │  └─────────────────────┘  │          │ │
│  │          │  [██████░░░] 弱          │          │ │
│  │          │                           │          │ │
│  │          │  确认密码 *                │          │ │
│  │          │  ┌─────────────────────┐  │          │ │
│  │          │  │  ••••••••            │  │          │ │
│  │          │  └─────────────────────┘  │          │ │
│  │          │                           │          │ │
│  │          │  姓   名 *                 │          │ │
│  │          │  ┌─────────────────────┐  │          │ │
│  │          │  │                     │  │          │ │
│  │          │  └─────────────────────┘  │          │ │
│  │          │                           │          │ │
│  │          │  性   别 *  [ 男 ▼ ]      │          │ │
│  │          │  生   日 *  [____-__-__]   │          │ │
│  │          │  手 机 号   [____________]  │          │ │
│  │          │                           │          │ │
│  │          │  [        注 册        ]  │          │ │
│  │          │                           │          │ │
│  │          │  已有账号？[去登录]         │          │ │
│  │          └───────────────────────────┘          │ │
│  │                                                 │ │
│  └─────────────────────────────────────────────────┘ │
└──────────────────────────────────────────────────────┘
```

**说明：**
- 与登录页风格一致的中心卡片布局
- 密码下方有强度指示条（弱/中/强三色渐变）
- 用户名失焦时触发异步唯一性检查（右侧 ✓ 或 ✗ 图标）
- 手机号为选填项，标注"(可选)"
- 「去登录」链接位于底部

### 7.3 Header 登录态变化

**未登录状态：**
```
┌──────────────────────────────────────────────────┐
│ [Logo] QA Live Healthcare  首页 问诊 医生 关于  [患者登录] │
└──────────────────────────────────────────────────┘
```

**已登录状态：**
```
┌──────────────────────────────────────────────────────────────┐
│ [Logo] QA Live Healthcare  首页 问诊 医生 关于  欢迎，张三 [退出] │
└──────────────────────────────────────────────────────────────┘
```

---

## 8. 验收标准

### 8.1 注册验收

| 编号 | 验收项 | 预期结果 | 优先级 |
|:----:|--------|---------|:------:|
| AC-RG-01 | 输入合法信息完成注册 | 注册成功，自动登录并跳转首页 | P0 |
| AC-RG-02 | 用户名已被占用 | 输入框红框 + 提示"该用户名已被注册"，阻止提交 | P0 |
| AC-RG-03 | 密码不足 6 位 | 密码框下方红字"密码长度需 6-20 位" | P0 |
| AC-RG-04 | 密码无字母 | 密码框下方红字"密码必须包含字母和数字" | P0 |
| AC-RG-05 | 两项密码不一致 | 确认密码框下方红字"两次输入的密码不一致" | P0 |
| AC-RG-06 | 必填字段为空 | 对应字段红框 + "此项为必填项" | P0 |
| AC-RG-07 | 手机号格式错误（非 11 位数字） | 手机号框下方红字"请输入正确的手机号格式" | P1 |
| AC-RG-08 | 注册成功后检查数据库 | patients 表新增一条记录，password 为 BCrypt 哈希值 | P0 |
| AC-RG-09 | API 响应不含 password | JSON 响应 data 中无 password 字段 | P0 |
| AC-RG-10 | 同一用户名二次注册被拦截 | 返回 400 USERNAME_EXISTS | P0 |

### 8.2 登录验收

| 编号 | 验收项 | 预期结果 | 优先级 |
|:----:|--------|---------|:------:|
| AC-LI-01 | 正确的用户名+密码登录 | 登录成功，Header 变更为"欢迎，{name}" | P0 |
| AC-LI-02 | 密码错误 | 表单顶部红色 Alert "用户名或密码错误" | P0 |
| AC-LI-03 | 不存在的用户名 | 同上统一提示（不泄露用户是否存在） | P0 |
| AC-LI-04 | 用户名为空 | 无法点击登录 / 提示"请输入用户名" | P0 |
| AC-LI-05 | 密码为空 | 无法点击登录 / 提示"请输入密码" | P0 |
| AC-LI-06 | 登录后刷新页面 | 登录态保持（Store 从 localStorage/localState 恢复） | P0 |
| AC-LI-07 | 点击退出 | Popconfirm 确认 → 退出成功 → Header 回复未登录态 | P0 |
| AC-LI-08 | 已登录状态下访问登录页 | 提示"您已登录"，提供去问诊入口 | P1 |

### 8.3 数据验收

- [ ] patients 表结构与设计文档一致
- [ ] 密码以 BCrypt 哈希值存储（60 字符，$2a$ 开头）
- [ ] API 响应中不含 password 字段
- [ ] username 有 UNIQUE 约束
- [ ] id 使用 UUID 格式（36 字符）

### 8.4 性能验收

- [ ] 注册接口 P99 响应 < 800ms
- [ ] 登录接口 P99 响应 < 500ms
- [ ] 用户名唯一性检查 < 300ms
- [ ] 登录页首次加载 < 2s

---

## 9. 时间规划

| 阶段 | 任务 | 开始日期 | 结束日期 | 状态 |
|------|------|:--------:|:--------:|:----:|
| P1 | 后端：Entity / DTO / Repository / Service / Controller 开发 | 待定 | 待定 | 待开始 |
| P2 | 后端：数据库建表 + pom.xml 依赖更新 | 待定 | 待定 | 待开始 |
| P3 | 前端：API 封装 + Store 扩展 | 待定 | 待定 | 待开始 |
| P4 | 前端：Login.vue / Register.vue 页面开发 | 待定 | 待定 | 待开始 |
| P5 | 前端：AppHeader 登录态适配 | 待定 | 待定 | 待开始 |
| P6 | 联调测试 + Bug 修复 | 待定 | 待定 | 待开始 |

---

## 10. 风险与应对

| 风险 | 可能性 | 影响程度 | 应对措施 | 负责人 |
|------|:------:|:-------:|---------|--------|
| BCrypt 性能开销（hash 计算 ~100ms） | 高 | 低 | 注册时可接受；登录可通过缓存缓解（v1.0 暂不需要） | 后端开发 |
| 前后端联调字段名不一致（驼峰 vs 下划线） | 中 | 中 | PRD 第 4 章明确 JSON 字段规范；前后端共同遵守 | 前后端开发 |
| 用户名唯一性竞态（并发注册相同用户名） | 低 | 中 | 数据库 UNIQUE KEY 保证兜底；前端先做 check 再提交 | 后端开发 |
| Spring Security 依赖引入导致冲突 | 低 | 高 | 仅引入 spring-security-crypto 包获取 BCrypt，不启用完整 Security 过滤链 | 后端开发 |

---

## 11. 附录

### 11.1 术语表

| 术语 | 解释 |
|------|------|
| **PRD** | Product Requirement Document，产品需求文档 |
| **BCrypt** | 一种基于 Blowfish 的自适应密码哈希函数，内建 salt，广泛用于密码安全存储 |
| **JPA** | Java Persistence API，Java 持久化规范 |
| **DTO** | Data Transfer Object，数据传输对象，用于层间传递数据 |
| **Bean Validation** | JSR 380 标准，提供声明式参数校验能力（@NotNull, @Size 等） |

### 11.2 参考资料

- [Spring Security BCrypt 官方文档](https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/bcrypt.html)
- [OWASP Password Storage Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html)
- [Ant Design Vue Form 组件文档](https://antdv.com/components/form)
- 本项目已有的 Doctor 模块实现（参考三层架构模式）

### 11.3 变更记录

| 版本 | 日期 | 变更内容 | 变更人 | 审批人 |
|------|------|---------|:------:|:------:|
| v1.0 | 2026-04-08 | 初始草案，包含注册/登录/状态管理全量需求定义 | 产品团队 | - |
