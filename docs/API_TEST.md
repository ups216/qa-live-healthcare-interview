# API 接口测试文档

> 本文档描述 qa-service-user 服务的接口测试流程
>
> - **服务地址**: `http://localhost:8080/api`
> - **接口认证**: Bearer Token (JWT)
> - **模拟验证码**: `123456`

---

## 目录

1. [环境准备](#1-环境准备)
2. [接口测试用例](#2-接口测试用例)
3. [业务流程测试](#3-业务流程测试)
4. [错误场景测试](#4-错误场景测试)
5. [测试结果记录](#5-测试结果记录)
6. [附录](#6-附录)

---

## 1. 环境准备

### 1.1 前置条件

- [ ] 服务已启动 (http://localhost:8080)
- [ ] 数据库已初始化

### 1.2 环境验证

```bash
curl -X GET "http://localhost:8080/api/test/cors"
```

**预期响应:**

```json
{
  "message": "CORS configuration is working!",
  "timestamp": 1743432000000,
  "service": "qa-service-user"
}
```

### 1.3 接口总览

| 接口 | 方法 | 路径 | 认证 | 说明 |
|------|------|------|------|------|
| 获取所有医生 | GET | `/api/doctors` | 否 | 支持 locale 参数 |
| 获取活跃医生 | GET | `/api/doctors/active` | 否 | 仅返回 isActive=true |
| 按用户名获取医生 | GET | `/api/doctors/{username}` | 否 | 支持 locale 参数 |
| 按 ID 获取医生 | GET | `/api/doctors/id/{id}` | 否 | 支持 locale 参数 |
| 发送验证码 | POST | `/api/patients/sms/send` | 否 | |
| 患者注册 | POST | `/api/patients/register` | 否 | |
| 患者登录 | POST | `/api/patients/login` | 否 | |
| 刷新 Token | POST | `/api/patients/token/refresh` | 否 | |
| 登出 | POST | `/api/patients/logout` | 是 | |
| 获取患者信息 | GET | `/api/patients/profile` | 是 | |
| 更新患者信息 | PUT | `/api/patients/profile` | 是 | |
| 检查手机号 | GET | `/api/patients/check-phone` | 否 | |

### 1.4 locale 参数说明

| 值 | 说明 |
|-----|------|
| `zh-CN` | 中文（默认） |
| `en-US` | 英文 |

---

## 2. 接口测试用例

### 2.1 医生相关接口

#### TC-DOC-001: 获取所有医生

**curl 命令:**

```bash
# 中文（默认）
curl -X GET "http://localhost:8080/api/doctors"

# 英文
curl -X GET "http://localhost:8080/api/doctors?locale=en-US"
```

**预期结果:** 返回医生数组，每个医生包含 id, username, name, title, department, avatar, experience, specialties, isActive

**检查点:**
- [ ] 返回类型为数组
- [ ] 数据条数 >= 5
- [ ] 英文模式下职称翻译正确（主任医师 → Chief Physician）
- [ ] 英文模式下科室翻译正确（心内科 → Cardiology）

#### TC-DOC-002: 获取活跃医生

**curl 命令:**

```bash
curl -X GET "http://localhost:8080/api/doctors/active?locale=zh-CN"
```

**预期结果:** 仅返回 `isActive: true` 的医生

**检查点:**
- [ ] 所有返回医生的 isActive 字段为 true

#### TC-DOC-003: 按用户名获取医生详情

**curl 命令:**

```bash
curl -X GET "http://localhost:8080/api/doctors/dr-zhang-wei?locale=zh-CN"
curl -X GET "http://localhost:8080/api/doctors/dr-zhang-wei?locale=en-US"
```

**检查点:**
- [ ] 返回单个医生对象（非数组）
- [ ] 英文模式下专长翻译正确

#### TC-DOC-004: 按 ID 获取医生详情

**curl 命令:**

```bash
curl -X GET "http://localhost:8080/api/doctors/id/doc001?locale=zh-CN"
```

**检查点:**
- [ ] 返回单个医生对象

#### TC-DOC-005: 获取不存在的医生

**curl 命令:**

```bash
curl -X GET "http://localhost:8080/api/doctors/dr-not-exist"
curl -X GET "http://localhost:8080/api/doctors/id/not-exist-id"
```

**预期结果:** HTTP 404，响应体为空

**检查点:**
- [ ] 返回 HTTP 404 状态码

---

### 2.2 用户注册与登录接口

#### TC-AUTH-001: 发送验证码

**curl 命令:**

```bash
curl -X POST "http://localhost:8080/api/patients/sms/send" \
  -H "Content-Type: application/json" \
  -d '{"phone": "13800138000", "type": "register"}'
```

**预期响应:**

```json
{
  "code": 0,
  "message": "验证码已发送",
  "data": {"expiresIn": 300}
}
```

**检查点:**
- [ ] code = 0
- [ ] expiresIn = 300（5分钟）

#### TC-AUTH-002: 患者注册

**curl 命令:**

```bash
curl -X POST "http://localhost:8080/api/patients/register" \
  -H "Content-Type: application/json" \
  -d '{
    "phone": "13800138000",
    "code": "123456",
    "password": "Test1234",
    "name": "张三",
    "gender": 1,
    "birthday": "1990-01-01"
  }'
```

**预期响应:**

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
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
    "expiresIn": 7200
  }
}
```

**检查点:**
- [ ] code = 0
- [ ] patient 包含完整字段（id, phone, name, gender, birthday）
- [ ] 返回 accessToken 和 refreshToken
- [ ] gender 和 birthday 为可选字段（可不传）

#### TC-AUTH-003: 检查手机号

**curl 命令:**

```bash
# 已注册
curl -X GET "http://localhost:8080/api/patients/check-phone?phone=13800138000"
# 未注册
curl -X GET "http://localhost:8080/api/patients/check-phone?phone=13999999999"
```

**检查点:**
- [ ] 已注册手机号返回"已注册"
- [ ] 未注册手机号返回"未注册"

#### TC-AUTH-004: 患者登录

**curl 命令:**

```bash
curl -X POST "http://localhost:8080/api/patients/login" \
  -H "Content-Type: application/json" \
  -d '{"phone": "13800138000", "password": "Test1234"}'
```

**预期响应:**

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
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
    "expiresIn": 7200
  }
}
```

**检查点:**
- [ ] code = 0
- [ ] phone 脱敏为 138****8000
- [ ] 返回 accessToken 和 refreshToken

#### TC-AUTH-005: 刷新 Token

**curl 命令:**

```bash
# 将 <refreshToken> 替换为实际值
curl -X POST "http://localhost:8080/api/patients/token/refresh" \
  -H "Content-Type: application/json" \
  -d '{"refreshToken": "<refreshToken>"}'
```

**预期响应:**

```json
{
  "code": 0,
  "message": "刷新成功",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
    "expiresIn": 7200
  }
}
```

**检查点:**
- [ ] code = 0
- [ ] 返回新的 accessToken 和 refreshToken

#### TC-AUTH-006: 获取患者信息

**curl 命令:**

```bash
# 将 <accessToken> 替换为实际值
curl -X GET "http://localhost:8080/api/patients/profile" \
  -H "Authorization: Bearer <accessToken>"
```

**预期响应:**

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": "p550e8400-e29b-41d4-a716-446655440000",
    "phone": "138****8000",
    "name": "张三",
    "gender": 1,
    "birthday": "1990-01-01",
    "avatar": null,
    "questionCount": 5,
    "createdAt": "2026-04-03T10:00:00Z"
  }
}
```

**检查点:**
- [ ] code = 0
- [ ] 返回患者完整信息

#### TC-AUTH-007: 更新患者信息

**curl 命令:**

```bash
# 将 <accessToken> 替换为实际值
curl -X PUT "http://localhost:8080/api/patients/profile" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <accessToken>" \
  -d '{"name": "张三新版", "gender": 2, "birthday": "1990-06-15"}'
```

**检查点:**
- [ ] code = 0
- [ ] 返回更新后的患者信息

#### TC-AUTH-008: 退出登录

**curl 命令:**

```bash
# 将 <accessToken> 替换为实际值
curl -X POST "http://localhost:8080/api/patients/logout" \
  -H "Authorization: Bearer <accessToken>"
```

**预期响应:**

```json
{
  "code": 0,
  "message": "退出成功"
}
```

**检查点:**
- [ ] code = 0

---

## 3. 业务流程测试

### 3.1 完整用户流程

```bash
BASE_URL="http://localhost:8080/api"

echo "========== 1. 发送验证码 =========="
curl -X POST "$BASE_URL/patients/sms/send" \
  -H "Content-Type: application/json" \
  -d '{"phone": "13900139000", "type": "register"}'

echo -e "\n\n========== 2. 患者注册 =========="
curl -X POST "$BASE_URL/patients/register" \
  -H "Content-Type: application/json" \
  -d '{"phone": "13900139000", "code": "123456", "password": "Test1234", "name": "测试用户", "gender": 1, "birthday": "1995-05-15"}'

echo -e "\n\n========== 3. 检查手机号 =========="
curl -X GET "$BASE_URL/patients/check-phone?phone=13900139000"

echo -e "\n\n========== 4. 患者登录 =========="
curl -X POST "$BASE_URL/patients/login" \
  -H "Content-Type: application/json" \
  -d '{"phone": "13900139000", "password": "Test1234"}'
```

### 3.2 中英文切换对比

```bash
BASE_URL="http://localhost:8080/api"

echo "========== 医生列表 - 中文 =========="
curl -s -X GET "$BASE_URL/doctors?locale=zh-CN"

echo -e "\n\n========== 医生列表 - 英文 =========="
curl -s -X GET "$BASE_URL/doctors?locale=en-US"

echo -e "\n\n========== 医生详情 - 中文 =========="
curl -s -X GET "$BASE_URL/doctors/dr-zhang-wei?locale=zh-CN"

echo -e "\n\n========== 医生详情 - 英文 =========="
curl -s -X GET "$BASE_URL/doctors/dr-zhang-wei?locale=en-US"
```

### 3.3 Windows PowerShell 版本

```powershell
$BASE_URL = "http://localhost:8080/api"

# 发送验证码
Invoke-RestMethod -Method Post -Uri "$BASE_URL/patients/sms/send" `
  -ContentType "application/json" `
  -Body '{"phone": "13900139000", "type": "register"}'

# 注册
Invoke-RestMethod -Method Post -Uri "$BASE_URL/patients/register" `
  -ContentType "application/json" `
  -Body '{"phone": "13900139000", "code": "123456", "password": "Test1234", "name": "测试用户", "gender": 1}'

# 登录
Invoke-RestMethod -Method Post -Uri "$BASE_URL/patients/login" `
  -ContentType "application/json" `
  -Body '{"phone": "13900139000", "password": "Test1234"}'

# 获取医生列表
Invoke-RestMethod -Method Get -Uri "$BASE_URL/doctors?locale=zh-CN"
Invoke-RestMethod -Method Get -Uri "$BASE_URL/doctors?locale=en-US"
```

---

## 4. 错误场景测试

| 用例ID | 场景 | curl 命令 | 预期错误码 |
|--------|------|-----------|------------|
| TC-ERR-001 | 验证码错误 | `curl -X POST "$BASE_URL/patients/register" -H "Content-Type: application/json" -d '{"phone": "13800138000", "code": "000000", "password": "Test1234", "name": "测试"}'` | 40003 |
| TC-ERR-002 | 密码格式错误 | `curl -X POST "$BASE_URL/patients/register" -H "Content-Type: application/json" -d '{"phone": "13900139002", "code": "123456", "password": "123", "name": "测试"}'` | 40002 |
| TC-ERR-003 | 手机号已注册 | `curl -X POST "$BASE_URL/patients/sms/send" -H "Content-Type: application/json" -d '{"phone": "13800138000", "type": "register"}'` | 40901 |
| TC-ERR-004 | 密码错误 | `curl -X POST "$BASE_URL/patients/login" -H "Content-Type: application/json" -d '{"phone": "13800138000", "password": "WrongPass123"}'` | 40101 |
| TC-ERR-005 | 手机号未注册 | `curl -X POST "$BASE_URL/patients/login" -H "Content-Type: application/json" -d '{"phone": "13999999999", "password": "Test1234"}'` | 40401 |
| TC-ERR-006 | 未携带 Token | `curl -X GET "$BASE_URL/patients/profile"` | 40101 |
| TC-ERR-007 | Token 无效 | `curl -X GET "$BASE_URL/patients/profile" -H "Authorization: Bearer invalid.token"` | 40101 |
| TC-ERR-008 | Refresh Token 无效 | `curl -X POST "$BASE_URL/patients/token/refresh" -H "Content-Type: application/json" -d '{"refreshToken": "invalid.token"}'` | 40103 |

---

## 5. 测试结果记录

### 5.1 测试汇总

| 用例ID | 模块 | 用例名称 | 预期结果 | 实际结果 | 状态 | 备注 |
|--------|------|----------|----------|----------|------|------|
| TC-DOC-001 | 医生 | 获取所有医生 | 返回数组，支持中英文 | | | |
| TC-DOC-002 | 医生 | 获取活跃医生 | 仅 isActive=true | | | |
| TC-DOC-003 | 医生 | 按用户名获取 | 返回单个对象 | | | |
| TC-DOC-004 | 医生 | 按 ID 获取 | 返回单个对象 | | | |
| TC-DOC-005 | 医生 | 获取不存在医生 | HTTP 404 | | | |
| TC-AUTH-001 | 认证 | 发送验证码 | code=0, expiresIn=300 | | | |
| TC-AUTH-002 | 认证 | 患者注册 | code=0, 返回 Token | | | |
| TC-AUTH-003 | 认证 | 检查手机号 | 正确判断已注册/未注册 | | | |
| TC-AUTH-004 | 认证 | 患者登录 | code=0, phone 脱敏 | | | |
| TC-AUTH-005 | 认证 | 刷新 Token | 返回新 Token | | | |
| TC-AUTH-006 | 认证 | 获取患者信息 | 返回完整信息 | | | |
| TC-AUTH-007 | 认证 | 更新患者信息 | code=0 | | | |
| TC-AUTH-008 | 认证 | 退出登录 | code=0 | | | |
| TC-ERR-001 | 错误 | 验证码错误 | 40003 | | | |
| TC-ERR-002 | 错误 | 密码格式错误 | 40002 | | | |
| TC-ERR-003 | 错误 | 手机号已注册 | 40901 | | | |
| TC-ERR-004 | 错误 | 密码错误 | 40101 | | | |
| TC-ERR-005 | 错误 | 手机号未注册 | 40401 | | | |
| TC-ERR-006 | 错误 | 未携带 Token | 40101 | | | |
| TC-ERR-007 | 错误 | Token 无效 | 40101 | | | |
| TC-ERR-008 | 错误 | Refresh Token 无效 | 40103 | | | |

### 5.2 测试统计

| 功能模块 | 用例数 | 通过 | 失败 | 通过率 |
|----------|--------|------|------|--------|
| 医生接口 | 5 | | | |
| 用户认证 | 8 | | | |
| 错误场景 | 8 | | | |
| **总计** | **21** | | | |

### 5.3 测试人员签字

| 项目 | 签字 | 日期 |
|------|------|------|
| 测试人员 | | |
| 审核人员 | | |
| 测试日期 | | |

---

## 6. 附录

### 6.1 curl 命令速查

```bash
# GET 请求
curl -X GET "url"

# POST 请求
curl -X POST "url" -H "Content-Type: application/json" -d '{"key": "value"}'

# PUT 请求
curl -X PUT "url" -H "Content-Type: application/json" -d '{"key": "value"}'

# 带认证
curl -X GET "url" -H "Authorization: Bearer <token>"
```

### 6.2 HTTP 状态码

| 状态码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 404 | 资源不存在 |
| 409 | 资源冲突 |
| 429 | 请求过于频繁 |
| 500 | 服务器内部错误 |

### 6.3 业务错误码

| 区间 | 说明 |
|------|------|
| 0 | 成功 |
| 40001-40099 | 参数校验错误 |
| 40101-40199 | 认证授权错误 |
| 40401-40499 | 资源不存在 |
| 40901-40999 | 资源冲突 |
| 42901-42999 | 频率限制 |
| 50001-50099 | 服务器内部错误 |

### 6.4 中英文翻译对照

| 中文 | 英文 |
|------|------|
| 主任医师 | Chief Physician |
| 副主任医师 | Deputy Chief Physician |
| 主治医师 | Attending Physician |
| 心内科 | Cardiology |
| 儿科 | Pediatrics |
| 骨科 | Orthopedics |
| 妇产科 | Obstetrics and Gynecology |
| 消化内科 | Gastroenterology |
| 高血压 | Hypertension |
| 冠心病 | Coronary Heart Disease |
| 心律失常 | Arrhythmia |
| 骨折 | Fracture |
| 关节炎 | Arthritis |
| 运动损伤 | Sports Injury |
| 15年临床经验 | 15 years clinical experience |
