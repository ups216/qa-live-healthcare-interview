# 接口测试文档

---

## 1. 接口清单

| 序号 | 模块 | 接口名称 | 方法 | 路径 | 是否需认证 |
|------|------|---------|------|------|-----------|
| 1 | 登录认证 | 门诊用户登录 | POST | `/api/auth/login` | 否 |
| 2 | 登录认证 | 退出登录 | POST | `/api/auth/logout` | 是 |
| 3 | 登录认证 | 刷新Token | POST | `/api/auth/refresh` | 是 |
| 4 | 登录认证 | 获取当前用户信息 | GET | `/api/auth/userinfo` | 是 |
| 5 | 医生管理 | 首页-获取激活医生列表 | GET | `/api/doctor/home/list` | 否 |
| 6 | 医生管理 | 分页获取医生列表 | GET | `/api/doctor/list` | 否 |
| 7 | 医生管理 | 获取医生详情 | GET | `/api/doctor/detail/{id}` | 否 |
| 8 | 医生管理 | 按科室查询医生列表 | GET | `/api/doctor/department/{department}` | 否 |

---

## 2. 测试环境说明

| 项目 | 说明 |
|------|------|
| 服务地址 | `http://localhost:8080` |
| 数据库 | MySQL 8.x，库名 `healthcare`，端口 3306 |
| 数据库账号 | root / root123456 |
| 认证方式 | JWT Bearer Token，有效期 24 小时 |
| 密码加密 | BCrypt |
| CORS | 允许所有来源 |
| 当前安全策略 | 所有接口 `permitAll`（无需认证即可访问） |

### 测试账号 - 门诊用户（outpatient_user）

| username | password | name | status |
|----------|----------|------|--------|
| patient001 | password123 | 张三 | 启用(1) |
| patient002 | password123 | 李四 | 启用(1) |
| patient003 | password123 | 王五 | 启用(1) |
| patient004 | password123 | 赵六 | 启用(1) |
| patient005 | password123 | 孙七 | **禁用(0)** |

### 测试数据 - 医生（doctor）

| id | name | department | isActive |
|----|------|-----------|----------|
| doc001 | 张伟医生 | 心内科 | true |
| doc002 | 李娜医生 | 儿科 | true |
| doc003 | 王强医生 | 骨科 | true |
| doc004 | 刘敏医生 | 妇产科 | **false** |
| doc005 | 陈杰医生 | 消化内科 | true |

### 前置条件

1. MySQL 服务已启动，`healthcare` 数据库已创建
2. `doctor` 表和 `outpatient_user` 表已执行初始化 SQL（`sql/init-doctor.sql`、`sql/init-outpatient-user.sql`）
3. `qa-service-user` 服务已在 8080 端口启动

---

## 3. 接口测试流程与预期结果

---

### 3.1 门诊用户登录

**接口：** `POST /api/auth/login`

**请求头：**
```
Content-Type: application/json
```

**请求体字段：**

| 字段 | 类型 | 必填 | 校验规则 |
|------|------|------|---------|
| username | String | 是 | 4-20位字母、数字或下划线 |
| password | String | 是 | 6-20位 |

#### 测试用例 3.1.1：正常登录

```bash
curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "patient001", "password": "password123"}'
```

**预期结果：**
- HTTP 状态码：200
- 返回 JSON 中 `code` = 200
- `message` = "登录成功"
- `data.token` 为非空字符串（JWT格式，以 eyJ 开头）
- `data.tokenType` = "Bearer"
- `data.expiresIn` = 86400000
- `data.userInfo.id` = 1
- `data.userInfo.username` = "patient001"
- `data.userInfo.name` = "张三"
- `data.userInfo.status` = 1

**验证要点：** 记录返回的 token 值，后续需认证接口依赖此 token。

#### 测试用例 3.1.2：密码错误

```bash
curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "patient001", "password": "wrongpassword"}'
```

**预期结果：**
- 返回 JSON 中 `code` = 401
- `message` = "用户名或密码错误"
- `data.remainingAttempts` 为整数（剩余尝试次数，从5递减）
- `data.errorCode` = "AUTH_101"

#### 测试用例 3.1.3：用户名不存在

```bash
curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "nonexistent", "password": "password123"}'
```

**预期结果：**
- `code` = 401
- `message` = "用户名或密码错误"
- `data.remainingAttempts` = 5
- `data.errorCode` = "AUTH_101"

#### 测试用例 3.1.4：账号已禁用

```bash
curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "patient005", "password": "password123"}'
```

**预期结果：**
- `code` = 401
- `message` = "账号已被禁用，请联系管理员"
- `data.remainingAttempts` = 0
- `data.errorCode` = "AUTH_103"

#### 测试用例 3.1.5：参数校验 - 用户名格式错误（少于4位）

```bash
curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "ab", "password": "password123"}'
```

**预期结果：**
- HTTP 状态码：400
- `code` = 400
- `message` = "参数校验失败"
- `data.username` = "用户名必须是4-20位字母、数字或下划线"

#### 测试用例 3.1.6：参数校验 - 密码过短（少于6位）

```bash
curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "patient001", "password": "12345"}'
```

**预期结果：**
- HTTP 状态码：400
- `code` = 400
- `message` = "参数校验失败"
- `data.password` = "密码长度必须在6-20位之间"

#### 测试用例 3.1.7：参数校验 - 字段为空

```bash
curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "", "password": ""}'
```

**预期结果：**
- HTTP 状态码：400
- `code` = 400
- `data` 中包含 `username` 和 `password` 的校验错误信息

---

### 3.2 退出登录

**接口：** `POST /api/auth/logout`

**请求头：**
```
Authorization: Bearer <token>
```

#### 测试用例 3.2.1：正常退出

> 前置：先执行 3.1.1 获取有效 token

```bash
curl -s -X POST http://localhost:8080/api/auth/logout \
  -H "Authorization: Bearer <token>"
```

**预期结果：**
- `code` = 200
- `message` = "退出成功"
- `data` = null

#### 测试用例 3.2.2：无 Token 退出

```bash
curl -s -X POST http://localhost:8080/api/auth/logout
```

**预期结果：**
- `code` = 200（当前实现不校验 token 必填）
- `message` = "退出成功"

---

### 3.3 刷新 Token

**接口：** `POST /api/auth/refresh`

**请求头：**
```
Authorization: Bearer <token>
```

#### 测试用例 3.3.1：正常刷新

> 前置：先执行 3.1.1 获取有效 token

```bash
curl -s -X POST http://localhost:8080/api/auth/refresh \
  -H "Authorization: Bearer <token>"
```

**预期结果：**
- `code` = 200
- `message` = "刷新成功"
- `data.token` 为新的 JWT 字符串（与旧 token 不同）
- `data.tokenType` = "Bearer"
- `data.expiresIn` = 86400000
- `data.userInfo` 包含用户基本信息

#### 测试用例 3.3.2：无效 Token

```bash
curl -s -X POST http://localhost:8080/api/auth/refresh \
  -H "Authorization: Bearer invalid.token.here"
```

**预期结果：**
- `code` = 401
- `message` = "Token无效或已过期"

#### 测试用例 3.3.3：缺少 Authorization 头

```bash
curl -s -X POST http://localhost:8080/api/auth/refresh
```

**预期结果：**
- `code` = 401
- `message` = "Token格式错误"

---

### 3.4 获取当前用户信息

**接口：** `GET /api/auth/userinfo`

**请求头：**
```
Authorization: Bearer <token>
```

#### 测试用例 3.4.1：正常获取

> 前置：先执行 3.1.1 获取有效 token

```bash
curl -s http://localhost:8080/api/auth/userinfo \
  -H "Authorization: Bearer <token>"
```

**预期结果：**
- `code` = 200
- `message` = "success"
- `data.id` = 1
- `data.username` = "patient001"
- `data.name` = "张三"
- `data.phone` = "13800138001"
- `data.gender` = "男"
- `data.birthday` = "1990-05-15"
- `data.status` = 1
- `data` 中不包含 `password` 字段（或 password 为 null/空）

#### 测试用例 3.4.2：无效 Token

```bash
curl -s http://localhost:8080/api/auth/userinfo \
  -H "Authorization: Bearer invalid.token.here"
```

**预期结果：**
- `code` = 401
- `message` = "Token无效或已过期"

#### 测试用例 3.4.3：缺少 Authorization 头

```bash
curl -s http://localhost:8080/api/auth/userinfo
```

**预期结果：**
- `code` = 401
- `message` = "请先登录"

---

### 3.5 首页 - 获取激活状态的医生列表

**接口：** `GET /api/doctor/home/list`

**请求参数：** 无

#### 测试用例 3.5.1：正常查询

```bash
curl -s http://localhost:8080/api/doctor/home/list
```

**预期结果：**
- `code` = 200
- `message` = "success"
- `data` 为数组，包含 4 条记录（仅 isActive=true 的医生）
- 数据中**不包含** doc004（刘敏医生，isActive=false）
- 每条记录包含字段：`id`, `username`, `name`, `title`, `department`, `avatar`, `experience`, `specialties`, `isActive`, `createdAt`, `updatedAt`
- `isActive` 全部为 true

**验证要点：** 确认返回数量为 4（5个医生中 doc004 为非激活状态）

---

### 3.6 分页获取医生列表

**接口：** `GET /api/doctor/list`

**请求参数：**

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| page | int | 1 | 页码 |
| pageSize | int | 10 | 每页数量 |

#### 测试用例 3.6.1：默认分页查询

```bash
curl -s "http://localhost:8080/api/doctor/list"
```

**预期结果：**
- `code` = 200
- `data.list` 为数组，包含 5 条记录（全部医生）
- `data.total` = 5
- `data.page` = 1
- `data.pageSize` = 10
- `data.totalPages` = 1

#### 测试用例 3.6.2：自定义分页 - 第1页每页2条

```bash
curl -s "http://localhost:8080/api/doctor/list?page=1&pageSize=2"
```

**预期结果：**
- `data.list` 包含 2 条记录
- `data.total` = 5
- `data.page` = 1
- `data.pageSize` = 2
- `data.totalPages` = 3

#### 测试用例 3.6.3：自定义分页 - 第2页每页2条

```bash
curl -s "http://localhost:8080/api/doctor/list?page=2&pageSize=2"
```

**预期结果：**
- `data.list` 包含 2 条记录（与第1页不同）
- `data.page` = 2
- `data.totalPages` = 3

#### 测试用例 3.6.4：自定义分页 - 第3页每页2条

```bash
curl -s "http://localhost:8080/api/doctor/list?page=3&pageSize=2"
```

**预期结果：**
- `data.list` 包含 1 条记录
- `data.page` = 3
- `data.totalPages` = 3

#### 测试用例 3.6.5：超大页码（超出数据范围）

```bash
curl -s "http://localhost:8080/api/doctor/list?page=999&pageSize=10"
```

**预期结果：**
- `data.list` 为空数组 `[]`
- `data.total` = 5
- `data.page` = 999

---

### 3.7 获取医生详情

**接口：** `GET /api/doctor/detail/{id}`

**路径参数：**

| 参数 | 类型 | 说明 |
|------|------|------|
| id | String | 医生ID |

#### 测试用例 3.7.1：查询存在的医生

```bash
curl -s http://localhost:8080/api/doctor/detail/doc001
```

**预期结果：**
- `code` = 200
- `message` = "success"
- `data.id` = "doc001"
- `data.username` = "dr-zhang-wei"
- `data.name` = "张伟医生"
- `data.title` = "主任医师"
- `data.department` = "心内科"
- `data.experience` = "15年临床经验"
- `data.specialties` = "[\"高血压\", \"冠心病\", \"心律失常\"]"
- `data.isActive` = true

#### 测试用例 3.7.2：查询非激活状态的医生

```bash
curl -s http://localhost:8080/api/doctor/detail/doc004
```

**预期结果：**
- `code` = 200
- `data.id` = "doc004"
- `data.name` = "刘敏医生"
- `data.isActive` = false

#### 测试用例 3.7.3：查询不存在的医生

```bash
curl -s http://localhost:8080/api/doctor/detail/notexist
```

**预期结果：**
- `code` = 404
- `message` = "医生不存在"
- `data` = null

---

### 3.8 按科室查询医生列表

**接口：** `GET /api/doctor/department/{department}`

**路径参数：**

| 参数 | 类型 | 说明 |
|------|------|------|
| department | String | 科室名称 |

> Windows PowerShell 中 URL 含中文需编码，如 `心内科` → `%E5%BF%83%E5%86%85%E7%A7%91`

#### 测试用例 3.8.1：查询心内科

```bash
curl -s http://localhost:8080/api/doctor/department/心内科
```

**预期结果：**
- `code` = 200
- `data` 为数组，包含 1 条记录
- `data[0].name` = "张伟医生"
- `data[0].department` = "心内科"

#### 测试用例 3.8.2：查询儿科

```bash
curl -s http://localhost:8080/api/doctor/department/儿科
```

**预期结果：**
- `code` = 200
- `data` 包含 1 条记录
- `data[0].name` = "李娜医生"

#### 测试用例 3.8.3：查询妇产科（含非激活医生）

```bash
curl -s http://localhost:8080/api/doctor/department/妇产科
```

**预期结果：**
- `code` = 200
- `data` 包含 1 条记录
- `data[0].name` = "刘敏医生"
- `data[0].isActive` = false

#### 测试用例 3.8.4：查询不存在的科室

```bash
curl -s http://localhost:8080/api/doctor/department/神经外科
```

**预期结果：**
- `code` = 200
- `data` 为空数组 `[]`

---

## 4. 测试执行顺序建议

由于部分接口存在依赖关系，建议按以下顺序执行：

```
Step 1: 启动服务与数据库，确认环境就绪
Step 2: 执行 3.1.1（正常登录）→ 获取并保存 token
Step 3: 执行 3.5 ~ 3.8（医生接口，无依赖）
Step 4: 使用 token 执行 3.2 ~ 3.4（认证接口）
Step 5: 执行异常场景测试（3.1.2 ~ 3.1.7）
Step 6: 执行边界场景测试（3.6.5、3.7.3、3.8.4）
```

---

## 5. 错误码速查

| 错误码 | 含义 | 触发场景 |
|--------|------|---------|
| AUTH_101 | 用户名或密码错误 | 用户名不存在 / 密码错误 |
| AUTH_102 | 账号已锁定 | 连续5次密码错误，锁定10分钟 |
| AUTH_103 | 账号已被禁用 | 用户 status=0 |
| AUTH_104 | Token无效或已过期 | refresh/userinfo 时 token 不合法 |
