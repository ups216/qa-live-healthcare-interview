# API 测试说明

## 测试环境准备

1. 启动 MySQL 数据库和 phpMyAdmin：
   ```bash
   docker-compose up -d
   ```

2. 构建并运行 qa-service-user 服务：
   ```bash
   cd server/qa-service-user
   ./mvnw clean package -DskipTests
   java -jar target/qa-service-user-0.0.1-SNAPSHOT.jar
   ```

## 测试脚本执行

### 使用 Bash 脚本测试

1. 确保脚本具有执行权限：
   ```bash
   chmod +x docs/api-test.sh
   ```

2. 执行测试脚本：
   ```bash
   ./docs/api-test.sh
   ```

### 使用 curl 命令手动测试

1. 获取所有医生列表：
   ```bash
   curl -X GET "http://localhost:8080/api/doctors" -H "Content-Type: application/json"
   ```

2. 获取活跃的医生列表：
   ```bash
   curl -X GET "http://localhost:8080/api/doctors/active" -H "Content-Type: application/json"
   ```

3. 根据部门获取医生列表：
   ```bash
   curl -X GET "http://localhost:8080/api/doctors/department/心内科" -H "Content-Type: application/json"
   ```

4. 根据 ID 获取医生详情：
   ```bash
   curl -X GET "http://localhost:8080/api/doctors/doc001" -H "Content-Type: application/json"
   ```

5. 根据用户名获取医生详情：
   ```bash
   curl -X GET "http://localhost:8080/api/doctors/username/dr-zhang-wei" -H "Content-Type: application/json"
   ```

## 测试结果验证

- 所有 API 请求应返回 HTTP 200 OK 状态码
- 获取所有医生列表应返回 5 个医生的详细信息
- 获取活跃的医生列表应返回 4 个活跃医生的详细信息
- 根据部门获取医生列表应返回对应部门的医生信息
- 根据 ID 或用户名获取医生详情应返回对应医生的详细信息
