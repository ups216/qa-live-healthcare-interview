# 医疗健康咨询平台 - 开发指南

## 项目概述

这是一个基于Vue 3 + Spring Boot的在线医疗健康咨询平台，支持医生患者在线问诊、多语言切换等功能。

## 技术栈

### 前端 (Vue 3)
- **框架**: Vue 3 + TypeScript + Vite
- **UI组件**: Ant Design Vue
- **状态管理**: Vue reactive store
- **路由**: Vue Router
- **多语言**: 自定义i18n系统
- **端口**: 5173

### 后端 (Spring Boot)
- **框架**: Spring Boot 3.5.7 + Java 17
- **数据库**: H2 (开发) / MySQL (生产)
- **ORM**: Spring Data JPA
- **端口**: 8080

### 数据库
- **开发**: H2内存数据库 (无需安装)
- **生产**: MySQL 8.0 + phpMyAdmin
- **表结构**: 见 `init-scripts/` 目录

## 项目结构

```
qa-live-healthcare-interview/
├── web/qa-web/                    # 前端Vue项目
│   ├── src/
│   │   ├── components/           # 组件
│   │   ├── views/               # 页面
│   │   ├── store/               # 状态管理
│   │   ├── locales/             # 多语言资源
│   │   ├── data/                # 静态数据
│   │   └── hooks/               # 自定义钩子
│   └── package.json
├── server/                      # 后端服务
│   └── qa-service-user/        # 用户服务
│       ├── src/main/java/      # Java源代码
│       ├── src/main/resources/ # 配置文件
│       └── pom.xml
├── init-scripts/                # 数据库初始化脚本
├── docker-compose.yml          # Docker配置 (MySQL + phpMyAdmin)
├── api-test.html               # API测试页面
└── DEVELOPMENT_GUIDE.md        # 本指南
```

## 快速开始

### 前提条件
- Node.js 16+ (前端)
- Java 17 (后端，可选)
- Docker (可选，用于MySQL)

### 1. 启动前端服务
```bash
# 进入前端目录
cd web/qa-web

# 安装依赖 (如果未安装)
npm install

# 启动开发服务器
npm run dev
```

前端将在 `http://localhost:5173` 启动。

### 2. 启动后端服务 (可选)
```bash
# 进入后端目录
cd server/qa-service-user

# 使用Maven Wrapper启动 (需要Java 17)
./mvnw spring-boot:run
```

后端将在 `http://localhost:8080` 启动，提供医生数据API。

### 3. 启动MySQL数据库 (可选)
```bash
# 使用Docker启动MySQL和phpMyAdmin
docker-compose up -d

# 访问phpMyAdmin管理界面
# http://localhost:8080
# 用户名: qa_user
# 密码: qa_password
```

## API接口

### 医生相关API
- `GET /api/doctors/active` - 获取活跃医生列表
- `GET /api/doctors` - 获取所有医生
- `GET /api/doctors/statistics` - 获取医生统计
- `GET /api/doctors/{id}` - 根据ID获取医生
- `GET /api/doctors/username/{username}` - 根据用户名获取医生

### 数据格式示例
```json
{
  "success": true,
  "message": "Success",
  "data": [
    {
      "id": "doc001",
      "username": "dr-zhang-wei",
      "name": "张伟医生",
      "title": "主任医师",
      "department": "心内科",
      "avatar": "https://example.com/avatar.jpg",
      "experience": "15年临床经验",
      "isActive": true,
      "specialties": ["高血压", "冠心病", "心律失常"]
    }
  ]
}
```

## 数据库设计

### 主要表结构
1. **doctors** - 医生信息表
2. **departments** - 科室表
3. **specialties** - 专业领域表
4. **doctor_specialties** - 医生专业关联表

### 初始化脚本
- `init-scripts/01-create-tables.sql` - 创建表结构
- `init-scripts/02-insert-initial-data.sql` - 插入初始数据
- `init-scripts/03-create-views.sql` - 创建视图
- `init-scripts/04-create-procedures.sql` - 创建存储过程

## 多语言支持

### 语言资源文件
- `src/locales/zh-CN.json` - 中文资源
- `src/locales/en-US.json` - 英文资源

### 使用方式
```typescript
import { t } from '../locales';

// 在组件中使用
const title = t('home.title');
```

### 语言切换
- 右上角语言切换按钮
- 支持中英文实时切换
- 语言偏好保存在localStorage

## 开发注意事项

### 前端
1. 数据层使用 `src/store/index.ts` 管理
2. API调用失败时自动回退到本地数据
3. 组件按需加载，支持懒加载

### 后端
1. 使用H2内存数据库简化开发
2. 自动初始化测试数据
3. 支持跨域请求

### 数据库
1. 开发环境使用H2，无需配置
2. 生产环境可切换MySQL
3. 提供完整迁移脚本

## 测试

### API测试
打开 `api-test.html` 文件进行API接口测试。

### 前端测试
```bash
# 运行前端测试
npm run test

# 运行开发服务器
npm run dev
```

### 后端测试
```bash
# 运行后端测试
./mvnw test

# 启动应用测试
./mvnw spring-boot:run
```

## 部署

### 前端部署
```bash
# 构建生产版本
npm run build

# 输出在 dist/ 目录
```

### 后端部署
```bash
# 打包JAR文件
./mvnw clean package

# 运行JAR文件
java -jar target/qa-service-user-*.jar
```

### 数据库部署
1. 运行 `init-scripts/` 中的SQL脚本
2. 或使用 `docker-compose.yml` 启动MySQL

## 故障排除

### 前端无法启动
1. 检查Node.js版本
2. 清除node_modules并重新安装
3. 检查端口5173是否被占用

### 后端无法启动
1. 检查Java 17是否安装
2. 检查端口8080是否被占用
3. 检查Maven依赖是否下载完成

### API不可用
1. 检查后端服务是否启动
2. 查看浏览器控制台错误
3. 前端会自动使用备用数据

### 数据库连接问题
1. 检查MySQL服务是否运行
2. 验证连接配置
3. 使用H2数据库进行开发

## 贡献指南

1. Fork项目
2. 创建功能分支
3. 提交更改
4. 创建Pull Request

## 许可证

MIT License