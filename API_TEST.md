# QA Service User API 测试文档

## 概述

本文档提供了 qa-service-user 服务中所有 API 的测试说明，包括医生相关的 API 测试用例。

## 服务信息

- **服务名称**: qa-service-user
- **服务端口**: 8081
- **基础 URL**: http://localhost:8081
- **API 前缀**: /api/doctors

## API 列表

### 1. 获取所有医生列表

**接口地址**: `GET /api/doctors`

**描述**: 获取系统中所有医生的信息列表

**请求参数**: 无

**响应格式**:
```json
[
  {
    "id": 1,
    "username": "dr_zhang",
    "name": "张医生",
    "title": "主任医师",
    "department": "心血管内科",
    "experience": "从事心血管疾病诊疗工作15年，擅长高血压、冠心病等疾病的诊治",
    "specialties": "[\"高血压\", \"冠心病\", \"心律失常\"]",
    "avatar": "https://images.pexels.com/photos/3771090/pexels-photo-3771090.jpeg?auto=compress&cs=tinysrgb&w=600",
    "createdAt": "2026-03-25 15:00:00",
    "updatedAt": "2026-03-25 15:00:00"
  }
]
```

**测试命令**:
```bash
curl -X GET "http://localhost:8081/api/doctors" \
  -H "Content-Type: application/json"
```

### 2. 根据科室获取医生列表

**接口地址**: `GET /api/doctors/department/{department}`

**描述**: 根据指定的科室名称获取该科室的所有医生

**路径参数**:
- `department`: 科室名称（如：心血管内科、呼吸内科等）

**响应格式**: 同上

**测试命令**:
```bash
curl -X GET "http://localhost:8081/api/doctors/department/心血管内科" \
  -H "Content-Type: application/json"
```

### 3. 按医生姓名搜索

**接口地址**: `GET /api/doctors/search?name={name}`

**描述**: 根据医生姓名关键字搜索医生

**查询参数**:
- `name`: 医生姓名关键字

**响应格式**: 同上

**测试命令**:
```bash
curl -X GET "http://localhost:8081/api/doctors/search?name=张" \
  -H "Content-Type: application/json"
```

### 4. 根据用户名获取医生详情

**接口地址**: `GET /api/doctors/{username}`

**描述**: 根据医生用户名获取医生的详细信息

**路径参数**:
- `username`: 医生用户名

**响应格式**:
```json
{
  "id": 1,
  "username": "dr_zhang",
  "name": "张医生",
  "title": "主任医师",
  "department": "心血管内科",
  "experience": "从事心血管疾病诊疗工作15年，擅长高血压、冠心病等疾病的诊治",
  "specialties": "[\"高血压\", \"冠心病\", \"心律失常\"]",
  "avatar": "https://images.pexels.com/photos/3771090/pexels-photo-3771090.jpeg?auto=compress&cs=tinysrgb&w=600",
  "createdAt": "2026-03-25 15:00:00",
  "updatedAt": "2026-03-25 15:00:00"
}
```

**测试命令**:
```bash
curl -X GET "http://localhost:8081/api/doctors/dr_zhang" \
  -H "Content-Type: application/json"
```

### 5. 创建新医生

**接口地址**: `POST /api/doctors`

**描述**: 创建新的医生信息

**请求体**:
```json
{
  "username": "dr_new",
  "name": "新医生",
  "title": "主治医师",
  "department": "内科",
  "experience": "新医生介绍",
  "specialties": "[\"内科疾病\"]",
  "avatar": "https://example.com/avatar.jpg"
}
```

**响应格式**: 返回创建的医生信息

**测试命令**:
```bash
curl -X POST "http://localhost:8081/api/doctors" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "dr_new",
    "name": "新医生",
    "title": "主治医师",
    "department": "内科",
    "experience": "新医生介绍",
    "specialties": "[\"内科疾病\"]",
    "avatar": "https://example.com/avatar.jpg"
  }'
```

### 6. 删除医生

**接口地址**: `DELETE /api/doctors/{id}`

**描述**: 根据医生ID删除医生信息

**路径参数**:
- `id`: 医生ID

**响应**: HTTP 204 No Content

**测试命令**:
```bash
curl -X DELETE "http://localhost:8081/api/doctors/1" \
  -H "Content-Type: application/json"
```

## 测试脚本

### 医生列表 API 测试脚本

创建文件 `test-doctors-api.sh`:

```bash
#!/bin/bash

# QA Service User API 测试脚本
# 测试医生列表相关的 API

BASE_URL="http://localhost:8081/api/doctors"

echo "=== QA Service User API 测试 ==="
echo "基础 URL: $BASE_URL"
echo ""

# 测试 1: 获取所有医生列表
echo "测试 1: 获取所有医生列表"
echo "请求: GET $BASE_URL"
response=$(curl -s -w "%{http_code}" -X GET "$BASE_URL" -H "Content-Type: application/json")
http_code="${response: -3}"
body="${response%???}"

echo "状态码: $http_code"
echo "响应内容:"
echo "$body" | jq '.' 2>/dev/null || echo "$body"
echo ""

# 测试 2: 根据科室获取医生
echo "测试 2: 根据科室获取医生（心血管内科）"
echo "请求: GET $BASE_URL/department/心血管内科"
response=$(curl -s -w "%{http_code}" -X GET "$BASE_URL/department/心血管内科" -H "Content-Type: application/json")
http_code="${response: -3}"
body="${response%???}"

echo "状态码: $http_code"
echo "响应内容:"
echo "$body" | jq '.' 2>/dev/null || echo "$body"
echo ""

# 测试 3: 按姓名搜索医生
echo "测试 3: 按姓名搜索医生（关键字：张）"
echo "请求: GET $BASE_URL/search?name=张"
response=$(curl -s -w "%{http_code}" -X GET "$BASE_URL/search?name=张" -H "Content-Type: application/json")
http_code="${response: -3}"
body="${response%???}"

echo "状态码: $http_code"
echo "响应内容:"
echo "$body" | jq '.' 2>/dev/null || echo "$body"
echo ""

# 测试 4: 获取特定医生详情
echo "测试 4: 获取特定医生详情（用户名：dr_zhang）"
echo "请求: GET $BASE_URL/dr_zhang"
response=$(curl -s -w "%{http_code}" -X GET "$BASE_URL/dr_zhang" -H "Content-Type: application/json")
http_code="${response: -3}"
body="${response%???}"

echo "状态码: $http_code"
echo "响应内容:"
echo "$body" | jq '.' 2>/dev/null || echo "$body"
echo ""

echo "=== 测试完成 ==="
```

### 运行测试

1. 确保数据库和后端服务已启动
2. 给脚本执行权限：
   ```bash
   chmod +x test-doctors-api.sh
   ```
3. 运行测试脚本：
   ```bash
   ./test-doctors-api.sh
   ```

## 前置条件

### 1. 启动 MySQL 数据库和 phpMyAdmin

```bash
cd qa-live-healthcare-interview
docker-compose up -d
```

### 2. 启动后端服务

```bash
cd qa-live-healthcare-interview/server/qa-service-user
mvn spring-boot:run
```

### 3. 验证服务状态

```bash
# 检查服务是否启动
curl -I http://localhost:8081/actuator/health
```

## 预期结果

- 所有 GET 请求应返回 HTTP 200 状态码
- 响应内容应包含正确的医生信息
- 搜索功能应正确过滤结果
- 数据库连接正常，能正确读取初始化数据

## 注意事项

1. 确保 MySQL 数据库已正确初始化
2. 后端服务端口 8081 未被占用
3. 网络连接正常
4. 如需格式化 JSON 输出，请安装 `jq` 工具