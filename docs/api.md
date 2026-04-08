# QA Service User API 文档

## 概述

本文档描述 qa-service-user 服务提供的所有 API 接口。

**基础 URL**: `http://localhost:8080/api`

**响应格式**: 所有 API 返回 JSON 格式，包含以下字段：
- `code`: 状态码 (200 成功, 401 未授权, 404 未找到)
- `message`: 响应消息
- `data`: 响应数据

## 跨域支持

API 支持跨域请求，允许来自以下源的请求：
- `http://localhost:5173`
- `http://127.0.0.1:5173`

## API 列表

### 1. 获取所有医生列表

获取系统中所有医生的信息。

**请求**
```
GET /doctors?lang={lang}
```

**参数**
| 参数 | 类型 | 必填 | 说明 |
|-----|------|-----|------|
| lang | string | 否 | 语言，可选值 `zh` 或 `en`，默认 `zh` |

**响应示例**
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
      "experience": "15年临床经验",
      "specialties": ["高血压", "冠心病", "心律失常"],
      "avatar": "https://images.pexels.com/...",
      "isActive": true
    }
  ]
}
```

---

### 2. 获取在线医生列表

获取当前在线的医生列表。

**请求**
```
GET /doctors/active?lang={lang}
```

**参数**
| 参数 | 类型 | 必填 | 说明 |
|-----|------|-----|------|
| lang | string | 否 | 语言，可选值 `zh` 或 `en`，默认 `zh` |

**响应示例**
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
      "isActive": true
    }
  ]
}
```

---

### 3. 获取医生详情

根据医生 ID 获取详细信息。

**请求**
```
GET /doctors/{id}?lang={lang}
```

**参数**
| 参数 | 类型 | 必填 | 说明 |
|-----|------|-----|------|
| id | string | 是 | 医生ID，如 `doc001` |
| lang | string | 否 | 语言，可选值 `zh` 或 `en`，默认 `zh` |

**响应示例 - 成功**
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
    "experience": "15年临床经验",
    "specialties": ["高血压", "冠心病", "心律失常"],
    "avatar": "https://images.pexels.com/...",
    "isActive": true
  }
}
```

**响应示例 - 医生不存在**
```json
{
  "code": 404,
  "message": "Doctor not found"
}
```

---

### 4. 根据用户名获取医生

根据用户名查询医生信息。

**请求**
```
GET /doctors/username/{username}?lang={lang}
```

**参数**
| 参数 | 类型 | 必填 | 说明 |
|-----|------|-----|------|
| username | string | 是 | 用户名，如 `dr-zhang-wei` |
| lang | string | 否 | 语言，可选值 `zh` 或 `en`，默认 `zh` |

---

### 5. 医生登录

医生使用用户名和密码登录。

**请求**
```
POST /doctors/login?lang={lang}
Content-Type: application/json

{
  "username": "dr-zhang-wei",
  "password": "123456"
}
```

**参数**
| 参数 | 类型 | 必填 | 说明 |
|-----|------|-----|------|
| username | string | 是 | 用户名 |
| password | string | 是 | 密码 |
| lang | string | 否 | 语言，可选值 `zh` 或 `en`，默认 `zh` |

**响应示例 - 成功**
```json
{
  "code": 200,
  "message": "Login successful",
  "data": {
    "id": "doc001",
    "username": "dr-zhang-wei",
    "name": "张伟医生",
    "title": "主任医师",
    "department": "心内科",
    "isActive": true
  }
}
```

**响应示例 - 失败**
```json
{
  "code": 401,
  "message": "Invalid username or password"
}
```

---

## 数据模型

### Doctor (医生)

| 字段 | 类型 | 说明 |
|-----|------|------|
| id | string | 医生唯一标识 |
| username | string | 登录用户名 |
| name | string | 医生姓名 |
| title | string | 职称（主任医师/副主任医师等）|
| department | string | 所属科室 |
| experience | string | 工作经验描述 |
| specialties | array | 擅长领域列表 |
| avatar | string | 头像图片 URL |
| isActive | boolean | 是否在线 |

---

## 测试方法

### 使用 curl

```bash
# 获取在线医生列表（中文）
curl http://localhost:8080/api/doctors/active?lang=zh

# 获取在线医生列表（英文）
curl http://localhost:8080/api/doctors/active?lang=en

# 医生登录
curl -X POST http://localhost:8080/api/doctors/login \
  -H "Content-Type: application/json" \
  -d '{"username":"dr-zhang-wei","password":"123456"}'
```

### 使用测试脚本

项目提供了自动化测试脚本：

```bash
# PowerShell 脚本
powershell -ExecutionPolicy Bypass -File test-api.ps1

# Batch 脚本
./test-api.bat
```

详细测试说明请参考 [API_TEST.md](../API_TEST.md)

---

## 健康检查

API 服务提供健康检查端点：

```
GET http://localhost:8080/actuator/health
```

返回 `{"status":"UP"}` 表示服务正常运行。
