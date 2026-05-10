# QA Service User API 测试用例

本文档包含 qa-service-user 模块所有接口的 curl 测试用例。

## 基础信息

- **服务名称**: qa-service-user
- **基础URL**: http://localhost:8080
- **默认端口**: 8080

---

## 1. 医生管理 API (`/api/doctors`)

### 1.1 获取所有医生列表

**接口**: `GET /api/doctors`

**描述**: 返回数据库中所有医生的信息

**请求参数**: 无

**curl 测试命令**:
```bash
curl -X GET http://localhost:8080/api/doctors \
  -H "Content-Type: application/json"
```

**预期响应**:
```json
[
  {
    "id": "doc001",
    "username": "dr-zhang-wei",
    "name": "张伟医生",
    "title": "主任医师",
    "department": "心内科",
    "avatar": "https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400",
    "experience": "15年临床经验",
    "specialties": ["高血压", "冠心病", "心律失常"],
    "isActive": true
  }
]
```

---

### 1.2 获取在线医生列表

**接口**: `GET /api/doctors/active`

**描述**: 返回所有在线状态的医生信息

**请求参数**: 无

**curl 测试命令**:
```bash
curl -X GET http://localhost:8080/api/doctors/active \
  -H "Content-Type: application/json"
```

**预期响应**: 与"获取所有医生列表"相同，但只包含 isActive=true 的医生

---

### 1.3 根据ID获取医生信息

**接口**: `GET /api/doctors/{id}`

**描述**: 根据医生ID获取单个医生的详细信息

**路径参数**:
- `id`: 医生ID（例如：doc001）

**curl 测试命令**:
```bash
# 测试存在的医生ID
curl -X GET http://localhost:8080/api/doctors/doc001 \
  -H "Content-Type: application/json"

# 测试不存在的医生ID
curl -X GET http://localhost:8080/api/doctors/nonexistent \
  -H "Content-Type: application/json"
```

**预期响应 (成功)**:
```json
{
  "id": "doc001",
  "username": "dr-zhang-wei",
  "name": "张伟医生",
  "title": "主任医师",
  "department": "心内科",
  "avatar": "https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400",
  "experience": "15年临床经验",
  "specialties": ["高血压", "冠心病", "心律失常"],
  "isActive": true
}
```

**预期响应 (不存在)**: HTTP 404 Not Found

---

### 1.4 根据用户名获取医生信息

**接口**: `GET /api/doctors/username/{username}`

**描述**: 根据用户名获取单个医生的详细信息

**路径参数**:
- `username`: 医生用户名（例如：dr-zhang-wei）

**curl 测试命令**:
```bash
# 测试存在的用户名
curl -X GET http://localhost:8080/api/doctors/username/dr-zhang-wei \
  -H "Content-Type: application/json"

# 测试不存在的用户名
curl -X GET http://localhost:8080/api/doctors/username/nonexistent \
  -H "Content-Type: application/json"
```

**预期响应 (成功)**:
```json
{
  "id": "doc001",
  "username": "dr-zhang-wei",
  "name": "张伟医生",
  "title": "主任医师",
  "department": "心内科",
  "avatar": "https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400",
  "experience": "15年临床经验",
  "specialties": ["高血压", "冠心病", "心律失常"],
  "isActive": true
}
```

**预期响应 (不存在)**: HTTP 404 Not Found

---

## 2. 患者认证 API (`/api/v1/patients`)

### 2.1 患者登录

**接口**: `POST /api/v1/patients/login`

**描述**: 患者用户登录，返回JWT token和用户信息

**请求头**:
- `Content-Type: application/json`

**请求体**:
```json
{
  "username": "patient001",
  "password": "Patient123"
}
```

**验证规则**:
- 用户名：3-50个字符，只能包含字母、数字和下划线
- 密码：8-128个字符，必须包含字母和数字

**curl 测试命令**:
```bash
# 测试有效凭据
curl -X POST http://localhost:8080/api/v1/patients/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "patient001",
    "password": "Patient123"
  }'

# 测试无效凭据（错误密码）
curl -X POST http://localhost:8080/api/v1/patients/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "patient001",
    "password": "WrongPassword123"
  }'

# 测试无效格式（用户名太短）
curl -X POST http://localhost:8080/api/v1/patients/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "ab",
    "password": "Patient123"
  }'

# 测试无效格式（密码缺少数字）
curl -X POST http://localhost:8080/api/v1/patients/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "patient001",
    "password": "OnlyLetters"
  }'
```

**预期响应 (成功 - 200)**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 7200000,
    "user": {
      "id": 1,
      "username": "patient001",
      "email": "patient001@example.com",
      "phone": "13800138001"
    }
  }
}
```

**预期响应 (失败 - 401)**:
```json
{
  "code": 401,
  "message": "用户名或密码错误",
  "data": null
}
```

**预期响应 (失败 - 400)**:
```json
{
  "code": 400,
  "message": "验证失败",
  "data": null
}
```

---

### 2.2 获取当前用户信息

**接口**: `GET /api/v1/patients/me`

**描述**: 使用JWT token获取当前登录用户的详细信息

**请求头**:
- `Authorization: Bearer <jwt_token>`

**curl 测试命令**:
```bash
# 首先登录获取token
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/patients/login \
  -H "Content-Type: application/json" \
  -d '{"username":"patient001","password":"Patient123"}' | \
  grep -o '"token":"[^"]*"' | cut -d'"' -f4)

# 使用token获取用户信息
curl -X GET http://localhost:8080/api/v1/patients/me \
  -H "Authorization: Bearer $TOKEN"

# 测试无效token
curl -X GET http://localhost:8080/api/v1/patients/me \
  -H "Authorization: Bearer invalid_token_here"

# 测试缺少token
curl -X GET http://localhost:8080/api/v1/patients/me
```

**预期响应 (成功 - 200)**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "patient001",
    "email": "patient001@example.com",
    "phone": "13800138001"
  }
}
```

**预期响应 (失败 - 401)**:
```json
{
  "code": 401,
  "message": "无效的Token",
  "data": null
}
```

---

## 3. 测试 API (`/api/test`)

### 3.1 测试CORS配置 (GET)

**接口**: `GET /api/test/cors`

**描述**: 测试CORS配置是否正常工作

**curl 测试命令**:
```bash
curl -X GET http://localhost:8080/api/test/cors \
  -H "Content-Type: application/json"
```

**预期响应**:
```json
{
  "message": "CORS configuration is working!",
  "timestamp": 1699999999999,
  "service": "qa-service-user"
}
```

---

### 3.2 测试CORS配置 (POST)

**接口**: `POST /api/test/cors`

**描述**: 测试POST请求的CORS配置

**请求头**:
- `Content-Type: application/json`

**请求体** (可选):
```json
{
  "test": "data"
}
```

**curl 测试命令**:
```bash
# 不带数据
curl -X POST http://localhost:8080/api/test/cors \
  -H "Content-Type: application/json"

# 带数据
curl -X POST http://localhost:8080/api/test/cors \
  -H "Content-Type: application/json" \
  -d '{"test":"data","message":"Hello World"}'
```

**预期响应**:
```json
{
  "message": "POST request with CORS is working!",
  "receivedData": {
    "test": "data",
    "message": "Hello World"
  },
  "timestamp": 1699999999999,
  "service": "qa-service-user"
}
```

---

### 3.3 测试OPTIONS请求

**接口**: `OPTIONS /api/test/cors`

**描述**: 测试CORS预检请求

**curl 测试命令**:
```bash
curl -X OPTIONS http://localhost:8080/api/test/cors \
  -H "Origin: http://localhost:5173" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Content-Type" \
  -v
```

**预期响应**: 应该返回200状态码和适当的CORS头信息

---

## 4. Actuator 监控端点

### 4.1 健康检查

**接口**: `GET /actuator/health`

**描述**: 检查应用健康状态

**curl 测试命令**:
```bash
curl -X GET http://localhost:8080/actuator/health
```

**预期响应**:
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "MySQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 1000000000000,
        "free": 500000000000,
        "threshold": 10485760,
        "exists": true
      }
    }
  }
}
```

---

### 4.2 应用信息

**接口**: `GET /actuator/info`

**描述**: 获取应用基本信息

**curl 测试命令**:
```bash
curl -X GET http://localhost:8080/actuator/info
```

**预期响应**:
```json
{
  "app": {
    "name": "qa-service-user",
    "description": "QA Service User - Healthcare QA System User Management Service",
    "version": "0.0.1-SNAPSHOT",
    "encoding": "UTF-8",
    "java": {
      "version": "17"
    }
  },
  "team": "QA Healthcare Team",
  "environment": "development",
  "build": {
    "timestamp": "2025-11-03"
  },
  "features": "CORS,Actuator,Health Checks,User Management"
}
```

---

### 4.3 指标信息

**接口**: `GET /actuator/metrics`

**描述**: 获取应用性能指标

**curl 测试命令**:
```bash
# 获取所有可用指标
curl -X GET http://localhost:8080/actuator/metrics

# 获取特定指标（如JVM内存）
curl -X GET http://localhost:8080/actuator/metrics/jvm.memory.used
```

---

### 4.4 环境信息

**接口**: `GET /actuator/env`

**描述**: 获取应用环境变量和配置信息

**curl 测试命令**:
```bash
curl -X GET http://localhost:8080/actuator/env
```

---

## 5. 综合测试场景

### 5.1 完整登录流程测试

```bash
#!/bin/bash

echo "=== 完整登录流程测试 ==="

# 1. 登录获取token
echo "1. 正在登录..."
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/v1/patients/login \
  -H "Content-Type: application/json" \
  -d '{"username":"patient001","password":"Patient123"}')

echo "登录响应:"
echo $LOGIN_RESPONSE | python3 -m json.tool

# 2. 提取token
TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
  echo "❌ 登录失败，未获取到token"
  exit 1
fi

echo "✅ 登录成功，获取到token"

# 3. 使用token获取用户信息
echo ""
echo "2. 获取用户信息..."
USER_RESPONSE=$(curl -s -X GET http://localhost:8080/api/v1/patients/me \
  -H "Authorization: Bearer $TOKEN")

echo "用户信息响应:"
echo $USER_RESPONSE | python3 -m json.tool

# 4. 获取医生列表
echo ""
echo "3. 获取医生列表..."
DOCTORS_RESPONSE=$(curl -s -X GET http://localhost:8080/api/doctors)

echo "医生列表响应:"
echo $DOCTORS_RESPONSE | python3 -m json.tool

echo ""
echo "=== 测试完成 ==="
```

---

### 5.2 错误处理测试

```bash
#!/bin/bash

echo "=== 错误处理测试 ==="

# 1. 测试无效凭据
echo "1. 测试无效凭据..."
curl -s -X POST http://localhost:8080/api/v1/patients/login \
  -H "Content-Type: application/json" \
  -d '{"username":"patient001","password":"WrongPassword"}' | \
  python3 -m json.tool

# 2. 测试无效token
echo ""
echo "2. 测试无效token..."
curl -s -X GET http://localhost:8080/api/v1/patients/me \
  -H "Authorization: Bearer invalid_token" | \
  python3 -m json.tool

# 3. 测试不存在的医生
echo ""
echo "3. 测试不存在的医生..."
curl -s -X GET http://localhost:8080/api/doctors/nonexistent | \
  python3 -m json.tool

# 4. 测试验证失败
echo ""
echo "4. 测试验证失败（用户名太短）..."
curl -s -X POST http://localhost:8080/api/v1/patients/login \
  -H "Content-Type: application/json" \
  -d '{"username":"ab","password":"Patient123"}' | \
  python3 -m json.tool

echo ""
echo "=== 错误处理测试完成 ==="
```

---

## 6. 性能测试

### 6.1 并发请求测试

```bash
#!/bin/bash

echo "=== 并发请求测试 ==="

# 测试医生列表API的并发性能
echo "测试 GET /api/doctors 并发性能..."
for i in {1..10}; do
  curl -s -o /dev/null -w "请求 $i: %{time_total}s\n" \
    http://localhost:8080/api/doctors &
done

wait
echo "并发测试完成"

# 测试登录API的并发性能
echo ""
echo "测试 POST /api/v1/patients/login 并发性能..."
for i in {1..5}; do
  curl -s -o /dev/null -w "请求 $i: %{time_total}s\n" \
    -X POST http://localhost:8080/api/v1/patients/login \
    -H "Content-Type: application/json" \
    -d '{"username":"patient001","password":"Patient123"}' &
done

wait
echo "登录并发测试完成"
```

---

## 7. 测试数据准备

### 7.1 测试账号

| 用户名 | 密码 | 邮箱 | 手机号 | 说明 |
|-------|------|------|-------|------|
| patient001 | Patient123 | patient001@example.com | 13800138001 | 正常患者账号 |
| patient002 | Patient123 | patient002@example.com | 13800138002 | 正常患者账号 |
| patient003 | Patient123 | patient003@example.com | 13800138003 | 正常患者账号 |

### 7.2 测试医生数据

| ID | 用户名 | 姓名 | 职称 | 科室 | 状态 |
|----|--------|------|------|------|------|
| doc001 | dr-zhang-wei | 张伟医生 | 主任医师 | 心内科 | 在线 |
| doc002 | dr-li-na | 李娜医生 | 副主任医师 | 内分泌科 | 在线 |
| doc003 | dr-wang-fang | 王芳医生 | 主治医师 | 神经内科 | 在线 |
| doc004 | dr-liu-min | 刘敏医生 | 主任医师 | 消化内科 | 离线 |
| doc005 | dr-chen-jie | 陈杰医生 | 副主任医师 | 呼吸内科 | 在线 |

---

## 8. 常见问题排查

### 8.1 连接问题

```bash
# 检查服务是否运行
curl -I http://localhost:8080/actuator/health

# 检查端口是否监听
netstat -tulpn | grep 8080

# 检查Docker容器状态
docker-compose ps
```

### 8.2 数据库连接问题

```bash
# 检查MySQL容器状态
docker-compose ps mysql

# 查看应用日志
docker-compose logs qa-service-user

# 直接连接数据库测试
docker-compose exec mysql mysql -u qa_user -pqa_password123 qa_healthcare
```

### 8.3 JWT Token问题

```bash
# 解码JWT token（需要安装jq）
TOKEN="your_jwt_token_here"
echo $TOKEN | cut -d'.' -f2 | base64 -d 2>/dev/null | jq .

# 检查token是否过期
# JWT token有效期为2小时（7200000毫秒）
```

---

## 9. 自动化测试脚本

### 9.1 完整API测试套件

```bash
#!/bin/bash

# API测试套件
BASE_URL="http://localhost:8080"
PASS=0
FAIL=0

# 颜色输出
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

test_endpoint() {
  local name=$1
  local method=$2
  local url=$3
  local data=$4
  local expected_code=$5
  
  echo -n "测试: $name ... "
  
  if [ "$method" = "GET" ]; then
    response=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL$url")
  else
    response=$(curl -s -o /dev/null -w "%{http_code}" -X "$method" \
      -H "Content-Type: application/json" \
      -d "$data" "$BASE_URL$url")
  fi
  
  if [ "$response" = "$expected_code" ]; then
    echo -e "${GREEN}✅ PASS${NC} (HTTP $response)"
    ((PASS++))
  else
    echo -e "${RED}❌ FAIL${NC} (期望: $expected_code, 实际: $response)"
    ((FAIL++))
  fi
}

echo "=========================================="
echo "  QA Service User API 测试套件"
echo "=========================================="
echo ""

# 医生API测试
echo "📋 医生API测试"
test_endpoint "获取所有医生" "GET" "/api/doctors" "" "200"
test_endpoint "获取在线医生" "GET" "/api/doctors/active" "" "200"
test_endpoint "根据ID获取医生" "GET" "/api/doctors/doc001" "" "200"
test_endpoint "根据用户名获取医生" "GET" "/api/doctors/username/dr-zhang-wei" "" "200"
test_endpoint "获取不存在的医生" "GET" "/api/doctors/nonexistent" "" "404"
echo ""

# 患者API测试
echo "👤 患者API测试"
test_endpoint "患者登录-有效凭据" "POST" "/api/v1/patients/login" \
  '{"username":"patient001","password":"Patient123"}' "200"
test_endpoint "患者登录-无效凭据" "POST" "/api/v1/patients/login" \
  '{"username":"patient001","password":"WrongPassword"}' "401"
test_endpoint "患者登录-无效格式" "POST" "/api/v1/patients/login" \
  '{"username":"ab","password":"Patient123"}' "400"
echo ""

# 测试API
echo "🧪 测试API"
test_endpoint "CORS GET测试" "GET" "/api/test/cors" "" "200"
test_endpoint "CORS POST测试" "POST" "/api/test/cors" '{"test":"data"}' "200"
echo ""

# Actuator端点
echo "📊 Actuator端点"
test_endpoint "健康检查" "GET" "/actuator/health" "" "200"
test_endpoint "应用信息" "GET" "/actuator/info" "" "200"
test_endpoint "指标信息" "GET" "/actuator/metrics" "" "200"
echo ""

# 总结
echo "=========================================="
echo "  测试结果总结"
echo "=========================================="
echo -e "通过: ${GREEN}$PASS${NC}"
echo -e "失败: ${RED}$FAIL${NC}"
echo "总计: $((PASS + FAIL))"
echo ""

if [ $FAIL -eq 0 ]; then
  echo -e "${GREEN}🎉 所有测试通过！${NC}"
  exit 0
else
  echo -e "${RED}⚠️  有测试失败，请检查上述结果${NC}"
  exit 1
fi
```

---

## 10. 注意事项

1. **服务启动**: 确保 qa-service-user 服务已启动并运行在 8080 端口
2. **数据库连接**: 确保 MySQL 数据库已启动并可访问
3. **测试数据**: 确保数据库中已有测试数据（医生和患者用户）
4. **CORS配置**: API支持跨域请求，允许前端直接调用
5. **JWT Token**: Token有效期为2小时，过期后需要重新登录
6. **密码规则**: 密码必须包含字母和数字，长度8-128个字符
7. **用户名规则**: 用户名只能包含字母、数字和下划线，长度3-50个字符

---

## 11. 更新日志

| 日期 | 版本 | 说明 |
|------|------|------|
| 2026-05-10 | v1.0 | 初始版本，包含所有API端点的测试用例 |

---

**文档维护**: QA Healthcare Team  
**最后更新**: 2026-05-10