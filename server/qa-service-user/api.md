# API 文档 — qa-service-user

> **服务名称**: qa-service-user  
> **Base URL**: `http://localhost:8080/api`  
> **协议**: HTTP/1.1  
> **数据格式**: JSON（`Content-Type: application/json`）  
> **字符编码**: UTF-8  
> **文档版本**: v1.0.0  
> **最后更新**: 2025-06

---

## 目录

- [通用约定](#通用约定)
  - [统一响应格式](#统一响应格式)
  - [错误码说明](#错误码说明)
- [Doctors — 医生模块](#doctors--医生模块)
  - [GET /doctors](#1-get-doctors--查询医生列表)
  - [GET /doctors/{id}](#2-get-doctorsid--按-id-查询医生详情)
  - [GET /doctors/u/{username}](#3-get-doctorsuusername--按-username-查询医生详情)

---

## 通用约定

### 统一响应格式

所有接口均返回如下 JSON 结构：

```json
{
  "code":    200,
  "message": "success",
  "data":    { ... }
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | `integer` | 业务状态码，与 HTTP 状态码保持一致 |
| `message` | `string` | 提示信息，成功时为 `"success"` |
| `data` | `any` | 业务数据，失败时为 `null` |

---

### 错误码说明

| HTTP 状态码 | `code` | 说明 |
|------------|--------|------|
| 200 | 200 | 请求成功 |
| 400 | 400 | 请求参数错误 |
| 404 | 404 | 资源不存在 |
| 500 | 500 | 服务器内部错误 |

错误响应示例：

```json
{
  "code": 404,
  "message": "医生不存在，id=doc999",
  "data": null
}
```

---

## Doctors — 医生模块

### Doctor 对象结构

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | `string` | 医生唯一标识，如 `doc001` |
| `username` | `string` | 登录用户名 / URL slug，如 `dr-zhang-wei` |
| `name` | `string` | 医生姓名 |
| `title` | `string` | 职称，如 `主任医师` |
| `department` | `string` | 科室，如 `心内科` |
| `avatar` | `string` | 头像图片 URL |
| `experience` | `string` | 从业经验描述，如 `15年临床经验` |
| `specialties` | `string[]` | 专长列表 |
| `isActive` | `boolean` | 是否在线：`true` 在线，`false` 离线 |
| `createdAt` | `string` | 创建时间，格式 `yyyy-MM-dd HH:mm:ss` |
| `updatedAt` | `string` | 更新时间，格式 `yyyy-MM-dd HH:mm:ss` |

> `password` 字段不在任何响应中返回。

---

### 1. GET /doctors — 查询医生列表

查询医生列表，支持按在线状态过滤。前端 **医生列表页（Doctors.vue）** 和首页开放诊室区块使用此接口。

#### 请求

```
GET /api/doctors
```

#### Query 参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `isActive` | `boolean` | 否 | `true` 只返回在线医生；`false` 只返回离线医生；不传则返回全部 |

#### 响应

**HTTP 200 — 成功**

`data` 为 Doctor 对象数组，在线医生排在前面，同状态内按 `id` 升序排列。

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
      "avatar": "https://...",
      "experience": "15年临床经验",
      "specialties": ["高血压", "冠心病", "心律失常"],
      "isActive": true,
      "createdAt": "2025-06-01 10:00:00",
      "updatedAt": "2025-06-01 10:00:00"
    }
  ]
}
```

**HTTP 400 — 参数错误**（`isActive` 传入非布尔值时）

```json
{
  "code": 400,
  "message": "参数 isActive 类型错误，应为 boolean",
  "data": null
}
```

#### curl 示例

```bash
# 查询全部医生
curl -s http://localhost:8080/api/doctors | python3 -m json.tool

# 仅查询在线医生
curl -s "http://localhost:8080/api/doctors?isActive=true" | python3 -m json.tool

# 仅查询离线医生
curl -s "http://localhost:8080/api/doctors?isActive=false" | python3 -m json.tool
```

---

### 2. GET /doctors/{id} — 按 ID 查询医生详情

根据医生 ID 查询单条医生详情。

#### 请求

```
GET /api/doctors/{id}
```

#### Path 参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `id` | `string` | 是 | 医生 ID，如 `doc001` |

#### 响应

**HTTP 200 — 成功**

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
    "avatar": "https://...",
    "experience": "15年临床经验",
    "specialties": ["高血压", "冠心病", "心律失常"],
    "isActive": true,
    "createdAt": "2025-06-01 10:00:00",
    "updatedAt": "2025-06-01 10:00:00"
  }
}
```

**HTTP 404 — 医生不存在**

```json
{
  "code": 404,
  "message": "医生不存在，id=doc999",
  "data": null
}
```

#### curl 示例

```bash
# 查询 doc001
curl -s http://localhost:8080/api/doctors/doc001 | python3 -m json.tool

# 查询不存在的 ID（期望 404）
curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/doctors/doc999
```

---

### 3. GET /doctors/u/{username} — 按 username 查询医生详情

根据医生 username（URL slug）查询单条医生详情。前端路由 `/consultation/:username` 进入诊室时使用此接口。

#### 请求

```
GET /api/doctors/u/{username}
```

#### Path 参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `username` | `string` | 是 | 医生用户名，如 `dr-zhang-wei` |

#### 响应

**HTTP 200 — 成功**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "doc002",
    "username": "dr-li-na",
    "name": "李娜医生",
    "title": "副主任医师",
    "department": "儿科",
    "avatar": "https://...",
    "experience": "10年临床经验",
    "specialties": ["儿童感冒", "儿童发育", "疫苗接种"],
    "isActive": true,
    "createdAt": "2025-06-01 10:00:00",
    "updatedAt": "2025-06-01 10:00:00"
  }
}
```

**HTTP 404 — 医生不存在**

```json
{
  "code": 404,
  "message": "医生不存在，username=dr-unknown",
  "data": null
}
```

#### curl 示例

```bash
# 按 username 查询
curl -s http://localhost:8080/api/doctors/u/dr-li-na | python3 -m json.tool

# 查询不存在的 username（期望 404）
curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/doctors/u/dr-unknown
```

---

## 附录：接口汇总

| Method | Path | 功能 | 前端使用场景 |
|--------|------|------|------------|
| GET | `/api/doctors` | 查询医生列表（可按在线状态过滤） | Doctors.vue 列表页、Home.vue 开放诊室 |
| GET | `/api/doctors/{id}` | 按 ID 查询医生详情 | 内部跳转、管理后台 |
| GET | `/api/doctors/u/{username}` | 按 username 查询医生详情 | `/consultation/:username` 进入诊室 |
