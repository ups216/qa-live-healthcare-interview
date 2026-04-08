# API测试说明

本文档用于说明如何执行医生列表页面的API测试。

## 测试环境准备

1. **启动后端服务**
   ```bash
   # 进入后端服务目录
   cd server/qa-service-user
   
   # 构建项目
   mvn clean package -DskipTests
   
   # 启动服务
   java -jar target/qa-service-user-0.0.1-SNAPSHOT.jar
   ```

2. **启动MySQL数据库**
   ```bash
   # 在项目根目录执行
   docker-compose up -d
   ```

3. **验证服务状态**
   - 后端服务：访问 `http://localhost:8081/api/doctors` 应该返回医生列表
   - MySQL数据库：访问 `http://localhost:8080` 可以打开phpMyAdmin

## 执行测试

### 方法1: 直接执行测试脚本

```bash
# 在项目根目录执行
chmod +x api-test.sh
./api-test.sh
```

### 方法2: 手动执行curl命令

#### 1. 获取所有医生
```bash
curl -X GET "http://localhost:8081/api/doctors" -H "Accept: application/json"
```

#### 2. 获取在线医生
```bash
curl -X GET "http://localhost:8081/api/doctors/active" -H "Accept: application/json"
```

#### 3. 根据ID获取医生
```bash
curl -X GET "http://localhost:8081/api/doctors/doc001" -H "Accept: application/json"
```

#### 4. 根据用户名获取医生
```bash
curl -X GET "http://localhost:8081/api/doctors/username/dr-zhang-wei" -H "Accept: application/json"
```

#### 5. 初始化医生数据
```bash
curl -X POST "http://localhost:8081/api/doctors/init" -H "Content-Type: application/json" -H "Accept: application/json"
```

## 测试预期结果

1. **获取所有医生**：返回包含所有医生的JSON数组
2. **获取在线医生**：返回只包含在线医生的JSON数组
3. **根据ID获取医生**：返回指定ID的医生详情
4. **根据用户名获取医生**：返回指定用户名的医生详情
5. **初始化医生数据**：成功初始化默认医生数据

## 注意事项

- 确保后端服务和MySQL数据库都已启动
- 确保API地址和端口正确（默认：http://localhost:8081）
- 首次测试时，建议先执行初始化医生数据的API
