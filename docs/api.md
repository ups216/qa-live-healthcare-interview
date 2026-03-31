# qa-service-user API 文档

## 服务概述

- **服务名称**: qa-service-user
- **基础 URL**: `http://localhost:8080/api`
- **数据格式**: JSON
- **字符编码**: UTF-8

## 目录

- [医生管理 API](#医生管理-api)
  - [获取所有医生](#1-获取所有医生)
  - [获取活跃医生](#2-获取活跃医生)
  - [根据用户名获取医生](#3-根据用户名获取医生)
  - [根据 ID 获取医生](#4-根据-id-获取医生)
- [测试 API](#测试-api)
  - [CORS 测试 (GET)](#5-cors-测试-get)
  - [CORS 测试 (POST)](#6-cors-测试-post)
- [数据类型](#数据类型)
- [错误处理](#错误处理)

---

## 医生管理 API

### 1. 获取所有医生

获取系统中所有医生的列表。

**请求**

```http
GET /api/doctors?locale={locale}
```

**参数**

| 参数名 | 类型   | 必填 | 默认值   | 说明                     |
|--------|--------|------|----------|--------------------------|
| locale | string | 否   | zh-CN    | 语言设置: `zh-CN` / `en-US` |

**示例请求**

```bash
# 中文
curl -X GET "http://localhost:8080/api/doctors?locale=zh-CN"

# 英文
curl -X GET "http://localhost:8080/api/doctors?locale=en-US"
```

**响应示例 (zh-CN)**

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
  },
  {
    "id": "doc002",
    "username": "dr-li-na",
    "name": "李娜医生",
    "title": "副主任医师",
    "department": "儿科",
    "avatar": "https://images.pexels.com/photos/5327585/pexels-photo-5327585.jpeg?auto=compress&cs=tinysrgb&w=400",
    "experience": "10年临床经验",
    "specialties": ["儿童感冒", "儿童发育", "疫苗接种"],
    "isActive": true
  }
]
```

**响应示例 (en-US)**

```json
[
  {
    "id": "doc001",
    "username": "dr-zhang-wei",
    "name": "Dr. Zhang Wei",
    "title": "Chief Physician",
    "department": "Cardiology",
    "avatar": "https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400",
    "experience": "15 years clinical experience",
    "specialties": ["Hypertension", "Coronary Heart Disease", "Arrhythmia"],
    "isActive": true
  }
]
```

---

### 2. 获取活跃医生

获取当前在线/活跃的医生列表。

**请求**

```http
GET /api/doctors/active?locale={locale}
```

**参数**

| 参数名 | 类型   | 必填 | 默认值   | 说明                     |
|--------|--------|------|----------|--------------------------|
| locale | string | 否   | zh-CN    | 语言设置: `zh-CN` / `en-US` |

**示例请求**

```bash
# 中文
curl -X GET "http://localhost:8080/api/doctors/active?locale=zh-CN"

# 英文
curl -X GET "http://localhost:8080/api/doctors/active?locale=en-US"
```

**响应**

返回 `Doctor` 对象数组（仅包含 `isActive: true` 的医生）

---

### 3. 根据用户名获取医生

根据用户名获取单个医生的详细信息。

**请求**

```http
GET /api/doctors/{username}?locale={locale}
```

**路径参数**

| 参数名  | 类型   | 说明         |
|---------|--------|--------------|
| username | string | 医生用户名   |

**查询参数**

| 参数名 | 类型   | 必填 | 默认值   | 说明                     |
|--------|--------|------|----------|--------------------------|
| locale | string | 否   | zh-CN    | 语言设置: `zh-CN` / `en-US` |

**示例请求**

```bash
# 中文
curl -X GET "http://localhost:8080/api/doctors/dr-wang-qiang?locale=zh-CN"

# 英文
curl -X GET "http://localhost:8080/api/doctors/dr-wang-qiang?locale=en-US"
```

**成功响应 (200)**

```json
{
  "id": "doc003",
  "username": "dr-wang-qiang",
  "name": "王强医生",
  "title": "主治医师",
  "department": "骨科",
  "avatar": "https://images.pexels.com/photos/5452293/pexels-photo-5452293.jpeg?auto=compress&cs=tinysrgb&w=400",
  "experience": "8年临床经验",
  "specialties": ["骨折", "关节炎", "运动损伤"],
  "isActive": true
}
```

**失败响应 (404)**

```
HTTP/1.1 404 Not Found
```

---

### 4. 根据 ID 获取医生

根据医生 ID 获取单个医生的详细信息。

**请求**

```http
GET /api/doctors/id/{id}?locale={locale}
```

**路径参数**

| 参数名 | 类型   | 说明       |
|--------|--------|------------|
| id     | string | 医生 ID    |

**查询参数**

| 参数名 | 类型   | 必填 | 默认值   | 说明                     |
|--------|--------|------|----------|--------------------------|
| locale | string | 否   | zh-CN    | 语言设置: `zh-CN` / `en-US` |

**示例请求**

```bash
# 中文
curl -X GET "http://localhost:8080/api/doctors/id/doc001?locale=zh-CN"

# 英文
curl -X GET "http://localhost:8080/api/doctors/id/doc001?locale=en-US"
```

**成功响应 (200)**

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

**失败响应 (404)**

```
HTTP/1.1 404 Not Found
```

---

## 测试 API

### 5. CORS 测试 (GET)

测试跨域资源共享配置是否正常工作。

**请求**

```http
GET /api/test/cors
```

**示例请求**

```bash
curl -X GET "http://localhost:8080/api/test/cors"
```

**响应**

```json
{
  "message": "CORS configuration is working!",
  "timestamp": 1743432000000,
  "service": "qa-service-user"
}
```

---

### 6. CORS 测试 (POST)

测试 POST 请求的跨域资源共享配置。

**请求**

```http
POST /api/test/cors
Content-Type: application/json

{
  "key": "value"
}
```

**示例请求**

```bash
curl -X POST "http://localhost:8080/api/test/cors" \
  -H "Content-Type: application/json" \
  -d '{"key": "value", "name": "test"}'
```

**响应**

```json
{
  "message": "POST request with CORS is working!",
  "receivedData": {
    "key": "value",
    "name": "test"
  },
  "timestamp": 1743432000000,
  "service": "qa-service-user"
}
```

---

## 数据类型

### Doctor 对象

| 字段       | 类型     | 说明                     |
|------------|----------|--------------------------|
| id         | string   | 医生唯一标识符           |
| username   | string   | 医生用户名（用于登录）   |
| name       | string   | 医生姓名                 |
| title      | string   | 职称                     |
| department | string   | 所属科室                 |
| avatar     | string   | 头像 URL                 |
| experience | string   | 临床经验描述             |
| specialties| string[] | 专业领域数组             |
| isActive   | boolean  | 是否在线/活跃            |

### Doctor 原始数据参考

| id     | username       | name      | title    | department | isActive |
|--------|----------------|-----------|----------|------------|----------|
| doc001 | dr-zhang-wei  | 张伟医生  | 主任医师 | 心内科     | true     |
| doc002 | dr-li-na      | 李娜医生  | 副主任医师| 儿科       | true     |
| doc003 | dr-wang-qiang | 王强医生  | 主治医师 | 骨科       | true     |
| doc004 | dr-liu-min    | 刘敏医生  | 主任医师 | 妇产科     | false    |
| doc005 | dr-chen-jie   | 陈杰医生  | 副主任医师| 消化内科   | true     |

---

## 错误处理

### 常见 HTTP 状态码

| 状态码 | 说明                         |
|--------|------------------------------|
| 200    | 请求成功                     |
| 404    | 资源未找到                   |
| 500    | 服务器内部错误               |

### 错误响应

当请求的资源不存在时，返回空响应体：

```
HTTP/1.1 404 Not Found
Content-Length: 0
```

---

## 翻译功能

系统支持实时翻译功能，当 `locale` 参数设置为 `en-US` 时：

- **职称翻译**: 主任医师 → Chief Physician, 副主任医师 → Deputy Chief Physician, 主治医师 → Attending Physician
- **科室翻译**: 心内科 → Cardiology, 儿科 → Pediatrics, 骨科 → Orthopedics
- **经验翻译**: "15年临床经验" → "15 years clinical experience"
- **专长翻译**: "高血压" → "Hypertension", "冠心病" → "Coronary Heart Disease"

> 注意：翻译服务依赖外部 API (MyMemory)，如果翻译超时（5秒），系统会返回原始中文数据。
