# API 测试文档

本文档描述了 QA Service User 后端的 API 测试脚本。

## 测试脚本

**文件:** `test-api.sh`

一个使用 `curl` 测试医生 API 接口的 bash 脚本。

## 前置条件

- Bash shell
- curl 命令行工具
- 后端服务运行在 `http://localhost:8080`
- MySQL 数据库运行并包含种子数据

## 使用方法

```bash
# 赋予脚本执行权限
chmod +x test-api.sh

# 运行测试脚本
./test-api.sh
```

## 测试用例

### 测试 1: 获取所有医生
- **接口:** `GET /api/doctors`
- **描述:** 从数据库获取所有医生信息
- **预期响应:** HTTP 200，返回医生对象 JSON 数组
- **验证:** 检查 HTTP 状态码并统计返回的医生数量

### 测试 2: 获取在线医生
- **接口:** `GET /api/doctors/active`
- **描述:** 仅获取在线（活跃）的医生
- **预期响应:** HTTP 200，返回在线医生对象 JSON 数组
- **验证:** 检查 HTTP 状态码并统计在线医生数量 (isActive=true)

### 测试 3: CORS 头信息验证
- **接口:** `OPTIONS /api/doctors`
- **描述:** 验证 CORS 头信息是否正确配置以支持前端访问
- **预期响应:** HTTP 200，包含 Access-Control-Allow-Origin 头
- **验证:** 检查 CORS 头信息是否存在

### 测试 4: 响应结构验证
- **接口:** `GET /api/doctors`
- **描述:** 验证响应包含所有必需字段
- **预期字段:**
  - `id` - 医生唯一标识符
  - `username` - 医生登录用户名
  - `name` - 医生显示名称
  - `title` - 医生职称 (如: 主任医师)
  - `department` - 科室名称
  - `avatar` - 头像图片 URL
  - `experience` - 临床经验描述
  - `specialties` - 专长字符串数组
  - `isActive` - 在线状态布尔值

## API 接口

| 方法 | 接口 | 描述 |
|--------|----------|-------------|
| GET | `/api/doctors` | 获取所有医生 |
| GET | `/api/doctors/active` | 仅获取在线医生 |

## 响应示例

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

## 故障排除

### 连接被拒绝
- 确保后端服务运行在 8080 端口
- 检查服务是否成功启动：`./mvnw spring-boot:run`

### 空响应
- 验证 MySQL 容器是否运行：`docker-compose ps`
- 检查数据库是否已填充数据
- 查看后端日志中的数据库连接错误

### CORS 错误
- 后端已配置为允许所有来源
- 如果从不同来源测试，请在 `application.properties` 中更新 CORS 设置

## 相关文件

- 后端控制器: `server/qa-service-user/src/main/java/com/leansofx/qaserviceuser/controller/DoctorController.java`
- 数据库结构: `server/qa-service-user/src/main/resources/db/init.sql`
- Docker Compose: `docker-compose.yml`
