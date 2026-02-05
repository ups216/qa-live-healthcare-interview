# qa-service-user API 说明

本文档对 **qa-service-user 服务中的所有 API** 进行说明，并给出测试方式（与 README 文档要求一致）。服务默认端口：**8080**。

---

## 1. 医生列表 API

### GET /api/doctors

获取医生列表，供前端医生页面（Doctors.vue）及首页开放诊室使用。

| 项目 | 说明 |
|------|------|
| **方法** | GET |
| **路径** | /api/doctors |
| **请求参数** | 无 |
| **响应** | 200 OK，Body 为 JSON 数组，每项为医生对象（不含 password） |

**响应示例**：

```json
[
  {
    "id": "doc001",
    "username": "dr-zhang-wei",
    "name": "张伟医生",
    "title": "主任医师",
    "department": "心内科",
    "avatar": "https://images.pexels.com/photos/5215024/...",
    "experience": "15年临床经验",
    "specialties": ["高血压", "冠心病", "心律失常"],
    "isActive": true
  }
]
```

**错误响应**：数据库或服务异常时由全局异常处理返回 **500**，Body 形如 `{"message": "错误描述"}`；前端可解析该字段用于展示「加载失败」等提示。

**测试方式**：见项目根目录 [API_TEST.md](../API_TEST.md)，或执行：
```bash
bash scripts/test-doctor-api.sh
```
手动测试：`curl -s http://localhost:8080/api/doctors`

---

## 2. CORS 测试接口

用于验证跨域配置，便于前后端分离联调。

### GET /api/test/cors

| 项目 | 说明 |
|------|------|
| **方法** | GET |
| **路径** | /api/test/cors |
| **响应** | 200 OK，JSON 含 message、timestamp、service |

**测试**：`curl -s http://localhost:8080/api/test/cors`

### POST /api/test/cors

| 项目 | 说明 |
|------|------|
| **方法** | POST |
| **路径** | /api/test/cors |
| **Body** | 可选 JSON |
| **响应** | 200 OK，JSON 含 message、receivedData、timestamp、service |

**测试**：`curl -s -X POST -H "Content-Type: application/json" -d '{}' http://localhost:8080/api/test/cors`

### OPTIONS /api/test/cors

预检请求，由 CORS 配置统一处理，无需单独实现。浏览器在跨域 GET/POST 前会自动发送 OPTIONS。

---

## 汇总表

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/doctors | 医生列表 |
| GET | /api/test/cors | CORS 测试（GET） |
| POST | /api/test/cors | CORS 测试（POST） |
| OPTIONS | /api/test/cors | CORS 预检（框架处理） |

以上接口的自动化测试脚本与执行说明见 [API_TEST.md](../API_TEST.md)。
