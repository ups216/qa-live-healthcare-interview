# Docker 环境使用说明

## 概述

本项目使用 Docker Compose 部署 MySQL 数据库和 phpMyAdmin 管理界面。

## 服务说明

### 1. MySQL 数据库
- **容器名称**: qa-mysql
- **镜像**: mysql:8.0
- **端口**: 3306 (宿主机) -> 3306 (容器)
- **数据库名**: qa_healthcare
- **用户名**: qa_user
- **密码**: qa_password123
- **Root 密码**: root123456

### 2. phpMyAdmin
- **容器名称**: qa-phpmyadmin
- **镜像**: phpmyadmin/phpmyadmin
- **端口**: 8080 (宿主机) -> 80 (容器)
- **访问地址**: http://localhost:8080

## 快速开始

### 启动服务

```bash
docker-compose up -d
```

### 查看服务状态

```bash
docker-compose ps
```

### 查看日志

```bash
# 查看所有服务日志
docker-compose logs -f

# 查看特定服务日志
docker-compose logs -f mysql
docker-compose logs -f phpmyadmin
```

### 停止服务

```bash
docker-compose down
```

### 停止并删除数据卷（谨慎使用）

```bash
docker-compose down -v
```

## 访问方式

### 1. 从宿主机访问 MySQL

使用 MySQL 客户端连接：
```bash
mysql -h localhost -P 3306 -u qa_user -pqa_password123 qa_healthcare
```

或使用 root 用户：
```bash
mysql -h localhost -P 3306 -u root -proot123456
```

### 2. 通过 phpMyAdmin 访问

1. 打开浏览器访问: http://localhost:8080
2. 登录信息：
   - 服务器: mysql
   - 用户名: qa_user (或 root)
   - 密码: qa_password123 (或 root123456)

### 3. 在应用中使用

在您的 Spring Boot 应用中配置数据源：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/qa_healthcare?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=qa_user
spring.datasource.password=qa_password123
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

## 初始化脚本

`init-scripts/` 目录下的所有 `.sql` 文件会在 MySQL 容器首次启动时自动执行。

当前包含：
- `01-init.sql`: 创建示例 users 表并插入测试数据

您可以在此目录下添加更多的 SQL 初始化脚本。

## 数据持久化

MySQL 数据存储在 Docker 卷 `mysql_data` 中，即使容器被删除，数据也会保留。

查看数据卷：
```bash
docker volume ls | grep mysql_data
```

备份数据：
```bash
docker run --rm -v mysql_data:/data -v $(pwd):/backup alpine tar czf /backup/mysql-backup.tar.gz -C /data .
```

恢复数据：
```bash
docker run --rm -v mysql_data:/data -v $(pwd):/backup alpine tar xzf /backup/mysql-backup.tar.gz -C /data
```

## 常见问题

### 1. 端口冲突

如果 3306 或 8080 端口已被占用，可以修改 `docker-compose.yml` 中的端口映射：

```yaml
ports:
  - "3307:3306"  # 将宿主机的 3307 映射到容器的 3306
  - "8081:80"    # 将宿主机的 8081 映射到容器的 80
```

### 2. 无法连接 MySQL

- 确认容器正在运行：`docker-compose ps`
- 检查防火墙设置
- 等待 MySQL 完全启动（首次启动可能需要 30-60 秒）

### 3. 重置数据库

如果要完全重置数据库（包括删除所有数据）：

```bash
docker-compose down -v
docker-compose up -d
```

**警告**: 这将删除所有数据！

## 安全建议

⚠️ **生产环境注意事项**:

1. 修改默认密码为强密码
2. 不要将 `.env` 文件提交到版本控制
3. 限制 phpMyAdmin 的访问权限
4. 使用 HTTPS
5. 考虑使用 Docker secrets 管理敏感信息

## 网络架构

所有服务都在 `qa-network` 网络中，可以通过服务名称互相访问：
- MySQL: `mysql:3306`
- phpMyAdmin: `phpmyadmin:80`

## 更多命令

```bash
# 进入 MySQL 容器
docker exec -it qa-mysql mysql -u root -p

# 进入 phpMyAdmin 容器
docker exec -it qa-phpmyadmin bash

# 重启特定服务
docker-compose restart mysql

# 查看容器资源使用情况
docker stats
```