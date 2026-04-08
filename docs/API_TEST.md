# API 测试用例文档

| 属性 | 内容 |
|------|------|
| **文档版本** | v2.0 |
| **项目名称** | QA Live Healthcare - 在线医疗问诊平台 |
| **撰写日期** | 2026-04-08 |
| **测试范围** | 最近 3 次 commit 新增的全部业务接口（6 个端点） |
| **基础 URL** | `http://localhost:8080` |
| **前置条件** | Docker MySQL 运行中 (`qa-mysql`)，Spring Boot 服务运行中 (`8080`) |

---

## 目录

1. [接口总览](#1-接口总览)
2. [患者模块 (Patient)](#2-患者模块-patient)
   - 2.1 用户注册 `POST /api/patients/register`
   - 2.2 用户登录 `POST /api/patients/login`
   - 2.3 退出登录 `POST /api/patients/logout`
   - 2.4 检查用户名可用性 `GET /api/patients/check-username`
3. [医生模块 (Doctor)](#3-医生模块-doctor)
   - 3.1 获取医生列表 `GET /api/doctors`
   - 3.2 获取医生详情 `GET /api/doctors/{id}`

---

## 1. 接口总览

| 模块 | 方法 | 路径 | 功能 | Commit |
|------|:----:|------|------|--------|
| Patient | POST | `/api/patients/register` | 用户注册 | 54bd436 |
| Patient | POST | `/api/patients/login` | 用户登录 | 54bd436 |
| Patient | POST | `/api/patients/logout` | 退出登录 | 54bd436 |
| Patient | GET | `/api/patients/check-username` | 检查用户名可用性 | 54bd436 |
| Doctor | GET | `/api/doctors` | 获取医生列表 | 2d70563 |
| Doctor | GET | `/api/doctors/{id}` | 获取医生详情 | 2d70563 |

---

## 2. 患者模块 (Patient)

### 2.1 用户注册

**接口信息**

```
POST /api/patients/register
Content-Type: application/json
```

**请求体参数**

| 参数名 | 类型 | 必填 | 校验规则 |
|--------|------|:----:|---------|
| username | string | 是 | 3-20 字符；仅字母/数字/下划线；首字符必须为字母或下划线 |
| password | string | 是 | 6-20 字符；必须同时包含至少一个字母和一个数字 |
| confirmPassword | string | 是 | 必须与 password 完全一致 |
| name | string | 是 | 2-10 个字符（中文/英文） |
| gender | string | 是 | 枚举值："男" / "女" / "其他" |
| birthday | string | 是 | 合法日期格式 (YYYY-MM-DD)，不能晚于当前日期 |
| phone | string | 否 | 可选，如填写需符合 11 位手机号格式 |

---

#### 正向测试用例

| 用例编号 | 名称 | 请求体 | 预期 HTTP Code | 预期响应内容 |
|----------|------|--------|:-------------:|--------------|
| PAT-RG-001 | 正常注册（全量字段） | 见下方 | 200 | 返回 PatientDTO，不含 password；data 不为 null |
| PAT-RG-002 | 注册时不填手机号（phone 为空） | 同上，去掉 phone 字段 | 200 | 注册成功，phone 为 null 或不存在 |
| PAT-RG-003 | 用户名使用下划线开头 | username=`_testuser2026`, 密码=Pass123456 | 200 | 注册成功 |
| PAT-RG-004 | 用户名使用数字和字母混合 | username=`user123abc`, 密码=Test9999 | 200 | 注册成功 |

**PAT-RG-001 请求体示例：**

```json
{
    "username": "testpatient01",
    "password": "Pass123456",
    "confirmPassword": "Pass123456",
    "name": "测试用户",
    "gender": "男",
    "birthday": "1995-06-15",
    "phone": "13800138001"
}
```

**PAT-RG-001 预期响应：**

```json
{
    "data": {
        "id": "<UUID字符串，36位>",
        "username": "testpatient01",
        "name": "测试用户",
        "gender": "男",
        "birthday": "1995-06-15",
        "phone": "13800138001",
        "active": true
    },
    "total": 0,
    "error": null,
    "message": null
}
```

---

#### 反向测试用例 — 用户名校验

| 用例编号 | 名称 | 请求要点 | 预期 HTTP Code | 预期 error 字段 | 预期 message 关键词 |
|----------|------|----------|:-------------:|:---------------:|---------------------|
| PAT-RG-101 | 用户名为空字符串 | `"username":""` | 400 | VALIDATION_ERROR | 不能为空 |
| PAT-RG-102 | 缺少 username 字段 | 请求体中不含 username | 400 | VALIDATION_ERROR | 不能为空 |
| PAT-RG-103 | 用户名不足 3 位 | username=`ab` | 400 | VALIDATION_ERROR | 3-20个字符之间 |
| PAT-RG-104 | 用户名超过 20 位 | username=`abcdefghijklmnopqrstuvwxyz` (26位) | 400 | VALIDATION_ERROR | 3-20个字符之间 |
| PAT-RG-105 | 用户名以数字开头 | username=`123abc` | 400 | VALIDATION_ERROR | 以字母或下划线开头 |
| PAT-RG-106 | 用户名以特殊字符开头 | username=`@admin` | 400 | VALIDATION_ERROR | 以字母或下划线开头 |
| PAT-RG-107 | 用户名含中文 | username=`张三2026` | 400 | VALIDATION_ERROR | 只能包含字母/数字/下划线 |
| PAT-RG-108 | 用户名含空格 | username=`test user` | 400 | VALIDATION_ERROR | 只能包含字母/数字/下划线 |
| PAT-RG-109 | 用户名已存在（重复注册） | 使用已注册的 username | 400 | USERNAME_EXISTS | 该用户名已被注册 |

---

#### 反向测试用例 — 密码校验

| 用例编号 | 名称 | 请求要点 | 预期 HTTP Code | 预期 error 字段 | 预期 message 关键词 |
|----------|------|----------|:-------------:|:---------------:|---------------------|
| PAT-RG-201 | 密码为空 | `"password":""` | 400 | VALIDATION_ERROR | 不能为空 |
| PAT-RG-202 | 缺少 password 字段 | 请求体中不含 password | 400 | VALIDATION_ERROR | 不能为空 |
| PAT-RG-203 | 密码不足 6 位 | password=`Abc12` (5位) | 400 | VALIDATION_ERROR | 6-20个字符之间 |
| PAT-RG-204 | 密码超过 20 位 | password=`Abcdefghij123456789012` (22位) | 400 | VALIDATION_ERROR | 6-20个字符之间 |
| PAT-RG-205 | 密码纯字母无数字 | password=`abcdefg` | 400 | VALIDATION_ERROR | 包含至少一个字母和一个数字 |
| PAT-RG-206 | 密码纯数字无字母 | password=`123456789` | 400 | VALIDATION_ERROR | 包含至少一个字母和一个数字 |
| PAT-RG-207 | 密码纯特殊字符 | password=`!@#$%^&*()` | 400 | VALIDATION_ERROR | 包含至少一个字母和一个数字 |
| PAT-RG-208 | 两次密码不一致 | password=Pass123, confirmPassword=Pass456 | 400 | VALIDATION_ERROR | 两次输入的密码不一致 |
| PAT-RG-209 | 确认密码为空 | confirmPassword 为空 | 400 | VALIDATION_ERROR | 不能为空 |

---

#### 反向测试用例 — 其他字段校验

| 用例编号 | 名称 | 请求要点 | 预期 HTTP Code | 预期 error 字段 | 预期 message 关键词 |
|----------|------|----------|:-------------:|:---------------:|---------------------|
| PAT-RG-301 | 姓名为空 | `"name":""` | 400 | VALIDATION_ERROR | 姓名不能为空 |
| PAT-RG-302 | 姓名不足 2 位 | name=`A` | 400 | VALIDATION_ERROR | 2-10个字符之间 |
| PAT-RG-303 | 姓名超过 10 位 | name=`这是一个非常长的姓名测试` | 400 | VALIDATION_ERROR | 2-10个字符之间 |
| PAT-RG-304 | 性别为空 | `"gender":""` | 400 | VALIDATION_ERROR | 性别不能为空 |
| PAT-RG-305 | 性别值不在枚举内 | gender=`未知` | 200 | — | 后端不拦截枚举值，直接存储到数据库 |
| PAT-RG-306 | 生日为空 | birthday 缺失或 null | 400 | VALIDATION_ERROR | 生日不能为空 |
| PAT-RG-307 | 生日是未来日期 | birthday=`2099-12-31` | 400 | VALIDATION_ERROR | 不能晚于当前日期 |
| PAT-RG-308 | 手机号格式错误（非11位） | phone=`13800138` (8位) | 400 | VALIDATION_ERROR | 正确的手机号格式 |
| PAT-RG-309 | 手机号格式错误（非1开头） | phone=`23800138001` | 400 | VALIDATION_ERROR | 正确的手机号格式 |
| PAT-RG-310 | 手机号含非法字符 | phone=`138-00138001` | 400 | VALIDATION_ERROR | 正确的手机号格式 |

---

#### 异常与边界测试用例

| 用例编号 | 名称 | 请求要点 | 预期 HTTP Code | 预期 error 字段 | 备注 |
|----------|------|----------|:-------------:|:---------------:|------|
| PAT-RG-401 | Content-Type 不是 JSON | 发送 `Content-Type: text/plain` | 415 / 400 | — | 服务器无法解析请求体 |
| PAT-RG-402 | 请求体为空 JSON `{}` | 所有字段缺失 | 400 | VALIDATION_ERROR | 多字段校验失败 |
| PAT-RG-403 | 请求体不是合法 JSON | body=`{invalid json` | 400 | — | JSON 解析失败 |
| PAT-RG-404 | 请求体超大（极端边界） | username 填充 10000 个字符 | 400 | VALIDATION_ERROR | 超出长度限制 |
| PAT-RG-405 | SQL 注入尝试 | username=`'; DROP TABLE patients;--` | 400 | VALIDATION_ERROR | 应被 @Pattern 校验规则拦截（含特殊字符） |
| PAT-RG-406 | XSS 注入尝试 | name=`<img>x</img>` (≤10字符) | 200 | — | 存储后应被转义输出，payload 需满足 name 的 @Size(2-10) 约束 |
| PAT-RG-407 | 特殊 Unicode 字符 | name=`🎉😀中文姓名🏥` | 200 / 400 | 取决于实现 | 测试编码处理能力 |
| PAT-RG-408 | 并发注册相同用户名 | 两个并发请求同 username | 一个 200，一个 400 | USERNAME_EXISTS | 数据库 UNIQUE KEY 兜底 |
| PAT-RG-409 | 生日为极早日期 | birthday=`1900-01-01` | 200 | null | 合法日期应允许 |
| PAT-RG-410 | 密码含特殊字符但满足基本规则 | password=`P@ss123456` (含@符号) | 200 | null | @Pattern 仅要求字母+数字，不排斥其他字符 |

---

### 2.2 用户登录

**接口信息**

```
POST /api/patients/login
Content-Type: application/json
```

**请求体参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|:----:|------|
| username | string | 是 | 已注册的用户名 |
| password | string | 是 | 注册时设置的密码明文 |

---

#### 正向测试用例

| 用例编号 | 名称 | 请求体 | 预期 HTTP Code | 预期响应内容 |
|----------|------|--------|:-------------:|--------------|
| PAT-LI-001 | 正确账号密码登录 | 见下方 | 200 | 返回完整 PatientDTO，active=true |
| PAT-LI-002 | 连续多次正确登录 | 同一用户连续登录 5 次 | 200 | 全部成功，返回一致数据 |

**PAT-LI-001 请求体示例：**

```json
{
    "username": "testpatient01",
    "password": "Pass123456"
}
```

**PAT-LI-001 预期响应：**

```json
{
    "data": {
        "id": "<对应 UUID>",
        "username": "testpatient01",
        "name": "测试用户",
        "gender": "男",
        "birthday": "1995-06-15",
        "phone": "13800138001",
        "active": true
    },
    "total": 0,
    "error": null,
    "message": null
}
```

> **关键验证点**：响应 data 中 **绝不能包含 password 字段**。

---

#### 反向测试用例

| 用例编号 | 名称 | 请求要点 | 预期 HTTP Code | 预期 error 字段 | 预期 message | 安全性说明 |
|----------|------|----------|:-------------:|:---------------:|-------------|-----------|
| PAT-LI-101 | 密码错误 | 正确 username，错误 password | 401 | INVALID_CREDENTIALS | 用户名或密码错误 | **不得泄露"密码错误"** |
| PAT-LI-102 | 用户不存在 | username=`nonexistent_user_99999` | 401 | INVALID_CREDENTIALS | 用户名或密码错误 | **不得泄露"用户不存在"** |
| PAT-LI-103 | 用户名为空 | `"username":""` | 400 | VALIDATION_ERROR | 请输入用户名 | — |
| PAT-LI-104 | 密码为空 | `"password":""` | 400 | VALIDATION_ERROR | 请输入密码 | — |
| PAT-LI-105 | 缺少 username 字段 | 仅有 password | 400 | VALIDATION_ERROR | 请输入用户名 | — |
| PAT-LI-106 | 缺少 password 字段 | 仅有 username | 400 | VALIDATION_ERROR | 请输入密码 | — |
| PAT-LI-107 | 两者都为空 | username 和 password 都为空 | 400 | VALIDATION_ERROR | — | — |
| PAT-LI-108 | 账号已被禁用 | is_active=false 的用户登录 | 403 | ACCOUNT_DISABLED | 账号已被禁用 | 区分于密码错误 |

---

#### 异常与边界测试用例

| 用例编号 | 名称 | 请求要点 | 预期 HTTP Code | 备注 |
|----------|------|----------|:-------------:|------|
| PAT-LI-201 | SQL 注入尝试（用户名） | username=`' OR '1'='1` | 401 | BCrypt 匹配必然失败，返回 401（@Pattern 不匹配时走认证流程返回 INVALID_CREDENTIALS） |
| PAT-LI-202 | SQL 注入尝试（密码） | password=`' OR '1'='1` | 401 | BCrypt 匹配必然失败，返回 401 |
| PAT-LI-203 | 超长输入 | username/password 各 10000 字符 | 401 | 超长 username 不匹配 @Pattern 后走认证流程，返回 INVALID_CREDENTIALS |
| PAT-LI-204 | 请求体为空 JSON `{}` | 无任何字段 | 400 | @NotBlank 校验触发 |
| PAT-LI-205 | 非 JSON 格式请求 | body=`raw text` | 415 | Spring Boot 对非 JSON Content-Type 返回 Unsupported Media Type |
| PAT-LI-206 | 大小写敏感性 | 注册时 username=testpatient01，登录时用 TestPatient01 | 401 | 应用层精确匹配用户名大小写，不依赖 DB collation |
| PAT-LI-207 | 密码前后有空格 | password 含前导/尾随空格 | 401 | BCrypt 严格匹配，空格也算入 |

---

### 2.3 退出登录

**接口信息**

```
POST /api/patients/logout
Content-Type: application/json
```

**请求体**：无需请求体。

---

#### 正向测试用例

| 用例编号 | 名称 | 请求体 | 预期 HTTP Code | 预期响应内容 |
|----------|------|--------|:-------------:|--------------|
| PAT-LO-001 | 正常退出登录 | 无请求体 | 200 | `{ "data": null, "total": 0, "error": null, "message": "已退出登录" }` |
| PAT-LO-002 | 未登录状态下调用退出 | 无请求体 | 200 | 当前版本无状态设计，退出总是成功 |
| PAT-LO-003 | 连续调用退出 3 次 | 连续发送 3 次 POST | 200 | 全部成功 |

---

#### 异常测试用例

| 用例编号 | 名称 | 请求要点 | 预期 HTTP Code | 备注 |
|----------|------|----------|:-------------:|------|
| PAT-LO-101 | 带 JSON 请求体 | body=`{"foo":"bar"}` | 200 | 请求体被忽略 |
| PAT-LO-102 | 非 POST 方法 | 使用 GET / PUT / DELETE 访问该路径 | 405 | Method Not Allowed |

---

### 2.4 检查用户名可用性

**接口信息**

```
GET /api/patients/check-username?username=<value>
```

**请求参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|:----:|------|
| username | string | 是 | 待检查的用户名（URL query parameter） |

---

#### 正向测试用例

| 用例编号 | 名称 | 请求参数 | 预期 HTTP Code | 预期 available 值 |
|----------|------|----------|:-------------:|:------------------:|
| PAT-CU-001 | 检查未注册的用户名 | username=newuser_2026 | 200 | `true` |
| PAT-CU-002 | 检查已注册的用户名 | username=testpatient01 (已注册) | 200 | `false` |
| PAT-CU-003 | 边界：最小长度(3) | username=abc | 200 | 取决于是否已注册 |
| PAT-CU-004 | 边界：最大长度(20) | username=`abcdefghijklmnopqrst` (20位) | 200 | 取决于是否已注册 |

**PAT-CU-001 预期响应：**

```json
{
    "data": {
        "available": true,
        "username": "newuser_2026"
    },
    "error": null
}
```

**PAT-CU-002 预期响应：**

```json
{
    "data": {
        "available": false,
        "username": "testpatient01"
    },
    "error": null
}
```

---

#### 反向测试用例

| 用例编号 | 名称 | 请求参数 | 预期 HTTP Code | 备注 |
|----------|------|----------|:-------------:|------|
| PAT-CU-101 | 缺少 username 参数 | 不传 query param | 400 | Spring Boot 对缺失 @RequestParam 默认返回 400 Bad Request |
| PAT-CU-102 | username 为空字符串 | username= | 200 | 空串不会匹配任何记录 → available=true |
| PAT-CU-103 | username 含 URL 特殊字符 | username=`hello%20world` | 200 | URL 编码后的值用于查询 |
| PAT-CU-104 | username 含中文 | username=`张三测试` | 200 | 中文不匹配已有记录 → available=true |

---

## 3. 医生模块 (Doctor)

### 3.1 获取医生列表

**接口信息**

```
GET /api/doctors
```

**请求参数**：无。

---

#### 正向测试用例

| 用例编号 | 名称 | 预期 HTTP Code | 预期响应内容 |
|----------|------|:-------------:|--------------|
| DOC-LIST-001 | 获取全部医生列表 | 200 | 返回 DoctorDTO 数组，每项含 id/name/title/department/specialties/active 等字段 |
| DOC-LIST-002 | 验证数据结构完整性 | 200 | 每条记录的字段齐全且类型正确 |
| DOC-LIST-003 | 验证 total 与 data 数量一致 | 200 | total == data.length |

**DOC-LIST-001 预期响应结构：**

```json
{
    "data": [
        {
            "id": "doc001",
            "username": "dr_wang",
            "name": "王医生",
            "title": "主任医师",
            "department": "内科",
            "avatar": "...",
            "experience": "20年临床经验",
            "specialties": ["心血管", "高血压"],
            "active": true
        }
    ],
    "total": <数组长度>,
    "error": null,
    "message": null
}
```

---

#### 异常测试用例

| 用例编号 | 名称 | 请求方式 | 预期 HTTP Code | 备注 |
|----------|------|----------|:-------------:|------|
| DOC-LIST-101 | 使用 POST 方法访问 | POST /api/doctors | 405 | Method Not Allowed |
| DOC-LIST-102 | 带无效 query 参数 | GET /api/doctors?foo=bar | 200 | 多余参数被忽略 |
| DOC-LIST-103 | 数据库为空时的行为 | doctors 表清空后查询 | 200 | data=[] , total=0 |

---

### 3.2 获取医生详情

**接口信息**

```
GET /api/doctors/{id}
```

**路径参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|:----:|------|
| id | string | 是 | 医生的唯一标识 ID |

---

#### 正向测试用例

| 用例编号 | 名称 | 请求路径 | 预期 HTTP Code | 预期响应 |
|----------|------|----------|:-------------:|---------|
| DOC-ID-001 | 查询存在的医生 | GET /api/doctors/doc001 | 200 | 返回单个 DoctorDTO |
| DOC-ID-002 | 验证返回数据完整性 | 同上 | 200 | 所有**必填**字段（id/name/title/department/specialties/active）不为 null；avatar/experience 为可选项可为空 |

**DOC-ID-001 预期响应：**

```json
{
    "data": {
        "id": "doc001",
        "username": "dr_wang",
        "name": "王医生",
        "title": "主任医师",
        "department": "内科",
        "avatar": "...",
        "experience": "20年临床经验",
        "specialties": ["心血管", "高血压"],
        "active": true
    },
    "total": 0,
    "error": null,
    "message": null
}
```

---

#### 反向测试用例

| 用例编号 | 名称 | 请求路径 | 预期 HTTP Code | 预期 error 字段 | 预期 message |
|----------|------|----------|:-------------:|:---------------:|-------------|
| DOC-ID-101 | ID 不存在 | /api/doctors/nonexistent_id_99999 | 404 | Doctor not found | Doctor with id ... not found |
| DOC-ID-102 | ID 为空字符串 | /api/doctors/ | 404 / 400 | 取决于路由匹配 | — |

---

#### 异常与边界测试用例

| 用例编号 | 名称 | 请求路径 | 预期 HTTP Code | 备注 |
|----------|------|----------|:-------------:|------|
| DOC-ID-201 | ID 含 SQL 注入 | /api/doctors/' OR '1'='1 | 404 | 安全处理后未找到 |
| DOC-ID-201 | ID 含路径遍历 | /api/doctors/../patients | 404 / 400 | 应被路由安全拦截 |
| DOC-ID-202 | 超长 ID | /api/doctors/<1000个字符的ID> | 404 | 未找到记录 |
| DOC-ID-203 | 使用 POST 方法 | POST /api/doctors/doc001 | 405 | Method Not Allowed |

---

## 附录 A：curl 命令速查表

### 患者 — 注册

```bash
# 正向：正常注册
curl -s -X POST http://localhost:8080/api/patients/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser001","password":"Pass123456","confirmPassword":"Pass123456","name":"测试一号","gender":"男","birthday":"1990-01-15","phone":"13900139001"}'

# 反向：用户名重复
curl -s -X POST http://localhost:8080/api/patients/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser001","password":"NewPass123","confirmPassword":"NewPass123","name":"测试二号","gender":"女","birthday":"1995-06-20"}'

# 反向：密码不一致
curl -s -X POST http://localhost:8080/api/patients/register \
  -H "Content-Type: application/json" \
  -d '{"username":"newuser002","password":"Pass123456","confirmPassword":"DiffPass789","name":"测试三号","gender":"男","birthday":"1988-03-10"}'

# 反向：密码纯字母
curl -s -X POST http://localhost:8080/api/patients/register \
  -H "Content-Type: application/json" \
  -d '{"username":"newuser003","password":"abcdefg","confirmPassword":"abcdefg","name":"测试四号","gender":"女","birthday":"2000-07-22"}'

# 反向：用户名不足3位
curl -s -X POST http://localhost:8080/api/patients/register \
  -H "Content-Type: application/json" \
  -d '{"username":"ab","password":"Pass123456","confirmPassword":"Pass123456","name":"测试五号","gender":"其他","birthday":"1992-11-11"}'
```

### 患者 — 登录

```bash
# 正向：正确登录
curl -s -X POST http://localhost:8080/api/patients/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser001","password":"Pass123456"}'

# 反向：密码错误
curl -s -w "\nHTTP_CODE:%{http_code}" -X POST http://localhost:8080/api/patients/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser001","password":"wrongpass"}'

# 反向：用户不存在
curl -s -w "\nHTTP_CODE:%{http_code}" -X POST http://localhost:8080/api/patients/login \
  -H "Content-Type: application/json" \
  -d '{"username":"no_such_user_99999","password":"Pass123456"}'

# 反向：空用户名
curl -s -w "\nHTTP_CODE:%{http_code}" -X POST http://localhost:8080/api/patients/login \
  -H "Content-Type: application/json" \
  -d '{"username":"","password":"Pass123456"}'
```

### 患者 — 退出 & 检查用户名

```bash
# 退出登录
curl -s -X POST http://localhost:8080/api/patients/logout

# 检查用户名可用（未注册）
curl -s "http://localhost:8080/api/patients/check-username?username=brandnewuser"

# 检查用户名可用（已注册）
curl -s "http://localhost:8080/api/patients/check-username?username=testuser001"
```

### 医生 — 列表 & 详情

```bash
# 医生列表
curl -s http://localhost:8080/api/doctors | python3 -m json.tool

# 医生详情（替换为实际存在的 ID）
curl -s http://localhost:8080/api/doctors/doc001 | python3 -m json.tool

# 医生详情（不存在的 ID）
curl -s -w "\nHTTP_CODE:%{http_code}" http://localhost:8080/api/doctors/fake_id_99999
```

---

## 附录 B：安全性专项验证清单

| 编号 | 检查项 | 对应用例 | 验证方法 |
|------|--------|---------|---------|
| SEC-01 | 密码不以明文存储 | PAT-RG-001 | 查数据库 patients 表，password 字段应为 `$2a$12$...` 格式（60字符） |
| SEC-02 | API 响应不含 password 字段 | PAT-RG-001, PAT-LI-001 | 检查 JSON response 的 data 中无 password 键 |
| SEC-03 | 登录失败统一提示（防枚举） | PAT-LI-101, PAT-LI-102 | "用户不存在" 和 "密码错误" 返回完全相同的 message |
| SEC-04 | SQL 注入防护 | PAT-RG-405, PAT-LI-201, DOC-ID-201 | 输入 SQL payload 后不应报错或泄漏信息 |
| SEC-05 | XSS 防护 | PAT-RG-406 | 存储 script 标签后在 HTML 中应被转义输出 |
| SEC-06 | BCrypt 强度 | 数据库直接查看 | password 哈希值应以 `$2a$12$` 开头（factor >= 10） |
| SEC-07 | 用户名唯一性约束 | PAT-RG-109, PAT-RG-408 | 并发注册相同 username 时只有一个成功 |
| SEC-08 | 跨域配置 | 所有接口 | CORS 配置允许前端跨域访问 |

---

## 附录 C：测试执行记录模板

| 执行日期 | 执行人 | 总用例数 | 通过 | 失败 | 跳过 | 通过率 |
|----------|--------|:-------:|:----:|:----:|:----:|:-----:|
| _________ | ________ | _______ | ____ | ____ | ____ | _____ |

### 失败用例详情

| 用例编号 | 失败原因 | 严重程度 | 修复建议 |
|----------|---------|:-------:|---------|
| | | | |
