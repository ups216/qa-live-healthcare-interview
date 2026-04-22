# API 接口测试文档

> 本文档描述 QA Healthcare 系统所有后端 API 接口的测试流程和预期结果。

---

## 前置条件

- 后端服务 `qa-service-user` 已启动（端口 8080）
- MySQL 数据库已初始化（执行过 `01-schema.sql` 和 `02-data.sql`）
- 已安装 curl 命令行工具

---

## 1. 基础测试接口

### 1.1 CORS 测试（GET）

```bash
curl -X GET http://localhost:8080/api/test/cors
```

**预期响应**：
```json
{
  "message": "CORS configuration is working!",
  "timestamp": 1745318400000,
  "service": "qa-service-user"
}
```

### 1.2 CORS 测试（POST）

```bash
curl -X POST http://localhost:8080/api/test/cors \
  -H "Content-Type: application/json" \
  -d '{"test": "hello"}'
```

**预期响应**：
```json
{
  "message": "POST request with CORS is working!",
  "receivedData": {"test": "hello"},
  "timestamp": 1745318400000,
  "service": "qa-service-user"
}
```

### 1.3 健康检查

```bash
curl -X GET http://localhost:8080/actuator/health
```

**预期响应**：
```json
{
  "status": "UP"
}
```

---

## 2. 患者接口

### 2.1 患者登录（成功）

```bash
curl -X POST http://localhost:8080/api/patient/login \
  -H "Content-Type: application/json" \
  -d '{"username": "patient-zhao-ming", "password": "123456"}'
```

**预期响应**（200）：
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "id": "patient001",
    "username": "patient-zhao-ming",
    "name": "赵明",
    "birthday": "1985-03-15",
    "phone": "138****1234",
    "gender": "男"
  }
}
```

### 2.2 患者登录（失败 - 错误密码）

```bash
curl -X POST http://localhost:8080/api/patient/login \
  -H "Content-Type: application/json" \
  -d '{"username": "patient-zhao-ming", "password": "wrongpassword"}'
```

**预期响应**（401）：
```json
{
  "code": 401,
  "message": "用户名或密码错误",
  "data": null
}
```

### 2.3 患者登录（失败 - 不存在的用户名）

```bash
curl -X POST http://localhost:8080/api/patient/login \
  -H "Content-Type: application/json" \
  -d '{"username": "nonexistent-user", "password": "123456"}'
```

**预期响应**（401）：
```json
{
  "code": 401,
  "message": "用户名或密码错误",
  "data": null
}
```

### 2.4 患者注册（成功）

```bash
curl -X POST http://localhost:8080/api/patient/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test-user-001",
    "password": "123456",
    "name": "测试用户",
    "birthday": "1995-01-01",
    "phone": "138****0001",
    "gender": "男"
  }'
```

**预期响应**（200）：
```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "id": "patient1745318400000",
    "username": "test-user-001",
    "name": "测试用户",
    "birthday": "1995-01-01",
    "phone": "138****0001",
    "gender": "男"
  }
}
```

### 2.5 患者注册（失败 - 用户名已存在）

```bash
curl -X POST http://localhost:8080/api/patient/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "patient-zhao-ming",
    "password": "123456",
    "name": "重复用户",
    "birthday": "1990-01-01"
  }'
```

**预期响应**（400）：
```json
{
  "code": 400,
  "message": "该用户名已被注册",
  "data": null
}
```

### 2.6 获取患者信息

```bash
curl -X GET http://localhost:8080/api/patient/patient001
```

**预期响应**（200）：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "patient001",
    "username": "patient-zhao-ming",
    "name": "赵明",
    "birthday": "1985-03-15",
    "phone": "138****1234",
    "gender": "男"
  }
}
```

### 2.7 获取患者信息（不存在）

```bash
curl -X GET http://localhost:8080/api/patient/nonexistent
```

**预期响应**（404）：
```json
{
  "code": 404,
  "message": "患者不存在",
  "data": null
}
```

---

## 3. 医生接口

### 3.1 医生登录（成功）

```bash
curl -X POST http://localhost:8080/api/doctor/login \
  -H "Content-Type: application/json" \
  -d '{"username": "dr-zhang-wei", "password": "123456"}'
```

**预期响应**（200）：
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "id": "doc001",
    "username": "dr-zhang-wei",
    "name": "张伟医生",
    "title": "主任医师",
    "department": "心内科",
    "avatar": "https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400",
    "experience": "15年临床经验",
    "isActive": true,
    "specialties": ["高血压", "冠心病", "心律失常"]
  }
}
```

### 3.2 医生登录（失败 - 错误密码）

```bash
curl -X POST http://localhost:8080/api/doctor/login \
  -H "Content-Type: application/json" \
  -d '{"username": "dr-zhang-wei", "password": "wrongpassword"}'
```

**预期响应**（401）：
```json
{
  "code": 401,
  "message": "用户名或密码错误",
  "data": null
}
```

### 3.3 医生登录（失败 - 不存在的用户名）

```bash
curl -X POST http://localhost:8080/api/doctor/login \
  -H "Content-Type: application/json" \
  -d '{"username": "dr-nonexistent", "password": "123456"}'
```

**预期响应**（401）：
```json
{
  "code": 401,
  "message": "用户名或密码错误",
  "data": null
}
```

### 3.4 获取所有医生列表

```bash
curl -X GET http://localhost:8080/api/doctor/list
```

**预期响应**（200）：
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
      "isActive": true,
      "specialties": ["高血压", "冠心病", "心律失常"]
    }
  ]
}
```

### 3.5 获取在线医生列表

```bash
curl -X GET http://localhost:8080/api/doctor/active
```

**预期响应**（200）：返回 `isActive` 为 `true` 的医生列表（4条，不含刘敏医生）

### 3.6 根据ID获取医生信息

```bash
curl -X GET http://localhost:8080/api/doctor/doc001
```

**预期响应**（200）：返回张伟医生的完整信息

### 3.7 根据用户名获取医生信息

```bash
curl -X GET http://localhost:8080/api/doctor/username/dr-zhang-wei
```

**预期响应**（200）：返回张伟医生的完整信息

### 3.8 获取不存在的医生

```bash
curl -X GET http://localhost:8080/api/doctor/nonexistent
```

**预期响应**（404）：
```json
{
  "code": 404,
  "message": "医生不存在",
  "data": null
}
```

---

## 4. 完整测试脚本

将以下脚本保存为 `test-api.sh`（Linux/Mac）或手动逐条执行：

```bash
#!/bin/bash
BASE_URL="http://localhost:8080"
PASS=0
FAIL=0

test_api() {
  local name="$1"
  local method="$2"
  local url="$3"
  local data="$4"
  local expect_code="$5"

  echo -n "Testing: $name ... "

  if [ "$method" = "GET" ]; then
    response=$(curl -s -w "\n%{http_code}" "$BASE_URL$url")
  else
    response=$(curl -s -w "\n%{http_code}" -X "$method" "$BASE_URL$url" \
      -H "Content-Type: application/json" \
      -d "$data")
  fi

  http_code=$(echo "$response" | tail -1)
  body=$(echo "$response" | sed '$d')

  if [ "$http_code" = "$expect_code" ]; then
    echo "PASS (HTTP $http_code)"
    PASS=$((PASS + 1))
  else
    echo "FAIL (expected $expect_code, got $http_code)"
    echo "  Response: $body"
    FAIL=$((FAIL + 1))
  fi
}

echo "========== QA Healthcare API Test =========="

# 基础测试
test_api "CORS GET" "GET" "/api/test/cors" "" "200"
test_api "Health Check" "GET" "/actuator/health" "" "200"

# 患者登录测试
test_api "Patient Login Success" "POST" "/api/patient/login" \
  '{"username":"patient-zhao-ming","password":"123456"}' "200"
test_api "Patient Login Wrong Password" "POST" "/api/patient/login" \
  '{"username":"patient-zhao-ming","password":"wrong"}' "401"
test_api "Patient Login Not Exist" "POST" "/api/patient/login" \
  '{"username":"no-user","password":"123456"}' "401"

# 患者注册测试
test_api "Patient Register Duplicate" "POST" "/api/patient/register" \
  '{"username":"patient-zhao-ming","password":"123456","name":"test","birthday":"1990-01-01"}' "400"

# 患者查询测试
test_api "Get Patient By ID" "GET" "/api/patient/patient001" "" "200"
test_api "Get Patient Not Found" "GET" "/api/patient/nonexistent" "" "404"

# 医生接口测试
test_api "Doctor Login Success" "POST" "/api/doctor/login" \
  '{"username":"dr-zhang-wei","password":"123456"}' "200"
test_api "Doctor Login Wrong Password" "POST" "/api/doctor/login" \
  '{"username":"dr-zhang-wei","password":"wrong"}' "401"
test_api "Doctor Login Not Exist" "POST" "/api/doctor/login" \
  '{"username":"dr-nonexistent","password":"123456"}' "401"
test_api "Doctor List" "GET" "/api/doctor/list" "" "200"
test_api "Active Doctors" "GET" "/api/doctor/active" "" "200"
test_api "Doctor By ID" "GET" "/api/doctor/doc001" "" "200"
test_api "Doctor By Username" "GET" "/api/doctor/username/dr-zhang-wei" "" "200"
test_api "Doctor Not Found" "GET" "/api/doctor/nonexistent" "" "404"

echo ""
echo "========== Results =========="
echo "PASS: $PASS"
echo "FAIL: $FAIL"
echo "Total: $((PASS + FAIL))"
