# API 测试文档 - qa-service-user 服务

本文档详细说明如何使用基于 curl 的测试脚本来测试 qa-service-user 服务中的医生相关 API。

## 概述

本测试脚本使用标准的 curl 命令构建，不依赖任何第三方测试框架，确保测试的简洁性和可移植性。

## 测试脚本文件

- **文件名**: `api-test.sh`
- **位置**: 项目根目录
- **功能**: 包含 10 个测试用例，覆盖所有医生相关 API 端点

## 测试环境要求

### 1. 系统环境
- Linux 系统（推荐 Ubuntu/CentOS）
- macOS 系统
- Windows 系统（需要安装 Git Bash、WSL 或 Cygwin）

### 2. 软件依赖
- `curl` - HTTP 客户端工具（必需）
- `python` - JSON 格式化工具（可选，用于美化输出）

### 3. 服务要求
- MySQL 数据库服务（通过 docker-compose 启动）
- qa-service-user 后端服务（运行在端口 8081）

## 测试用例列表

| 序号 | 测试用例名称 | API 端点 | 测试目的 |
|------|-------------|---------|----------|
| 1 | 获取所有医生列表 | `GET /api/doctors` | 测试基本数据获取功能 |
| 2 | 获取活跃医生列表 | `GET /api/doctors/active` | 测试状态过滤功能 |
| 3 | 根据ID获取医生详情 | `GET /api/doctors/{id}` | 测试单条数据查询 |
| 4 | 根据科室获取医生列表 | `GET /api/doctors/department/{dept}` | 测试科室过滤功能 |
| 5 | 根据专业领域获取医生列表 | `GET /api/doctors/specialty/{spec}` | 测试专业领域过滤 |
| 6 | 搜索医生 | `GET /api/doctors/search?name=xx&dept=xx` | 测试复合搜索功能 |
| 7 | 获取所有科室列表 | `GET /api/doctors/departments` | 测试去重数据获取 |
| 8 | 获取所有专业领域列表 | `GET /api/doctors/specialties` | 测试专业领域去重 |
| 9 | 测试不存在的医生ID | `GET /api/doctors/nonexistent` | 测试错误处理机制 |
| 10 | 测试错误请求 | `GET /api/doctors/invalid` | 测试404错误处理 |

## 测试执行步骤

### 步骤1：启动后端服务

确保以下服务正在运行：

```bash
# 1. 启动数据库服务
cd /d/treaWork/qa-live-healthcare-interview
docker-compose up -d

# 2. 启动后端服务
cd server/qa-service-user
mvn spring-boot:run
```

### 步骤2：执行测试脚本

#### 方法一：在 Linux/macOS 系统执行

```bash
# 1. 给脚本添加执行权限
chmod +x api-test.sh

# 2. 执行测试
./api-test.sh
```

#### 方法二：在 Windows 系统使用 Git Bash

```bash
# 在 Git Bash 中执行
bash api-test.sh
```

#### 方法三：直接使用 bash 执行

```bash
# 无需修改权限，直接执行
bash api-test.sh
```

### 步骤3：查看测试结果

脚本执行后会显示：
- 每个测试用例的执行状态（通过/失败）
- 详细的 curl 命令
- API 响应内容（格式化显示）
- HTTP 状态码验证
- 测试结果汇总

## 测试用例详情

### 测试用例 1：获取所有医生列表

**测试目的**: 验证基本数据获取功能

**curl 命令**:
```bash
curl -X GET "http://localhost:8081/api/doctors"
```

**预期响应**:
```json
{
    "success": true,
    "message": "获取医生列表成功",
    "data": [
        {
            "id": "doc001",
            "name": "张伟医生",
            "department": "心内科",
            ...
        }
    ]
}
```

**验证点**:
- HTTP 状态码为 200
- 响应状态为 "success"
- 包含医生数据数组

### 测试用例 2：获取活跃医生列表

**测试目的**: 验证状态过滤功能

**curl 命令**:
```bash
curl -X GET "http://localhost:8081/api/doctors/active"
```

**预期响应**: 只返回 `isActive=true` 的医生

**验证点**:
- 只包含活跃医生数据
- 数据条数正确

### 测试用例 3：根据ID获取医生详情

**测试目的**: 验证单条数据查询

**curl 命令**:
```bash
curl -X GET "http://localhost:8081/api/doctors/doc001"
```

**预期响应**: 返回指定ID的医生详细信息

**验证点**:
- 返回正确的医生信息
- 包含完整的医生详情

### 测试用例 4：根据科室获取医生列表

**测试目的**: 验证科室过滤功能

**curl 命令**:
```bash
curl -X GET "http://localhost:8081/api/doctors/department/心内科"
```

**预期响应**: 只返回指定科室的医生

**验证点**:
- 所有返回医生的科室都是"心内科"
- 数据条数正确

### 测试用例 5：根据专业领域获取医生列表

**测试目的**: 验证专业领域过滤功能

**curl 命令**:
```bash
curl -X GET "http://localhost:8081/api/doctors/specialty/心脏病"
```

**预期响应**: 只返回指定专业领域的医生

**验证点**:
- 医生专业领域包含"心脏病"
- 数据条数正确

### 测试用例 6：搜索医生

**测试目的**: 验证复合搜索功能

**curl 命令**:
```bash
curl -X GET "http://localhost:8081/api/doctors/search?name=张伟&department=心内科"
```

**预期响应**: 返回符合搜索条件的医生

**验证点**:
- 返回匹配搜索条件的医生
- 支持多条件组合搜索

### 测试用例 7：获取所有科室列表

**测试目的**: 验证去重数据获取功能

**curl 命令**:
```bash
curl -X GET "http://localhost:8081/api/doctors/departments"
```

**预期响应**: 返回所有不重复的科室名称列表

**验证点**:
- 返回正确的科室列表
- 列表内容不重复

### 测试用例 8：获取所有专业领域列表

**测试目的**: 验证专业领域去重功能

**curl 命令**:
```bash
curl -X GET "http://localhost:8081/api/doctors/specialties"
```

**预期响应**: 返回所有不重复的专业领域列表

**验证点**:
- 返回正确的专业领域列表
- 列表内容不重复

### 测试用例 9：测试不存在的医生ID

**测试目的**: 验证错误处理机制

**curl 命令**:
```bash
curl -X GET "http://localhost:8081/api/doctors/nonexistent"
```

**预期响应**: 返回错误信息或 404 状态

**验证点**:
- 正确处理不存在的资源
- 返回适当的错误信息

### 测试用例 10：测试错误请求

**测试目的**: 验证 404 错误处理

**curl 命令**:
```bash
curl -X GET "http://localhost:8081/api/doctors/invalid"
```

**预期响应**: 返回 404 状态码

**验证点**:
- HTTP 状态码为 404
- 正确处理无效路径

## 手动测试命令

如果您想手动测试特定 API，可以使用以下 curl 命令：

```bash
# 获取所有医生（基础测试）
curl -X GET "http://localhost:8081/api/doctors"

# 获取活跃医生（状态过滤测试）
curl -X GET "http://localhost:8081/api/doctors/active"

# 根据ID获取医生（单条数据测试）
curl -X GET "http://localhost:8081/api/doctors/doc001"

# 根据科室获取医生（分类过滤测试）
curl -X GET "http://localhost:8081/api/doctors/department/心内科"

# 搜索医生（复合条件测试）
curl -X GET "http://localhost:8081/api/doctors/search?name=张伟"

# 获取科室列表（去重数据测试）
curl -X GET "http://localhost:8081/api/doctors/departments"

# 测试错误处理（异常情况测试）
curl -X GET "http://localhost:8081/api/doctors/nonexistent"
```

## 故障排除

### 常见问题及解决方案

1. **连接失败错误**
   ```
   curl: (7) Failed to connect to localhost port 8081: Connection refused
   ```
   **解决方案**: 检查后端服务是否运行在端口 8081

2. **权限错误**
   ```
   bash: ./api-test.sh: Permission denied
   ```
   **解决方案**: 执行 `chmod +x api-test.sh`

3. **命令未找到错误**
   ```
   bash: curl: command not found
   ```
   **解决方案**: 安装 curl 工具

4. **中文乱码问题**
   **解决方案**: 确保终端支持 UTF-8 编码

### 调试技巧

1. **查看详细请求信息**
   ```bash
   curl -v -X GET "http://localhost:8081/api/doctors"
   ```

2. **只检查HTTP状态码**
   ```bash
   curl -s -o /dev/null -w "%{http_code}" "http://localhost:8081/api/doctors"
   ```

3. **保存响应到文件**
   ```bash
   curl -X GET "http://localhost:8081/api/doctors" > response.json
   ```

4. **设置超时时间**
   ```bash
   curl --max-time 10 -X GET "http://localhost:8081/api/doctors"
   ```

## 测试结果示例

成功执行脚本后，您将看到类似以下输出：

```
开始执行 qa-service-user API 测试
基础URL: http://localhost:8081/api/doctors
测试时间: 2024年 03月 31日 星期日 10:30:00 CST
==================================================
检查后端服务连通性...
✓ 后端服务连接正常
==================================================
测试: 获取所有医生列表
URL: http://localhost:8081/api/doctors
==================================================
执行curl命令:
curl -X GET "http://localhost:8081/api/doctors"
响应内容:
{
    "success": true,
    "message": "获取医生列表成功",
    "data": [...]
}
✓ 测试通过
✓ HTTP状态码正确 (200)

...更多测试用例...

==================================================
测试结果汇总:
通过: 10
失败: 0
总计: 10
🎉 所有测试通过！
==================================================
测试完成时间: 2024年 03月 31日 星期日 10:32:15 CST
```

## 维护说明

- 定期更新测试用例以匹配 API 变更
- 添加新的 API 端点测试
- 确保测试数据与数据库同步
- 更新文档以反映最新的测试要求
- 保持测试脚本的简洁性和可移植性