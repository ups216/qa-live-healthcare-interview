---
name: qa-live-healthcare-需求实现方案
overview: 针对 README 面试要求中的 3 项需求（首页中英文切换、医生数据 MySQL 化 + 前后端联调、问诊用户账号密码登录）输出一份完整的落地方案文档（/docs/IMPLEMENTATION_PLAN.md），覆盖 MyBatis 持久层、JWT 鉴权、docker-compose 编排、curl 测试与全套交付文档，并给出可执行的 AI Coding 提示词编排。
todos:
  - id: prepare-context
    content: 用 [subagent:code-explorer] 补齐 Consultation.vue 与 DoctorRoom.vue 对 doctors 的全部消费点，确定零改动边界
    status: completed
  - id: write-plan-foundation
    content: 编写 docs/IMPLEMENTATION_PLAN.md 第 1-3 章：现状盘点、架构全景图、分支与提交策略
    status: completed
    dependencies:
      - prepare-context
  - id: write-plan-req1
    content: 编写方案需求1 章节：locales 目录结构、15 条 key 映射表、LangSwitcher 与 AppHeader 挂载、Home.vue 改造边界
    status: completed
  - id: write-plan-req2
    content: 编写方案需求2 章节：docker-compose 编排、t_doctor 表与 JSON 列选型、MyBatis 分层、4 个医生接口契约、前端 api 层与三态、Vite proxy
    status: completed
    dependencies:
      - write-plan-foundation
  - id: write-plan-req3
    content: 编写方案需求3 章节：PRD 模板裁剪、t_patient 表、三个患者接口、JWT 拦截器白名单、PatientLogin 页面与路由守卫
    status: completed
    dependencies:
      - write-plan-req2
  - id: write-plan-test-docs
    content: 编写方案测试章节：curl 用例覆盖矩阵、可复制命令模板、scripts/api-test.sh 编排、TEST_REPORT 模板
    status: completed
    dependencies:
      - write-plan-req3
  - id: write-plan-prompts-risk
    content: 编写方案交付物与风险章节：docs 文件树、视频录制脚本、PR 模板、AI 提示词分批编排、10 项风险与缓解、1 天排期表
    status: completed
    dependencies:
      - write-plan-test-docs
  - id: review-plan
    content: 通读校验方案文档，确保路径/接口/依赖与代码库事实一致且每条可转为 AI 提示词
    status: completed
    dependencies:
      - write-plan-prompts-risk
---

## 产品概述

面向 `qa-live-healthcare-interview` 在线医疗问诊项目，产出一份可直接指导 AI Coding 分步执行的 Markdown 方案文档 `/Users/bhd/IdeaProjects/qa-live-healthcare-interview/docs/IMPLEMENTATION_PLAN.md`。方案需覆盖 README 面试要求中的 3 项编码需求 + 2 项测试要求 + 3 项交付物要求，且严格遵循「所有内容使用 AI Coding 提示词完成」的约束。

## 核心功能

1. **首页中英文切换**：在全局导航右上角新增语言切换下拉（中文/English），切换后首页全部 UI 文案即时变更；语言包存放于 `web/qa-web/src/locales`；翻译范围限定为首页，Header/Footer 保持中文。
2. **医生数据 MySQL 化与前后端联调**：以 docker-compose 编排 MySQL + phpMyAdmin；`qa-service-user` 提供医生列表 REST API（MyBatis 持久层）；前端医生页面改为从后端拉取，完成 Vite 代理与三态（加载/错误/空）联调。
3. **问诊用户账号密码登录**：编写 PRD 模板与 PRD 文档（含任务拆分步骤），并据其实现独立登录/注册页与 JWT 鉴权链路，替换原有「姓名+生日」验证。
4. **接口测试体系**：以 curl 编写全量接口用例，产出 `/docs/API_TEST.md` 与可复用测试脚本、测试报告模板。
5. **交付文档矩阵**：docs 下 PRD 模板、PRD 文档、设计文档、API_TEST、测试报告、prompts.md 及视频录制脚本清单、PR 描述模板。

## 已确认选型约束

- 患者登录：新增独立 `/patient/login`（登录+注册 Tab）彻底替换旧流程，患者入 MySQL，BCrypt 存储密码。
- 持久层：MyBatis XML（Mapper 接口 + XML SQL）。
- 鉴权：JWT + 拦截器，前端 localStorage 存 token，axios 拦截器注入。
- i18n：切换器置于 AppHeader 右上角，翻译范围仅限 Home.vue。

## 技术栈基线（已核对源码，直接沿用）

**后端**：Maven（模块内均带 `mvnw`/`mvnw.cmd`，`distributionType=only-script` → Maven 3.9.11）、Spring Boot 3.5.7（`spring-boot-starter-parent`，无 server 根聚合 pom）、Java 17、`spring-boot-starter-web` + `actuator`。模块 `qa-service-user`(:8080) / `qa-service-question`(:8081) / `qa-service-statistic`(空)。**无 Spring Cloud、无注册中心、无任何数据库依赖**。

**前端**：Vue 3.5 + TypeScript 5.5 + Vite 5.4 + vue-router 4.6 + **Ant Design Vue 4.2.6**（`main.ts` 全量 `app.use(Antd)`）；状态管理为手写 `reactive` 单例 `src/store/index.ts`（无 Pinia）；全部 `<script setup lang="ts">`；样式为 `<style scoped>` 纯 CSS。

**现状缺口（方案需列为改造项）**：

1. 无 `vue-i18n`、无 `src/locales` 目录。
2. 无 HTTP 客户端（`src` 下 `axios|fetch|baseURL|proxy` 搜索 0 命中）；`vite.config.ts` 全文 7 行，无 proxy、无 alias。
3. `@ant-design/icons-vue` 未在 `package.json` 声明，现靠传递依赖解析。
4. `qa-service-user` 缺 service/entity/mapper/dto/exception 分层、缺统一响应封装与全局异常处理、缺 datasource 配置。
5. 无 `docker-compose.yml`、无 `/docs`、无测试脚本、无 CI。
6. `tsconfig.app.json` 开启 `strict` + `noUnusedLocals` + `noUnusedParameters`，生成代码有未使用变量即 build 失败。

## 实现方案总览

### 架构分层

```
浏览器(Vue3+i18n+axios) --/api--> Vite proxy(5173) --> qa-service-user:8080
                                                        ├─ Controller (Doctor/Patient/Test)
                                                        ├─ Service
                                                        ├─ Mapper (XML) ── MySQL 8 (docker + phpMyAdmin:8088)
                                                        └─ JwtInterceptor (白名单) 
```

### 关键设计决策与理由

- **MyBatis XML 置于 `src/main/resources/mapper/*.xml`**（而非 `src/main/java` 同级）：避免 Maven 默认不打包含 Java 目录 XML 的经典坑，`mybatis.mapper-locations=classpath*:/mapper/**/*.xml` 即可命中，零额外 `<resources>` 配置。
- **specialties 用 JSON 列 + 自定义 TypeHandler**：QPS 低、改动面小；子表需额外 JOIN/N+1 处理，收益不足以抵消成本。若 MySQL 版本不支持 JSON 可降级为 `VARCHAR(512)` 逗号分隔 + TypeHandler。
- **统一响应 `ApiResponse<T>{code,message,data,timestamp}`**：现 `TestController` 裸返 Map，新增接口前先立规范，避免前端逐接口适配。同时新增 `@RestControllerAdvice` 处理业务/参数/系统异常。
- **VO 剔除 password**：医生 DoctorVO 不含密码字段；前端 `Doctor` 类型需移除 `password`，并同步评估 `DoctorLogin.vue` 的本地校验逻辑（需求3 不改造医生登录，故保留 JSON 或改用医生登录接口，方案给出取舍）。
- **store 去 JSON 化的最小爆炸半径**：Doctors.vue 改为 `api/doctor.ts` 拉取；Home.vue 的 `getActiveDoctors()`/`getStatistics()` 仍读同一 `doctors` 数组 → 统一由 store 新增 `loadDoctors()` 在 App 挂载时拉取并写入 `state.doctors`，使 Consultation.vue(9 处)、DoctorRoom.vue(6 处) 零改动，避免连锁回归。
- **JWT 用 `jjwt` 0.12.x（api/impl/jackson 三件套）+ HandlerInterceptor**，仅引 `spring-security-crypto` 的 `BCryptPasswordEncoder`，不引完整 Spring Security（避免默认登录页/CSRF 干扰现有裸接口）。
- **MyBatis 日志**：开发期 `mybatis.configuration.log-impl=org.apache.ibatis.logging.stdout.StdOutImpl`，交付前改回 Slf4j，防止日志泄漏 SQL 参数。

### 依赖版本处理原则

`mybatis-spring-boot-starter`、`mysql-connector-j`、`jjwt`（0.12.x）、`vue-i18n`（^9）、`axios` 的具体版本号在方案中标为「执行阶段需结合 Spring Boot 3.5.7 兼容矩阵 / npm registry 实时核对」，不在方案中硬编码可能过期版本。

### 接口契约

- `GET /api/doctors`、`GET /api/doctors/active`、`GET /api/doctors/{id}`、`GET /api/doctors/username/{username}`
- `POST /api/patient/register`、`POST /api/patient/login`（返回 token）、`GET /api/patient/me`（需 Bearer）
- 白名单：`/api/doctors/**`、`/api/patient/login`、`/api/patient/register`、`/api/test/**`、`/actuator/**`

### 实现注意点（防回归）

- CORS 双配置冲突：`application.properties` 写 `allow-credentials=false`，而 `CorsConfig` Java Bean 为 `allowCredentials(true)` 且 Bean 优先生效 —— 方案要求统一收敛到 Java 配置并收窄 `allowedOriginPatterns` 到 `http://localhost:5173`。
- MySQL 连接串须带 `serverTimezone=Asia/Shanghai` 与 `useUnicode=true&characterEncoding=utf8mb4`。
- `is_active` → `isActive` 依赖 `mybatis.configuration.map-underscore-to-camel-case=true`。
- 前端 Azure：所有新增 `.vue` 在严格模式下必须通过 `npm run build`（`vue-tsc -b`）验证。

## 目录结构

```
/Users/bhd/IdeaProjects/qa-live-healthcare-interview/
├── docs/                                        # [NEW] 交付文档根目录（README 要求）
│   ├── IMPLEMENTATION_PLAN.md                   # [NEW] 本次主产物：全局需求实现方案
│   ├── PRD_TEMPLATE.md                          # [NEW] PRD 模板（基于 .asdm/toolsets/prd-builder/spec/feature-prd-spec.md 裁剪）
│   ├── PRD-patient-auth.md                      # [NEW] 用模板产出的「问诊用户账号密码登录」PRD，含 ≤10 的拆分步骤
│   ├── DESIGN-patient-auth.md                   # [NEW] 设计文档：表结构、接口契约、JWT 时序、前端状态流
│   ├── API_TEST.md                              # [NEW] curl 测试流程说明 + 用例矩阵，供 AI 执行测试
│   ├── TEST_REPORT.md                           # [NEW] 测试报告模板（执行后填充实际结果）
│   └── prompts.md                               # [NEW] 全过程使用的提示词清单
├── docker-compose.yml                           # [NEW] mysql:8.0 + phpmyadmin:8088 + healthcheck + volume + network
├── db/init/
│   ├── 01-schema.sql                            # [NEW] t_doctor / t_patient 建表
│   └── 02-data.sql                              # [NEW] 5 条医生种子数据（含 specialties JSON）
├── scripts/api-test.sh                          # [NEW] 一键 curl 回归脚本
├── server/qa-service-user/
│   ├── pom.xml                                  # [MODIFY] 增 mybatis/mybatis-spring-boot-starter、mysql-connector-j、 jjwt 三件套、spring-security-crypto
│   └── src/main/java/com/leansofx/qaserviceuser/
│       ├── common/ApiResponse.java              # [NEW] 统一响应封装
│       ├── exception/GlobalExceptionHandler.java# [NEW] @RestControllerAdvice
│       ├── entity/Doctor.java  Patient.java     # [NEW] 实体
│       ├── dto/DoctorVO.java  LoginRequest.java RegisterRequest.java LoginResponse.java  # [NEW] DTO
│       ├── mapper/DoctorMapper.java PatientMapper.java               # [NEW] Mapper 接口
│       ├── handler/JsonStringListTypeHandler.java                    # [NEW] JSON↔List<String>
│       ├── service/DoctorService.java PatientService.java            # [NEW] 业务层
│       ├── controller/DoctorController.java PatientController.java   # [NEW] REST 接口
│       ├── config/JwtInterceptor.java WebConfig.java CorsConfig.java # [NEW/MODIFY] 拦截链路与跨域收敛
│       └── util/JwtUtil.java                    # [NEW] 签发/校验 token
│   └── src/main/resources/
│       ├── application.properties               # [MODIFY] datasource + mybatis + jwt 配置
│       └── mapper/DoctorMapper.xml PatientMapper.xml  # [NEW] XML SQL
└── web/qa-web/
    ├── package.json                             # [MODIFY] 增 vue-i18n@^9、axios、显式声明 @ant-design/icons-vue
    ├── vite.config.ts                           # [MODIFY] server.proxy /api -> http://localhost:8080
    ├── src/locales/zh-CN.ts en-US.ts index.ts   # [NEW] 语言包与 createI18n（localStorage 持久化 + 语言嗅探）
    ├── src/components/LangSwitcher.vue          # [NEW] 右上角语言下拉
    ├── src/components/AppHeader.vue             # [MODIFY] 挂载 LangSwitcher + 样式 gap 适配
    ├── src/views/Home.vue                       # [MODIFY] 全部文案替换为 $t(...)
    ├── src/views/Doctors.vue                    # [MODIFY] onMounted 拉取 + 三态
    ├── src/views/PatientLogin.vue               # [NEW] 登录/注册 Tabs
    ├── src/api/doctor.ts patient.ts request.ts  # [NEW] axios 封装（拦截器注入 Bearer、401 处理）
    ├── src/store/index.ts                       # [MODIFY] 新增 loadDoctors/loginPatient/patient token 态
    ├── src/router/index.ts                      # [MODIFY] 新增 /patient/login + 路由守卫
    └── src/main.ts                              # [MODIFY] app.use(i18n)
```

## Agent Extensions

### SubAgent

- **code-explorer**
- Purpose：在编写「现状盘点」与 JS工程师核对 codebase 事实（如 Consultation.vue/DoctorRoom.vue 对 store 的耦合点、.asdm PRD 模板细节）时做定向补充探查
- Expected outcome：方案中的每条路径与行号引用均可在仓库中验证，避免出现不存在的文件或 API

### Skill

- **find-skills**
- Purpose：若执行阶段需要额外的文档生成（docx/pdf）或浏览器自动化（截屏取证用于视频/测试报告）技能，先检索是否有可用 skill
- Expected outcome：明确到可用的 skill 名称，避免臆造能力