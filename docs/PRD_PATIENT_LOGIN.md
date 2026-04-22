# 产品需求文档 (PRD) - 问诊用户登录功能

---

## 文档信息

| 字段 | 内容 |
|------|------|
| 文档标题 | 问诊用户登录功能 |
| 版本号 | V1.0.0 |
| 状态 | 草稿 |
| 创建日期 | 2026-04-22 |
| 最后更新 | 2026-04-22 |
| 产品负责人 | [待填写] |
| 研发负责人 | [待填写] |
| 测试负责人 | [待填写] |

---

## 1. 文档概述

### 1.1 背景与目的

目前问诊用户（患者）使用系统时，需要通过姓名和出生日期进行身份验证。为了提升用户体验和安全性，需要增加用户名密码登录功能，允许患者使用预设的账号密码登录系统。

**目标：**
- 提供更安全的身份验证方式（用户名+密码 vs 姓名+出生日期）
- 简化用户登录流程，提升操作便捷性
- 为后续用户中心、问诊记录等功能提供基础

### 1.2 适用范围

本文档适用于问诊平台患者登录模块的设计、开发与测试。

### 1.3 术语定义

| 术语 | 定义 |
|------|------|
| 问诊用户/患者 | 需要咨询医疗问题的平台使用者 |
| 登录凭证 | 用户名和密码的组合，用于验证用户身份 |
| 会话 | 用户登录后与服务端建立的临时连接状态 |

---

## 2. 用户故事与需求概述

### 2.1 用户角色

| 角色 | 描述 | 权限范围 |
|------|------|----------|
| 问诊用户（患者） | 需要咨询医疗问题的用户 | 登录、发起问诊、查看历史问诊记录 |

### 2.2 用户故事 (User Story)

| # | 用户故事 | 优先级 | 状态 |
|---|---------|--------|------|
| US-PL-01 | 作为患者，我希望使用用户名和密码登录，以便更安全便捷地使用平台 | P0 | 已完成 |
| US-PL-02 | 作为患者，我希望在登录失败时看到明确的错误提示，以便了解登录失败原因 | P1 | 已完成 |
| US-PL-03 | 作为患者，我希望登录状态能够保持一段时间，避免频繁重新登录 | P2 | 待开发 |
| US-PL-04 | 作为患者，我希望我的密码使用更安全的加密方式存储，以防止密码泄露导致账户被盗 | P1 | 待开发 |

---

## 3. 功能需求

### 3.1 功能列表

| 功能编号 | 功能名称 | 功能描述 | 优先级 | 所属模块 |
|---------|---------|---------|--------|----------|
| FR-PL-001 | 患者登录页面 | 提供用户名密码输入界面 | P0 | 登录模块 |
| FR-PL-002 | 登录验证 | 验证用户名密码正确性 | P0 | 登录模块 |
| FR-PL-003 | 登录状态管理 | 维护用户登录状态 | P1 | 登录模块 |
| FR-PL-004 | 密码安全升级 | 将MD5密码加密升级为BCrypt加密 | P1 | 安全模块 |

### 3.2 详细功能需求

#### FR-PL-001: 患者登录页面

**功能描述：**
提供独立的患者登录页面，包含用户名、密码输入框和登录按钮。

**需求详情：**

| 字段 | 类型 | 必填 | 校验规则 |
|------|------|------|----------|
| 用户名 | String | 是 | 4-20位字母或数字 |
| 密码 | String | 是 | 6-20位字符 |

**界面设计：**

```
┌─────────────────────────────────────┐
│                                     │
│         [Logo/平台名称]               │
│                                     │
│  ┌─────────────────────────────┐    │
│  │        患者登录              │    │
│  │                             │    │
│  │  用户名                     │    │
│  │  �─────────────────────────┐ │    │
│  │  │                         │ │    │
│  │  └─────────────────────────┘ │    │
│  │                             │    │
│  │  密码                       │    │
│  │  ┌─────────────────────────┐ │    │
│  │  │                         │ │    │
│  │  └─────────────────────────┘ │    │
│  │                             │    │
│  │  [       登录       ]       │    │
│  │                             │    │
│  │  [返回首页]    [注册账号]   │    │
│  └─────────────────────────────┘    │
│                                     │
└─────────────────────────────────────┘
```

**交互说明：**
- 用户名输入框获取焦点时显示浅蓝色边框
- 密码输入框默认显示为密文，可切换显示/隐藏
- 登录按钮点击后显示 loading 状态
- 登录成功后跳转到首页并显示用户信息

---

#### FR-PL-002: 登录验证

**功能描述：**
验证用户输入的用户名和密码是否正确，匹配数据库中的患者信息。

**需求详情：**

| 字段 | 类型 | 说明 |
|------|------|------|
| 输入用户名 | String | 用户在登录页输入的用户名 |
| 输入密码 | String | 用户在登录页输入的密码 |
| 验证方式 | POST | 将用户名密码发送至后端验证 |

**业务规则：**
- 用户名或密码为空时，提示"用户名/密码不能为空"
- 用户名不存在时，提示"用户名或密码错误"
- 密码错误时，提示"用户名或密码错误"（不区分具体哪个错误，防止暴力破解）
- 登录成功后生成会话Token

**接口需求：**

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 患者登录 | POST | `/api/patients/login` | 用户名密码验证 |

**请求参数：**

```json
{
  "username": "patient001",
  "password": "123456"
}
```

**响应参数：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "patient": {
      "id": "patient001",
      "username": "patient001",
      "name": "赵明",
      "phone": "138****1234",
      "gender": "男"
    }
  }
}
```

**错误响应：**

```json
{
  "code": 401,
  "message": "用户名或密码错误",
  "data": null
}
```

---

#### FR-PL-003: 登录状态管理

**功能描述：**
维护用户的登录状态，包括会话Token和用户信息。

**需求详情：**

| 字段 | 类型 | 说明 |
|------|------|------|
| Token | String | 服务端返回的会话凭证 |
| 用户信息 | Object | 当前登录患者的基本信息 |

**存储策略：**
- Token 存储在 localStorage
- 用户信息存储在 Vue store 和 localStorage
- 页面刷新后从 localStorage 恢复登录状态

**登出功能：**
- 清除 localStorage 中的 Token 和用户信息
- 重置 Vue store 中的登录状态
- 跳转到首页

---

#### FR-PL-004: 密码安全升级

**功能描述：**
将现有 MD5 密码加密方式升级为 BCrypt 加密，提供更强的密码安全性。

**问题背景：**
- MD5 已被证明是不安全的哈希算法，存在彩虹表攻击风险
- MD5 不支持盐值（salt），相同密码总是产生相同的哈希值
- BCrypt 是专门为密码哈希设计的算法，支持自适应工作因子

**技术方案：**

| 项目 | 当前方案 | 升级方案 |
|------|---------|---------|
| 加密算法 | MD5 | BCrypt |
| 盐值 | 无 | 自动生成随机盐值 |
| 哈希长度 | 32字符 | 60字符 |
| 暴力破解防护 | 无 | 可配置工作因子（默认10） |
| Java库 | MessageDigest | Spring Security BCrypt |

**升级步骤：**
1. 在 pom.xml 中添加 `spring-boot-starter-security` 依赖
2. 在 Service 层使用 `BCryptPasswordEncoder` 进行密码验证
3. 数据库中密码字段长度从 100 扩展到 255
4. 编写数据迁移脚本，将现有 MD5 密码批量转换为 BCrypt

**数据库变更：**

```sql
ALTER TABLE patients MODIFY COLUMN password VARCHAR(255) NOT NULL;
```

**后端实现示例：**

```java
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class PatientService {
    
    @Autowired
    private PatientRepository patientRepository;
    
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    public Optional<Patient> login(String username, String password) {
        Optional<Patient> patient = patientRepository.findByUsername(username);
        
        if (patient.isPresent() && passwordEncoder.matches(password, patient.get().getPassword())) {
            return patient;
        }
        return Optional.empty();
    }
    
    // 密码加密方法（用于注册或密码重置）
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
```

**兼容性处理：**
- 登录时自动检测密码格式（MD5 或 BCrypt）
- 新注册用户自动使用 BCrypt 加密
- 分批次引导用户修改密码

**风险控制：**
- 升级期间保留 MD5 验证作为 fallback
- 提供管理员批量重置密码工具
- 记录密码迁移日志

---

## 4. 非功能需求

### 4.1 性能需求

| 指标 | 要求 |
|------|------|
| 登录页面加载时间 | < 1秒 |
| 登录请求响应时间 | < 800ms |
| Token 验证时间 | < 100ms |

### 4.2 安全性需求

| 要求 | 说明 |
|------|------|
| 密码加密传输 | 使用 HTTPS 加密传输 |
| 密码加密存储 | 数据库中存储 MD5/SHA256 加密后的密码 |
| 防暴力破解 | 登录失败5次后锁定账户15分钟 |
| XSS 防护 | 用户输入进行 HTML 转义 |

### 4.3 兼容性需求

| 类型 | 要求 |
|------|------|
| 浏览器兼容 | Chrome、Firefox、Safari、Edge 最新版本 |
| 移动端适配 | 适配手机浏览器 |

---

## 5. 数据字典

### 5.1 数据库表结构

#### patients 表（需新增字段）

| 字段名 | 数据类型 | 约束 | 说明 |
|--------|----------|------|------|
| id | VARCHAR(50) | PK | 患者唯一ID |
| username | VARCHAR(50) | UNIQUE, NOT NULL | 登录用户名 |
| password | VARCHAR(100) | NOT NULL | 登录密码（加密） |
| name | VARCHAR(100) | NOT NULL | 患者姓名 |
| birthday | DATE | - | 出生日期 |
| phone | VARCHAR(20) | - | 联系电话 |
| gender | VARCHAR(10) | - | 性别 |
| created_at | TIMESTAMP | - | 创建时间 |
| updated_at | TIMESTAMP | - | 更新时间 |

---

## 6. 数据库初始化

### 6.1 初始化 SQL

```sql
-- Create patients table
CREATE TABLE IF NOT EXISTS patients (
    id VARCHAR(50) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    birthday DATE,
    phone VARCHAR(20),
    gender VARCHAR(10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insert sample patient data (password is MD5 of '123456')
INSERT INTO patients (id, username, password, name, birthday, phone, gender) VALUES
('patient001', 'patient001', 'e10adc3949ba59abbe56e057f20f883e', '赵明', '1985-03-15', '138****1234', '男'),
('patient002', 'patient002', 'e10adc3949ba59abbe56e057f20f883e', '孙丽', '1990-07-22', '139****5678', '女'),
('patient003', 'patient003', 'e10adc3949ba59abbe56e057f20f883e', '周杰', '1978-11-08', '137****9012', '男'),
('patient004', 'patient004', 'e10adc3949ba59abbe56e057f20f883e', '吴芳', '1995-05-20', '136****3456', '女'),
('patient005', 'patient005', 'e10adc3949ba59abbe56e057f20f883e', '郑浩', '1988-09-12', '135****7890', '男');
```

---

## 7. API 接口设计

### 7.1 患者登录接口

**基本信息：**

| 项目 | 说明 |
|------|------|
| 接口名称 | 患者登录 |
| 请求方式 | POST |
| 请求路径 | `/api/patients/login` |
| Content-Type | application/json |

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码（明文） |

**成功响应示例：**

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "patient": {
      "id": "patient001",
      "username": "patient001",
      "name": "赵明",
      "phone": "138****1234",
      "gender": "男"
    }
  }
}
```

**失败响应示例：**

```json
{
  "code": 401,
  "message": "用户名或密码错误",
  "data": null
}
```

---

## 8. 前端实现

### 8.1 页面路径

| 页面 | 路径 | 说明 |
|------|------|------|
| 患者登录页 | `/patient/login` | 独立的患者登录页面 |

### 8.2 路由配置

```typescript
// router/index.ts
{
  path: '/patient/login',
  name: 'PatientLogin',
  component: () => import('../views/PatientLogin.vue'),
  meta: { requiresGuest: true }
}
```

### 8.3 Store 状态管理

```typescript
// store/index.ts 扩展
interface Patient {
  id: string;
  username: string;
  name: string;
  phone: string;
  gender: string;
}

interface State {
  currentPatient: Patient | null;
  patientToken: string | null;
}
```

---

## 9. 风险与依赖

### 9.1 风险识别

| 风险编号 | 风险描述 | 影响程度 | 应对措施 |
|---------|---------|----------|----------|
| RISK-PL-01 | 数据库服务不可用导致登录失败 | 高 | 添加本地缓存验证作为fallback |
| RISK-PL-02 | Token 泄露导致账户被盗用 | 高 | 使用 HTTPS + Token 定期过期机制 |
| RISK-PL-03 | 前端登录页跨域问题 | 中 | 后端配置 CORS 允许前端域名 |

### 9.2 依赖项

| 依赖项 | 类型 | 说明 |
|--------|------|------|
| qa-service-user | 后端服务 | 提供患者登录 API |
| MySQL 数据库 | 数据存储 | 存储患者信息 |
| vue-router | 前端路由 | 页面导航和登录拦截 |

---

## 10. 测试计划

### 10.1 测试用例

| 用例编号 | 用例描述 | 前置条件 | 测试步骤 | 预期结果 |
|---------|---------|----------|----------|----------|
| TC-PL-01 | 正确用户名密码登录 | 用户名 patient001，密码 123456 | 输入正确凭证点击登录 | 登录成功，跳转首页 |
| TC-PL-02 | 错误密码登录 | 用户名 patient001，密码错误 | 输入错误密码点击登录 | 提示"用户名或密码错误" |
| TC-PL-03 | 不存在用户登录 | 用户名不存在 | 输入不存在用户名 | 提示"用户名或密码错误" |
| TC-PL-04 | 空用户名登录 | 用户名为空 | 不输入用户名直接点击登录 | 提示"用户名不能为空" |
| TC-PL-05 | 空密码登录 | 密码为空 | 不输入密码直接点击登录 | 提示"密码不能为空" |
| TC-PL-06 | 登录状态持久化 | 已登录状态 | 刷新页面 | 登录状态保持不变 |
| TC-PL-07 | 登出功能 | 已登录状态 | 点击退出登录 | 清除登录状态，跳转首页 |

### 10.2 验收标准

- [ ] 患者可以使用用户名和密码成功登录
- [ ] 登录失败时显示明确的错误提示
- [ ] 登录状态在页面刷新后保持
- [ ] 退出登录后正确清除状态
- [ ] 登录接口返回正确的 Token 和用户信息

---

## 11. 发布计划

### 11.1 版本规划

| 版本 | 计划日期 | 主要内容 | 负责人 |
|------|----------|---------|--------|
| V1.0.0 | YYYY-MM-DD | 完成患者登录基础功能 | [待填写] |

### 11.2 部署环境

| 环境 | 地址 | 说明 |
|------|------|------|
| 开发环境 | localhost:8080 | 本地开发 |
| 数据库 | localhost:3306 | MySQL 服务 |

---

## 12. 附录

### 12.1 测试账号

| 用户名 | 密码 | 姓名 |
|--------|------|------|
| patient001 | 123456 | 赵明 |
| patient002 | 123456 | 孙丽 |
| patient003 | 123456 | 周杰 |
| patient004 | 123456 | 吴芳 |
| patient005 | 123456 | 郑浩 |

### 12.2 参考文档

- [MD5 在线加密工具](https://www.md5online.org/)
- [JWT 官方文档](https://jwt.io/)
- [Spring Security 官方文档](https://spring.io/projects/spring-security)

### 12.3 更新日志

| 日期 | 版本 | 更新内容 | 作者 |
|------|------|---------|------|
| 2026-04-22 | V1.0.0 | 初始版本 | AI Assistant |
| 2026-04-22 | V1.1.0 | 添加用户故事 US-PL-04：密码安全升级（MD5→BCrypt）| AI Assistant |

---

**文档结束**
