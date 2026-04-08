# QA Service User API 文档

## 1. 服务概述

QA Service User 是医疗问答系统的用户管理服务，主要负责医生信息的管理和查询。该服务提供了完整的医生信息CRUD操作，支持多条件筛选查询，并与前端医生页面实现数据交互。

## 2. 技术栈

- **后端框架**: Spring Boot 3.5.7
- **数据库**: MySQL 8.0
- **ORM框架**: Spring Data JPA
- **API风格**: RESTful
- **部署方式**: 独立服务

## 3. API 列表

| API路径 | 方法 | 功能描述 | 请求参数 | 成功响应 (200 OK) |
|---------|------|----------|----------|-------------------|
| `/api/doctors` | GET | 获取所有医生 | 无 | `[{"id": "doc001", "name": "张伟医生", ...}]` |
| `/api/doctors/{id}` | GET | 根据ID获取医生 | `id` (路径参数) | `{"id": "doc001", "name": "张伟医生", ...}` |
| `/api/doctors/conditions` | GET | 多条件查询医生 | `name`, `department`, `title`, `isActive` | `[{"id": "doc001", "name": "张伟医生", ...}]` |
| `/api/doctors/departments` | GET | 获取所有科室 | 无 | `["心内科", "儿科", "骨科", ...]` |
| `/api/doctors/titles` | GET | 获取所有职称 | 无 | `["主任医师", "副主任医师", "主治医师", ...]` |
| `/api/doctors` | POST | 创建医生 | 医生对象 (JSON) | `{"id": "doc006", "name": "新医生", ...}` |
| `/api/doctors/{id}` | PUT | 更新医生 | `id` (路径参数), 医生对象 (JSON) | `{"id": "doc001", "name": "更新后的名称", ...}` |
| `/api/doctors/{id}` | DELETE | 删除医生 | `id` (路径参数) | `204 No Content` |

## 4. 数据模型

### Doctor 实体

| 字段名 | 类型 | 描述 | 约束 |
|--------|------|------|------|
| `id` | String | 医生ID | 主键, 非空 |
| `username` | String | 用户名 | 唯一, 非空 |
| `password` | String | 密码 | 非空 |
| `name` | String | 医生姓名 | 非空 |
| `title` | String | 职称 | 非空 |
| `department` | String | 科室 | 非空 |
| `avatar` | String | 头像URL | 非空 |
| `experience` | String | 临床经验 | 非空 |
| `specialties` | String | 专长 (JSON格式) | 非空 |
| `isActive` | boolean | 在线状态 | 非空, 默认true |
| `createdAt` | LocalDateTime | 创建时间 | 非空 |
| `updatedAt` | LocalDateTime | 更新时间 | 非空 |

### DoctorDTO (数据传输对象)

| 字段名 | 类型 | 描述 |
|--------|------|------|
| `id` | String | 医生ID |
| `username` | String | 用户名 |
| `password` | String | 密码 |
| `name` | String | 医生姓名 |
| `title` | String | 职称 |
| `department` | String | 科室 |
| `avatar` | String | 头像URL |
| `experience` | String | 临床经验 |
| `specialties` | List<String> | 专长 (数组格式) |
| `isActive` | boolean | 在线状态 |
| `createdAt` | LocalDateTime | 创建时间 |
| `updatedAt` | LocalDateTime | 更新时间 |

## 5. 测试环境配置

### 5.1 数据库配置

- **数据库名**: `healthcare_db`
- **用户名**: `root`
- **密码**: `123456`
- **端口**: `8888`
- **字符集**: `utf8mb4`
- **排序规则**: `utf8mb4_unicode_ci`

### 5.2 服务配置

- **服务端口**: `8080`
- **CORS配置**: 允许所有跨域请求
- **日志级别**: INFO

## 6. 测试流程

### 6.1 环境准备

1. **启动数据库服务**:
   ```bash
   cd server
   docker compose up -d
   ```

2. **启动后端服务**:
   - 运行 `QaServiceUserApplication` 类
   - 或使用 Maven 命令: `./mvnw spring-boot:run`

3. **启动前端服务**:
   ```bash
   cd web/qa-web
   npm run dev
   ```

### 6.2 API测试

1. **执行测试脚本**:
   ```bash
   cd server
   bash test-doctors-api.sh
   ```

2. **手动测试**:
   - 使用 Postman 或 curl 工具
   - 访问 http://localhost:8080/api/doctors 测试API

### 6.3 前端测试

1. **访问医生页面**:
   - 打开浏览器访问 http://localhost:5173/doctors
   - 验证医生列表是否正常显示

2. **测试筛选功能**:
   - 测试姓名搜索
   - 测试科室下拉选择
   - 测试职称下拉选择
   - 测试在线状态下拉选择
   - 测试多条件组合筛选

3. **测试分页功能**:
   - 验证分页控件是否正常工作
   - 验证页码切换是否正确

## 7. 测试结果判断标准

### 7.1 功能验证

- ✅ **API响应状态码**: 所有API端点返回正确的HTTP状态码
- ✅ **数据格式**: 响应数据格式正确，符合JSON规范
- ✅ **筛选功能**: 筛选条件正确生效，返回符合条件的结果
- ✅ **数据完整性**: 所有必要字段都被正确返回
- ✅ **Specialties字段**: `specialties`字段以数组格式返回
- ✅ **中文显示**: 中文内容正常显示，无乱码

### 7.2 性能验证

- ✅ **响应时间**: API响应时间 < 1秒
- ✅ **并发处理**: 支持同时多个请求
- ✅ **资源使用**: 内存和CPU使用合理

### 7.3 可靠性验证

- ✅ **错误处理**: 边界条件和异常情况处理正确
- ✅ **服务稳定性**: 长时间运行无崩溃
- ✅ **数据库连接**: 数据库连接稳定

## 8. 常见问题排查

### 8.1 服务启动问题

| 问题 | 可能原因 | 解决方案 |
|------|----------|----------|
| 端口8080被占用 | 其他服务正在使用该端口 | 停止占用端口的服务或修改配置文件中的端口 |
| 数据库连接失败 | MySQL服务未启动或配置错误 | 检查MySQL服务状态和连接配置 |
| 依赖项缺失 | Maven依赖未正确安装 | 执行 `mvn clean install` 重新安装依赖 |

### 8.2 API调用问题

| 问题 | 可能原因 | 解决方案 |
|------|----------|----------|
| 404错误 | API路径错误或服务未启动 | 检查API路径是否正确，确认服务已启动 |
| 500错误 | 服务器内部错误 | 查看后端日志，检查数据库连接和代码异常 |
| 400错误 | 请求参数错误 | 检查请求参数格式是否正确 |
| CORS错误 | 跨域配置问题 | 检查后端CORS配置是否正确 |

### 8.3 数据显示问题

| 问题 | 可能原因 | 解决方案 |
|------|----------|----------|
| 数据为空 | 数据库中无数据 | 检查数据库初始化脚本是否执行，确认数据已插入 |
| 乱码 | 字符编码问题 | 检查数据库字符集配置和API响应头 |
| Specialties显示异常 | 数据格式转换问题 | 检查后端DTO转换逻辑和前端处理方式 |

## 9. 部署与维护

### 9.1 部署步骤

1. **构建项目**:
   ```bash
   cd server/qa-service-user
   ./mvnw clean package
   ```

2. **运行服务**:
   ```bash
   java -jar target/qa-service-user-0.0.1-SNAPSHOT.jar
   ```

3. **数据库初始化**:
   - 确保MySQL服务运行
   - 执行初始化脚本创建表结构和插入数据

### 9.2 维护建议

- **定期备份**：定期备份数据库数据
- **监控日志**：定期检查服务日志，及时发现问题
- **版本控制**：使用Git进行代码版本控制
- **安全更新**：定期更新依赖包，修复安全漏洞

## 10. 总结

QA Service User 服务提供了完整的医生信息管理功能，包括：

- ✅ 医生信息的增删改查
- ✅ 多条件组合查询
- ✅ 科室和职称列表获取
- ✅ 在线状态管理
- ✅ 与前端医生页面的无缝集成

该服务采用了现代化的技术栈和架构设计，具有良好的可维护性和扩展性。通过完整的测试流程和监控机制，可以确保服务的稳定运行和数据的安全可靠。
