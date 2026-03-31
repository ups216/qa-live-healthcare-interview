# API 测试说明

## 测试脚本

本项目提供了两种测试脚本：

### 1. PowerShell 脚本 (推荐)

```bash
powershell -ExecutionPolicy Bypass -File test-api.ps1
```

**特点：**
- 彩色输出，便于查看测试结果
- 自动记录详细日志到 `test-api.log`
- 显示测试通过/失败统计

### 2. Batch 脚本

```bash
./test-api.bat
```

## 测试用例

| 测试编号 | 测试内容 | 方法 | URL | 预期结果 |
|---------|---------|------|-----|---------|
| 1 | 获取所有医生(中文) | GET | `/api/doctors?lang=zh` | 返回所有医生列表，中文信息 |
| 2 | 获取所有医生(英文) | GET | `/api/doctors?lang=en` | 返回所有医生列表，英文信息 |
| 3 | 获取在线医生(中文) | GET | `/api/doctors/active?lang=zh` | 返回在线医生，中文信息 |
| 4 | 获取在线医生(英文) | GET | `/api/doctors/active?lang=en` | 返回在线医生，英文信息 |
| 5 | 获取医生详情 | GET | `/api/doctors/doc001?lang=zh` | 返回指定医生详情 |
| 6 | 医生登录成功 | POST | `/api/doctors/login` | 用户名密码正确，返回医生信息 |
| 7 | 医生登录失败 | POST | `/api/doctors/login` | 用户名密码错误，返回 401 |

## 登录请求示例

**请求：**
```json
POST /api/doctors/login?lang=zh
Content-Type: application/json

{
  "username": "dr-zhang-wei",
  "password": "123456"
}
```

**成功响应：**
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
    "specialties": ["高血压", "冠心病", "心律失常"],
    "isActive": true
  }
}
```

**失败响应：**
```json
{
  "code": 401,
  "message": "Invalid username or password"
}
```

## 日志文件

测试详细日志保存在 `test-api.log` 文件中，包含：
- 每个测试的请求 URL 和方法
- 完整的请求和响应内容
- 错误信息（如有）

## 手动测试

也可以使用 curl 手动测试：

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
