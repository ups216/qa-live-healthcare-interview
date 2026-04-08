# QA Service User API 文档

本文档描述了 qa-service-user 服务的所有 API 接口。

## 基础信息

- **服务地址:** `http://localhost:8080`
- **基础路径:** `/api`
- **CORS 配置:** 允许所有来源访问 (`*`)

---

## 医生管理接口

### 1. 获取所有医生

获取数据库中所有医生的完整列表。

- **URL:** `/api/doctors`
- **方法:** `GET`
- **Content-Type:** `application/json`

#### 请求参数

无

#### 响应示例

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

#### 响应字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| id | string | 医生唯一标识符 |
| username | string | 医生登录用户名 |
| name | string | 医生姓名 |
| title | string | 医生职称 |
| department | string | 所属科室 |
| avatar | string | 头像图片 URL |
| experience | string | 临床经验描述 |
| specialties | array | 专长列表 |
| isActive | boolean | 是否在线 |

#### HTTP 状态码

| 状态码 | 说明 |
|--------|------|
| 200 | 成功返回医生列表 |
| 500 | 服务器内部错误 |

---

### 2. 获取在线医生

仅获取当前在线（活跃）的医生列表。

- **URL:** `/api/doctors/active`
- **方法:** `GET`
- **Content-Type:** `application/json`

#### 请求参数

无

#### 响应示例

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

#### 响应字段说明

与 `/api/doctors` 接口相同。

#### HTTP 状态码

| 状态码 | 说明 |
|--------|------|
| 200 | 成功返回在线医生列表 |
| 500 | 服务器内部错误 |

---

## 测试接口

### 3. CORS 测试 (GET)

用于测试 CORS 配置是否正确。

- **URL:** `/api/test/cors`
- **方法:** `GET`
- **Content-Type:** `application/json`

#### 请求参数

无

#### 响应示例

```json
{
  "message": "CORS configuration is working!",
  "timestamp": 1707654321000,
  "service": "qa-service-user"
}
```

#### HTTP 状态码

| 状态码 | 说明 |
|--------|------|
| 200 | CORS 配置正常 |

---

### 4. CORS 测试 (POST)

用于测试 POST 请求的 CORS 配置。

- **URL:** `/api/test/cors`
- **方法:** `POST`
- **Content-Type:** `application/json`

#### 请求参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| - | object | 否 | 任意 JSON 数据 |

#### 请求示例

```json
{
  "test": "data",
  "value": 123
}
```

#### 响应示例

```json
{
  "message": "POST request with CORS is working!",
  "receivedData": {
    "test": "data",
    "value": 123
  },
  "timestamp": 1707654321000,
  "service": "qa-service-user"
}
```

#### HTTP 状态码

| 状态码 | 说明 |
|--------|------|
| 200 | POST 请求成功 |

---

## 测试脚本

项目根目录提供了自动化测试脚本 [`test-api.sh`](/data/workspace/work/qa-live-healthcare-interview/test-api.sh)：

```bash
# 运行 API 测试
./test-api.sh
```

该脚本会测试以下内容：
1. 获取所有医生接口
2. 获取在线医生接口
3. CORS 头信息验证
4. 响应结构验证

---

## 错误处理

所有 API 在发生错误时会返回适当的 HTTP 状态码：

| 状态码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 404 | 资源未找到 |
| 500 | 服务器内部错误 |

---

## 相关文件

- **控制器:** `server/qa-service-user/src/main/java/com/leansofx/qaserviceuser/controller/DoctorController.java`
- **实体类:** `server/qa-service-user/src/main/java/com/leansofx/qaserviceuser/entity/Doctor.java`
- **DTO:** `server/qa-service-user/src/main/java/com/leansofx/qaserviceuser/dto/DoctorDTO.java`
- **仓库:** `server/qa-service-user/src/main/java/com/leansofx/qaserviceuser/repository/DoctorRepository.java`
- **配置:** `server/qa-service-user/src/main/resources/application.properties`
