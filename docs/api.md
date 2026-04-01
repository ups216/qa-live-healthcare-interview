# API 文档 - qa-service-user 服务

本文档详细说明 qa-service-user 服务中的所有 API 接口，包括请求格式、响应格式、参数说明和示例。

## 概述

qa-service-user 服务提供医生数据管理的 RESTful API 接口，支持医生信息的增删改查、搜索和分类功能。

### 基础信息

- **基础 URL**: `http://localhost:8081/api/doctors`
- **认证方式**: 无需认证（开发环境）
- **数据格式**: JSON
- **字符编码**: UTF-8

### 响应格式

所有 API 响应都遵循统一的格式：

```json
{
    "success": true,
    "message": "操作成功消息",
    "data": {}
}
```

或错误响应：

```json
{
    "success": false,
    "message": "错误描述信息",
    "errorCode": "ERROR_CODE"
}
```

## API 端点列表

### 1. 获取所有医生列表

获取系统中所有医生的详细信息。

**端点**: `GET /api/doctors`

**请求参数**: 无

**响应示例**:
```json
{
    "success": true,
    "message": "获取医生列表成功",
    "data": [
        {
            "id": "doc001",
            "username": "dr-zhang-wei",
            "name": "张伟医生",
            "title": "主任医师",
            "department": "心内科",
            "avatar": "https://example.com/avatar1.jpg",
            "experience": "15年临床经验",
            "isActive": true,
            "specialties": ["心脏病", "高血压", "心律失常"]
        },
        {
            "id": "doc002",
            "username": "dr-li-na",
            "name": "李娜医生",
            "title": "副主任医师",
            "department": "儿科",
            "avatar": "https://example.com/avatar2.jpg",
            "experience": "10年临床经验",
            "isActive": true,
            "specialties": ["儿科常见病", "儿童保健", "疫苗接种"]
        }
    ]
}
```

**curl 测试命令**:
```bash
curl -X GET "http://localhost:8081/api/doctors"
```

### 2. 获取活跃医生列表

获取当前处于活跃状态的医生列表。

**端点**: `GET /api/doctors/active`

**请求参数**: 无

**响应示例**:
```json
{
    "success": true,
    "message": "获取活跃医生列表成功",
    "data": [
        {
            "id": "doc001",
            "name": "张伟医生",
            "department": "心内科",
            "title": "主任医师",
            "isActive": true
        }
    ]
}
```

**curl 测试命令**:
```bash
curl -X GET "http://localhost:8081/api/doctors/active"
```

### 3. 根据ID获取医生详情

根据医生ID获取详细的医生信息。

**端点**: `GET /api/doctors/{id}`

**路径参数**:
- `id` (string, 必需) - 医生ID

**响应示例**:
```json
{
    "success": true,
    "message": "获取医生详情成功",
    "data": {
        "id": "doc001",
        "username": "dr-zhang-wei",
        "name": "张伟医生",
        "title": "主任医师",
        "department": "心内科",
        "avatar": "https://example.com/avatar1.jpg",
        "experience": "15年临床经验",
        "isActive": true,
        "createdAt": "2024-01-15T10:30:00Z",
        "updatedAt": "2024-03-30T14:20:00Z",
        "specialties": ["心脏病", "高血压", "心律失常"]
    }
}
```

**curl 测试命令**:
```bash
curl -X GET "http://localhost:8081/api/doctors/doc001"
```

### 4. 根据科室获取医生列表

根据科室名称筛选医生列表。

**端点**: `GET /api/doctors/department/{department}`

**路径参数**:
- `department` (string, 必需) - 科室名称

**响应示例**:
```json
{
    "success": true,
    "message": "获取心内科医生列表成功",
    "data": [
        {
            "id": "doc001",
            "name": "张伟医生",
            "title": "主任医师",
            "department": "心内科",
            "experience": "15年临床经验"
        },
        {
            "id": "doc003",
            "name": "王强医生",
            "title": "主治医师",
            "department": "心内科",
            "experience": "8年临床经验"
        }
    ]
}
```

**curl 测试命令**:
```bash
curl -X GET "http://localhost:8081/api/doctors/department/心内科"
```

### 5. 根据专业领域获取医生列表

根据专业领域筛选医生列表。

**端点**: `GET /api/doctors/specialty/{specialty}`

**路径参数**:
- `specialty` (string, 必需) - 专业领域名称

**响应示例**:
```json
{
    "success": true,
    "message": "获取心脏病专业医生列表成功",
    "data": [
        {
            "id": "doc001",
            "name": "张伟医生",
            "department": "心内科",
            "specialties": ["心脏病", "高血压", "心律失常"]
        }
    ]
}
```

**curl 测试命令**:
```bash
curl -X GET "http://localhost:8081/api/doctors/specialty/心脏病"
```

### 6. 搜索医生

根据多个条件搜索医生。

**端点**: `GET /api/doctors/search`

**查询参数**:
- `name` (string, 可选) - 医生姓名
- `department` (string, 可选) - 科室名称
- `specialty` (string, 可选) - 专业领域

**响应示例**:
```json
{
    "success": true,
    "message": "搜索医生成功",
    "data": [
        {
            "id": "doc001",
            "name": "张伟医生",
            "department": "心内科",
            "title": "主任医师",
            "specialties": ["心脏病", "高血压"]
        }
    ]
}
```

**curl 测试命令**:
```bash
# 按姓名搜索
curl -X GET "http://localhost:8081/api/doctors/search?name=张伟"

# 按科室搜索
curl -X GET "http://localhost:8081/api/doctors/search?department=心内科"

# 多条件搜索
curl -X GET "http://localhost:8081/api/doctors/search?name=张伟&department=心内科"
```

### 7. 获取所有科室列表

获取系统中所有不重复的科室名称列表。

**端点**: `GET /api/doctors/departments`

**请求参数**: 无

**响应示例**:
```json
{
    "success": true,
    "message": "获取科室列表成功",
    "data": ["心内科", "儿科", "骨科", "妇产科", "神经科"]
}
```

**curl 测试命令**:
```bash
curl -X GET "http://localhost:8081/api/doctors/departments"
```

### 8. 获取所有专业领域列表

获取系统中所有不重复的专业领域列表。

**端点**: `GET /api/doctors/specialties`

**请求参数**: 无

**响应示例**:
```json
{
    "success": true,
    "message": "获取专业领域列表成功",
    "data": ["心脏病", "高血压", "儿科常见病", "骨科手术", "妇产科手术"]
}
```

**curl 测试命令**:
```bash
curl -X GET "http://localhost:8081/api/doctors/specialties"
```

## 数据模型

### Doctor 医生模型

```json
{
    "id": "string",           // 医生ID，唯一标识
    "username": "string",      // 用户名，唯一
    "name": "string",         // 医生姓名
    "title": "string",        // 职称（主任医师、副主任医师等）
    "department": "string",   // 所属科室
    "avatar": "string",       // 头像URL
    "experience": "string",   // 临床经验描述
    "isActive": "boolean",    // 是否活跃
    "createdAt": "string",    // 创建时间（ISO格式）
    "updatedAt": "string",    // 更新时间（ISO格式）
    "specialties": ["string"] // 专业领域列表
}
```

### ApiResponse 响应模型

```json
{
    "success": "boolean",     // 操作是否成功
    "message": "string",      // 操作结果消息
    "data": "object"          // 响应数据（成功时）
}
```

### ErrorResponse 错误响应模型

```json
{
    "success": false,
    "message": "string",      // 错误描述
    "errorCode": "string"     // 错误代码（可选）
}
```

## 错误处理

### HTTP 状态码

| 状态码 | 说明 | 场景 |
|--------|------|------|
| 200 | 成功 | 请求处理成功 |
| 400 | 错误请求 | 参数验证失败 |
| 404 | 未找到 | 资源不存在 |
| 500 | 服务器错误 | 内部服务器错误 |

### 常见错误代码

| 错误代码 | 说明 | 解决方案 |
|----------|------|----------|
| DOCTOR_NOT_FOUND | 医生不存在 | 检查医生ID是否正确 |
| INVALID_PARAMETER | 参数无效 | 检查请求参数格式 |
| DATABASE_ERROR | 数据库错误 | 检查数据库连接 |

## 使用示例

### 前端调用示例（JavaScript）

```javascript
// 获取所有医生列表
async function getAllDoctors() {
    try {
        const response = await fetch('http://localhost:8081/api/doctors');
        const result = await response.json();
        
        if (result.success) {
            console.log('医生列表:', result.data);
            return result.data;
        } else {
            console.error('获取失败:', result.message);
            return [];
        }
    } catch (error) {
        console.error('请求错误:', error);
        return [];
    }
}

// 搜索医生
async function searchDoctors(name, department) {
    const params = new URLSearchParams();
    if (name) params.append('name', name);
    if (department) params.append('department', department);
    
    try {
        const response = await fetch(`http://localhost:8081/api/doctors/search?${params}`);
        const result = await response.json();
        
        if (result.success) {
            return result.data;
        } else {
            console.error('搜索失败:', result.message);
            return [];
        }
    } catch (error) {
        console.error('请求错误:', error);
        return [];
    }
}
```

### Python 调用示例

```python
import requests
import json

# 获取所有医生列表
def get_all_doctors():
    try:
        response = requests.get('http://localhost:8081/api/doctors')
        result = response.json()
        
        if result['success']:
            return result['data']
        else:
            print(f"获取失败: {result['message']}")
            return []
    except Exception as e:
        print(f"请求错误: {e}")
        return []

# 根据科室获取医生
def get_doctors_by_department(department):
    try:
        url = f'http://localhost:8081/api/doctors/department/{department}'
        response = requests.get(url)
        result = response.json()
        
        if result['success']:
            return result['data']
        else:
            print(f"获取失败: {result['message']}")
            return []
    except Exception as e:
        print(f"请求错误: {e}")
        return []
```

## 测试指南

### 测试脚本使用

项目根目录提供了完整的测试脚本 `api-test.sh`，包含所有 API 端点的测试用例。

**执行测试**:
```bash
# 给脚本添加执行权限
chmod +x api-test.sh

# 执行测试
./api-test.sh
```

**测试脚本功能**:
- 自动检查服务连通性
- 执行所有 API 端点测试
- 验证响应格式和状态码
- 生成详细的测试报告

### 手动测试命令

```bash
# 基础功能测试
curl -X GET "http://localhost:8081/api/doctors"

# 过滤功能测试
curl -X GET "http://localhost:8081/api/doctors/active"
curl -X GET "http://localhost:8081/api/doctors/department/心内科"

# 搜索功能测试
curl -X GET "http://localhost:8081/api/doctors/search?name=张伟"

# 数据统计测试
curl -X GET "http://localhost:8081/api/doctors/departments"
curl -X GET "http://localhost:8081/api/doctors/specialties"

# 错误处理测试
curl -X GET "http://localhost:8081/api/doctors/nonexistent"
```

## 部署说明

### 开发环境

1. **启动数据库**:
   ```bash
   docker-compose up -d
   ```

2. **启动后端服务**:
   ```bash
   cd server/qa-service-user
   mvn spring-boot:run
   ```

3. **验证服务**:
   ```bash
   curl -X GET "http://localhost:8081/api/doctors"
   ```

### 生产环境

1. **配置数据库连接**
2. **设置环境变量**
3. **部署 Spring Boot 应用**
4. **配置反向代理和 SSL**

## 版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| 1.0.0 | 2024-03-31 | 初始版本，提供基础 CRUD 功能 |

## 技术支持

如有问题请联系开发团队或查看项目文档。

---

*本文档最后更新于 2024年3月31日*