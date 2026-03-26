# 项目完成总结

## 完成的任务

### 1. 数据库配置

- 创建了 `docker-compose.yml` 文件，用于启动 MySQL 数据库和 phpMyAdmin
- 配置了 MySQL 数据库的用户名、密码和数据库名
- 配置了 phpMyAdmin 作为数据库管理界面

### 2. 后端服务开发

- 修改了 `qa-service-user` 服务的 `pom.xml` 文件，添加了数据库依赖
- 创建了 `Doctor` 实体类，映射到 `doctors` 表
- 创建了 `DoctorRepository` 接口，提供数据库操作方法
- 创建了 `DoctorService` 类，实现业务逻辑
- 创建了 `DoctorController` 类，提供 RESTful API 接口
- 创建了 `DataInitializer` 类，用于初始化数据库数据
- 配置了 `application.properties` 文件，连接到 MySQL 数据库
- 在 qa-service-user 服务中创建可以支持前端 [医生页面](https://github.com/jaycGong/qa-live-healthcare-interview/blob/main/web/qa-web/src/views/Doctors.vue) 显示医生列表所需要的api

### 3. 前端集成

- 修改了 `store/index.ts` 文件，使其从后端 API 获取医生数据
- 添加了 `fetchDoctors` 函数，用于从后端 API 获取医生列表
- 添加了 `refreshDoctors` 方法，用于刷新医生数据
- 添加了数据格式转换，确保前端数据结构与后端一致
- 当前应用的数据全部使用json文件的方式在前端项目中存储

### 4. API 测试

- 创建了 `docs/api-test.sh` 脚本，用于测试医生列表 API
- 创建了 `docs/API_TEST.md` 文件，用于说明 API 测试的执行方法
- 创建了 `docs/api.md` 文档，用于对 qa-service-user 服务中的所有 API 测试进行说明和测试用例脚本

### 5. 语言切换功能

- 前端已经实现了中英文切换功能，在页面右上角添加了语言切换下拉菜单
- 当用户选择中文/English 选项时，页面会动态切换到对应语言

## 技术栈

- **前端**：Vue 3, TypeScript, Ant Design Vue
- **后端**：Spring Boot, Spring Data JPA, MySQL
- **容器化**：Docker, Docker Compose
- **测试**：curl

## 如何运行

1. 启动数据库服务：
   ```bash
   docker-compose up -d
   ```
2. 构建并运行后端服务：
   ```bash
   cd server/qa-service-user
   ./mvnw clean package -DskipTests
   java -jar target/qa-service-user-0.0.1-SNAPSHOT.jar
   ```
3. 启动前端服务：
   ```bash
   cd web/qa-web
   npm install
   npm run dev
   ```
4. 访问应用：
   - 前端：<http://localhost:5173>
   - phpMyAdmin：<http://localhost:8080>

## API 接口

- `GET /api/doctors` - 获取所有医生列表
- `GET /api/doctors/active` - 获取活跃的医生列表
- `GET /api/doctors/department/{department}` - 根据部门获取医生列表
- `GET /api/doctors/{id}` - 根据 ID 获取医生详情
- `GET /api/doctors/username/{username}` - 根据用户名获取医生详情
- `POST /api/doctors` - 创建新医生
- `PUT /api/doctors/{id}` - 更新医生信息
- `DELETE /api/doctors/{id}` - 删除医生

