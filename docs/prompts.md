# Prompts 记录

本文档记录在完成 QA Live Healthcare Interview 面试项目过程中使用的所有 AI Coding 提示词。

---

## 初始阶段

### 提示词 1
> 帮我分析一下这个项目

**目的**：了解项目整体结构、技术栈、数据模型

**产出**：项目分析报告，梳理了前端（Vue 3 + Ant Design Vue）、后端（Spring Boot）、数据模型（Doctor/Patient/Question）、路由表等

**目的**：确认 prompts.md 的记录方式，决定由 AI 自动记录

### 提示词 3
> 根据你分析的项目结构生成当前项目的一个skills，供以后使用，然后后续的提示词统一存放到 /docs/prompts.md里面

**目的**：创建项目级 skill 以便后续 AI 编码时快速获取上下文，同时建立 prompts.md 文件

---

## 需求2：医生数据后端化

### 提示词 4
> 其中 创建 docker-compose.yml 用于创建mysql数据库，并挂接phpmyadmin作为数据库服务器管理界面，要安装docker服务吗 我windows系统安装docker失败了怎么办

**目的**：创建 Docker Compose 配置文件和数据库初始化 SQL 脚本；解决 Windows 安装 Docker 失败的问题，改用本地 MySQL

**产出**：docker-compose.yml（MySQL 8.0 + phpMyAdmin）、server/init-db/01-schema.sql、server/init-db/02-data.sql

### 提示词 5
> 好的，我自己安装了数据库，那这个数据从哪里来，还有表怎么创建

**目的**：从前端 JSON 数据迁移到数据库，创建建表和初始数据 SQL 脚本

**产出**：将 doctor-user-list.json、patient-user.json、question-list.json 中的数据迁移到 SQL 脚本中

---

## 需求3：患者用户名密码登录 — PRD 编写

### 提示词 6
> 首先在 /docs 目录中编写一个规范的PRD文档模版

**目的**：创建标准 PRD 文档模板，供后续特性开发使用

**产出**：/docs/PRD-template.md

### 提示词 7
> 使用刚才生成的模板编写本次需求的PRD 文档，要求必须深刻理解当前需求，符合实际要求，也放到docs目录下

**目的**：基于模板为"患者用户名密码登录"特性编写完整的 PRD 文档

**产出**：/docs/PRD-patient-login.md

### 提示词 8
> 根据PRD文档，分布实现当前系统的需求

**目的**：继续完成 PRD 文档的剩余部分（实现计划、验收标准等）

---

## 需求3：患者用户名密码登录 — 开发实施

### 提示词 9
> 我们的数据库是mysql： jdbc://localhost:3306/qa_healthcare root/1234， 帮我生成到配置文件里面，然后配置pom里面所需的数据库依赖包

**目的**：配置后端 Spring Boot 的 MySQL 数据源连接和 Maven 依赖

**产出**：更新 application.properties（MySQL URL/用户名/密码/JPA配置）、更新 pom.xml（spring-boot-starter-data-jpa、mysql-connector-j、spring-security-crypto、spring-boot-starter-validation）

### 提示词 10
> 继续

**目的**：按照 PRD 中的实现计划继续开发——完成后端实体、Repository、Service、Controller，以及前端页面改造

**产出**：后端 Patient/PatientRepository/PatientDTO/LoginRequest/RegisterRequest/PatientController/UserService；前端 Consultation.vue 登录/注册表单改造；Vite 代理配置

### 提示词 11
> npm 不识别，帮我安装下

**目的**：解决 npm 命令无法识别的问题

**产出**：确认需要安装 Node.js

### 提示词 12
> nodejs， 针对当前项目，安装哪个版本的

**目的**：确认当前项目所需的 Node.js 版本

**产出**：推荐 Node.js 20 LTS（Vite 5.4 / Vue 3.5 要求）

### 提示词 13
> npm run dev ... 'vite' 不是内部或外部命令

**目的**：解决 vite 命令不识别的问题

**产出**：需先执行 npm install 安装 node_modules

### 提示词 14
> 好的，前端页面怎么访问

**目的**：确认前端开发服务器启动后的访问地址

**产出**：默认 http://localhost:5173

### 提示词 15
> 前端我已经启动了，怎么访问

**目的**：确认前端访问方式

**产出**：浏览器打开 http://localhost:5173

### 提示词 16
> 默认的用户名和密码是多少

**目的**：获取系统测试账号信息

**产出**：医生 dr-zhang-wei/123456，患者赵明 patient-zhao-ming/123456

### 提示词 17（附带 SQL 错误日志）
> [SQL Error] Unknown column 'p1_0.password' in 'field list'

**目的**：解决数据库 patient 表缺少 username/password 列的问题

**产出**：/server/init-db/03-migrate-patient.sql 迁移脚本，为 patient 表添加 username 和 password 列

### 提示词 18（附带 BCrypt 截图）
> rawPassword: 123456, patient.getPassword(): $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy

**目的**：解决 BCrypt 密码哈希不匹配的问题——SQL 中硬编码的哈希值与 "123456" 不对应

**产出**：新增临时接口 POST /api/patient/init-passwords，使用 BCryptPasswordEncoder.encode("123456") 重新生成正确的哈希值并更新数据库

### 提示词 19
> init-passwords 帮我改成get调用

**目的**：将 init-passwords 接口从 POST 改为 GET，方便在浏览器中直接测试

**产出**：PatientController 中 @PostMapping → @GetMapping

---

## 需求1：首页中英文国际化

### 提示词 20
> 检查下这个需求有没有做：给web端的首页添加中英文切换能力，在页面右上角添加语言切换下拉菜单，在用户选择 中文/English 选项时动态切换页面显示内容到对应语言。中英文语言资源文件需要保存在 web/qa-web/src/locales 目录中。只需要处理首页本身，无需处理其他页面

**目的**：验证需求1完成情况

**产出**：确认 vue-i18n 集成、语言资源文件、切换功能均已实现

---

## 需求2：前后端联调修复

### 提示词 21
> 检查第二个需求有没有做：当前应用的数据全部使用json文件的方式在前端项目中存储，请将医生相关的数据使用mysql数据库存储。具体要求如下：创建 docker-compose.yml 用于创建mysql数据库，并挂接phpmyadmin作为数据库服务器管理界面；在 qa-service-user 服务中创建可以支持前端医生页面显示医生列表所需要的api；完成前后端联调

**目的**：发现前端 store 中医生数据仍从 JSON 加载而非后端 API，需要修复前后端联调

**产出**：识别出 store/index.ts 仍 import doctor-user-list.json，loginDoctor 仍用本地比对

### 提示词 22
> 是的，帮我修复下

**目的**：修复前后端联调——store 改为 API 调用、后端增加医生登录接口、App.vue 启动加载医生数据

**产出**：
- 后端：DoctorController 新增 POST /api/doctor/login，UserService 新增 loginDoctor 方法
- 前端：store 移除 JSON 导入，新增 loadDoctors() 异步方法，loginDoctor 改为调用后端 API
- 前端：App.vue onMounted 调用 loadDoctors()
- 前端：DoctorLogin.vue 适配异步登录

---

## 测试与交付

### 提示词 23
> 根据测试要求和交付要求，生成对应的测试报告和产物

**目的**：更新 API_TEST.md（增加医生登录接口测试用例）、生成测试报告、更新 prompts.md

**产出**：更新 /docs/API_TEST.md，新建 /docs/test-report.md

### 提示词 24
> 我已经重新设置了环境变量17，继续进行实际执行 curl 测试

**目的**：使用 Java 17+ 环境启动后端服务，执行 PowerShell 自动化测试脚本，验证全部 16 个 API 接口

**产出**：16 个接口全部 PASS（100% 通过率），更新 /docs/test-report.md 为含实际测试结果的正式版

---

## 提示词统计

| 阶段 | 数量 | 提示词编号 |
|------|------|-----------|
| 初始阶段 | 3 | 1-3 |
| 需求2：医生数据后端化 | 2 | 4-5 |
| 需求3：PRD编写 | 3 | 6-8 |
| 需求3：开发实施 | 11 | 9-19 |
| 需求1：国际化 | 1 | 20 |
| 需求2：联调修复 | 2 | 21-22 |
| 测试与交付 | 2 | 23-24 |
| **合计** | **24** | — |
