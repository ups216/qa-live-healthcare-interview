# 接口 curl 测试命令

> 服务端口：`8080`，基础地址：`http://localhost:8080`

---

## 一、门诊用户登录接口

### 1.1 正常登录

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "patient001", "password": "password123"}'
```

**返回示例（成功）：**
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400000,
    "userInfo": {
      "id": 1,
      "username": "patient001",
      "name": "张三",
      "status": 1
    }
  }
}
```

### 1.2 密码错误

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "patient001", "password": "wrongpassword"}'
```

**返回示例：**
```json
{
  "code": 401,
  "message": "用户名或密码错误",
  "data": {
    "remainingAttempts": 4,
    "errorCode": "AUTH_101"
  }
}
```

### 1.3 用户名不存在

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "nobody", "password": "password123"}'
```

**返回示例：**
```json
{
  "code": 401,
  "message": "用户名或密码错误",
  "data": {
    "remainingAttempts": 5,
    "errorCode": "AUTH_101"
  }
}
```

### 1.4 账号已禁用（patient005 status=0）

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "patient005", "password": "password123"}'
```

**返回示例：**
```json
{
  "code": 401,
  "message": "账号已被禁用，请联系管理员",
  "data": {
    "remainingAttempts": 0,
    "errorCode": "AUTH_103"
  }
}
```

### 1.5 参数校验失败（用户名格式错误）

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "ab", "password": "123"}'
```

**返回示例（HTTP 400）：**
```json
{
  "code": 400,
  "message": "参数校验失败",
  "data": {
    "username": "用户名必须是4-20位字母、数字或下划线",
    "password": "密码长度必须在6-20位之间"
  }
}
```

### 1.6 退出登录（需替换真实 Token）

```bash
curl -X POST http://localhost:8080/api/auth/logout \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

**返回示例：**
```json
{
  "code": 200,
  "message": "退出成功",
  "data": null
}
```

### 1.7 刷新 Token（需替换真实 Token）

```bash
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

**返回示例：**
```json
{
  "code": 200,
  "message": "刷新成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...(新Token)",
    "tokenType": "Bearer",
    "expiresIn": 86400000,
    "userInfo": {
      "id": 1,
      "username": "patient001",
      "name": "张三",
      "status": 1
    }
  }
}
```

### 1.8 获取当前用户信息（需替换真实 Token）

```bash
curl http://localhost:8080/api/auth/userinfo \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

**返回示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "patient001",
    "name": "张三",
    "phone": "13800138001",
    "gender": "男",
    "birthday": "1990-05-15",
    "status": 1,
    "errorCount": 0,
    "createTime": "...",
    "updateTime": "..."
  }
}
```

---

## 二、医生列表查询接口

### 2.1 首页 - 获取激活状态的医生列表

```bash
curl http://localhost:8080/api/doctor/home/list
```

**返回示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": "doc001",
      "username": "dr-zhang-wei",
      "name": "张伟医生",
      "title": "主任医师",
      "department": "心内科",
      "avatar": "https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400",
      "experience": "15年临床经验",
      "specialties": "[\"高血压\", \"冠心病\", \"心律失常\"]",
      "isActive": true,
      "createdAt": "2025-11-03T...",
      "updatedAt": "2025-11-03T..."
    }
  ]
}
```

> 仅返回 `is_active = 1` 的医生（排除 doc004 刘敏医生）

### 2.2 医生页面 - 分页获取医生列表（默认 page=1, pageSize=10）

```bash
curl "http://localhost:8080/api/doctor/list?page=1&pageSize=10"
```

**返回示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "id": "doc001",
        "username": "dr-zhang-wei",
        "name": "张伟医生",
        "title": "主任医师",
        "department": "心内科",
        "avatar": "...",
        "experience": "15年临床经验",
        "specialties": "[\"高血压\", \"冠心病\", \"心律失常\"]",
        "isActive": true,
        "createdAt": "...",
        "updatedAt": "..."
      }
    ],
    "total": 5,
    "page": 1,
    "pageSize": 10,
    "totalPages": 1
  }
}
```

### 2.3 分页查询 - 第2页每页2条

```bash
curl "http://localhost:8080/api/doctor/list?page=2&pageSize=2"
```

### 2.4 获取医生详情

```bash
curl http://localhost:8080/api/doctor/detail/doc001
```

**返回示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "doc001",
    "username": "dr-zhang-wei",
    "name": "张伟医生",
    "title": "主任医师",
    "department": "心内科",
    "avatar": "https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400",
    "experience": "15年临床经验",
    "specialties": "[\"高血压\", \"冠心病\", \"心律失常\"]",
    "isActive": true,
    "createdAt": "...",
    "updatedAt": "..."
  }
}
```

### 2.5 医生不存在

```bash
curl http://localhost:8080/api/doctor/detail/notexist
```

**返回示例：**
```json
{
  "code": 404,
  "message": "医生不存在",
  "data": null
}
```

### 2.6 按科室查询医生列表

```bash
curl http://localhost:8080/api/doctor/department/心内科
```

> Windows PowerShell 中 URL 含中文可能需编码：`http://localhost:8080/api/doctor/department/%E5%BF%83%E5%86%85%E7%A7%91`

**返回示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": "doc001",
      "username": "dr-zhang-wei",
      "name": "张伟医生",
      "title": "主任医师",
      "department": "心内科",
      ...
    }
  ]
}
```

### 2.7 其他科室测试

```bash
# 儿科
curl http://localhost:8080/api/doctor/department/儿科

# 骨科
curl http://localhost:8080/api/doctor/department/骨科

# 妇产科（doc004 刘敏医生，isActive=false，但仍会返回）
curl http://localhost:8080/api/doctor/department/妇产科

# 消化内科
curl http://localhost:8080/api/doctor/department/消化内科
```

---

## 三、测试账号速查

### 门诊用户（outpatient_user）

| username    | password     | name | status   |
|-------------|-------------|------|----------|
| patient001  | password123 | 张三 | 启用 (1) |
| patient002  | password123 | 李四 | 启用 (1) |
| patient003  | password123 | 王五 | 启用 (1) |
| patient004  | password123 | 赵六 | 启用 (1) |
| patient005  | password123 | 孙七 | 禁用 (0) |

### 医生（doctor）

| id     | name    | department | isActive |
|--------|---------|-----------|----------|
| doc001 | 张伟医生 | 心内科     | true     |
| doc002 | 李娜医生 | 儿科       | true     |
| doc003 | 王强医生 | 骨科       | true     |
| doc004 | 刘敏医生 | 妇产科     | false    |
| doc005 | 陈杰医生 | 消化内科    | true     |

---

## 四、请求头说明

| 请求头 | 值 | 适用场景 |
|--------|-----|---------|
| `Content-Type` | `application/json` | POST 请求带 JSON Body |
| `Authorization` | `Bearer <token>` | 需认证的接口（logout/refresh/userinfo） |
