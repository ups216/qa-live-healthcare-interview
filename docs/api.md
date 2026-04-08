# QA Service User API 文档

## 概述

本文档详细说明了 qa-service-user 服务中的所有 API 接口，包括医生管理相关的 API。

## 服务信息

- **服务名称**: qa-service-user
- **服务端口**: 8081
- **基础 URL**: http://localhost:8081
- **API 前缀**: /api/doctors
- **数据库**: MySQL 8.0
- **框架**: Spring Boot 3.5.7

## API 列表

### 1. 获取所有医生列表

**接口地址**: `GET /api/doctors`

**描述**: 获取系统中所有医生的信息列表

**请求参数**: 无

**响应格式**:
```json
[
  {
    "id": 1,
    "username": "dr_zhang",
    "name": "张医生",
    "title": "主任医师",
    "department": "心血管内科",
    "experience": "从事心血管疾病诊疗工作15年，擅长高血压、冠心病等疾病的诊治",
    "specialties": "[\"高血压\", \"冠心病\", \"心律失常\"]",
    "avatar": "https://images.pexels.com/photos/3771090/pexels-photo-3771090.jpeg?auto=compress&cs=tinysrgb&w=600",
    "createdAt": "2026-03-25 15:00:00",
    "updatedAt": "2026-03-25 15:00:00"
  }
]
```

**状态码**:
- 200: 请求成功
- 500: 服务器内部错误

**使用场景**: 医生列表页面展示、医生搜索功能

### 2. 根据科室获取医生列表

**接口地址**: `GET /api/doctors/department/{department}`

**描述**: 根据指定的科室名称获取该科室的所有医生

**路径参数**:
- `department`: 科室名称（如：心血管内科、呼吸内科等）

**响应格式**: 同上

**状态码**:
- 200: 请求成功
- 404: 科室不存在
- 500: 服务器内部错误

**使用场景**: 按科室筛选医生、科室介绍页面

### 3. 按医生姓名搜索

**接口地址**: `GET /api/doctors/search?name={name}`

**描述**: 根据医生姓名关键字搜索医生

**查询参数**:
- `name`: 医生姓名关键字（必填）

**响应格式**: 同上

**状态码**:
- 200: 请求成功
- 400: 请求参数错误
- 500: 服务器内部错误

**使用场景**: 医生搜索功能、快速查找特定医生

### 4. 根据用户名获取医生详情

**接口地址**: `GET /api/doctors/{username}`

**描述**: 根据医生用户名获取医生的详细信息

**路径参数**:
- `username`: 医生用户名

**响应格式**:
```json
{
  "id": 1,
  "username": "dr_zhang",
  "name": "张医生",
  "title": "主任医师",
  "department": "心血管内科",
  "experience": "从事心血管疾病诊疗工作15年，擅长高血压、冠心病等疾病的诊治",
  "specialties": "[\"高血压\", \"冠心病\", \"心律失常\"]",
  "avatar": "https://images.pexels.com/photos/3771090/pexels-photo-3771090.jpeg?auto=compress&cs=tinysrgb&w=600",
  "createdAt": "2026-03-25 15:00:00",
  "updatedAt": "2026-03-25 15:00:00"
}
```

**状态码**:
- 200: 请求成功
- 404: 医生不存在
- 500: 服务器内部错误

**使用场景**: 医生详情页面、问诊前了解医生信息

### 5. 创建新医生

**接口地址**: `POST /api/doctors`

**描述**: 创建新的医生信息

**请求体**:
```json
{
  "username": "dr_new",
  "name": "新医生",
  "title": "主治医师",
  "department": "内科",
  "experience": "新医生介绍",
  "specialties": "[\"内科疾病\"]",
  "avatar": "https://example.com/avatar.jpg"
}
```

**字段说明**:
- `username`: 用户名（唯一，必填）
- `name`: 医生姓名（必填）
- `title`: 职称（必填）
- `department`: 科室（必填）
- `experience`: 经验介绍（可选）
- `specialties`: 专业领域（JSON格式字符串，必填）
- `avatar`: 头像URL（可选）

**响应格式**: 返回创建的医生信息

**状态码**:
- 201: 创建成功
- 400: 请求参数错误
- 409: 用户名已存在
- 500: 服务器内部错误

**使用场景**: 管理员添加新医生、医生注册

### 6. 删除医生

**接口地址**: `DELETE /api/doctors/{id}`

**描述**: 根据医生ID删除医生信息

**路径参数**:
- `id`: 医生ID

**响应**: HTTP 204 No Content

**状态码**:
- 204: 删除成功
- 404: 医生不存在
- 500: 服务器内部错误

**使用场景**: 管理员删除医生信息

## 数据库设计

### 医生表 (doctors)

| 字段名 | 类型 | 描述 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键ID | 自增，主键 |
| username | VARCHAR(50) | 用户名 | 非空，唯一 |
| name | VARCHAR(100) | 姓名 | 非空 |
| title | VARCHAR(100) | 职称 | 非空 |
| department | VARCHAR(100) | 科室 | 非空 |
| experience | TEXT | 经验介绍 | 可选 |
| specialties | JSON | 专业领域 | JSON格式 |
| avatar | VARCHAR(255) | 头像URL | 可选 |
| created_at | TIMESTAMP | 创建时间 | 默认当前时间 |
| updated_at | TIMESTAMP | 更新时间 | 默认当前时间，自动更新 |

## 技术栈

- **后端框架**: Spring Boot 3.5.7
- **数据库**: MySQL 8.0
- **ORM**: Spring Data JPA
- **API 文档**: 本文档
- **测试工具**: curl, bash script

## 部署说明

### 1. 环境要求

- Java 17+
- Maven 3.6+
- Docker 20.10+
- MySQL 8.0

### 2. 启动步骤

#### 启动数据库
```bash
cd qa-live-healthcare-interview
docker-compose up -d
```

#### 启动后端服务
```bash
cd qa-live-healthcare-interview/server/qa-service-user
mvn spring-boot:run
```

### 3. 验证部署

```bash
# 检查服务健康状态
curl http://localhost:8081/actuator/health

# 测试医生列表API
curl http://localhost:8081/api/doctors
```

## 测试说明

详细的 API 测试说明请参考 [API_TEST.md](../API_TEST.md) 文档。

### 测试脚本

运行测试脚本：
```bash
cd qa-live-healthcare-interview
chmod +x test-doctors-api.sh
./test-doctors-api.sh
```

### 测试覆盖

- ✅ 获取所有医生列表
- ✅ 根据科室筛选医生
- ✅ 按姓名搜索医生
- ✅ 获取医生详情
- ✅ 创建新医生
- ✅ 删除医生

## 错误处理

### 常见错误码

| 状态码 | 描述 | 解决方案 |
|--------|------|----------|
| 400 | 请求参数错误 | 检查请求参数格式和必填字段 |
| 404 | 资源不存在 | 检查路径参数是否正确 |
| 409 | 资源冲突 | 检查唯一约束（如用户名） |
| 500 | 服务器内部错误 | 检查服务日志，联系开发人员 |

### 错误响应格式

```json
{
  "timestamp": "2026-03-25T07:30:00.000+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Doctor not found",
  "path": "/api/doctors/invalid"
}
```

## 安全考虑

- API 支持跨域访问（CORS）
- 建议在生产环境中添加身份验证
- 敏感操作（创建、删除）需要权限控制
- 数据库连接使用安全配置

## 性能优化

- 使用 JPA 缓存减少数据库查询
- 合理设置数据库连接池大小
- 考虑添加分页功能（当前返回所有数据）
- 使用索引优化查询性能

## 版本历史

- v1.0.0 (2026-03-25): 初始版本，包含基础医生管理功能