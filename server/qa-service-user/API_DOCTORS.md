# 医生列表 API 文档

## 概述
本文档描述了 qa-service-user 服务中提供的医生列表相关 API 接口。

## 基础信息
- **服务名称**: qa-service-user
- **基础URL**: http://localhost:8080
- **API前缀**: /api/doctors

## API 接口列表

### 1. 获取所有医生列表
**接口**: `GET /api/doctors`

**描述**: 返回数据库中所有医生的信息

**请求参数**: 无

**响应示例**:
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

**响应字段说明**:
- `id`: 医生ID
- `username`: 用户名（用于登录）
- `name`: 医生姓名
- `title`: 职称
- `department`: 科室
- `avatar`: 头像URL
- `experience`: 从业经验描述
- `specialties`: 专长列表
- `isActive`: 是否在线

---

### 2. 获取在线医生列表
**接口**: `GET /api/doctors/active`

**描述**: 返回所有在线状态的医生信息

**请求参数**: 无

**响应示例**: 与"获取所有医生列表"相同，但只包含 isActive=true 的医生

---

### 3. 根据ID获取医生信息
**接口**: `GET /api/doctors/{id}`

**描述**: 根据医生ID获取单个医生的详细信息

**路径参数**:
- `id`: 医生ID（例如：doc001）

**响应示例**:
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

**错误响应**:
- `404 Not Found`: 当指定的医生ID不存在时

---

### 4. 根据用户名获取医生信息
**接口**: `GET /api/doctors/username/{username}`

**描述**: 根据用户名获取单个医生的详细信息

**路径参数**:
- `username`: 医生用户名（例如：dr-zhang-wei）

**响应示例**: 与"根据ID获取医生信息"相同

**错误响应**:
- `404 Not Found`: 当指定的用户名不存在时

---

## 技术实现

### 数据库表结构
```sql
CREATE TABLE doctors (
    id VARCHAR(50) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    title VARCHAR(100) NOT NULL,
    department VARCHAR(100) NOT NULL,
    avatar VARCHAR(500),
    experience VARCHAR(200),
    specialties TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 项目结构
```
qa-service-user/
├── src/main/java/com/leansofx/qaserviceuser/
│   ├── controller/
│   │   └── DoctorController.java      # REST API控制器
│   ├── service/
│   │   └── DoctorService.java         # 业务逻辑层
│   ├── repository/
│   │   └── DoctorRepository.java      # 数据访问层
│   ├── entity/
│   │   └── Doctor.java                # JPA实体类
│   └── dto/
│       └── DoctorDTO.java             # 数据传输对象
└── src/main/resources/
    └── application.properties          # 应用配置
```

### 依赖项
- Spring Boot 3.5.7
- Spring Data JPA
- MySQL Connector/J
- Jackson (JSON处理)

## 测试示例

### 使用 curl 测试
```bash
# 获取所有医生
curl http://localhost:8080/api/doctors

# 获取在线医生
curl http://localhost:8080/api/doctors/active

# 根据ID获取医生
curl http://localhost:8080/api/doctors/doc001

# 根据用户名获取医生
curl http://localhost:8080/api/doctors/username/dr-zhang-wei
```

### 前端集成示例
在 Vue.js 组件中使用：
```javascript
// 获取医生列表
const response = await fetch('http://localhost:8080/api/doctors');
const doctors = await response.json();

// 获取在线医生
const response = await fetch('http://localhost:8080/api/doctors/active');
const activeDoctors = await response.json();
```

## 注意事项
1. 确保 Docker MySQL 容器正在运行
2. 确保 qa-service-user 服务已启动并监听 8080 端口
3. API 支持跨域请求（CORS），允许前端直接调用
4. specialties 字段在数据库中存储为 JSON 字符串，API 返回时会自动解析为数组
