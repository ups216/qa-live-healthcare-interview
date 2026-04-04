# PRD：问诊用户用户名密码登录功能

| 属性 | 内容 |
|------|------|
| **文档版本** | 1.0 |
| **创建日期** | 2026-04-03 |
| **最后更新** | 2026-04-03 |
| **文档状态** | 草稿 |
| **负责人** | - |
| **相关链接** | [PRD文档模板](./prd-patient-login.md) |

---

## 1. 概述

### 1.1 项目背景

**项目名称：** QA Live Healthcare 在线医疗问诊平台

**现有系统概况：**

| 组件 | 技术栈 | 状态 |
|------|--------|------|
| 前端 | Vue 3 + TypeScript + Vite | 已上线 |
| 后端-用户服务 | Spring Boot + JPA + MySQL | 已上线 |
| 后端-问诊服务 | Spring Boot | 已上线 |
| 数据库 | MySQL 8.0 | 已部署 |


### 1.2 现有验证方式问题

当前问诊平台患者身份验证存在安全问题：

| 问题 | 描述 | 影响程度 |
|------|------|----------|
| 安全性不足 | 无需密码即可登录 | 高 |
| 身份冒用风险 | 缺乏有效的身份验证机制 | 高 |
| 无法密码找回 | 用户遗忘验证信息后无法恢复 | 中 |
| 缺乏用户体系 | 无法积累患者历史问诊记录 | 中 |

### 1.3 目标

为问诊患者提供安全的用户名密码登录方式，建立完整的患者账户体系：

1. **安全目标**：实现标准用户名密码认证，保障账户安全
2. **扩展目标**：为后续功能（病历管理、处方查看、健康档案）奠定基础

### 1.4 范围

**本次开发范围：**

| 类型 | 功能 | 说明 |
|------|------|------|
| 后端 | 患者注册 API | 手机号+密码+验证码 |
| 后端 | 患者登录 API | 手机号+密码，返回 JWT |
| 后端 | Token 管理 API | 刷新、验证、失效 |
| 后端 | 患者信息 API | 增删改查 |
| 前端 | 患者登录页面 | `/patient/login` |
| 前端 | 患者注册页面 | `/patient/register` |
| 前端 | Token 管理 | localStorage + 自动续期 |
| 前端 | 问诊页面集成 | 仅支持注册用户 |

**暂不开发范围：**

- 旧系统数据迁移
- 历史问诊记录兼容
- 忘记密码 / 密码重置
- 管理员后台
- 第三方登录（微信/支付宝）
- 短信服务真实发送（第一阶段使用模拟验证码）

---

## 2. 用户故事

### 2.1 用户角色

| 角色 | 描述 | 代表用户 |
|------|------|----------|
| 新患者 | 首次使用平台，需要注册后使用问诊 | 张三 |
| 老患者 | 已注册患者，通过手机号密码登录，查看历史问诊 | 赵明 |
| 游客 | 未登录用户，可浏览首页和医生列表 | 访客 |

### 2.2 用户故事与验收条件

```
【US-01】新患者注册

作为一名 首次使用平台的新患者
我想要 通过手机号和密码注册账户
以便 我可以安全地使用问诊服务

验收条件：
  - [ ] 可以输入手机号获取验证码
  - [ ] 可以设置8-20位包含字母和数字的密码
  - [ ] 可以填写姓名、性别、生日完成注册
  - [ ] 注册成功后自动登录
  - [ ] 注册后可以访问问诊页面
```

```
【US-02】老患者登录

作为一名 已注册的老患者
我想要 使用手机号和密码登录系统
以便 我可以查看我的历史问诊记录

验收条件：
  - [ ] 可以使用手机号+密码登录
  - [ ] 登录成功返回 Token
  - [ ] 登录后可查看个人历史问诊
  - [ ] 错误密码提示"手机号或密码错误"
```

```
【US-03】登录状态保持

作为一名 登录后的患者
我想要 关闭浏览器后再次访问无需重新登录
以便 我可以随时继续使用服务

验收条件：
  - [ ] Token 存储在 localStorage
  - [ ] 页面刷新后保持登录状态
  - [ ] Token 过期前自动刷新
  - [ ] Token 过期后跳转登录页
```

```
【US-04】安全退出

作为一名 已登录的患者
我想要 安全退出登录
以便 在公共设备上保护我的隐私

验收条件：
  - [ ] 点击退出按钮清除 Token
  - [ ] 退出后跳转登录页面
  - [ ] 无法使用旧 Token 访问 API
```


## 3. 功能需求

### 3.1 功能列表

| 编号 | 功能名称 | 优先级 | 依赖 | 说明 |
|------|----------|--------|------|------|
| F01 | 模拟短信验证码 | P0 | - | 验证码发送与校验（第一阶段） |
| F02 | 患者注册 | P0 | F01 | 手机号+密码+验证码注册 |
| F03 | 患者登录 | P0 | - | 手机号+密码登录，返回JWT |
| F04 | Token 管理 | P0 | F03 | Access/Refresh Token |
| F05 | 退出登录 | P0 | - | 清除 Token |

### 3.2 功能详细说明

#### F01：模拟短信验证码

**规则：**

| 参数 | 值 |
|------|-----|
| 验证码长度 | 6位数字 |
| 有效期 | 5分钟 |
| 发送间隔 | 同一手机号60秒 |
| 错误限制 | 同一验证码错误3次失效 |
| 模拟验证码 | `123456`（开发环境） |

**模拟实现说明：**

为加快开发进度，第一阶段使用内存存储验证码，后续替换为真实短信服务商。

#### F02：患者注册

**API 路径：** `POST /api/patients/register`

**请求参数：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| phone | string | 是 | 11位手机号 |
| code | string | 是 | 6位验证码 |
| password | string | 是 | 8-20位，字母+数字 |
| name | string | 是 | 2-20字符 |
| gender | integer | 否 | 性别：0-未知，1-男，2-女 |
| birthday | string | 否 | YYYY-MM-DD 格式 |

**业务规则：**

1. 验证手机号格式
2. 验证验证码正确性（未使用、未过期）
3. 验证密码强度
4. 检查手机号是否已注册
5. 密码加密存储（BCrypt）
6. 注册成功返回 Token

**响应示例：**

```json
{
  "code": 0,
  "message": "注册成功",
  "data": {
    "patient": {
      "id": "p550e8400-e29b-41d4-a716-446655440000",
      "phone": "13800138000",
      "name": "张三",
      "createdAt": "2026-04-03T10:00:00Z"
    },
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
  }
}
```

#### F03：患者登录

**API 路径：** `POST /api/patients/login`

**请求参数：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| phone | string | 是 | 手机号 |
| password | string | 是 | 密码 |

**业务规则：**

1. 验证账户存在
2. 验证密码正确（BCrypt）
3. 验证账户状态（非锁定）
4. 记录登录失败次数
5. 失败5次锁定15分钟
6. 登录成功重置失败次数

**错误响应：**

```json
{
  "code": 40101,
  "message": "手机号或密码错误"
}
```

| 错误码 | 说明 |
|--------|------|
| 40101 | 手机号或密码错误 |
| 40102 | 账户已锁定，请15分钟后重试 |
| 40401 | 该手机号未注册 |

#### F04：Token 管理

**JWT 结构设计：**

```json
{
  "header": {
    "alg": "HS256",
    "typ": "JWT"
  },
  "payload": {
    "sub": "p550e8400-e29b-41d4-a716-446655440000",
    "type": "access",
    "phone": "13800138000",
    "name": "张三",
    "iat": 1743667200,
    "exp": 1743674400
  }
}
```

**Token 配置：**

| Token 类型 | 有效期 | 存储位置 |
|-----------|--------|----------|
| Access Token | 2小时 | localStorage |
| Refresh Token | 7天 | localStorage |

**自动续期逻辑：**

```ascii
┌──────────────────────────────────────────────────────────────────────┐
│                          Token 刷新流程                               │
├──────────────────────────────────────────────────────────────────────┤
│                                                                      │
│    ┌─────────────┐                                                   │
│    │   发起请求   │                                                   │
│    └──────┬──────┘                                                   │
│           ▼                                                           │
│    ┌─────────────────┐     有效      ┌──────────────────┐             │
│    │  检查 Token     ├─────────────►│   正常请求/响应   │             │
│    └────────┬────────┘              └──────────────────┘             │
│             │                                                        │
│             │ 无效/过期 (401)                                         │
│             ▼                                                        │
│    ┌─────────────────┐     成功      ┌──────────────────┐             │
│    │  调用刷新接口   ├─────────────►│  更新本地 Token   │             │
│    └────────┬────────┘              └────────┬─────────┘             │
│             │                                │                        │
│             │ 失败                            │ 重试                   │
│             ▼                                ▼                        │
│    ┌─────────────────┐              ┌──────────────────┐             │
│    │  清除本地 Token  │              │   重试原请求      │             │
│    └────────┬────────┘              └──────────────────┘             │
│             │                                                        │
│             ▼                                                        │
│    ┌─────────────────┐                                               │
│    │   跳转登录页     │                                               │
│    └─────────────────┘                                               │
│                                                                      │
└──────────────────────────────────────────────────────────────────────┘
```

#### F05：退出登录

**API 路径：** `POST /api/patients/logout`

**前端操作：**

1. 调用 logout API（可选，后端删除 refresh token）
2. 清除 localStorage 中的 Token
3. 跳转登录页面

---

## 4. 页面原型

### 4.1 患者登录页 `/patient/login`

```
┌─────────────────────────────────────────────────────────┐
│  ┌─────────────────────────────────────────────────┐   │
│  │                                                 │   │
│  │              🔐 患者登录                         │   │
│  │                                                 │   │
│  │  ┌─────────────────────────────────────────┐   │   │
│  │  │  📱 手机号                              │   │   │
│  │  └─────────────────────────────────────────┘   │   │
│  │                                                 │   │
│  │  ┌─────────────────────────────────────────┐   │   │
│  │  │  🔒 密码                                │   │   │
│  │  └─────────────────────────────────────────┘   │   │
│  │                                                 │   │
│  │  ┌─────────────────────────────────────────┐   │   │
│  │  │           登  录                        │   │   │
│  │  └─────────────────────────────────────────┘   │   │
│  │                                                 │   │
│  │  ┌─────────────────────────────────────────┐   │   │
│  │  │           注册账号                        │   │   │
│  │  └─────────────────────────────────────────┘   │   │
│  │                                                 │   │
│  └─────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
```

### 4.2 患者注册页 `/patient/register`

```
┌─────────────────────────────────────────────────────────┐
│  ┌─────────────────────────────────────────────────┐   │
│  │                                                 │   │
│  │              📝 患者注册                         │   │
│  │                                                 │   │
│  │  ┌──────────────────────────────────┐ ┌─────┐ │   │
│  │  │  📱 手机号                       │ │获取验证码│ │   │
│  │  └──────────────────────────────────┘ └─────┘ │   │
│  │                                                 │   │
│  │  ┌─────────────────────────────────────────┐   │   │
│  │  │  🔢 验证码 (6位)                        │   │   │
│  │  └─────────────────────────────────────────┘   │   │
│  │                                                 │   │
│  │  ┌─────────────────────────────────────────┐   │   │
│  │  │  🔒 设置密码 (8-20位，字母+数字)         │   │   │
│  │  └─────────────────────────────────────────┘   │   │
│  │                                                 │   │
│  │  ┌─────────────────────────────────────────┐   │   │
│  │  │  🔒 确认密码                            │   │   │
│  │  └─────────────────────────────────────────┘   │   │
│  │                                                 │   │
│  │  ┌─────────────────────────────────────────┐   │   │
│  │  │  👤 姓名                                │   │   │
│  │  └─────────────────────────────────────────┘   │   │
│  │                                                 │   │
│  │  ┌─────────────────────────────────────────┐   │   │
│  │  │  ⚧ 性别 ▼                              │   │   │
│  │  └─────────────────────────────────────────┘   │   │
│  │                                                 │   │
│  │  ┌─────────────────────────────────────────┐   │   │
│  │  │  🎂 生日 ▼                              │   │   │
│  │  └─────────────────────────────────────────┘   │   │
│  │                                                 │   │
│  │  ┌─────────────────────────────────────────┐   │   │
│  │  │           注  册                        │   │   │
│  │  └─────────────────────────────────────────┘   │   │
│  │                                                 │   │
│  │            已有账户？登录                       │   │
│  │                                                 │   │
│  └─────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
```

---

## 5. 接口设计

### 5.1 接口列表

| 接口 | 方法 | 路径 | 描述 | 认证 |
|------|------|------|------|------|
| 发送验证码 | POST | `/api/patients/sms/send` | 发送注册验证码 | 否 |
| 患者注册 | POST | `/api/patients/register` | 创建患者账户 | 否 |
| 患者登录 | POST | `/api/patients/login` | 用户名密码登录 | 否 |
| 刷新Token | POST | `/api/patients/token/refresh` | 刷新访问令牌 | 否 |
| 登出 | POST | `/api/patients/logout` | 退出登录 | 是 |
| 获取患者信息 | GET | `/api/patients/profile` | 获取当前用户信息 | 是 |
| 更新患者信息 | PUT | `/api/patients/profile` | 更新个人信息 | 是 |
| 检查手机号 | GET | `/api/patients/check-phone` | 检查手机号是否已注册 | 否 |

### 5.2 接口详情

#### 5.2.1 发送验证码

**请求：**

```http
POST /api/patients/sms/send
Content-Type: application/json

{
  "phone": "13800138000",
  "type": "register"
}
```

**响应：**

```json
{
  "code": 0,
  "message": "验证码已发送",
  "data": {
    "expiresIn": 300
  }
}
```

**错误码：**

| 错误码 | 说明 |
|--------|------|
| 40001 | 手机号格式错误 |
| 42901 | 发送过于频繁，请60秒后重试 |
| 40901 | 该手机号已注册 |

#### 5.2.2 患者注册

**请求：**

```http
POST /api/patients/register
Content-Type: application/json

{
  "phone": "13800138000",
  "code": "123456",
  "password": "Test1234",
  "name": "张三",
  "gender": 1,
  "birthday": "1990-01-01"
}
```

**响应：**

```json
{
  "code": 0,
  "message": "注册成功",
  "data": {
    "patient": {
      "id": "p550e8400-e29b-41d4-a716-446655440000",
      "phone": "13800138000",
      "name": "张三",
      "gender": 1,
      "birthday": "1990-01-01",
      "createdAt": "2026-04-03T10:00:00Z"
    },
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 7200
  }
}
```

**错误码：**

| 错误码 | 说明 |
|--------|------|
| 40002 | 密码格式不符合要求 |
| 40003 | 验证码错误 |
| 40004 | 验证码已过期 |
| 40005 | 验证码已失效，请重新获取 |

#### 5.2.3 患者登录

**请求：**

```http
POST /api/patients/login
Content-Type: application/json

{
  "phone": "13800138000",
  "password": "Test1234"
}
```

**响应：**

```json
{
  "code": 0,
  "message": "登录成功",
  "data": {
    "patient": {
      "id": "p550e8400-e29b-41d4-a716-446655440000",
      "phone": "138****8000",
      "name": "张三",
      "createdAt": "2026-04-03T10:00:00Z"
    },
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 7200
  }
}
```

**错误码：**

| 错误码 | 说明 |
|--------|------|
| 40101 | 手机号或密码错误 |
| 40102 | 账户已锁定，请15分钟后重试 |
| 40401 | 该手机号未注册 |

#### 5.2.4 刷新Token

**请求：**

```http
POST /api/patients/token/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**响应：**

```json
{
  "code": 0,
  "message": "刷新成功",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 7200
  }
}
```

**错误码：**

| 错误码 | 说明 |
|--------|------|
| 40103 | Refresh Token 无效或已过期 |

#### 5.2.5 获取患者信息

**请求：**

```http
GET /api/patients/profile
Authorization: Bearer <accessToken>
```

**响应：**

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": "p550e8400-e29b-41d4-a716-446655440000",
    "phone": "138****8000",
    "name": "张三",
    "birthday": "1990-01-01",
    "gender": 0,
    "avatar": null,
    "questionCount": 5,
    "createdAt": "2026-04-03T10:00:00Z"
  }
}
```

### 5.3 全局错误响应格式

```json
{
  "code": 40101,
  "message": "手机号或密码错误",
  "timestamp": "2026-04-03T10:00:00Z",
  "path": "/api/patients/login"
}
```

**错误码规范：**

| 区间 | 说明 |
|------|------|
| 0 | 成功 |
| 40001-40099 | 参数校验错误 |
| 40101-40199 | 认证授权错误 |
| 40401-40499 | 资源不存在 |
| 40901-40999 | 资源冲突 |
| 42901-42999 | 频率限制 |
| 50001-50099 | 服务器内部错误 |

---

## 6. 数据模型

### 6.1 数据库表设计

#### 6.1.1 patients 表（患者表）

```sql
CREATE TABLE IF NOT EXISTS patients (
    id VARCHAR(36) PRIMARY KEY,
    phone VARCHAR(11) NOT NULL UNIQUE COMMENT '手机号',
    password_hash VARCHAR(255) NOT NULL COMMENT '密码哈希(BCrypt)',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    gender TINYINT DEFAULT 0 COMMENT '性别: 0-未知, 1-男, 2-女',
    birthday DATE COMMENT '生日',
    avatar VARCHAR(500) COMMENT '头像URL',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    failed_attempts INT DEFAULT 0 COMMENT '连续登录失败次数',
    locked_until DATETIME COMMENT '账户锁定截止时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_phone (phone),
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='患者表';
```

#### 6.1.2 patient_sms_codes 表（短信验证码表）

```sql
CREATE TABLE IF NOT EXISTS patient_sms_codes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    phone VARCHAR(11) NOT NULL COMMENT '手机号',
    code VARCHAR(6) NOT NULL COMMENT '验证码',
    type VARCHAR(20) NOT NULL COMMENT '验证码类型: register',
    used TINYINT DEFAULT 0 COMMENT '是否已使用',
    error_count INT DEFAULT 0 COMMENT '错误次数',
    expire_at DATETIME NOT NULL COMMENT '过期时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_phone_type (phone, type),
    INDEX idx_expire_at (expire_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='患者短信验证码表';
```

#### 6.1.3 patient_refresh_tokens 表（刷新令牌表）

```sql
CREATE TABLE IF NOT EXISTS patient_refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id VARCHAR(36) NOT NULL COMMENT '患者ID',
    token VARCHAR(255) NOT NULL UNIQUE COMMENT '令牌',
    device_info VARCHAR(255) COMMENT '设备信息',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    expired_at DATETIME NOT NULL COMMENT '过期时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_token (token),
    INDEX idx_patient_id (patient_id),
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='患者刷新令牌表';
```

### 6.2 实体类设计

#### Patient 实体

```java
@Entity
@Table(name = "patients")
public class Patient {
    @Id
    private String id;

    @Column(nullable = false, unique = true, length = 11)
    private String phone;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 50)
    private String name;

    private Integer gender;  // 0-未知, 1-男, 2-女

    private LocalDate birthday;

    private String avatar;

    @Column(nullable = false)
    private Integer status = 1;

    @Column(name = "failed_attempts")
    private Integer failedAttempts = 0;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
```

### 6.3 数据初始化

**初始化测试患者数据（用于开发调试）：**

```sql
-- 插入测试患者（密码均为 Test1234，BCrypt加密后）
INSERT INTO patients (id, phone, password_hash, name, birthday, status) VALUES
('p001', '13800138000', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '张三', '1990-01-01', 1),
('p002', '13900139000', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '李四', '1985-05-15', 1);
```

---

## 7. 前端设计

### 7.1 路由配置

```typescript
// router/index.ts 新增路由
{
  path: '/patient',
  children: [
    {
      path: 'login',
      name: 'PatientLogin',
      component: () => import('../views/PatientLogin.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: 'register',
      name: 'PatientRegister',
      component: () => import('../views/PatientRegister.vue'),
      meta: { requiresAuth: false }
    }
  ]
}
```

### 7.2 API 服务层

```typescript
// api/patient.ts
import axios from 'axios';
import { message } from 'ant-design-vue';

const API_BASE = '/api/patients';

export const patientApi = {
  // 发送验证码
  sendSmsCode(phone: string) {
    return axios.post(`${API_BASE}/sms/send`, { phone, type: 'register' });
  },

  // 注册
  register(data: { phone: string; code: string; password: string; name: string; birthday?: string }) {
    return axios.post(`${API_BASE}/register`, data);
  },

  // 登录
  login(phone: string, password: string) {
    return axios.post(`${API_BASE}/login`, { phone, password });
  },

  // 刷新Token
  refreshToken(refreshToken: string) {
    return axios.post(`${API_BASE}/token/refresh`, { refreshToken });
  },

  // 登出
  logout() {
    return axios.post(`${API_BASE}/logout`);
  },

  // 获取个人信息
  getProfile() {
    return axios.get(`${API_BASE}/profile`);
  }
};
```

### 7.3 Token 管理

```typescript
// utils/auth.ts
const ACCESS_TOKEN_KEY = 'patient_access_token';
const REFRESH_TOKEN_KEY = 'patient_refresh_token';

export const auth = {
  setTokens(accessToken: string, refreshToken: string) {
    localStorage.setItem(ACCESS_TOKEN_KEY, accessToken);
    localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken);
  },

  getAccessToken() {
    return localStorage.getItem(ACCESS_TOKEN_KEY);
  },

  getRefreshToken() {
    return localStorage.getItem(REFRESH_TOKEN_KEY);
  },

  clearTokens() {
    localStorage.removeItem(ACCESS_TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
  },

  isLoggedIn() {
    return !!this.getAccessToken();
  }
};
```

### 7.4 Axios 拦截器

```typescript
// utils/request.ts
import axios from 'axios';
import { auth } from './auth';
import { patientApi } from '../api/patient';

const service = axios.create({
  baseURL: '/api',
  timeout: 10000
});

// 请求拦截器：添加 Token
service.interceptors.request.use(
  (config) => {
    const token = auth.getAccessToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// 响应拦截器：处理 Token 过期
service.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      const refreshToken = auth.getRefreshToken();
      if (refreshToken) {
        try {
          const res = await patientApi.refreshToken(refreshToken);
          auth.setTokens(res.data.accessToken, res.data.refreshToken);
          // 重试原请求
          error.config.headers.Authorization = `Bearer ${res.data.accessToken}`;
          return service(error.config);
        } catch {
          auth.clearTokens();
          window.location.href = '/patient/login';
        }
      }
    }
    return Promise.reject(error);
  }
);
```

### 7.5 页面组件说明

| 组件 | 路径 | 说明 |
|------|------|------|
| PatientLogin.vue | `/patient/login` | 患者登录页 |
| PatientRegister.vue | `/patient/register` | 患者注册页 |

---

## 8. 安全需求

### 8.1 密码安全

| 需求 | 实现方式 |
|------|----------|
| 加密存储 | BCrypt (强度因子 10) |
| 传输安全 | HTTPS (生产环境) |
| 强度要求 | 8-20位，包含字母和数字 |
| 密码传输 | 不在 URL 中传递 |

### 8.2 登录安全

| 防护措施 | 阈值 | 处罚 |
|----------|------|------|
| 登录失败限制 | 5次 | 锁定15分钟 |
| 验证码错误限制 | 3次 | 验证码失效 |
| 同一手机发送间隔 | 60秒 | 拒绝发送 |

### 8.3 Token 安全

| 措施 | 说明 |
|------|------|
| Access Token | 有效期2小时，不可跨设备 |
| Refresh Token | 有效期7天，单设备使用 |
| 退出登录 | 删除 Refresh Token |
| Token 携带 | Authorization: Bearer {token} |

---

## 10. 非功能需求

### 10.1 性能需求

| 指标 | 要求 |
|------|------|
| 登录接口响应时间 | < 500ms |
| 注册接口响应时间 | < 1s |
| Token 刷新响应时间 | < 200ms |
| 并发登录支持 | 1000 QPS |

### 10.2 可用性需求

| 指标 | 要求 |
|------|------|
| 系统可用性 | 99.5% |
| 错误率 | < 0.1% |
| 页面加载时间 | < 3s |

### 10.3 国际化需求

| 页面 | 中文 | 英文 |
|------|------|------|
| 登录页 | ✓ | ✓ |
| 注册页 | ✓ | ✓ |
| 错误提示 | ✓ | ✓ |

---

## 11. 风险与依赖

### 11.1 风险项

| 风险 | 概率 | 影响 | 应对措施 |
|------|------|------|----------|
| 短信服务延迟 | 中 | 中 | 使用异步队列，预留重试机制 |
| Token 安全漏洞 | 低 | 高 | 使用 HTTPS，定期安全审计 |
| 并发登录问题 | 低 | 中 | Redis 存储 Token 状态 |

### 11.2 依赖项

| 依赖项 | 版本 | 说明 | 状态 |
|--------|------|------|------|
| MySQL | 8.0 | 数据库 | 已部署 |
| Spring Boot | 3.x | 后端框架 | 已有 |
| JWT | 0.11.x | Token 生成 | 需引入 |
| BCrypt | - | 密码加密 | Spring Security 已包含 |
| Redis | 7.x | Token 存储（可选） | 暂不使用 |

### 11.3 技术选型

| 组件 | 选型 | 替代方案 |
|------|------|----------|
| 短信服务 | 模拟（开发）→ 阿里云（生产） | 腾讯云、华为云 |
| Token 存储 | JWT (无状态) | Redis (有状态) |
| 密码加密 | BCrypt | Argon2 |
| 前端状态 | Pinia | Vuex |

---

## 12. 里程碑与排期

### 12.1 开发阶段

| 阶段 | 任务 | 工时 | 交付物 |
|------|------|------|--------|
| M1 | 需求评审与技术方案设计 | 1天 | 设计文档评审通过 |
| M2 | 数据库设计与实现 | 0.5天 | DDL 脚本、实体类 |
| M3 | 后端 API 开发 | 1.5天 | 患者注册/登录/Token API |
| M4 | 前端页面开发 | 1.5天 | 登录/注册页面 |
| M5 | 问诊页集成 | 0.5天 | 与问诊页面集成 |
| M6 | 前后端联调 | 0.5天 | 端到端测试通过 |
| M7 | 系统测试与修复 | 1天 | 测试报告 |
| M8 | 文档与上线 | 0.5天 | 上线文档 |
| **总计** | | **6.5天** | |

### 12.2 里程碑节点

| 节点 | 日期 | 说明 |
|------|------|------|
| M1 完成 | D+1 | 设计文档冻结 |
| M3 完成 | D+2.5 | API 内部测试通过 |
| M6 完成 | D+4 | 前后端联调通过 |
| 上线 | D+6.5 | 生产环境部署 |

---

## 13. 验收标准

### 13.1 功能验收

- [ ] **F01** 患者可通过手机号+验证码完成注册
- [ ] **F01** 验证码有效期为5分钟
- [ ] **F01** 验证码错误3次后失效
- [ ] **F02** 密码要求8-20位字母+数字
- [ ] **F02** 注册成功后自动登录
- [ ] **F03** 患者可通过手机号+密码登录
- [ ] **F03** 登录失败5次锁定15分钟
- [ ] **F03** 错误提示不区分具体字段
- [ ] **F04** Token 存储在 localStorage
- [ ] **F04** 页面刷新保持登录状态
- [ ] **F04** Token 过期前自动刷新
- [ ] **F04** Token 刷新失败跳转登录页
- [ ] **F05** 退出登录清除 Token
- [ ] **F05** 退出后无法使用旧 Token

### 13.2 非功能验收

- [ ] 所有 API 响应时间符合要求
- [ ] 登录/注册页面支持中英文
- [ ] 密码不明文存储
- [ ] 系统支持 1000 并发

### 13.3 测试要求

| 测试类型 | 覆盖率要求 |
|----------|-----------|
| 单元测试 | > 80% |
| API 测试 | 全覆盖 |
| E2E 测试 | 核心流程 |

---

## 14. 附录

### 14.1 术语表

| 术语 | 英文 | 定义 |
|------|------|------|
| 问诊患者 | Patient | 使用问诊服务的用户 |
| 短信验证码 | SMS Code | 发送到手机的数字验证码 |
| 访问令牌 | Access Token | 用于 API 鉴权的短期令牌 |
| 刷新令牌 | Refresh Token | 用于获取新访问令牌的长期令牌 |

### 14.2 错误码速查表

| 错误码 | 含义 | 用户提示 |
|--------|------|----------|
| 0 | 成功 | - |
| 40001 | 手机号格式错误 | 请输入正确的手机号 |
| 40002 | 密码格式错误 | 密码需8-20位，包含字母和数字 |
| 40003 | 验证码错误 | 验证码错误 |
| 40004 | 验证码过期 | 验证码已过期，请重新获取 |
| 40005 | 验证码失效 | 验证码已失效，请重新获取 |
| 40101 | 登录失败 | 手机号或密码错误 |
| 40102 | 账户锁定 | 账户已锁定，请15分钟后重试 |
| 40103 | Token无效 | 登录已过期，请重新登录 |
| 40401 | 用户不存在 | 该手机号未注册 |
| 40901 | 手机号已注册 | 该手机号已注册 |
| 42901 | 发送频繁 | 发送过于频繁，请60秒后重试 |

### 14.3 参考文档

| 文档 | 路径 |
|------|------|
| 项目 README | `/README.md` |
| API 文档 | `/docs/api.md` |
| PRD 模板 | `/docs/prd-patient-login.md` |
| JWT 官方文档 | https://jwt.io/ |

### 14.4 相关项目

| 项目 | 路径 | 说明 |
|------|------|------|
| 前端项目 | `/web/qa-web` | Vue 3 前端 |
| 用户服务 | `/server/qa-service-user` | Spring Boot 后端 |
| 问诊服务 | `/server/qa-service-question` | 问诊相关服务 |
| 数据库配置 | `/docker-compose.yml` | MySQL + phpMyAdmin |
| 数据初始化 | `/init.sql` | 初始数据 |

---

## 15. 修订记录

| 版本 | 日期 | 修改内容 | 修改人 |
|------|------|----------|--------|
| 1.0 | 2026-04-03 | 初始版本 | - |

---

*文档编写：*
*审核：*
*批准：*
