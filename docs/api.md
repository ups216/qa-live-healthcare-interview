# QA Service User API 文档

## 1. 概述

本文档描述了 qa-service-user 服务提供的所有 API 接口，主要用于医生相关的操作。

## 2. API 接口列表

### 2.1 医生管理接口

| API路径 | 方法 | 功能描述 |
|---------|------|----------|
| `/api/doctors` | GET | 获取所有医生列表 |
| `/api/doctors/active` | GET | 获取活跃的医生列表 |
| `/api/doctors/department/{department}` | GET | 根据部门获取医生列表 |
| `/api/doctors/{id}` | GET | 根据ID获取医生详情 |
| `/api/doctors/username/{username}` | GET | 根据用户名获取医生详情 |
| `/api/doctors` | POST | 创建新医生 |
| `/api/doctors/{id}` | PUT | 更新医生信息 |
| `/api/doctors/{id}` | DELETE | 删除医生 |

## 3. API 详细说明

### 3.1 获取所有医生列表

**请求信息**
- URL: `/api/doctors`
- 方法: `GET`
- 头信息: `Content-Type: application/json`

**响应示例**
```json
[
  {
    "id": "doc001",
    "username": "dr-zhang-wei",
    "password": "123456",
    "name": "张伟医生",
    "title": "主任医师",
    "department": "心内科",
    "avatar": "https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400",
    "experience": "15年临床经验",
    "specialties": ["高血压", "冠心病", "心律失常"],
    "active": true
  },
  // 其他医生信息...
]
```

### 3.2 获取活跃的医生列表

**请求信息**
- URL: `/api/doctors/active`
- 方法: `GET`
- 头信息: `Content-Type: application/json`

**响应示例**
```json
[
  {
    "id": "doc001",
    "username": "dr-zhang-wei",
    "password": "123456",
    "name": "张伟医生",
    "title": "主任医师",
    "department": "心内科",
    "avatar": "https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400",
    "experience": "15年临床经验",
    "specialties": ["高血压", "冠心病", "心律失常"],
    "active": true
  },
  // 其他活跃医生信息...
]
```

### 3.3 根据部门获取医生列表

**请求信息**
- URL: `/api/doctors/department/{department}`
- 方法: `GET`
- 头信息: `Content-Type: application/json`
- 路径参数: `department` - 部门名称

**响应示例**
```json
[
  {
    "id": "doc001",
    "username": "dr-zhang-wei",
    "password": "123456",
    "name": "张伟医生",
    "title": "主任医师",
    "department": "心内科",
    "avatar": "https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400",
    "experience": "15年临床经验",
    "specialties": ["高血压", "冠心病", "心律失常"],
    "active": true
  }
]
```

### 3.4 根据ID获取医生详情

**请求信息**
- URL: `/api/doctors/{id}`
- 方法: `GET`
- 头信息: `Content-Type: application/json`
- 路径参数: `id` - 医生ID

**响应示例**
```json
{
  "id": "doc001",
  "username": "dr-zhang-wei",
  "password": "123456",
  "name": "张伟医生",
  "title": "主任医师",
  "department": "心内科",
  "avatar": "https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400",
  "experience": "15年临床经验",
  "specialties": ["高血压", "冠心病", "心律失常"],
  "active": true
}
```

### 3.5 根据用户名获取医生详情

**请求信息**
- URL: `/api/doctors/username/{username}`
- 方法: `GET`
- 头信息: `Content-Type: application/json`
- 路径参数: `username` - 医生用户名

**响应示例**
```json
{
  "id": "doc001",
  "username": "dr-zhang-wei",
  "password": "123456",
  "name": "张伟医生",
  "title": "主任医师",
  "department": "心内科",
  "avatar": "https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400",
  "experience": "15年临床经验",
  "specialties": ["高血压", "冠心病", "心律失常"],
  "active": true
}
```

### 3.6 创建新医生

**请求信息**
- URL: `/api/doctors`
- 方法: `POST`
- 头信息: `Content-Type: application/json`
- 请求体:
```json
{
  "id": "doc006",
  "username": "dr-zhao-hua",
  "password": "123456",
  "name": "赵华医生",
  "title": "主治医师",
  "department": "神经内科",
  "avatar": "https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400",
  "experience": "10年临床经验",
  "specialties": ["头痛", "癫痫", "帕金森"],
  "active": true
}
```

**响应示例**
```json
{
  "id": "doc006",
  "username": "dr-zhao-hua",
  "password": "123456",
  "name": "赵华医生",
  "title": "主治医师",
  "department": "神经内科",
  "avatar": "https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400",
  "experience": "10年临床经验",
  "specialties": ["头痛", "癫痫", "帕金森"],
  "active": true
}
```

### 3.7 更新医生信息

**请求信息**
- URL: `/api/doctors/{id}`
- 方法: `PUT`
- 头信息: `Content-Type: application/json`
- 路径参数: `id` - 医生ID
- 请求体:
```json
{
  "username": "dr-zhang-wei-updated",
  "password": "654321",
  "name": "张伟医生(更新)",
  "title": "主任医师",
  "department": "心内科",
  "avatar": "https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400",
  "experience": "16年临床经验",
  "specialties": ["高血压", "冠心病", "心律失常", "心力衰竭"],
  "active": true
}
```

**响应示例**
```json
{
  "id": "doc001",
  "username": "dr-zhang-wei-updated",
  "password": "654321",
  "name": "张伟医生(更新)",
  "title": "主任医师",
  "department": "心内科",
  "avatar": "https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400",
  "experience": "16年临床经验",
  "specialties": ["高血压", "冠心病", "心律失常", "心力衰竭"],
  "active": true
}
```

### 3.8 删除医生

**请求信息**
- URL: `/api/doctors/{id}`
- 方法: `DELETE`
- 头信息: `Content-Type: application/json`
- 路径参数: `id` - 医生ID

**响应示例**
- 状态码: 204 No Content

## 4. 测试方法

### 4.1 环境准备

1. 启动 MySQL 数据库和 phpMyAdmin：
   ```bash
   docker-compose up -d
   ```

2. 构建并运行 qa-service-user 服务：
   ```bash
   cd server/qa-service-user
   ./mvnw clean package -DskipTests
   java -jar target/qa-service-user-0.0.1-SNAPSHOT.jar
   ```

### 4.2 执行测试

1. 使用 curl 命令测试 API：
   ```bash
   # 获取所有医生列表
   curl -X GET "http://localhost:8080/api/doctors" -H "Content-Type: application/json"

   # 获取活跃的医生列表
   curl -X GET "http://localhost:8080/api/doctors/active" -H "Content-Type: application/json"

   # 根据部门获取医生列表
   curl -X GET "http://localhost:8080/api/doctors/department/心内科" -H "Content-Type: application/json"

   # 根据ID获取医生详情
   curl -X GET "http://localhost:8080/api/doctors/doc001" -H "Content-Type: application/json"

   # 根据用户名获取医生详情
   curl -X GET "http://localhost:8080/api/doctors/username/dr-zhang-wei" -H "Content-Type: application/json"
   ```

2. 使用测试脚本测试：
   ```bash
   chmod +x docs/api-test.sh
   ./docs/api-test.sh
   ```

## 5. 注意事项

- 所有 API 请求均返回 JSON 格式的数据
- 成功的请求返回 HTTP 200 OK 状态码
- 创建新资源返回 HTTP 201 Created 状态码
- 删除资源返回 HTTP 204 No Content 状态码
- 资源不存在返回 HTTP 404 Not Found 状态码
- 服务器错误返回 HTTP 500 Internal Server Error 状态码
