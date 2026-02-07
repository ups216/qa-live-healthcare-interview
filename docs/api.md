# QA-Service-User API文档

本文档详细说明 qa-service-user 服务中的所有API接口。

## 基础信息

- **服务地址**：http://localhost:8081
- **API基础路径**：/api
- **请求格式**：JSON
- **响应格式**：JSON
- **跨域支持**：已启用（支持所有来源）

## 医生相关API

### 1. 获取所有医生

**接口**：GET /api/doctors

**功能**：获取系统中所有医生的列表

**请求参数**：无

**响应示例**：
```json
[
  {
    "id": "doc001",
    "username": "dr-zhang-wei",
    "name": "张伟医生",
    "title": "主任医师",
    "department": "心内科",
    "avatar": "https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg",
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
    "avatar": "https://images.pexels.com/photos/5327585/pexels-photo-5327585.jpeg",
    "experience": "10年临床经验",
    "specialties": ["儿童感冒", "儿童发育", "疫苗接种"],
    "isActive": true
  }
]
```

### 2. 获取在线医生

**接口**：GET /api/doctors/active

**功能**：获取当前在线的医生列表

**请求参数**：无

**响应示例**：
```json
[
  {
    "id": "doc001",
    "username": "dr-zhang-wei",
    "name": "张伟医生",
    "title": "主任医师",
    "department": "心内科",
    "avatar": "https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg",
    "experience": "15年临床经验",
    "specialties": ["高血压", "冠心病", "心律失常"],
    "isActive": true
  }
]
```

### 3. 根据ID获取医生

**接口**：GET /api/doctors/{id}

**功能**：根据医生ID获取详细信息

**请求参数**：
- `id`：医生ID（路径参数）

**响应示例**：
```json
{
  "id": "doc001",
  "username": "dr-zhang-wei",
  "name": "张伟医生",
  "title": "主任医师",
  "department": "心内科",
  "avatar": "https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg",
  "experience": "15年临床经验",
  "specialties": ["高血压", "冠心病", "心律失常"],
  "isActive": true
}
```

**错误响应**：
- 404 Not Found：当医生ID不存在时

### 4. 根据用户名获取医生

**接口**：GET /api/doctors/username/{username}

**功能**：根据医生用户名获取详细信息

**请求参数**：
- `username`：医生用户名（路径参数）

**响应示例**：
```json
{
  "id": "doc001",
  "username": "dr-zhang-wei",
  "name": "张伟医生",
  "title": "主任医师",
  "department": "心内科",
  "avatar": "https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg",
  "experience": "15年临床经验",
  "specialties": ["高血压", "冠心病", "心律失常"],
  "isActive": true
}
```

**错误响应**：
- 404 Not Found：当用户不存在时

### 5. 创建医生

**接口**：POST /api/doctors

**功能**：创建新的医生账户

**请求示例**：
```json
{
  "username": "dr-new-doctor",
  "name": "新医生",
  "title": "主治医师",
  "department": "外科",
  "avatar": "https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg",
  "experience": "5年临床经验",
  "specialties": ["普外科", "微创手术"],
  "isActive": true
}
```

**响应示例**：
```json
{
  "id": "doc006",
  "username": "dr-new-doctor",
  "name": "新医生",
  "title": "主治医师",
  "department": "外科",
  "avatar": "https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg",
  "experience": "5年临床经验",
  "specialties": ["普外科", "微创手术"],
  "isActive": true
}
```

### 6. 更新医生

**接口**：PUT /api/doctors/{id}

**功能**：更新医生信息

**请求参数**：
- `id`：医生ID（路径参数）

**请求示例**：
```json
{
  "username": "dr-zhang-wei",
  "name": "张伟医生",
  "title": "主任医师",
  "department": "心内科",
  "avatar": "https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg",
  "experience": "16年临床经验",
  "specialties": ["高血压", "冠心病", "心律失常", "心力衰竭"],
  "isActive": true
}
```

**响应示例**：
```json
{
  "id": "doc001",
  "username": "dr-zhang-wei",
  "name": "张伟医生",
  "title": "主任医师",
  "department": "心内科",
  "avatar": "https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg",
  "experience": "16年临床经验",
  "specialties": ["高血压", "冠心病", "心律失常", "心力衰竭"],
  "isActive": true
}
```

**错误响应**：
- 404 Not Found：当医生ID不存在时

### 7. 删除医生

**接口**：DELETE /api/doctors/{id}

**功能**：删除指定的医生

**请求参数**：
- `id`：医生ID（路径参数）

**响应**：
- 204 No Content：删除成功
- 404 Not Found：当医生ID不存在时

### 8. 初始化医生数据

**接口**：POST /api/doctors/init

**功能**：初始化默认医生数据

**请求参数**：无

**响应**：
- 200 OK：初始化成功

**说明**：
- 如果系统中已有医生数据，此接口不会执行任何操作
- 如果系统中没有医生数据，会创建5个默认医生账户

## 数据模型

### DoctorDTO

| 字段名 | 类型 | 描述 | 必需 |
|-------|------|------|------|
| id | String | 医生ID | 否（创建时自动生成） |
| username | String | 用户名 | 是 |
| name | String | 医生姓名 | 是 |
| title | String | 职称 | 是 |
| department | String | 科室 | 是 |
| avatar | String | 头像URL | 否 |
| experience | String | 临床经验 | 否 |
| specialties | Array<String> | 专长领域 | 否 |
| isActive | Boolean | 是否在线 | 否（默认false） |

## 错误处理

| 状态码 | 描述 | 示例 |
|-------|------|------|
| 200 | 成功 | 操作成功执行 |
| 201 | 创建成功 | 资源创建成功 |
| 204 | 无内容 | 操作成功但无返回内容 |
| 400 | 请求错误 | 请求参数无效 |
| 404 | 资源不存在 | 请求的资源未找到 |
| 500 | 服务器错误 | 服务器内部错误 |

## 测试建议

1. **首次使用**：先调用 `POST /api/doctors/init` 初始化医生数据
2. **功能测试**：使用 `GET /api/doctors` 验证数据是否正确加载
3. **联调测试**：确保前端可以正常调用这些API获取医生数据
4. **性能测试**：在大量数据情况下测试API响应时间

## 部署说明

1. **环境要求**：
   - Java 17+
   - Maven 3.8+
   - MySQL 8.0+

2. **部署步骤**：
   - 构建项目：`mvn clean package -DskipTests`
   - 启动服务：`java -jar target/qa-service-user-0.0.1-SNAPSHOT.jar`
   - 确保MySQL数据库已启动并可访问

3. **配置文件**：
   - 数据库配置：修改 `application.properties` 中的数据库连接信息
   - 端口配置：修改 `application.properties` 中的 `server.port`
