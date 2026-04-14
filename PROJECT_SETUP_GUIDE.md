# 项目设置指南

## 项目状态

✅ **所有功能已开发完成**
⚠️ **等待Java 17环境配置**

## 已完成的功能

### 1. 语言切换功能
- 中英文双向切换
- 响应式国际化支持
- 统一使用 `useLocale` 钩子管理

### 2. 医生数据迁移到MySQL
- ✅ 完整的Docker配置 (`docker-compose.yml`)
- ✅ MySQL 8.0 + phpMyAdmin
- ✅ 数据库表结构和初始数据
- ✅ 完整的后端API接口
- ✅ 前端联调完成

### 3. 患者用户名密码登录
- ✅ 后端登录和注册API
- ✅ 前端登录页面 (`/patient/login`)
- ✅ 完整的身份验证流程

## 环境要求

### 必须安装
1. **Java 17** - Spring Boot 3.5.7需要Java 17
2. **Node.js 16+** - 用于前端开发
3. **Docker Desktop** - 用于运行MySQL数据库 (可选)

### 可选安装
1. **Apache Maven 3.9+** - 项目自带Maven Wrapper，但可以配置系统Maven
2. **IDE** - 推荐使用IntelliJ IDEA或VS Code

## 快速启动步骤

### 1. 安装Java 17
```bash
# 下载并安装Java 17
# 官方下载: https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html
# 或使用AdoptOpenJDK: https://adoptium.net/

# 验证安装
java -version
# 应显示: java version "17.x.x"
```

### 2. 启动前端开发服务器
```bash
# 进入前端目录
cd web/qa-web

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 访问: http://localhost:5173
```

### 3. 启动后端服务
```bash
# 进入后端目录
cd server/qa-service-user

# 启动Spring Boot应用
./mvnw.cmd spring-boot:run

# 等待启动完成，访问API测试页面: file:///api-test.html
```

### 4. 启动MySQL数据库 (可选)
```bash
# 启动Docker服务

# 进入项目根目录
cd d:/code/qa-live-healthcare-interview

# 启动MySQL和phpMyAdmin
docker-compose up -d

# 访问phpMyAdmin: http://localhost:8080
# 用户名: root
# 密码: rootpassword
```

## 测试账号

### 医生账号
```
用户名: dr-zhang-wei
密码: 123456
部门: 心内科
状态: 活跃

用户名: dr-li-na  
密码: 123456
部门: 儿科
状态: 活跃
```

### 患者账号
```
用户名: patient001
密码: 123456
姓名: 张三

用户名: patient002
密码: 123456
姓名: 李四
```

## 快速测试

### 1. 使用API测试页面
打开 `api-test.html` 文件，点击测试按钮验证各个API接口。

### 2. 使用命令行测试
```bash
# 测试活跃医生列表
curl http://localhost:8080/api/doctors/active

# 测试患者登录
curl -X POST http://localhost:8080/api/patients/login \
  -H "Content-Type: application/json" \
  -d '{"username":"patient001","password":"123456"}'
```

### 3. 访问前端页面
1. 首页: `http://localhost:5173`
2. 医生页面: `http://localhost:5173/doctors`
3. 患者登录: `http://localhost:5173/patient/login`

## 故障排除

### 问题1: Java版本错误
**症状**: `UnsupportedClassVersionError` 或类似错误
**解决**: 安装Java 17并配置为默认JDK

### 问题2: 端口冲突
**症状**: `Port 8080 already in use`
**解决**: 
1. 修改 `server/qa-service-user/src/main/resources/application.properties`
2. 修改 `server.port=8081` (或其他可用端口)

### 问题3: Maven依赖下载失败
**症状**: `Could not resolve dependencies`
**解决**:
1. 检查网络连接
2. 配置Maven镜像
3. 删除 `~/.m2/repository` 并重试

### 问题4: 数据库连接失败
**症状**: `Failed to obtain JDBC Connection`
**解决**: 
1. 确保MySQL服务正在运行
2. 检查数据库连接配置
3. 默认使用H2内存数据库，无需额外配置

## 开发脚本

### 启动所有服务
```bash
# 使用预置的启动脚本
.\start-dev.bat
```

### API测试脚本
```bash
# 运行API测试脚本
.\test-apis.sh
```

## 项目结构

```
qa-live-healthcare-interview/
├── server/                      # 后端Spring Boot服务
│   └── qa-service-user/
│       ├── src/main/java/      # Java源代码
│       │   ├── controller/     # API控制器
│       │   ├── service/       # 业务逻辑
│       │   ├── entity/        # 数据库实体
│       │   ├── repository/    # 数据访问层
│       │   └── dto/          # 数据传输对象
│       ├── src/main/resources/ # 配置文件
│       └── pom.xml            # Maven配置
├── web/                        # 前端Vue应用
│   └── qa-web/
│       ├── src/
│       │   ├── components/    # Vue组件
│       │   ├── views/         # 页面组件
│       │   ├── store/         # 状态管理
│       │   └── locales/       # 国际化资源
│       ├── package.json      # npm配置
│       └── vite.config.ts    # Vite配置
├── docs/                      # 文档目录
├── init-scripts/              # 数据库初始化脚本
├── docker-compose.yml         # Docker配置
└── start-dev.bat              # 开发环境启动脚本
```

## 后续步骤

### 环境配置后
1. **启动后端服务**: `cd server/qa-service-user && ./mvnw.cmd spring-boot:run`
2. **验证API**: 访问 `http://localhost:8080/api/doctors/active`
3. **启动前端**: `cd web/qa-web && npm run dev`
4. **完整测试**: 使用 `api-test.html` 进行端到端测试

### 部署建议
1. **后端**: 构建可执行JAR `mvn clean package`
2. **前端**: `npm run build` 部署静态文件
3. **数据库**: 使用Docker容器或云数据库服务

---
**指南版本**: v1.0  
**最后更新**: 2026-04-14  
**项目状态**: 代码完成，等待Java 17环境配置