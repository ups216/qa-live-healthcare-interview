# qa-service-user API 文档

本文档说明 `qa-service-user` 服务中提供的 HTTP API 及其测试方式。

## 基本信息

- 服务地址：`http://localhost:8080`
- 所有接口均为 REST 风格 JSON API。

## 1. 健康检查与调试接口

### 1.1 CORS 测试接口

- **URL**：`GET /api/test/cors`
- **说明**：用于验证服务是否启动以及 CORS 配置是否正确。
- **请求示例**：

```bash
curl -i http://localhost:8080/api/test/cors
```

- **成功响应示例**：

```json
{
  "message": "CORS configuration is working!",
  "timestamp": 1730600000000,
  "service": "qa-service-user"
}
```

### 1.2 CORS POST 测试接口

- **URL**：`POST /api/test/cors`
- **说明**：用于验证前端 POST 请求与 CORS 配置。
- **请求示例**：

```bash
curl -i -X POST http://localhost:8080/api/test/cors \
  -H "Content-Type: application/json" \
  -d '{"hello":"world"}'
```

## 2. 医生相关 API

### 2.1 获取医生列表

- **URL**：`GET /api/doctors`
- **说明**：为前端“医生列表页面”和首页“开放诊室”提供医生数据，数据来源为 MySQL `healthcare.doctors` 表。
- **请求示例**：

```bash
curl -i http://localhost:8080/api/doctors
```

- **响应字段说明**（单个医生对象）：

| 字段名      | 类型      | 说明                         |
|-----------|---------|----------------------------|
| `id`      | string  | 医生 ID，如 `doc001`          |
| `username`| string  | 登录用户名                    |
| `password`| string  | 登录密码（演示环境为明文）          |
| `name`    | string  | 医生姓名                      |
| `title`   | string  | 职称                         |
| `department` | string | 科室                         |
| `avatar`  | string  | 头像 URL                    |
| `experience` | string | 临床经验描述                    |
| `specialties` | array(string) | 擅长领域列表（从 JSON 字段解析） |
| `active`  | boolean | 是否在线/在职，对应表中 `is_active` |

- **成功响应示例**：

```json
[
  {
    "id": "doc001",
    "username": "dr-zhang-wei",
    "password": "123456",
    "name": "张伟医生",
    "title": "主任医师",
    "department": "心内科",
    "avatar": "https://images.pexels.com/...",
    "experience": "15年临床经验",
    "specialties": ["高血压", "冠心病", "心律失常"],
    "active": true
  }
]
```

## 3. 测试建议

1. 启动数据库与初始化数据：

```bash
docker compose up -d
```

2. 启动 `qa-service-user`：

```bash
cd server/qa-service-user
mvn spring-boot:run
```

3. 使用 `curl` / Postman 先调用 `/api/test/cors`，确认服务可用，然后调用 `/api/doctors`，确认医生数据返回正常。

更多关于医生列表 API 的 `curl` 示例与说明，可参考项目根目录的 `API_TEST.md` 文件。

