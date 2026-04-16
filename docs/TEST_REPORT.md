# 接口测试报告

| 项目 | 信息 |
|------|------|
| 项目名称 | QA Live Healthcare Interview |
| 测试日期 | 2026-04-16 |
| 测试环境 | Windows / JDK 17 / MySQL 8.x / Spring Boot |
| 服务地址 | http://localhost:8080 |
| 测试依据 | docs/API_TEST.md |
| 测试工具 | curl.exe |

---

## 1. 测试概述

本次测试针对 `qa-service-user` 服务的全部 8 个 API 接口进行功能验证，涵盖正常场景、异常场景和边界场景，共计 25 个测试用例。

**测试范围：**

| 模块 | 接口数量 | 用例数量 |
|------|---------|---------|
| 登录认证（Auth） | 4 | 14 |
| 医生管理（Doctor） | 4 | 11 |
| **合计** | **8** | **25** |

---

## 2. 测试用例执行结果

### 2.1 登录认证模块

| 用例编号 | 用例名称 | 接口 | 预期 | 实际 | 结果 |
|---------|---------|------|------|------|------|
| 3.1.1 | 正常登录 | POST /api/auth/login | code=200, 返回token | code=200, token=eyJhbG...yQF, userInfo正常 | ✅ PASS |
| 3.1.2 | 密码错误 | POST /api/auth/login | code=401, AUTH_101 | code=401, remainingAttempts=4, errorCode=AUTH_101 | ✅ PASS |
| 3.1.3 | 用户名不存在 | POST /api/auth/login | code=401, AUTH_101 | code=401, remainingAttempts=5, errorCode=AUTH_101 | ✅ PASS |
| 3.1.4 | 账号已禁用 | POST /api/auth/login | code=401, AUTH_103 | code=401, remainingAttempts=0, errorCode=AUTH_103 | ✅ PASS |
| 3.1.5 | 用户名格式错误 | POST /api/auth/login | code=400, 校验失败 | code=400, username校验提示 | ✅ PASS |
| 3.1.6 | 密码过短 | POST /api/auth/login | code=400, 校验失败 | code=400, password校验提示 | ✅ PASS |
| 3.1.7 | 字段为空 | POST /api/auth/login | code=400, 校验失败 | code=400, 两个字段均有校验提示 | ✅ PASS |
| 3.2.1 | 正常退出 | POST /api/auth/logout | code=200 | code=200, "退出成功" | ✅ PASS |
| 3.2.2 | 无Token退出 | POST /api/auth/logout | code=200 | code=200, "退出成功" | ✅ PASS |
| 3.3.1 | 正常刷新Token | POST /api/auth/refresh | code=200, 新token | code=200, 新token生成, userInfo正常 | ✅ PASS |
| 3.3.2 | 无效Token刷新 | POST /api/auth/refresh | code=401 | code=401, "Token无效或已过期" | ✅ PASS |
| 3.3.3 | 缺少Authorization | POST /api/auth/refresh | code=401 | code=401, "Token格式错误" | ✅ PASS |
| 3.4.1 | 正常获取用户信息 | GET /api/auth/userinfo | code=200, 用户信息 | code=200, 完整用户信息返回 | ⚠️ PASS（含问题） |
| 3.4.2 | 无效Token | GET /api/auth/userinfo | code=401 | code=401, "Token无效或已过期" | ✅ PASS |
| 3.4.3 | 缺少Authorization | GET /api/auth/userinfo | code=401 | code=401, "请先登录" | ✅ PASS |

### 2.2 医生管理模块

| 用例编号 | 用例名称 | 接口 | 预期 | 实际 | 结果 |
|---------|---------|------|------|------|------|
| 3.5.1 | 首页激活医生列表 | GET /api/doctor/home/list | 4条记录(仅active) | 4条记录, doc004被排除 | ⚠️ PASS（含问题） |
| 3.6.1 | 默认分页查询 | GET /api/doctor/list | 5条,total=5 | 5条记录, total=5, page=1 | ⚠️ PASS（含问题） |
| 3.6.2 | 分页第1页/2条 | GET /api/doctor/list?page=1&pageSize=2 | 2条,totalPages=3 | 2条, totalPages=3 | ⚠️ PASS（含问题） |
| 3.6.3 | 分页第2页/2条 | GET /api/doctor/list?page=2&pageSize=2 | 2条 | 2条(doc003,doc004) | ⚠️ PASS（含问题） |
| 3.6.4 | 分页第3页/2条 | GET /api/doctor/list?page=3&pageSize=2 | 1条 | 1条(doc005) | ⚠️ PASS（含问题） |
| 3.6.5 | 超大页码 | GET /api/doctor/list?page=999 | 空列表 | list=[], total=5 | ✅ PASS |
| 3.7.1 | 查询存在医生 | GET /api/doctor/detail/doc001 | code=200 | code=200, 完整医生信息 | ⚠️ PASS（含问题） |
| 3.7.2 | 查询非激活医生 | GET /api/doctor/detail/doc004 | code=200, isActive=false | code=200, isActive=false | ⚠️ PASS（含问题） |
| 3.7.3 | 医生不存在 | GET /api/doctor/detail/notexist | code=404 | code=404, "医生不存在" | ✅ PASS |
| 3.8.1 | 按科室-心内科 | GET /api/doctor/department/心内科 | 1条 | 1条(doc001) | ⚠️ PASS（含问题） |
| 3.8.2 | 按科室-儿科 | GET /api/doctor/department/儿科 | 1条 | 1条(doc002) | ⚠️ PASS（含问题） |
| 3.8.3 | 按科室-妇产科 | GET /api/doctor/department/妇产科 | 1条(含非激活) | 1条(doc004, isActive=false) | ⚠️ PASS（含问题） |
| 3.8.4 | 不存在的科室 | GET /api/doctor/department/神经外科 | 空数组 | data=[] | ✅ PASS |

---

## 3. 结果统计

| 指标 | 数量 | 占比 |
|------|------|------|
| 总用例数 | 25 | 100% |
| ✅ 完全通过 | 12 | 48% |
| ⚠️ 通过（含问题） | 13 | 52% |
| ❌ 失败 | 0 | 0% |

> 所有接口功能逻辑正确，均可正常返回预期结果。⚠️ 标记的用例存在数据安全问题（详见第4节）。

---

## 4. 成功/失败说明

### 4.1 功能正确性

所有 25 个测试用例的**业务逻辑均验证通过**：

- **登录接口**：正常登录返回 token，密码错误/用户不存在/账号禁用均返回正确错误码和提示
- **参数校验**：用户名格式、密码长度、空值校验均生效
- **Token机制**：刷新Token返回新token，无效/缺失Token正确拦截
- **医生列表**：首页仅返回激活医生，分页计算正确，科室查询正常
- **医生详情**：存在/不存在/非激活状态的医生均正确返回
- **边界场景**：超大页码返回空列表，不存在的科室/医生ID返回对应错误

### 4.2 实际返回与预期的差异

| 差异项 | 预期 | 实际 | 说明 |
|--------|------|------|------|
| expiresIn | 86400000 (毫秒) | 86400 (秒) | JWT过期时间单位不一致，实际返回的是秒数而非毫秒 |
| userInfo.id | 1 | 6 | 数据库自增ID，与初始化SQL执行次数有关，非bug |

---

## 5. 问题与建议

### 🔴 严重问题（P0 - 必须修复）

#### 5.1 医生接口返回 password 字段（敏感信息泄露）

**涉及接口：** 所有医生接口（/api/doctor/home/list、/api/doctor/list、/api/doctor/detail/{id}、/api/doctor/department/{department}）

**问题描述：** 医生列表和详情接口的返回数据中包含 `password` 字段，且为明文存储（如 `"password": "123456"`），这是严重的安全隐患。

**实际返回示例：**
```json
{
  "id": "doc001",
  "username": "dr-zhang-wei",
  "password": "123456",
  "name": "张伟医生",
  ...
}
```

**修复建议：**
1. 在 `DoctorMapper.xml` 的所有 SELECT 语句中移除 `password` 列
2. 或在 `Doctor` 实体类中用 `@JsonIgnore` 注解标记 password 字段
3. 同时建议将 doctor 表的密码改为 BCrypt 加密存储

#### 5.2 用户信息接口返回 password 字段（敏感信息泄露）

**涉及接口：** GET /api/auth/userinfo

**问题描述：** 获取当前用户信息接口返回了 BCrypt 加密后的 password 字段。

**实际返回示例：**
```json
{
  "id": 6,
  "username": "patient001",
  "password": "$2a$10$qUnIQkrGaxjYBc8Z7VaYi...",
  "name": "张三",
  ...
}
```

**修复建议：** 在 `OutpatientUser` 实体类的 password 字段上添加 `@JsonIgnore` 注解，或创建专用的 UserInfoVO 不包含 password。

### 🟡 一般问题（P1 - 建议修复）

#### 5.3 JWT expiresIn 单位不一致

**问题描述：** 登录和刷新Token接口返回的 `expiresIn` 值为 `86400`（秒），但 `application.properties` 中配置的 `jwt.expiration=86400000`（毫秒）。LoginResponse 中直接使用了 `jwtUtils.getExpiration()` 返回值，但该值未经转换。

**修复建议：** 统一 expiresIn 的语义，要么返回毫秒（86400000），要么在 JwtUtils 中提供秒级方法返回 86400，并在文档/API注释中明确单位。

#### 5.4 医生表密码明文存储

**问题描述：** `doctor` 表的 password 字段为明文存储（SQL 初始数据为 `123456`），与门诊用户表的 BCrypt 加密方式不一致。

**修复建议：** 如医生表也需要密码认证，应改用 BCrypt 加密存储；如仅用于展示不需要登录，可移除 password 字段。

#### 5.5 分页查询性能问题

**问题描述：** `/api/doctor/list` 分页接口中，`total` 的计算方式为 `doctorService.getDoctorList().size()`，即先查询全量数据再取 size，在数据量大时存在性能问题。

**修复建议：** 新增 `selectDoctorCount` SQL 使用 `SELECT COUNT(*)` 获取总数，避免全量查询。

### 🟢 优化建议（P2 - 可选优化）

#### 5.6 安全配置过于宽松

**问题描述：** `SecurityConfig` 中 `.anyRequest().permitAll()` 使得所有接口无需认证即可访问，包括理论上需要认证的接口（如 /api/auth/userinfo、/api/auth/refresh）。

**修复建议：** 在 JWT 过滤器实现完成后，将医生接口等改为需要认证，仅保留 `/api/auth/login` 为 permitAll。

#### 5.7 退出登录接口未实际失效Token

**问题描述：** `/api/auth/logout` 接口当前为空实现，JWT 是无状态的，退出后 Token 仍可使用。

**修复建议：** 可引入 Token 黑名单机制（如 Redis），在 logout 时将 Token 加入黑名单，请求时校验黑名单。

#### 5.8 账号锁定机制缺少自动解锁

**问题描述：** 账号锁定依赖 `lock_until` 时间字段，但 `AuthServiceImpl` 中检查锁定状态时使用了 `user.getLockUntil().isAfter(LocalDateTime.now())`，如果时间已过则允许登录，但没有重置 `error_count` 和 `lock_until` 字段。

**修复建议：** 在锁定时间过期后首次登录时，先重置 error_count 和 lock_until。

---

## 6. 测试结论

本次测试的 8 个接口、25 个测试用例**功能逻辑全部验证通过**，接口返回结构和业务处理符合预期设计。

但发现 **2 个 P0 级安全问题**（password 字段泄露）需要**立即修复**，3 个 P1 级问题和 3 个 P2 级优化建议建议在后续迭代中处理。

| 优先级 | 数量 | 状态 |
|--------|------|------|
| P0 严重 | 2 | 待修复 |
| P1 一般 | 3 | 建议修复 |
| P2 优化 | 3 | 可选优化 |
