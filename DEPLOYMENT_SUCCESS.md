# Docker 环境部署成功 ✅

## 🎉 部署状态

所有服务已成功启动并运行！

### 服务状态

| 服务 | 容器名称 | 状态 | 端口 |
|------|---------|------|------|
| MySQL | qa-mysql | ✅ Running | localhost:3306 |
| phpMyAdmin | qa-phpmyadmin | ✅ Running | http://localhost:8080 |

## 📊 数据库信息

### MySQL 连接信息
- **主机**: localhost
- **端口**: 3306
- **数据库**: qa_healthcare
- **用户名**: qa_user
- **密码**: qa_password123

### Root 账户
- **用户名**: root
- **密码**: root123456

## 🌐 访问方式

### 1. phpMyAdmin（推荐用于管理）

**访问地址**: http://localhost:8080

**登录步骤**:
1. 打开浏览器访问 http://localhost:8080
2. 输入以下信息：
   - 服务器: `mysql`
   - 用户名: `qa_user`
   - 密码: `qa_password123`
3. 点击"执行"按钮

### 2. MySQL 命令行

```bash
# 使用 qa_user 连接
mysql -h localhost -P 3306 -u qa_user -pqa_password123 qa_healthcare

# 或使用 root 连接
mysql -h localhost -P 3306 -u root -proot123456
```

### 3. Docker 容器内访问

```bash
# 进入 MySQL 容器
docker exec -it qa-mysql mysql -u root -proot123456 qa_healthcare
```

## 📁 已初始化的数据

### 数据库: qa_healthcare

#### 表: users

| id | username | email | created_at |
|----|----------|-------|------------|
| 1 | admin | admin@example.com | 2026-05-10 08:44:23 |
| 2 | doctor1 | doctor1@example.com | 2026-05-10 08:44:23 |
| 3 | patient1 | patient1@example.com | 2026-05-10 08:44:23 |

## 🔧 常用命令

### 查看服务状态
```bash
docker-compose ps
```

### 查看日志
```bash
# 查看所有日志
docker-compose logs -f

# 查看 MySQL 日志
docker-compose logs -f mysql

# 查看 phpMyAdmin 日志
docker-compose logs -f phpmyadmin
```

### 停止服务
```bash
docker-compose down
```

### 重启服务
```bash
docker-compose restart
```

### 使用管理脚本
```bash
# 启动
./docker-manager.sh start

# 停止
./docker-manager.sh stop

# 查看状态
./docker-manager.sh status

# 查看日志
./docker-manager.sh logs

# 连接 MySQL
./docker-manager.sh mysql

# 备份数据库
./docker-manager.sh backup
```

## 🚀 在 Spring Boot 应用中使用

在 `server/qa-service-user/src/main/resources/application.properties` 中配置：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/qa_healthcare?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=qa_user
spring.datasource.password=qa_password123
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA 配置
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

## 📝 下一步

1. **访问 phpMyAdmin**: 打开 http://localhost:8080 管理数据库
2. **开发后端 API**: 在 `qa-service-user` 服务中创建医生相关 API
3. **配置数据源**: 在 Spring Boot 应用中配置 MySQL 连接
4. **创建实体类**: 根据业务需求创建 JPA 实体

## ⚠️ 注意事项

1. **首次启动**: MySQL 首次启动可能需要 30-60 秒完成初始化
2. **数据持久化**: 数据存储在 Docker 卷中，即使容器删除数据也会保留
3. **端口占用**: 确保 3306 和 8080 端口未被其他程序占用
4. **安全性**: 生产环境请修改默认密码并使用环境变量管理敏感信息

## 🆘 故障排除

### 无法连接 MySQL
```bash
# 检查容器状态
docker-compose ps

# 查看 MySQL 日志
docker-compose logs mysql

# 重启 MySQL
docker-compose restart mysql
```

### 端口冲突
编辑 `docker-compose.yml`，修改端口映射：
```yaml
ports:
  - "3307:3306"  # 改用 3307 端口
  - "8081:80"    # 改用 8081 端口
```

### 重置所有数据（谨慎使用）
```bash
docker-compose down -v
docker-compose up -d
```

---

**部署时间**: 2026-05-10  
**Docker Compose 版本**: 3.8  
**MySQL 版本**: 8.0  
**phpMyAdmin**: Latest