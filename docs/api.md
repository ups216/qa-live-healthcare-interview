# qa-service-user API 说明

本文档描述 qa-service-user 服务中的**全部** API，包括请求方法、路径、响应格式及测试说明。API 测试执行方式见项目根目录 [API_TEST.md](../API_TEST.md)。

---

## 基础信息

- **服务名**：qa-service-user
- **默认端口**：8080
- **基础路径**：无统一前缀，REST 资源在 `/api` 下
- **数据格式**：请求/响应均为 JSON，`Content-Type: application/json`，字符集 UTF-8

---

## 1. 获取医生列表

### 1.1 接口说明

供前端医生页面（Doctors.vue）及首页统计/开放诊室使用，返回所有医生信息，**不包含 password**。

### 1.2 请求

| 项目 | 说明 |
|------|------|
| 方法 | GET |
| 路径 | `/api/doctors` |
| 请求参数 | 无 |
| 请求体 | 无 |

### 1.3 响应

| 项目 | 说明 |
|------|------|
| 状态码 | 200 OK |
| Content-Type | application/json（建议 **charset=UTF-8**，避免中文乱码） |
| 响应体 | JSON 数组，每项为医生对象 |

**医生对象字段：**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | string | 主键 |
| username | string | 登录名 |
| name | string | 显示姓名 |
| title | string | 职称 |
| department | string | 科室 |
| avatar | string | 头像 URL |
| experience | string | 经验描述 |
| specialties | string[] | 专长列表 |
| isActive | boolean | 是否在线 |

**不包含字段**：password（安全要求）。

### 1.4 响应示例

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

### 1.5 错误码

| HTTP 状态码 | 说明 |
|-------------|------|
| 200 | 成功 |
| 500 | 服务端异常（如数据库不可用） |

### 1.6 测试说明

使用 curl 或项目提供的脚本测试，详见 [API_TEST.md](../API_TEST.md)。

```bash
curl -s http://localhost:8080/api/doctors
```

---

## 2. CORS 测试接口（GET）

### 2.1 接口说明

用于验证跨域配置是否生效。

### 2.2 请求

| 项目 | 说明 |
|------|------|
| 方法 | GET |
| 路径 | `/api/test/cors` |
| 请求参数 | 无 |
| 请求体 | 无 |

### 2.3 响应

| 项目 | 说明 |
|------|------|
| 状态码 | 200 OK |
| 响应体 | JSON 对象，包含 message、timestamp、service 等 |

**示例：**

```json
{
  "message": "CORS configuration is working!",
  "timestamp": 1739320000000,
  "service": "qa-service-user"
}
```

### 2.4 测试说明

```bash
curl -s http://localhost:8080/api/test/cors
```

---

## 3. CORS 测试接口（POST）

### 3.1 接口说明

用于验证 POST 请求的跨域与请求体接收。

### 3.2 请求

| 项目 | 说明 |
|------|------|
| 方法 | POST |
| 路径 | `/api/test/cors` |
| Content-Type | application/json |
| 请求体 | 可选，任意 JSON 对象 |

### 3.3 响应

| 项目 | 说明 |
|------|------|
| 状态码 | 200 OK |
| 响应体 | JSON，包含 message、receivedData、timestamp、service |

**示例：**

```bash
curl -s -X POST -H "Content-Type: application/json" -d '{"key":"value"}' http://localhost:8080/api/test/cors
```

---

## 4. CORS 预检（OPTIONS）

### 4.1 接口说明

浏览器在跨域非简单请求前会发送 OPTIONS 预检请求，由 Spring Boot 与 CORS 配置自动处理。

### 4.2 请求

| 项目 | 说明 |
|------|------|
| 方法 | OPTIONS |
| 路径 | `/api/test/cors` |

### 4.3 响应

返回 200 及 CORS 相关响应头（如 Access-Control-Allow-Origin）。

---

## 5. 错误码与通用约定

| HTTP 状态码 | 说明 |
|-------------|------|
| 200 | 成功 |
| 500 | 服务端异常（如数据库不可用），响应体为 JSON：`{"error":"INTERNAL_ERROR","message":"服务暂时不可用，请稍后重试"}` |

- 所有 API 响应均使用 UTF-8 编码（医生列表等接口显式设置 `Content-Type: application/json;charset=UTF-8`），避免中文乱码。
- 列表类接口不返回 password 等敏感字段；使用 JPA 参数化查询，防止 SQL 注入。

*文档与实现保持一致；测试执行方式见根目录 API_TEST.md。*
