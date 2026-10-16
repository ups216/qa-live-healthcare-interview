# qa-live-healthcare-interview 需求实现方案

> 本文对应 `README.md`「技术要求 / 需求列表 / 测试要求 / 交付物要求」全部条目，是后续 **AI Coding 分步执行**的蓝图。
> 文中每条结论均已对照仓库源码核验过路径与行号，可直接引用为提示词上下文。

| 项 | 内容 |
|---|---|
| 目标分支 | `feat/ai-interview-delivery`（特性分支，最终 PR → `main`） |
| 工期约束 | 1 天（建议 8~10 小时，见第 10 章排期表） |
| 强制约束 | **所有代码 / 测试 / 文档必须经 AI Coding 提示词产出**，不得手写（允许对 AI 产出做手工微调） |
| 已确认选型 | 患者登录=独立登录注册页；持久层=MyBatis XML；鉴权=JWT+拦截器；i18n=切换器在 AppHeader、翻译范围仅 Home.vue |
| 文档状态 | PLANNED |

---

## 目录

1. [项目现状盘点](#1-项目现状盘点)
2. [总体架构与改造全景](#2-总体架构与改造全景)
3. [分支与提交策略](#3-分支与提交策略)
4. [需求1：首页中英文切换](#4-需求1首页中英文切换)
5. [需求2：医生数据 MySQL 化与前后端联调](#5-需求2医生数据-mysql-化与前后端联调)
6. [需求3：问诊用户账号密码登录](#6-需求3问诊用户账号密码登录)
7. [测试方案：curl 接口测试](#7-测试方案curl-接口测试)
8. [交付物清单](#8-交付物清单)
9. [AI Coding 提示词编排](#9-ai-coding-提示词编排)
10. [风险与工期排期](#10-风险与工期排期)

---

## 1. 项目现状盘点

### 1.1 仓库结构

```
qa-live-healthcare-interview/
├── README.md                      2.39 KB / 41 行 —— 面试要求
├── .asdm/                         AI Coding 工具集元数据（prd-builder / context-builder / basic-tools）
├── _TRAINING_ASSETS/
│   ├── qa-service-question.zip    17.27 KB  种子工程备份
│   └── qa-service-user.zip        14.03 KB  种子工程备份
├── server/
│   ├── qa-service-user/           用户/医生域（唯一有业务雏形，端口 8080）
│   ├── qa-service-question/       问诊域（仅启动类，端口 8081）
│   └── qa-service-statistic/      统计域（仅 .gitkeep，完全空实现）
└── web/qa-web/                    Vue3 前端
```

> `.asdm/toolsets/prd-builder/spec/feature-prd-spec.md` 已存在一份结构化的 Feature PRD 模板（10 章），
> **是需求3「编写 PRD 模板」最直接的裁剪基线**，详见 §6.3.1。

### 1.2 后端技术栈基线（已核对 `server/qa-service-user/pom.xml`）

| 维度 | 现状 |
|---|---|
| 构建 | Maven；**无 server 根聚合 pom**，两模块各自以 `spring-boot-starter-parent:3.5.7` 为父（`relativePath` 空），独立构建 |
| Maven Wrapper | 每模块一份 `mvnw` / `mvnw.cmd`；`maven-wrapper.properties` 为 `wrapperVersion=3.3.4`、`distributionType=only-script`、`apache-maven-3.9.11`（**首次运行需联网下载 Maven**） |
| JDK | 17 |
| 框架 | Spring Boot 3.5.7；**无 Spring Cloud / Nacos / Eureka / Gateway / Config / OpenFeign** |
| 现有依赖 | `spring-boot-starter-web`、`spring-boot-starter-actuator`、`spring-boot-starter-test` |
| 配置 | `application.properties` 34 行：端口 8080、CORS、Actuator；**无 `spring.datasource.*`、无 `server.servlet.context-path`（上下文为 `/`）** |
| 分层 | `controller`（仅 `TestController`）、`config`（仅 `CorsConfig`）；**service / entity / dto / mapper / repository / exception 全不存在** |
| 响应封装 | **无**。`TestController` 直接返回 `Map<String, Object>`，**无 `@RestControllerAdvice`** |
| 数据库 | **无任何 DB 依赖**（无 jpa / mybatis / jdbc / mysql-connector / flyway） |
| 测试 | 仅 `QaServiceUserApplicationTests.contextLoads()` 冒烟；无 MockMvc、无 curl 脚本 |

现有唯一接口：

```9:35:server/qa-service-user/src/main/java/com/leansofx/qaserviceuser/controller/TestController.java
@RestController
@RequestMapping("/api/test")
public class TestController {
    @GetMapping("/cors")   // → Map{message,timestamp,service}
    @PostMapping("/cors")  // → Map{message,receivedData,timestamp,service}
    @RequestMapping(value = "/cors", method = RequestMethod.OPTIONS)
}
```

**已知配置冲突（改造时必须收敛）**：`application.properties` 写 `spring.web.cors.allow-credentials=false`，
但 `config/CorsConfig.java` 的 `CorsConfigurationSource` Bean 里是 `allowCredentials(true)`，
**Java Bean 优先生效**，实际行为为 `allowedOriginPatterns("*") + credentials=true`。

### 1.3 前端技术栈基线（已核对 `web/qa-web/package.json`）

| 维度 | 现状 |
|---|---|
| 框架 | Vue 3.5 + TypeScript 5.5 + Vite 5.4 + vue-router 4.6；全部 `<script setup lang="ts">` |
| UI | **Ant Design Vue 4.2.6**，`main.ts` 全量 `app.use(Antd)` + `reset.css` |
| 状态 | **无 Pinia/Vuex**，手写 `reactive` 单例 `src/store/index.ts`（158 行） |
| 样式 | `<style scoped>` 纯 CSS，主色 `#1890ff`，渐变 `linear-gradient(135deg,#667eea,#764ba2)`，`@media(max-width:768px)` 断点 |
| 类型 | 仅 4 个 `.ts` 文件（`main.ts`/`vite-env.d.ts`/`router/index.ts`/`store/index.ts`）；`Doctor`/`Patient`/`Question` 接口内联于 `store/index.ts:6-46` |
| i18n | **无 `vue-i18n`，无 `src/locales`** |
| HTTP | **无任何 HTTP 客户端**（`src` 下 `axios\|fetch\|baseURL\|proxy` 搜索 0 命中） |
| 构建配置 | `vite.config.ts` 全文 7 行，**无 `server.proxy`、无 `resolve.alias`、无 `base`** |
| TS 严格度 | `strict` + `noUnusedLocals` + `noUnusedParameters` → **生成代码残留未使用变量会 `npm run build` 失败** |
| 测试 | **无**（无 vitest/jest，无 `*.spec.ts`） |
| 容器/CI | **无** `docker-compose.yml` / `Dockerfile` / `.github` |

路由（`src/router/index.ts`，共 53 行）：`createWebHistory()` **无 basename**、**无懒加载**（7 视图全静态 import 打成单 chunk）、**无路由守卫**。

| path | name | 组件 |
|---|---|---|
| `/` | Home | Home.vue |
| `/consultation` | Consultation | Consultation.vue |
| `/consultation/:doctorUsername` | ConsultationRoom | Consultation.vue（**组件复用，参数变化不重挂载**） |
| `/doctors` | Doctors | Doctors.vue |
| `/about` | About | About.vue |
| `/doctor/login` | DoctorLogin | DoctorLogin.vue |
| `/doctor/room/:username` | DoctorRoom | DoctorRoom.vue |

### 1.4 数据流现状（纯前端 JSON）

```1:54:web/qa-web/src/store/index.ts
import doctorData from '../data/doctor-user-list.json';
import patientData from '../data/patient-user.json';
import questionData from '../data/question-list.json';
const state = reactive<State>({
  doctors: doctorData as Doctor[], patients: patientData as Patient[], questions: questionData as Question[],
  currentDoctor: null, currentPatient: null,
});
```

数据文件：`src/data/doctor-user-list.json`（5 位医生）、`patient-user.json`（5 位患者）、`question-list.json`。

```ts
// store/index.ts:6-17
export interface Doctor {
  id: string; username: string; password: string; name: string; title: string;
  department: string; avatar: string; experience: string; specialties: string[]; isActive: boolean;
}
```

医生 JSON 单条（建表直接对应）：

```json
{ "id":"doc001", "username":"dr-zhang-wei", "password":"123456", "name":"张伟医生",
  "title":"主任医师", "department":"心内科",
  "avatar":"https://images.pexels.com/photos/5215024/...w=400",
  "experience":"15年临床经验", "specialties":["高血压","冠心病","心律失常"], "isActive": true }
```

种子数据全集：`doc001` 张伟/主任医师/心内科/在线；`doc002` 李娜/副主任医师/儿科/在线；`doc003` 王强/主治医师/骨科/在线；
`doc004` 刘敏/主任医师/妇产科/**离线**；`doc005` 陈杰/副主任医师/消化内科/在线。密码全为明文 `123456`。

患者 JSON 单条：`{ "id":"patient001","name":"赵明","birthday":"1985-03-15","phone":"138****1234","gender":"男" }` —— **无 `username` / `password` 字段**，需求3 需新增列并做数据迁移。

### 1.5 缺口清单（方案必须处理的改造项）

| # | 缺口 | 影响需求 |
|---|---|---|
| G1 | 无 `vue-i18n`、无 `src/locales` | 需求1 |
| G2 | 无 HTTP 客户端；`vite.config.ts` 无 proxy | 需求2/3 |
| G3 | `@ant-design/icons-vue` **未在 `package.json` 声明**，5 个 vue 文件依赖它（靠传递依赖侥幸解析） | 全部 |
| G4 | 后端缺 service/entity/mapper/dto/exception 分层、缺统一响应封装与全局异常处理、缺 datasource | 需求2/3 |
| G5 | 无 `docker-compose.yml`、无 `Dockerfile` | 需求2 |
| G6 | 无 `/docs` 目录 | 需求3/测试/交付 |
| G7 | 无 curl 测试脚本、无测试报告 | 测试要求 |
| G8 | `index.html` 的 `<title>` 仍为 `Vite + Vue + TS`，`lang="en"` | 需求1（顺带） |
| G9 | CORS 双配置冲突（`allow-credentials` properties vs Java Bean 不一致） | 需求2/3 |
| G10 | 无 CI、无前端测试，回归靠手工 | 全部 |

### 1.6 store 消费点全表与「零改动边界」结论

这是需求2 能否安全落地的**关键前置结论**——把 `doctors` 改为远端拉取后，哪些视图必须改、哪些可不动。

| 文件 | `store.` 调用点 | 行号 | 读写 doctors？ |
|---|---|---|---|
| `views/Doctors.vue` | 1 + import = 2 | `53` import、`57` `store.state.doctors` | ✅ 直读（唯一所有者） |
| `views/Consultation.vue` | 9 + import = 10 | `179` `state.currentPatient`、`182` `getQuestionsByPatient`、`209` `getActiveDoctors()`、`215` `getDoctorByUsername()`、`230` `state.patients`、`234` `verifyPatient`、`244` `logoutPatient`、`283` `state.doctors.find()`、`285` `addQuestion` | ✅ `209/215/283` |
| `views/DoctorRoom.vue` | 6 + import = 7 | `135` `state.currentDoctor`、`140/146` `getQuestionsByDoctor`、`168` `logoutDoctor`、`203` `answerQuestion`、`212` `markQuestionAsAnswered` | ❌ 完全不碰 doctors |
| `views/Home.vue` | 2 + import = 3 | `121` `getStatistics()`、`122` `getActiveDoctors()` | ✅ 间接读 |
| `views/DoctorLogin.vue` | 1 + import = 2 | `81` `store.loginDoctor(username, password)` | ✅ 依赖 `password` 明文 |
| `views/About.vue` | **0** | 仅 import 7 个图标，模板全静态 | ❌ |
| `App.vue` / `AppHeader.vue` / `AppFooter.vue` / `router/index.ts` / `main.ts` | **0** | — | ❌ |

**零改动边界结论**

| 文件 | 结论 | 说明 |
|---|---|---|
| `Home.vue` | ✅ **零改动** | `121/122` 均为 `computed`，doctors 加载完成后自动重算 |
| `DoctorRoom.vue` | ✅ **零改动** | 只依赖 `state.currentDoctor`，不读 doctors 数组 |
| `About.vue` | ✅ **零改动** | 无 store |
| `DoctorLogin.vue` | ⚠️ **取决于后端** | `store.loginDoctor` 在 `store/index.ts:61` 做的是明文比对 `d.password === password`；后端 VO 若剔除 `password`，此处必改 |
| `Consultation.vue` | ⚠️ **技术可零改，但建议改** | 存在两处异步竞态，见下 |
| `Doctors.vue` | 🔧 **主动改造** | 唯一按需求要替换为远端数据源的出口 |
| `store/index.ts` | 🔧 **必改** | 加 `loadDoctors()`、`doctors` 初值改 `[]` |

**必须处理的两处异步竞态（改造前的隐藏 bug，异步化后会暴露）**

1. `Consultation.vue:215` —— `onMounted` 里**一次性同步**调用 `getDoctorByUsername()`。若首屏直开 `/consultation/:doctorUsername`
   而 `state.doctors` 尚未加载完成，`selectedDoctor` 会**静默拿不到**（`:216` 还额外校验 `doctor.isActive`，失败无任何提示，直接退化成未选中医生的下拉态）。
2. `AppFooter.vue:12-15` 用的是裸 `<a href="/...">`（**非 `<router-link>`**），点击会触发**整页刷新** → SPA 重新挂载、store 清空。
   这恰好是「首屏 doctors 未加载」竞态的高发入口。

**字段映射风险**：前端 `Doctor` 接口要求 `specialties: string[]` 与 `isActive: boolean`。
后端必须保证 JSON 反序列化后是**真数组**（非逗号串）与**真布尔**（非 `0/1` 字符串），否则 `Consultation.vue:216`、`Doctors.vue:38/41` 的真假判断会失真。

## 2. 总体架构与改造全景

### 2.1 目标架构

```
┌──────────────────────── 浏览器 ────────────────────────┐
│  Vue3 + Ant Design Vue                                  │
│  ├─ i18n 层      src/locales/{zh-CN,en-US,index}.ts     │  ← 需求1
│  ├─ LangSwitcher components/LangSwitcher.vue（右上角）  │
│  ├─ API 层       src/api/{request,doctor,patient}.ts    │  ← 需求2/3（axios + Bearer 拦截器）
│  └─ store        src/store/index.ts（reactive 单例）    │
└───────────────────────────┬────────────────────────────┘
                            │  /api/*  （同源请求）
              ┌─────────────▼──────────────┐
              │  Vite Dev Server :5173     │
              │  server.proxy['/api']      │  ← 新增，转发到 8080（开发期无跨域）
              └─────────────┬──────────────┘
                            │  http://localhost:8080
    ┌───────────────────────▼────────────────────────────────────┐
    │  qa-service-user:8080（Spring Boot 3.5.7 / Java 17）        │
    │  ┌──────────────────────────────────────────────────────┐  │
    │  │ JwtInterceptor（白名单放行 doctors/test/actuator）    │  │  ← 需求3
    │  ├──────────────────────────────────────────────────────┤  │
    │  │ Controller: DoctorController / PatientController     │  │
    │  ├──────────────────────────────────────────────────────┤  │
    │  │ Service:    DoctorService  / PatientService          │  │
    │  ├──────────────────────────────────────────────────────┤  │
    │  │ Mapper:     DoctorMapper   / PatientMapper（XML SQL） │  │  ← 需求2
    │  └───────────────────────────┬──────────────────────────┘  │
    │  ApiResponse<T> 统一封装 + GlobalExceptionHandler            │
    └──────────────────────────────┬─────────────────────────────┘
                                   │ JDBC (mysql-connector-j)
    ┌──────────────────────────────▼─────────────────────────────┐
    │  Docker Compose                                             │
    │  ├─ mysql:8.0        :3306  volume 持久化 + healthcheck     │
    │  └─ phpmyadmin       :8088  PMA_HOST=mysql                  │
    └─────────────────────────────────────────────────────────────┘
```

### 2.2 分层职责约定

| 层 | 包名 | 职责 | 禁止 |
|---|---|---|---|
| Controller | `com.leansofx.qaserviceuser.controller` | 参数接收、调用 Service、返回 `ApiResponse` | 不写 SQL、不做业务校验逻辑 |
| Service | `...service` | 业务规则、事务边界、密码加解密编排 | 不感知 HTTP |
| Mapper | `...mapper` + `resources/mapper/*.xml` | 纯 SQL 与结果映射 | 不含业务逻辑 |
| Entity | `...entity` | 与表一一对应 | 不直接对外暴露（含 `password`） |
| DTO/VO | `...dto` | 请求入参 VO 与响应 VO | 响应结构**必须**剔除 `password` |
| Common | `...common` | `ApiResponse<T>` | — |
| Exception | `...exception` | `@RestControllerAdvice` 统一兜底 | — |
| Config | `...config` | CORS、WebMvc 拦截器注册、MyBatis `@MapperScan` | — |
| Util | `...util` | `JwtUtil` | — |
| Handler | `...handler` | MyBatis `TypeHandler`（JSON ↔ `List<String>`） | — |

### 2.3 关键设计决策与理由

| # | 决策 | 理由 |
|---|---|---|
| D1 | MyBatis XML 放 **`src/main/resources/mapper/*.xml`**，不放 `src/main/java` 同级 | 规避 Maven 默认不打包含 Java 目录下 XML 的经典坑；`mybatis.mapper-locations=classpath*:/mapper/**/*.xml` 直接命中，**零 `<resources>` 额外配置** |
| D2 | `specialties` 用 **MySQL `JSON` 列 + 自定义 `TypeHandler`** | 医生列表页 QPS 极低；子表方案需额外 JOIN 或处理 N+1，收益不足以抵消成本。若目标 MySQL 版本不支持 JSON 列，降级为 `VARCHAR(512)` 逗号分隔 + 同一个 TypeHandler（对上层透明） |
| D3 | 引入 **统一响应 `ApiResponse<T>{code,message,data,timestamp}`** | 现状 `TestController` 裸返 `Map`，若新增 6 个接口仍各自裸返，前端需逐接口适配；先立规范再扩展成本最低。**同时新增 `@RestControllerAdvice`** 兜底业务/参数/系统异常 |
| D4 | **VO 剔除 `password`**，前端 `Doctor` 类型同步移除 `password` | 安全红线（即使演示项目也不能把密码明文下发）。连带影响见 D5 |
| D5 | **保留 `DoctorLogin.vue` 走 JSON/本地校验**，不作为本次改造范围 | README 需求3 只要求「为**问诊用户**提供用户名密码登录」，医生登录不在范围内。但 D4 剔除 password 后 `store.loginDoctor`（`store/index.ts:61` 明文比对）会失效 → **对策：`store/index.ts` 内部保留一份仅供医生登录用的本地凭证表**（继续 import `doctor-user-list.json` 的 username/password），而 `state.doctors` 改由 API 灌入且不含密码。如此 `DoctorLogin.vue` 保持零改动 |
| D6 | 前端统一由 **`store.loadDoctors()`** 将数据写入 `state.doctors`，在 `App.vue` `onMounted` 触发 | §1.6 已证明 Home/DoctorRoom/About 在此策略下零改动；`Consultation.vue` 只需补一处 await。**避免对每个消费点做连锁改造** |
| D7 | JWT 用 **`jjwt` 0.12.x（api/impl/jackson 三件套）+ `HandlerInterceptor`**，密码加密用 **`spring-security-crypto` 的 `BCryptPasswordEncoder`** | 不引入完整 Spring Security —— 后者会带来默认登录页、CSRF、默认鉴权，直接干扰现有裸接口（`/api/test/cors`、未来 `/api/doctors/**`）的可访问性，改造成本陡增 |
| D8 | MyBatis 日志开发期开 `org.apache.ibatis.logging.stdout.StdOutImpl` | AI 生成的 SQL 需要快速验证；**交付前必须改回 Slf4j**，防止控制台打印 SQL 参数造成的信息泄漏观感问题 |
| D9 | CORS 统一收敛到 `CorsConfig` Java Bean，并把 `allowedOriginPatterns` 收窄为 `http://localhost:5173` | 修掉 §1.2 的 properties/Java 双配置冲突；虽然开发期走 Vite proxy 不触发跨域，但 README 要求 curl 直连测试，保持收窄更符合生产习惯。**注意：`context-path` 保持 `/` 不变**，否则会破坏现有 `/api/test/cors` 契约 |
| D10 | MySQL 连接串显式带 `serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=UTF-8` | 避免 CST 时区歧义与中文乱码（医生数据全是中文）。**⚠️ 易错：`characterEncoding` 必须写 Java 字符集名 `UTF-8`，不能写 MySQL 服务端字符集名 `utf8mb4`，否则 JDBC 直接抛 `Unsupported character encoding 'utf8mb4'`**（此错误在 P8 联调时实际发生过，已修正） |

### 2.4 依赖版本处理原则

### 2.4 依赖版本核对结论（P1 已执行，核对日期 2026-10-15）

已从 Maven Central / npm registry 实测核对，结论如下：

| 依赖 | 选定版本 | 核对依据 |
|---|---|---|
| `mybatis-spring-boot-starter` | **3.0.5** | ⚠️ 仓库已存在 **4.0.0 / 4.0.1 / 4.1.0** 线，但其 `pom` 声明 `<spring-boot.version>4.1.0</spring-boot.version>`，**需 Spring Boot 4，与本项目 3.5.7 不兼容**。故取 3.x 线最新 `3.0.5`；同时排除 2.x 线（那是 Spring Boot 2.7） |
| `mysql-connector-j` | **不写 `<version>`** | 由 `spring-boot-starter-parent:3.5.7` 的 dependency management 纳管 |
| `jjwt-api` / `jjwt-impl` / `jjwt-jackson` | **0.12.7**（三者同版本） | Maven 元数据显示 0.12.x 最新为 `0.12.7`。**必须使用 0.12.x**：0.11.x 及以下的 `Jwts.parser().setSigningKey()` 写法在 0.12.x 已移除，混写编译失败 |
| `spring-security-crypto` | **不写 `<version>`** | 由 Spring Boot 纳管；只引入加密模块，**不引完整 Spring Security**（决策 D7） |
| `vue-i18n` | **^11.1.10**（实际解析 11.4.14） | npm dist-tags：`latest=11.4.14`、`stable=11.1.10`、`legacy9=9.14.5`。**⚠️ 计划原定 v9，实施时改判**：安装 9.14.5 时 npm 明确告警 "vue-i18n@9.14.5: v9 and v10 no longer supported. please migrate to v11"。v11 的 `peerDependencies` 为 `vue: ^3.0.0`、`engines.node >= 16`，与本项目 Vue 3.5 / Node 22 兼容，且 `createI18n` / `useI18n` / `$t` 的基础用法与 v9 一致，故改用受维护的 v11 稳定线 |
| `axios` | **^1.20.0** | npm latest |
| `@ant-design/icons-vue` | **^7.0.1** | `ant-design-vue@4.2.6` 的 `dependencies` 明确声明 `"@ant-design/icons-vue": "^7.0.0"`，故取 7.x；peer 为 `vue >=3.0.3` |

### 2.5 接口契约总表

| 方法 | 路径 | 鉴权 | 说明 | 需求 |
|---|---|---|---|---|
| GET | `/api/doctors` | 白名单 | 医生全量列表 | 需求2 |
| GET | `/api/doctors/active` | 白名单 | 仅在线医生（支撑 `Consultation.vue:209`、`Home.vue:122`） | 需求2 |
| GET | `/api/doctors/{id}` | 白名单 | 按主键查 | 需求2 |
| GET | `/api/doctors/username/{username}` | 白名单 | 按用户名查（支撑 `Consultation.vue:215` 的路由参数解析） | 需求2 |
| POST | `/api/patient/register` | 白名单 | 患者注册（BCrypt 落库） | 需求3 |
| POST | `/api/patient/login` | 白名单 | 患者登录，签发 JWT | 需求3 |
| GET | `/api/patient/me` | **Bearer** | 当前登录患者信息（验证 token 有效性） | 需求3 |
| GET/POST/OPTIONS | `/api/test/cors` | 白名单 | 既有接口，**回归基线，不得破坏** | — |

**JWT 拦截器白名单**：`/api/doctors/**`、`/api/patient/login`、`/api/patient/register`、`/api/test/**`、`/actuator/**`。
其余路径一律要求 `Authorization: Bearer <token>`，缺失/过期/签名错误返回 **401**。

---

## 3. 分支与提交策略

### 3.1 分支

```bash
git checkout main
git pull
git switch -c feat/ai-interview-delivery     # 特性分支（README 允许自定义命名）
```

分支名建议含语义前缀以便评审识别：`feat/ai-interview-delivery`。

### 3.2 提交粒度建议（每个 commit 对应第 9 章的一个提示词批次）

```
docs: add PRD template and implementation plan   # P3 ── PRD 模板
docs: add patient auth PRD and design doc        # P4 ── PRD/设计文档
feat(web): add i18n locales and lang switcher    # P5 ── 需求1
chore: add docker-compose for mysql+phpmyadmin   # P6 ── 需求2 基建
feat(user): add doctor CRUD api with mybatis     # P7 ── 需求2 后端
feat(web): fetch doctors from backend api        # P8 ── 需求2 联调
feat(user): add patient register/login with jwt  # P9 ── 需求3 后端
feat(web): add patient login page and guard      # P10 ─ 需求3 前端
test: add curl api test suite and report         # P11 ─ 测试
docs: add api test guide, prompts and pr desc    # P12 ─ 交付文档
```

### 3.3 PR 要求

- base = `main`，compare = `feat/ai-interview-delivery`
- PR 描述模板见 [§8.4](#84-pr-描述模板)
- **合并前自检清单**：见 [§8.5](#85-合并前自检清单)

---

## 4. 需求1：首页中英文切换

### 4.1 需求原文核对

> 给 web 端的首页添加中英文切换能力，在页面右上角添加语言切换下拉菜单，在用户选择 **中文/English** 选项时动态切换页面显示内容到对应语言。
> 中英文语言资源文件需要保存在 `web/qa-web/src/locales` 目录中。**只需要处理首页本身，无需处理其他页面。**

**范围约定（已与需求方确认）**：切换器是全局组件、挂在 `AppHeader.vue` 右上角，
但**翻译范围仅限 `Home.vue`**；导航菜单、页脚、其他页面文案保持中文不变。

### 4.2 实现设计

#### 4.2.1 目录结构（严格符合 README 指定路径）

```
web/qa-web/src/locales/
├── index.ts       # createI18n 实例 + 语言持久化 + <html lang> 同步 + 语言嗅探回落
├── zh-CN.ts       # 中文语言包（源真值）
└── en-US.ts       # 英文语言包
```

> 说明：用 `.ts` 而非 `.json`，可获得 **编译期 key 类型检查**；若某语言包缺失 key，`vue-tsc` 能辅助发现。
> 两个语言包导出同名结构的对象，`en-US.ts` 建议用 `satisfies typeof zhCN` 约束形状一致。

#### 4.2.2 `locales/index.ts` 职责

| 能力 | 实现要点 |
|---|---|
| i18n 实例 | `createI18n({ legacy: false, locale, fallbackLocale: 'zh-CN', messages })`；**`legacy: false`** 是使用 Composition API `$t` 的前提 |
| 持久化 | key 建议 `qa-live-locale`，从 `localStorage` 读初值；切换时回写 |
| 初始语言决策顺序 | ① `localStorage` 已存 → ② `navigator.language` 命中 `zh*`/`en*` → ③ 回落 `zh-CN` |
| `<html lang>` 同步 | `watch(locale)` → `document.documentElement.lang = ...`（顺带修掉 §1.5 的 G8：`index.html` 现为 `lang="en"`） |
| 注入顺序 | `main.ts` 中 `app.use(i18n)` 置于 `app.use(Antd)` **之前**，避免 AntD 组件内嵌文案 locale 打架 |

#### 4.2.3 完整 key 映射表（已逐行扫描 `Home.vue` 模板 1–104 行）

| # | `Home.vue` 行号 | 中文原文 | i18n key | English 草案 |
|---|---|---|---|---|
| 1 | 5 | 专业在线医疗问诊平台 | `home.hero.title` | Professional Online Medical Consultation Platform |
| 2 | 6 | 连接专业医生与患者,提供便捷、高效的医疗咨询服务 | `home.hero.subtitle` | Connecting professional doctors with patients for convenient, efficient medical consultation |
| 3 | 10 | 专业医生团队 | `home.hero.feature.professional` | Professional Medical Team |
| 4 | 14 | 实时在线问诊 | `home.hero.feature.realtime` | Real-time Online Consultation |
| 5 | 18 | 隐私安全保护 | `home.hero.feature.privacy` | Privacy & Security |
| 6 | 23 | 立即问诊 | `home.hero.action.consult` | Consult Now |
| 7 | 26 | 查看医生 | `home.hero.action.doctors` | Browse Doctors |
| 8 | 42 | 专业医生 | `home.stat.doctors` | Doctors |
| 9 | 51 | 问题总数 | `home.stat.questions` | Total Questions |
| 10 | 60 | 待响应问题 | `home.stat.pending` | Pending Questions |
| 11 | 69 | 在线诊室 | `home.stat.rooms` | Active Rooms |
| 12 | 75 | 开放诊室 | `home.rooms.title` | Open Consultation Rooms |
| 13 | 76 | 以下医生诊室正在开放,欢迎咨询 | `home.rooms.subtitle` | The following doctors' rooms are open — feel free to consult |
| 14 | 86 | 在线（`a-badge` 的 `text` **属性**） | `common.online` | Online |
| 15 | 99 | 进入诊室 | `common.enterRoom` | Enter Room |

> ⚠️ **易错点 1（第 14 条）**：`:86` 是 `<a-badge status="processing" text="在线" />`，
> `text` 是**属性绑定**，必须写成 `:text="$t('common.online')"`，只改插槽内容无效。
>
> ⚠️ **易错点 2（第 11 条以外的分割点）**：`:90` `{{ doctor.title }} · {{ doctor.department }}` 中的 `·` 是模板字符，
> 保留原样（不是文案）。
>
> ⚠️ **易错点 3**：`<h3>`/`<p>` 里的**数字 `{{ statistics.xxx }}` 不翻译**，只翻译相邻标签文案。

#### 4.2.4 `LangSwitcher.vue`（新组件）

位置：`web/qa-web/src/components/LangSwitcher.vue`

- 使用 Ant Design Vue 的 **`a-select`**（尺寸 `size="small"`，宽度 ~110px）而非 `a-dropdown`：
  `a-select` 自带当前值回显，用户一眼可见当前语言；`a-dropdown` 需要自己维护选中态文案，且标题栏空间紧张。
- 选项：`[{ value:'zh-CN', label:'中文' }, { value:'en-US', label:'English' }]`
  —— **注意 label 本身不翻译**（语言名永远是各自母语写法，这是 i18n 惯例）。
- 建议带 `GlobalOutlined` 前缀图标（来自 `@ant-design/icons-vue`，需顺带补齐显式依赖 G3）。
- `v-model:value="currentLocale"` + `watch` → 写 `localStorage` + 更新 i18n `locale`（这部分逻辑建议集中在 `locales/index.ts` 暴露的 `setLocale()` 里，组件只负责触发）。

#### 4.2.5 `AppHeader.vue` 挂载点与样式适配

现状（`:3-31`）：`.header-content` 是 `display:flex; justify-content:space-between`，三个子元素依次是
`.logo` → `.nav-menu`（`flex:1; margin:0 40px`）→ `.login-btn`（医生登录按钮）。

插入点：**在 `.login-btn` 之后追加 `<LangSwitcher />`**，即页面最右上角。注意：

1. `.header-content` 需要加 `gap: 12px`，否则按钮与切换器紧贴。
2. `@media (max-width:768px)` 下导航已很拥挤 —— 建议在小屏给 `.lang-switcher` 加 `margin-right` 或缩小宽度；
   更稳的做法是移动端隐藏 `a-select` 的文字标签只留图标。
3. `.header` 是 `position:fixed; z-index:1000`，切换器的下拉浮层需确认未被裁剪（`a-select` 默认挂 body，通常无此问题）。

#### 4.2.6 改动文件清单

| 文件 | 动作 | 说明 |
|---|---|---|
| `web/qa-web/package.json` | MODIFY | 新增 `vue-i18n`（^9）；**顺带显式声明 `@ant-design/icons-vue`**（G3） |
| `web/qa-web/src/locales/index.ts` | **NEW** | i18n 实例 + 持久化 + 语言嗅探 |
| `web/qa-web/src/locales/zh-CN.ts` | **NEW** | 15 条中文文案 |
| `web/qa-web/src/locales/en-US.ts` | **NEW** | 15 条英文文案（键结构必须与 zh-CN 一致） |
| `web/qa-web/src/components/LangSwitcher.vue` | **NEW** | 右上角下拉切换器 |
| `web/qa-web/src/components/AppHeader.vue` | MODIFY | 挂载 `<LangSwitcher />` + 样式 gap 适配（**不改任何现有中文文案**） |
| `web/qa-web/src/views/Home.vue` | MODIFY | 15 处文案替换为 `$t(...)`；`<script setup>` 需 `import { useI18n } from 'vue-i18n'` 取 `t` |
| `web/qa-web/src/main.ts` | MODIFY | `app.use(i18n)`（置于 Antd 之前） |
| `web/qa-web/index.html` | MODIFY | `<title>` 改为 `QA Live Healthcare`；`lang` 调整为 `zh-CN`（G8，运行时由 i18n watch 覆盖） |

### 4.3 验收标准

| # | 验收项 | 判定方式 |
|---|---|---|
| A1-1 | 首页右上角出现语言切换下拉，默认显示「中文」 | 目视 |
| A1-2 | 选择 English 后，首页 15 条文案**全部**即时变英文，**无需刷新** | 目视 + 对照 §4.2.3 表逐条核对 |
| A1-3 | 切回「中文」后文案正确复原 | 目视 |
| A1-4 | 刷新页面后保持上次选择的语言 | localStorage 持久化生效 |
| A1-5 | 语言包确实位于 `web/qa-web/src/locales` 目录 | 路径核对（README 硬性要求） |
| A1-6 | Header 导航、Footer、其他页面**仍为中文**（未被误改） | 逐页目视 |
| A1-7 | `npm run build`（含 `vue-tsc -b`）**零报错** | 命令行退出码 0（TS strict 下最易失败） |

> **明确不做**：doctor 的 `name`/`title`/`department`/`experience`/`specialties` 等业务数据字段来自数据库，
> 属数据而非 UI 文案，**不在本次 i18n 范围内**（README 亦未要求）。

---

## 5. 需求2：医生数据 MySQL 化与前后端联调

### 5.1 需求原文核对

> 当前应用的数据全部使用 json 文件的方式在前端项目中存储，请将**医生相关的数据**使用 mysql 数据库存储。
> - 创建 `docker-compose.yml` 用于创建 mysql 数据库，并挂接 phpmyadmin 作为数据库服务器管理界面
> - 在 `qa-service-user` 服务中创建可以支持前端 [医生页面](web/qa-web/src/views/Doctors.vue) 显示医生列表所需要的 api
> - 完成前后端联调

### 5.2 基础设施：docker-compose.yml

路径：**仓库根目录** `/Users/bhd/IdeaProjects/qa-live-healthcare-interview/docker-compose.yml`
（README 未指定路径；放根目录便于 `docker compose up -d` 一把起全环境）。

```yaml
services:
  mysql:
    image: mysql:8.0
    container_name: qa-live-mysql
    ports:
      - "3306:3306"          # 若本机已有 MySQL 占用 3306，改用 "3307:3306" 并同步改 jdbc url
    environment:
      MYSQL_ROOT_PASSWORD: root123456
      MYSQL_DATABASE: qa_live
      MYSQL_ROOT_HOST: '%'
      TZ: Asia/Shanghai
    command:
      - --character-set-server=utf8mb4
      - --collation-server=utf8mb4_unicode_ci
      - --default-authentication-plugin=mysql_native_password   # phpMyAdmin 兼容性更稳
    volumes:
      - mysql-data:/var/lib/mysql                                # 数据持久化
      - ./db/init:/docker-entrypoint-initdb.d:ro                 # 首次启动自动执行 *.sql
    healthcheck:                                                 # ← 关键：让 depends_on 真正可用
      test: ["CMD", "mysqladmin", "ping", "-h", "127.0.0.1", "-uroot", "-proot123456"]
      interval: 5s
      timeout: 5s
      retries: 20
      start_period: 30s
    networks: [qa-live-net]

  phpmyadmin:
    image: phpmyadmin:5
    container_name: qa-live-phpmyadmin
    ports:
      - "8088:80"
    environment:
      PMA_HOST: mysql
      PMA_PORT: 3306
      UPLOAD_LIMIT: 64M
    depends_on:
      mysql:
        condition: service_healthy                               # ← 依赖 healthcheck，而非仅启动
    networks: [qa-live-net]

volumes:
  mysql-data:
networks:
  qa-live-net:
    driver: bridge
```

**设计要点说明**

| 要点 | 理由 |
|---|---|
| `healthcheck` + `depends_on: condition: service_healthy` | 仅写 `depends_on`（不带 condition）只保证容器启动、不保证 MySQL 可连，是 Spring Boot 首次启动报 `Communications link failure` 的头号原因 |
| 初始化 SQL 挂到 `/docker-entrypoint-initdb.d` | MySQL 官方镜像约定，**仅在 volume 为空的首次启动**执行；重跑前需 `docker compose down -v` 清卷 |
| `mysql_native_password` | phpMyAdmin 对 MySQL 8 默认 `caching_sha2_password` 在部分版本上有兼容问题 |
| phpmyadmin 端口选 `8088` | 避让后端 8080/8081 与前端 5173 |
| 数据卷命名 `mysql-data` | 显式命名便于 `docker volume ls` 识别与清理 |

### 5.3 初始化 SQL

```
db/init/
├── 01-schema.sql    # 建库建表（幂等：DROP IF EXISTS / CREATE IF NOT EXISTS）
└── 02-data.sql      # 5 条医生种子数据
```

#### 5.3.1 `db/init/01-schema.sql`

```sql
CREATE DATABASE IF NOT EXISTS qa_live CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE qa_live;

-- 医生表
CREATE TABLE IF NOT EXISTS t_doctor (
    id           VARCHAR(32)  NOT NULL COMMENT '业务主键 doc001…',
    username     VARCHAR(64)  NOT NULL COMMENT '登录名，前端路由 /consultation/:doctorUsername 使用',
    password     VARCHAR(128) NOT NULL COMMENT '密码（迁移初期沿用明文，后续可改 BCrypt）',
    name         VARCHAR(64)  NOT NULL COMMENT '医生姓名',
    title        VARCHAR(64)  NOT NULL COMMENT '职称',
    department   VARCHAR(64)  NOT NULL COMMENT '科室',
    avatar       VARCHAR(512)          COMMENT '头像 URL',
    experience   VARCHAR(128)          COMMENT '从业年限描述',
    specialties  JSON                  COMMENT '擅长领域；不支持 JSON 时降级 VARCHAR(512)',
    is_active    TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否在线 1/0',
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_doctor_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='医生表';

-- 患者表（需求3 使用，与需求2 同批建好，避免二次改库）
CREATE TABLE IF NOT EXISTS t_patient (
    id          VARCHAR(32)  NOT NULL,
    username    VARCHAR(64)  NOT NULL COMMENT '登录用户名',
    password    VARCHAR(128) NOT NULL COMMENT 'BCrypt 密文',
    name        VARCHAR(64)  NOT NULL COMMENT '姓名',
    birthday    VARCHAR(16)           COMMENT '生日 YYYY-MM-DD',
    phone       VARCHAR(32)           COMMENT '手机号',
    gender      VARCHAR(8)            COMMENT '性别',
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_patient_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='问诊用户（患者）表';
```

**`specialties` 选型说明**：采用 JSON 列 + 自定义 `TypeHandler`（决策 D2）。
JSON 列的写入必须是合法 JSON 字符串（`["高血压","冠心病"]`），种子数据用单引号包裹 JSON 字符串写入即可。
若目标环境 MySQL < 5.7（不支持 JSON 类型），降级为 `VARCHAR(512)` 逗号分隔，**TypeHandler 接口不变**，上层无感。

#### 5.3.2 `db/init/02-data.sql`

将 §1.4 的 5 条医生数据逐条落地（`is_active` 分别 `1,1,1,0,1`，`password` 均为 `123456`，
`specialties` 为 JSON 数组）。**必须与 `doctor-user-list.json` 完全一致**，否则：
- `Consultation.vue:216` 的 `getDoctorByUsername` 依赖 `username` —— 对不上会导致路由进诊室失败；
- `Home.vue:122` 的在线诊室数量、`Doctors.vue` 的卡片渲染也会失真。

### 5.4 后端实现（qa-service-user）

#### 5.4.1 pom.xml 新增依赖

```xml
<!-- 持久层（MyBatis XML，见决策 D1） -->
<dependency>
  <groupId>org.mybatis.spring.boot</groupId>
  <artifactId>mybatis-spring-boot-starter</artifactId>
  <version><!-- 核对后填入 Spring Boot 3.x 兼容版本 --></version>
</dependency>
<dependency>
  <groupId>com.mysql</groupId>
  <artifactId>mysql-connector-j</artifactId>
  <!-- runtime；版本由 spring-boot-starter-parent 纳管，不写 version -->
</dependency>

<!-- 需求3 鉴权占位，此处一并引入避免二次改 pom -->
<dependency>
  <groupId>org.springframework.security</groupId>
  <artifactId>spring-security-crypto</artifactId>
</dependency>
<dependency>
  <groupId>io.jsonwebtoken</groupId><artifactId>jjwt-api</artifactId><version><!-- 0.12.x --></version></dependency>
<dependency>
  <groupId>io.jsonwebtoken</groupId><artifactId>jjwt-impl</artifactId><version><!-- 同上 --></version><scope>runtime</scope></dependency>
<dependency>
  <groupId>io.jsonwebtoken</groupId><artifactId>jjwt-jackson</artifactId><version><!-- 同上 --></version><scope>runtime</scope></dependency>
```

> **提示 AI 时的强制要求**：三个 jjwt 依赖版本必须一致；且必须是 0.12.x（0.11.x 的
> `Jwts.parser().setSigningKey()` 写法在 0.12.x 已废弃/删除，混写会编译失败）。

#### 5.4.2 `application.properties` 新增段

```properties
# ===== DataSource =====
# ⚠️ characterEncoding 必须用 "UTF-8"，不能写 "utf8mb4"（后者是 MySQL 服务端字符集名，
#    JDBC 侧写 utf8mb4 会抛 Unsupported character encoding 'utf8mb4'）
spring.datasource.url=jdbc:mysql://localhost:3306/qa_live?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=root123456
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# ===== MyBatis =====
mybatis.mapper-locations=classpath*:/mapper/**/*.xml
mybatis.type-aliases-package=com.leansofx.qaserviceuser.entity
mybatis.configuration.map-underscore-to-camel-case=true     # is_active → isActive（决策关键）
# 开发期打开便于 AI 校验 SQL，交付前改为注销或使用 Slf4j
mybatis.configuration.log-impl=org.apache.ibatis.logging.stdout.StdOutImpl

# ===== JWT（需求3）=====
jwt.secret=请替换为足够长的随机字符串-至少32字节
jwt.expiration-ms=86400000
```

**同时修掉 G9**：把 §1.2 提到的 properties 中 `spring.web.cors.allow-credentials` 等行与 `CorsConfig` Java Bean 统一，
推荐**删除 properties 里的 CORS 段**，只保留 Java Bean，并把 `allowedOriginPatterns` 由 `*` 收敛到 `http://localhost:5173`。
注意 URI 中的 `&` 在 `.properties` 里直接书写即可（不同于 `.yml` 需要引号）。

#### 5.4.3 新增文件清单

| 文件（相对 `src/main/java/com/leansofx/qaserviceuser/`） | 职责 |
|---|---|
| `common/ApiResponse.java` | 统一响应：`{ int code; String message; T data; long timestamp; }` + `ok(data)` / `fail(code,msg)` 静态工厂 |
| `exception/GlobalExceptionHandler.java` | `@RestControllerAdvice`；处理业务异常、参数校验异常、兜底 Exception → `ApiResponse` |
| `entity/Doctor.java` | 对应 `t_doctor`；含 `password`（**仅内部使用，不外泄**） |
| `dto/DoctorVO.java` | 对外响应模型；字段与前端 `Doctor` 接口对齐，**剔除 `password`** |
| `handler/JsonStringListTypeHandler.java` | `JSON` 列 ↔ `List<String>`（继承 `BaseTypeHandler<List<String>>`） |
| `mapper/DoctorMapper.java` | `@Mapper` 接口：`selectAll()` / `selectActive()` / `selectById(id)` / `selectByUsername(username)` |
| `service/DoctorService.java` | 调用 Mapper，Entity → `DoctorVO` 转换、null 处理 |
| `controller/DoctorController.java` | 4 个 GET 端点，返回 `ApiResponse<List<DoctorVO>>` / `ApiResponse<DoctorVO>` |
| `QaServiceUserApplication.java`（MODIFY） | 加 `@MapperScan("com.leansofx.qaserviceuser.mapper")` |

XML 位置（决策 D1）：`src/main/resources/mapper/DoctorMapper.xml`。

**`JsonStringListTypeHandler` 在 XML 中的用法**：`select` 的 `<result column="specialties" property="specialties" typeHandler="...JsonStringListTypeHandler"/>`。
必须显式声明，否则 `map-underscore-to-camel-case` 只解决列名映射，不解决 JSON 反序列化。

#### 5.4.4 接口契约

统一响应：

```json
{ "code": 0, "message": "success", "data": [ ... ], "timestamp": 1739520000000 }
```

`GET /api/doctors` → `data` 为医生数组，单项：

```json
{
  "id": "doc001", "username": "dr-zhang-wei", "name": "张伟医生",
  "title": "主任医师", "department": "心内科",
  "avatar": "https://images.pexels.com/photos/5215024/...w=400",
  "experience": "15年临床经验",
  "specialties": ["高血压", "冠心病", "心律失常"],
  "isActive": true
}
```

> **契约红线**：① 无 `password` 字段；② `specialties` 必须是**数组**；③ `isActive` 必须是**布尔**（不能是 `1`/`"1"`）。
> 这三点对应 §1.6 的字段映射风险。

| 端点 | 成功 | 异常 |
|---|---|---|
| `GET /api/doctors` | 200 + 全量 5 条 | DB 不可用 → 500 + `ApiResponse.fail` |
| `GET /api/doctors/active` | 200 + 4 条（`doc004` 离线被过滤） | — |
| `GET /api/doctors/{id}` | 200 | 不存在 → 建议 404（或 200 + `data:null`，方案统一取 **404 + fail**） |
| `GET /api/doctors/username/{username}` | 200 | 不存在 → 404 |

### 5.5 前端联调

#### 5.5.1 Vite proxy（必需）

```ts
// web/qa-web/vite.config.ts
export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      '/api': { target: 'http://localhost:8080', changeOrigin: true },
    },
  },
});
```

> 有了 proxy，开发期前端用**同源** `/api` 访问，不触发跨域；但 README 要求 curl 直连测试，
> 故后端 CORS 仍需保留并收敛（D9）。

#### 5.5.2 新增 API 层

```
web/qa-web/src/api/
├── request.ts    # axios 实例：baseURL '/api'、响应拦截器剥壳、401 处理
├── doctor.ts     # fetchDoctors / fetchActiveDoctors / fetchDoctorById / fetchDoctorByUsername
└── patient.ts    # （需求3）register / login / getMe
```

`request.ts` 职责：
- `baseURL: '/api'`，`timeout: 10000`
- 响应拦截器：`res.data.code === 0` → 返回 `res.data.data`；否则 `reject(message)`
- HTTP 401 → 清 localStorage token + `router.push('/patient/login')`（需求3 启用）
- 网络异常 / 5xx → 统一 `Promise.reject` 并允许调用方 catch

> 也可不引 axios 直接用原生 `fetch`（少一个依赖）；但 axios 的拦截器与超时控制更契合 D7 的 Bearer 注入需求，
> 且 AI 生成 axios 封装的准确率更高 → **选 axios**。

#### 5.5.3 store 改造（最小爆炸半径，决策 D6）

`web/qa-web/src/store/index.ts`：

| 改动 | 说明 |
|---|---|
| `doctors: []` | 初值由 JSON 改为空数组 |
| 新增 `async loadDoctors()` | 调 `GET /api/doctors` → 写入 `state.doctors`；失败时置空并对外抛错，由调用方决定降级 |
| **保留** `import doctorData from '../data/doctor-user-list.json'` | 仅供 `loginDoctor` 的本地凭证比对使用（决策 D5），使 `DoctorLogin.vue` 零改动 |
| `Doctor` 接口移除 `password` | 与新 VO 对齐；但 `loginDoctor` 内部比对走本地凭证表，不受影响 |
| （可选）新增 `state.doctorsLoading` | 便于 Home/Doctors 展示加载态 |

> **为什么不让 `Doctors.vue` 单独拉数据、而要在 store 里做一次加载？**
> §1.6 已证明 `Home.vue`（统计卡片 + 开放诊室）、`Consultation.vue`（医生下拉 + 选中医生活）同样消费 `doctors`。
> 若只有 Doctors.vue 自行拉取，则首页在直接访问时会显示 `0` 与空列表，属明显回归。**store 统一加载是唯一能同时满足「改造最小」与「功能不回退」的方案。**

#### 5.5.4 加载时机与竞态修复

在 `web/qa-web/src/App.vue` 的 `onMounted` 中 `await store.loadDoctors()`（`App.vue` 当前无 store 引用，改动面极小）。
同时处理 §1.6 的两处竞态：

| 竞态 | 修法 |
|---|---|
| `Consultation.vue:215` `onMounted` 同步读 | 改为 `await store.loadDoctors()` 后再 `getDoctorByUsername()`，或改为 `watch(store.state.doctors)` 响应式取值 |
| `Consultation` 组件被两条路由复用、参数变化不重挂载 | 用 `watch(() => route.params.doctorUsername)` 替代/补充 `onMounted` |
| `AppFooter.vue:12-15` 裸 `<a href>` 导致整页刷新 | 建议顺带改为 `<router-link>`（低风险、收益明确）。若不改，至少保证每页都能容忍「 doctors 未就绪」的中间态 |

#### 5.5.5 Doctors.vue 三态

替换 `views/Doctors.vue:57` 的 `computed(() => store.state.doctors)`：

- **loading**：`a-spin`（或骨架屏）包裹栅格
- **error**：`a-result` / `a-alert` 展示失败原因 + 「重试」按钮
- **empty**：`a-empty` 提示暂无医生
- 成功：保持现有卡片渲染不变（`v-for` / 字段均不动）

> 即使数据在 App 级预加载，Doctors.vue 仍建议实现三态：直开 `/doctors` 时若预加载未完成，
> 空列表不应被误读为「没有医生」。

### 5.6 改动文件总清单

| 文件 | 动作 |
|---|---|
| `docker-compose.yml`（仓库根） | **NEW** |
| `db/init/01-schema.sql`、`db/init/02-data.sql` | **NEW** |
| `server/qa-service-user/pom.xml` | MODIFY |
| `server/qa-service-user/src/main/resources/application.properties` | MODIFY |
| `.../common/ApiResponse.java` | **NEW** |
| `.../exception/GlobalExceptionHandler.java` | **NEW** |
| `.../entity/Doctor.java` | **NEW** |
| `.../dto/DoctorVO.java` | **NEW** |
| `.../handler/JsonStringListTypeHandler.java` | **NEW** |
| `.../mapper/DoctorMapper.java` | **NEW** |
| `.../service/DoctorService.java` | **NEW** |
| `.../controller/DoctorController.java` | **NEW** |
| `.../QaServiceUserApplication.java` | MODIFY（加 `@MapperScan`） |
| `.../src/main/resources/mapper/DoctorMapper.xml` | **NEW** |
| `web/qa-web/vite.config.ts` | MODIFY（proxy） |
| `web/qa-web/package.json` | MODIFY（axios） |
| `web/qa-web/src/api/request.ts`、`api/doctor.ts` | **NEW** |
| `web/qa-web/src/store/index.ts` | MODIFY（loadDoctors、初值、类型） |
| `web/qa-web/src/App.vue` | MODIFY（触发预加载） |
| `web/qa-web/src/views/Doctors.vue` | MODIFY（三态） |
| `web/qa-web/src/views/Consultation.vue` | MODIFY（仅修竞态，逻辑等价） |
| `web/qa-web/src/components/AppFooter.vue` | MODIFY（建议 `<a>` → `<router-link>`） |

### 5.7 验收标准

| # | 验收项 | 判定方式 |
|---|---|---|
| A2-1 | `docker compose up -d` 后 MySQL、phpMyAdmin 均健康 | `docker compose ps` (healthy) |
| A2-2 | 浏览器访问 `http://localhost:8088` 可进入 phpMyAdmin，能看到 `qa_live.t_doctor` 及 5 条数据 | 目视 |
| A2-3 | `curl http://localhost:8080/api/doctors` 返回 5 条、无 password、specialties 为数组、isActive 为布尔 | jq 断言 |
| A2-4 | `/api/doctors/active` 返回 4 条（过滤 doc004） | jq 断言 |
| A2-5 | 医生页面 `/doctors` 数据来自后端（**断网/停后端可复现失败态**） | 停止容器后页面显示错误态而非静态数据 |
| A2-6 | 首页统计数字与开放诊室列表**仍正确**（无回归） | 目视对比改造前后 |
| A2-7 | 医生登录 `/doctor/login` 用 `dr-zhang-wei / 123456` **仍能登录成功** | 功能验证（D5 是否生效的直接检验） |
| A2-8 | 从医生页点「进入诊室」→ `/consultation/:doctorUsername` 能正确选中医生 | 首屏直开 + 页内跳转**两种路径都要验** |
| A2-9 | `npm run build` 通过 | 退出码 0 |

---

## 6. 需求3：问诊用户账号密码登录

### 6.1 需求原文核对

> 为问诊用户提供用户名密码登录方式
> - 在 `/docs` 目录中编写规范的 PRD 文档模版
> - 指导 AI 使用以上模版编写此特性的 PRD 文档
> - 指导 AI 使用此 PRD 文档完成特性开发，如果需要进行拆分请在 PRD 文档中提供拆分步骤

**范围约定（已确认）**：新增独立 `/patient/login` 页面（登录 + 注册 Tab），患者数据入 MySQL，
**彻底替换**现有 `Consultation.vue` 中「姓名 + 生日」的身份验证（当前逻辑见 `store/index.ts:74-92`，不存在即自动建档）。

### 6.2 现状与改造点

现有患者流程（`Consultation.vue:4-49`、`223-247`）的问题：

| 现状 | 问题 |
|---|---|
| 表单字段 `姓名` + `生日`（dayjs `YYYY-MM-DD`） | 无用户名/密码概念，无法满足需求 |
| `store.verifyPatient(name, birthday)` **不存在即自动建档** | 任何人都可用任意姓名生日生成账户，非真实鉴权 |
| 状态仅存内存 `state.currentPatient` | 刷新即丢失登录态 |
| 患者数据只有 `{id,name,birthday,phone,gender}` | 无 `username`/`password` 列 |

改造后：

| 能力 | 落点 |
|---|---|
| 注册 | `POST /api/patient/register`，BCrypt 落库 |
| 登录 | `POST /api/patient/login`，返回 JWT |
| 登录态持久化 | `localStorage` 存 token，**刷新不丢**（对比现状 `currentPatient` 内存态） |
| 鉴权 | `GET /api/patient/me` 校验换取当前患者信息 |

### 6.3 文档产物（三步走）

#### 6.3.1 Step 1：`/docs/PRD_TEMPLATE.md`

**必须基于仓库已有的 `.asdm/toolsets/prd-builder/spec/feature-prd-spec.md` 裁剪**，不要另起炉灶
（该模板已含 10 章结构、语言一致性约定、状态机、任务拆分原则，且与 `.asdm` 生态一致）。

裁剪后的模板保留以下章节，并**新增两块 README 特定的内容**：

```
# Feature PRD: <Feature Name>
Feature ID / Created Date / Status / Language
1. Overview            1.1 Feature Summary   1.2 Objectives
2. User Stories        As a / I want to / So that + Acceptance Criteria
3. Functional Requirements   REQ-00x / Description / Priority / Related Stories
4. Non-Functional Requirements   Performance / Security / Scalability / Reliability
5. Technical Requirements       Architecture / Dependencies / Constraints
6. Success Criteria             （可度量）
7. Task Breakdown Principles    ← 必须保留 "≤10 tasks" 约束
8. Implementation Notes
9. Risks and Mitigations
10. Appendix                    References / Glossary
──────────────────────────────────────────────
【模板新增·面向本仓库】
附录 A：技术栈约束速查（Spring Boot 3.5.7 / Java 17 / MyBatis / Vue3+TS+AntDV / JWT）
附录 B：受影响文件清单模板（便于 AI 生成 PRD 时直接产出改造点）
附录 C：API 契约清单模板（方法 / 路径 / 鉴权 / 请求体 / 响应体 / 错误码）
```

**为什么这样裁剪**：原始 spec 是「给 AI 读的生成指令」，而 README 要求的是「给人读并给 AI 用的**模板文档**」。
去掉 `Language Guidelines` 这类元指令，补上本仓库的技术栈约束，才能让 AI 生成的 PRD 不跑偏。

#### 6.3.2 Step 2：`/docs/PRD-patient-auth.md`

用上面模板产出「问诊用户账号密码登录」PRD。**硬性要求**：

- Feature ID 建议 `PATIENT-AUTH-001`
- User Stories 至少覆盖：新用户注册、老用户登录、登录态保持、未登录访问问诊页被拦截、登出
- NFR 必须写：密码 BCrypt 加盐存储、**不得明文传输/落库**、token 有效期、接口幂等、验证码不在本期范围（显式声明不做）
- Task Breakdown **≤10 个**（模板第 7 章约束），建议拆法：
  1. 数据库表 `t_patient` 迁移 + 索引
  2. MyBatis Entity/Mapper/XML（含 `selectByUsername`）
  3. `PasswordEncoder` Bean + `JwtUtil`（签发/解析）
  4. `PatientService`（注册查重、登录校验、签发 token）
  5. `PatientController` 三个端点
  6. `JwtInterceptor` + `WebConfig` 注册白名单
  7. 前端 `api/patient.ts` + request 拦截器注入 Bearer/401 处理
  8. 前端 `PatientLogin.vue`（登录/注册 Tabs）
  9. 前端路由 `/patient/login` + `beforeEach` 守卫，改造 `Consultation.vue` 登录态读取
  10. curl 测试补充到 `API_TEST.md`

#### 6.3.3 Step 3：`/docs/PRD_EXECUTION_PROMPTS.md`

把 PRD 的每个 Task 转成**可直接投喂 AI 的提示词**（含：上下文文件路径、目标文件路径、验收标准），
并沉淀到 `/docs/prompts.md`。模板见 [§9](#9-ai-coding-提示词编排)。

### 6.4 数据模型

`t_patient` 已在 §5.3.1 一并建好（避免二次改库）。要点：

- `username` **唯一索引**，注册查重依赖它（也依赖 DB 兜底，不能只靠应用层查询）
- `password` `VARCHAR(128)` 存 BCrypt（`$2a$10$...` 固定 60 字符，留足余量）
- `birthday`/`phone`/`gender` 保留与旧 `patient-user.json` 对齐，便于未来迁移历史数据
- `id` 建议 `patient` + 雪花/UUID/时间戳，避免自增 ID 暴露业务量

**历史数据迁移（可选，建议本期做最小版）**：
`patient-user.json` 的 5 位患者无凭证，可选择 ① 不迁移，仅新注册用户可用；② 迁移并用默认密码（如 `123456`）初始化。
**推荐 ①**（避免制造弱口令账户），并在 PRD 的 Implementation Notes 中显式记录该决策。

### 6.5 接口契约

| 方法 | 路径 | 请求体 | 成功响应 `data` |
|---|---|---|---|
| POST | `/api/patient/register` | `{ username, password, name, birthday?, phone?, gender? }` | `{ id, username, name }` |
| POST | `/api/patient/login` | `{ username, password }` | `{ token, expiresIn, patient:{ id, username, name } }` |
| GET | `/api/patient/me` | —（Header: `Authorization: Bearer <token>`） | `{ id, username, name, birthday, phone, gender }` |

错误码约定（统一走 `ApiResponse`）：

| 场景 | HTTP | code | message 示例 |
|---|---|---|---|
| 用户名已存在（注册） | 400 | 1001 | 用户名已存在 |
| 参数缺失/格式非法 | 400 | 1002 | 参数校验失败：username 不能为空 |
| 用户不存在（登录） | 401 | 1003 | 用户不存在 |
| 密码错误（登录） | 401 | 1004 | 用户名或密码错误 |
| token 缺失 | 401 | 1005 | 未登录或登录已过期 |
| token 过期/签名错误 | 401 | 1006 | token 无效或已过期 |

> **安全细节**：登录失败时不区分「用户不存在」与「密码错误」给前端的最终提示（对外统一返回「用户名或密码错误」），
> 避免用户名枚举。上表 `1003/1004` 是内部 code，前端展示层统一文案。

### 6.6 后端实现要点

| 组件 | 要点 |
|---|---|
| `PasswordEncoderConfig` | 注册 `@Bean PasswordEncoder` → `new BCryptPasswordEncoder()`（只引 `spring-security-crypto`，**不引完整 Security**，决策 D7） |
| `JwtUtil` | 0.12.x 写法：`Jwts.builder().subject(id).claim("username",...).issuedAt(...).expiration(...).signWith(key).compact()`；密钥由 `Keys.hmacShaKeyFor(secret.getBytes(UTF_8))` 生成（**要求 ≥256 bit**） |
| `JwtInterceptor` | `preHandle` 读 `Authorization` → 去 `Bearer ` 前缀 → `Jwts.parser().verifyWith(key).build().parseSignedClaims(token)`；异常统一转 401；成功后把 `patientId` 写入 `request.setAttribute` 供 Controller 取用 |
| `WebConfig` | `implements WebMvcConfigurer`，`addInterceptors` 注册并 `excludePathPatterns`（白名单见 §2.5）。**注意 `/api/patient/me` 不在白名单** |
| `PatientService.register` | ① 参数校验 → ② `selectByUsername` 查重 → ③ BCrypt 加密 → ④ insert → ⑤ 返回脱敏结果（**不含 password**） |
| `PatientService.login` | ① 查用户，不存在 → 401 → ② `passwordEncoder.matches(raw, encoded)` → ③ 签发 JWT |
| XML | `resources/mapper/PatientMapper.xml`；insert 建议 `useGeneratedKeys="false"`（主键由应用层生成） |

### 6.7 前端实现要点

| 文件 | 动作 | 要点 |
|---|---|---|
| `src/views/PatientLogin.vue` | **NEW** | 参照 `DoctorLogin.vue` 的 UI 范式（`a-form` + `a-input-password` + prefix 图标 + `@finish`），改为 **`a-tabs`**：Tab1 登录（用户名+密码），Tab2 注册（用户名+密码+确认密码+姓名，可选生日/性别/手机）。**去掉** `DoctorLogin.vue:46-52` 那种明文测试账号 alert，改为注册引导 |
| `src/router/index.ts` | MODIFY | 新增 `{ path:'/patient/login', name:'PatientLogin', component: PatientLogin }`；新增 `beforeEach` 守卫：`/consultation` 需 token，否则 `redirect '/patient/login?redirect=<原路径>'` |
| `src/api/request.ts` | MODIFY | 请求拦截器注入 `Authorization: Bearer ${localStorage.token}`；响应拦截器 401 → 清 token + 跳登录（配合守卫形成闭环） |
| `src/api/patient.ts` | **NEW** | `register()` / `login()` / `getMe()` |
| `src/store/index.ts` | MODIFY | 新增 `state.currentPatient` 由 `/api/patient/me` 初始化；**替换** `verifyPatient`（不再「自动建档」）；新增 `loginPatient()` / `logoutPatient()`（token 清理）；App 启动时若 token 存在则拉取 `me` 恢复登录态 |
| `src/views/Consultation.vue` | MODIFY | 移除 `?store.verifyPatient` 的姓名/生日表单（`:4-49`），改为：`currentPatient` 为空 → 显示引导卡片 + 「去登录」按钮跳 `/patient/login`。这是本次最大的 UI 改动点 |
| `src/components/AppHeader.vue` | MODIFY（建议） | 「医生登录」按钮旁增加「患者登录 / 退出」入口；也可共用同一按钮组（注意 §4.2.5 的 LangSwitcher 已占位，需重排 flex） |

**`Consultation.vue` 改造后的判空逻辑**：`:277-299` 的 `submitQuestion` 已依赖 `currentPatient.value`，
改造后该值来自 bearer token 换取的结果，**只要保证 `currentPatient` 在问诊页渲染前就绪即可**，提交逻辑本身无需改。

### 6.8 改动文件总清单

| 文件 | 动作 |
|---|---|
| `docs/PRD_TEMPLATE.md` | **NEW** |
| `docs/PRD-patient-auth.md` | **NEW** |
| `docs/PRD_EXECUTION_PROMPTS.md` | **NEW** |
| `docs/DESIGN-patient-auth.md` | **NEW**（设计文档：表结构、接口契约、JWT 时序、前端状态流） |
| `db/init/01-schema.sql` | 已在 §5.3 包含 `t_patient` |
| `.../config/PasswordEncoderConfig.java` | **NEW** |
| `.../util/JwtUtil.java` | **NEW** |
| `.../config/JwtInterceptor.java` | **NEW** |
| `.../config/WebConfig.java` | **NEW** |
| `.../entity/Patient.java` | **NEW** |
| `.../dto/RegisterRequest.java` / `LoginRequest.java` / `LoginResponse.java` / `PatientVO.java` | **NEW** |
| `.../mapper/PatientMapper.java` + `resources/mapper/PatientMapper.xml` | **NEW** |
| `.../service/PatientService.java` | **NEW** |
| `.../controller/PatientController.java` | **NEW** |
| `web/qa-web/src/views/PatientLogin.vue` | **NEW** |
| `web/qa-web/src/api/patient.ts` | **NEW** |
| `web/qa-web/src/router/index.ts` | MODIFY（新路由 + 守卫） |
| `web/qa-web/src/store/index.ts` | MODIFY（患者登录态） |
| `web/qa-web/src/views/Consultation.vue` | MODIFY（移除旧验证表单） |
| `web/qa-web/src/components/AppHeader.vue` | MODIFY（患者登录入口） |

### 6.9 验收标准

| # | 验收项 | 判定方式 |
|---|---|---|
| A3-1 | `/docs` 下存在 PRD 模板，且模板含 ≥10 个章节结构 | 文档审阅 |
| A3-2 | `PRD-patient-auth.md` 严格遵循该模板结构产出 | 章节逐一比对 |
| A3-3 | PRD 中含明确的任务拆分步骤，且 **≤10** | 计数 |
| A3-4 | 注册新用户成功，数据库 `t_patient` 中 `password` 为 **BCrypt 密文**（非明文） | SQL 查询目视 + phpMyAdmin |
| A3-5 | 重复用户名注册被拒，返回约定错误码 | curl / jq |
| A3-6 | 正确用户名密码登录返回 token | curl |
| A3-7 | 错误密码登录失败（401），且不泄漏「用户是否存在」 | curl |
| A3-8 | 携带 token 访问 `/api/patient/me` 成功；不带/篡改 token 返回 401 | curl 三组对照 |
| A3-9 | 未登录访问 `/consultation` 被重定向到 `/patient/login` | 浏览器验证 |
| A3-10 | 登录后**刷新浏览器仍保持登录态** | 关闭再打开页面 |
| A3-11 | 登录后提问，`store.addQuestion` 使用的 `patientId/patientName` 取自登录用户 | 数据核对 |
| A3-12 | `POST /api/patient/login` 不在拦截白名单之外即被误拦 —— 反向确认登录接口本身可匿名访问 | curl 无 header 请求成功 |

---

## 7. 测试方案：curl 接口测试

### 7.1 需求原文核对

> 1. 使用 curl 构建对于以上需求中所涉及的所有接口的测试
> 2. 提供 `/docs/API_TEST.md` 用于描述以上接口测试流程，并指导 AI 使用这份文档完成测试并生成测试报告

### 7.2 前置条件与命令约定

```bash
# 0) 起环境
docker compose up -d
docker compose ps          # 确认 mysql / phpmyadmin 均为 healthy

# 1) 起后端
cd server/qa-service-user && ./mvnw spring-boot:run &

# 2) 通用变量（所有用例复用）
BASE=http://localhost:8080
```

**统一断言方式**：每条用例用 `-w` 输出 HTTP 状态码，用 `jq` 校验 JSON 结构。

```bash
curl -s -w "\nHTTP:%{http_code}\n" "$BASE/api/doctors"
```

> `-s` 静默、`-w` 追加状态码；若环境无 `jq`，`API_TEST.md` 需同时给出 `python3 -c` 的替代校验写法，
> 否则 AI 在无 jq 环境会卡住（macOS 默认不带 jq）。

### 7.3 用例矩阵

图例：`P`=正向、`N`=负向、`B`=边界、`A`=鉴权

| # | 用例 | 方法 + 路径 | 类型 | 期望 |
|---|---|---|---|---|
| **既有接口回归** ||||
| T01 | CORS GET 回归 | `GET /api/test/cors` | P | 200，`service=qa-service-user` |
| T02 | CORS POST 回归 | `POST /api/test/cors` -d `{"a":1}` | P | 200，回显 `receivedData` |
| T03 | CORS OPTIONS | `OPTIONS /api/test/cors` | P | 200/204 |
| T04 | 健康检查 | `GET /actuator/health` | P | 200，`status=UP` |
| **需求2 医生接口** ||||
| T05 | 医生全量列表 | `GET /api/doctors` | P | 200，`data` 长度 5，含 `doc001`~`doc005` |
| T06 | 响应契约校验 | `GET /api/doctors` | P | 每条**无** `password`；`specialties` 是 array；`isActive` 是 boolean |
| T07 | 在线医生 | `GET /api/doctors/active` | P | 200，长度 **4**（排除 `doc004`） |
| T08 | 按 id 查（存在） | `GET /api/doctors/doc001` | P | 200，`name=张伟医生`（中文 UTF-8 无乱码） |
| T09 | 按 id 查（不存在） | `GET /api/doctors/not-exist` | N | 404 + `code != 0` |
| T10 | 按 username 查（存在） | `GET /api/doctors/username/dr-zhang-wei` | P | 200，与 T08 同一条记录 |
| T11 | 按 username 查（不存在） | `GET /api/doctors/username/nobody` | N | 404 |
| T12 | 空 id | `GET /api/doctors/` （或 `/api/doctors/%20`） | B | 404/400，不应 500 |
| T13 | 特殊字符 | `GET /api/doctors/a'b` | B | 非 500，SQL 不报错（验证参数走预编译） |
| **需求3 患者注册** ||||
| T14 | 注册成功 | `POST /api/patient/register` 新用户名 | P | 200，`data.id` 非空，**无** `password` 字段 |
| T15 | 用户名重复 | 用 T14 同名再注册 | N | 错误码 1001 |
| T16 | 缺少用户名 | `{"password":"x"}` | N | 400，校验失败 |
| T17 | 缺少密码 | `{"username":"x"}` | N | 400 |
| T18 | 空字符串体 | `{}` | N | 400，不应 500 |
| T19 | 非法 Content-Type | 不带 `-H 'Content-Type: application/json'` | B | 415 或明确 400（需实现统一处理） |
| T20 | 超长用户名 | username 长度 > 64 | B | 400/500 均需非静默错误，且 DB 不产生脏数据 |
| **需求3 患者登录** ||||
| T21 | 登录成功 | T14 的凭据 | P | 200，`data.token` 非空，`expiresIn` > 0 |
| T22 | 密码错误 | 正确用户名 + 错密码 | N | 401 |
| T23 | 用户不存在 | 随机用户名 | N | 401，提示与 T22 **一致**（防枚举，见 §6.5） |
| T24 | 密码为空 | `{"username":"x","password":""}` | N | 400 |
| **需求3 token 鉴权** ||||
| T25 | 携带有效 token | `GET /api/patient/me` + `-H "Authorization: Bearer $TOKEN"` | A | 200，返回该患者信息 |
| T26 | 无 token | `GET /api/patient/me` | A | **401** |
| T27 | 篡改 token | `Bearer ${TOKEN}x` | A | 401 |
| T28 | 错误前缀 | `Authorization: $TOKEN`（缺 Bearer） | A | 401 |
| T29 | 过期 token | 手动构造过期 JWT（或用极小 expiration 重启） | A | 401 |
| T30 | 白名单反向验证 | `GET /api/doctors`（不带 token） | A | **200**（证明医生接口未被误拦） |
| T31 | 登录接口可匿名 | `POST /api/patient/login`（不带 token） | A | 200（证明未被误拦） |

> T29 说明：过期 token 可用同一密钥在本地临时签发一张 `exp` 已过期的 JWT 来构造，
> 在 `API_TEST.md` 中给出具体命令；若嫌麻烦，可用临时把 `jwt.expiration-ms` 改为 `-1` 重启的方式替代。

### 7.4 可复用脚本 `scripts/api-test.sh`

路径：`/Users/bhd/IdeaProjects/qa-live-healthcare-interview/scripts/api-test.sh`

设计要点：

1. `set -euo pipefail`（但断言失败不要中断全部，用计数器收集）
2. 用 shell 函数封装断言：

```bash
assert_eq() {   # assert_eq <实际> <期望> <用例ID>
  if [ "$1" = "$2" ]; then
    PASS=$((PASS+1)); printf '  \033[32mPASS\033[0m %s\n' "$3"
  else
    FAIL=$((FAIL+1)); printf '  \033[31mFAIL\033[0m %s (expected=%s actual=%s)\n' "$3" "$2" "$1"
  fi
}
http_code() { curl -s -o /dev/null -w '%{http_code}' "$@"; }
json_get() { curl -s "$@" | jq -r "$1"; }   # 注意参数顺序易用反，脚本内固定为 json_get <jq-expr> <curl args...>
```

3. 注册/登录用例使用**带时间戳的用户名**（`pt$(date +%s)`），保证脚本可重复执行不撞唯一键
4. token 提取：`TOKEN=$(curl -s -X POST "$BASE/api/patient/login" -H 'Content-Type: application/json' -d "{...}" | jq -r '.data.token')`
5. 结尾输出汇总：`TOTAL / PASS / FAIL`，`FAIL>0` 时 `exit 1`，便于后续接入 CI

### 7.5 报告产物

`/docs/TEST_REPORT.md` 模板结构：

```
# API 测试报告
- 执行时间 / 执行人 / 环境（OS、Docker 版本、JDK、Node）
- 被测服务版本、commit hash
- 环境准备步骤（可复现命令）
- 结果汇总表：用例总数 / 通过 / 失败 / 通过率
- 明细：每个用例的 请求命令 → 实际响应片段 → 结论
- 失败项分析与修复记录
- 截图附件索引（可放 phpMyAdmin 数据截图、Swagger/curl 输出截图）
- 结论与遗留问题
```

**AI 执行约定**：`API_TEST.md` 末尾必须写明让 AI「运行 `scripts/api-test.sh`，把真实输出回填到 `TEST_REPORT.md`，
禁止编造响应内容」。这是 README「指导 AI 使用这份文档完成测试并生成测试报告」的落点。

### 7.6 验收标准

| # | 验收项 |
|---|---|
| A4-1 | `/docs/API_TEST.md` 存在，覆盖 §7.3 全部用例，每条给出**可直接复制执行**的 curl 命令 |
| A4-2 | 文档含「环境准备 → 执行 → 断言 → 报告」完整流程说明 |
| A4-3 | `scripts/api-test.sh` 可重复执行（幂等，不因重复用户名失败） |
| A4-4 | `TEST_REPORT.md` 中的响应内容是**真实执行结果**（可抽查命令真实性） |
| A4-5 | T01~T04 既有接口**无回归** |

---

## 8. 交付物清单

### 8.1 `/docs` 目录最终形态

```
docs/
├── IMPLEMENTATION_PLAN.md        # 本文：全局需求实现方案
├── PRD_TEMPLATE.md               # PRD 模板（README 交付物要求 2）
├── PRD-patient-auth.md           # 特性 PRD：问诊用户账号密码登录（含 ≤10 拆分步骤）
├── PRD_EXECUTION_PROMPTS.md      # 由 PRD 转出的 AI 执行提示词序列
├── DESIGN-patient-auth.md        # 设计文档（README 交付物要求 2 的「设计文档（如果需要）」）
├── API_TEST.md                   # curl 测试流程文档（README 测试要求 2）
├── TEST_REPORT.md                # 测试报告（真实执行结果回填）
└── prompts.md                    # 全过程使用的所有提示词（README 交付物要求 3）
```

> 另有 `docker-compose.yml`、`db/init/*.sql`、`scripts/api-test.sh` 位于各自目录，不属于 `/docs`。

### 8.2 与 README 要求的逐条对应

| README 要求 | 落点 | 状态 |
|---|---|---|
| 分支要求：创建特性分支并推送 | `feat/ai-interview-delivery` | ☐ |
| 需求1 首页中英文切换 + `src/locales` | §4 | ☐ |
| 需求2-a docker-compose + phpmyadmin | §5.2 | ☐ |
| 需求2-b qa-service-user 医生 API | §5.4 | ☐ |
| 需求2-c 前后端联调 | §5.5 | ☐ |
| 需求3-a PRD 模板 | §6.3.1 → `docs/PRD_TEMPLATE.md` | ☐ |
| 需求3-b AI 用模板写 PRD | §6.3.2 → `docs/PRD-patient-auth.md` | ☐ |
| 需求3-c AI 用 PRD 开发（含拆分） | §6.3.3 → `docs/PRD_EXECUTION_PROMPTS.md` | ☐ |
| 测试要求1 curl 覆盖所有接口 | §7.3（31 条用例） | ☐ |
| 测试要求2 `/docs/API_TEST.md` + 生成报告 | §7.5 | ☐ |
| 交付1 代码 + 演示视频 | §8.3 | ☐ |
| 交付2 `/docs` 四类文档 | §8.1 | ☐ |
| 交付3 `/docs/prompts.md` | §9 | ☐ |
| 流程要求 PR 链接 → main | §8.4 | ☐ |

### 8.3 视频录制脚本清单

README 要求「视频中需要**完整展示**所完成的需求」。建议按以下顺序录制，每条录完简单停顿并口述：

| # | 场景 | 操作步骤 | 证明的需求点 |
|---|---|---|---|
| S1 | 环境演示 | `docker compose ps`（healthy）→ 浏览器打开 `localhost:8088` phpMyAdmin → 展开 `qa_live.t_doctor` → 展示 5 条数据与 `specialties` JSON 列、`t_patient` 表 | 需求2 基建 |
| S2 | 医生列表接口 | 终端 `curl -s localhost:8080/api/doctors \| jq` → 指向响应中**无 password**、`specialties` 为数组、`isActive` 为布尔 | 需求2 后端 + 契约 |
| S3 | 医生页联调 | 浏览器 `/doctors` 渲染 5 张卡片（doc004 显示「暂未开放」）→ **停掉后端容器** → 刷新页面显示错误/重试态 → 重启后端 → 恢复正常 | 需求2 前后端联调（真实性最强的一段） |
| S4 | 首页无回归 | `/` 页面统计数字与「开放诊室」列表正确 | 需求2 无回归 |
| S5 | 中文 → English | 首页右上角下拉选 English → 15 条文案即时切换（横向扫过 hero/统计/诊室三区） | 需求1 |
| S6 | English → 中文 + 持久化 | 切回中文 → **刷新浏览器** → 仍为中文 | 需求1 持久化 |
| S7 | 边界证明 | 切到 English 后跳到 `/doctors` 与 `/about`，展示**仅首页翻译** | 需求1 范围符合 README |
| S8 | 患者注册 | `/patient/login` 注册 Tab → 填用户名密码 → 提交成功 | 需求3 |
| S9 | 数据落库 | phpMyAdmin 刷新 `t_patient` → 展示该行 `password` 为 **`$2a$10$...` 密文** | 需求3 安全性 |
| S10 | 患者登录 + 保持 | 登录 Tab 登录 → 跳 `/consultation` → **刷新浏览器仍在线** → 向某医生提交一个问题 → 成功 | 需求3 登录态持久化 + 主链路 |
| S11 | 鉴权拦截 | 退出登录 → 直接访问 `/consultation` → 被重定向登录页；终端演示无 token 调 `/api/patient/me` 返 401 | 需求3 守卫 + JWT |
| S12 | 测试执行 | 运行 `bash scripts/api-test.sh` → 展示汇总 `PASS/FAIL` → 打开 `TEST_REPORT.md` | 测试要求 |
| S13 | 分支与 PR | `git log --oneline`（分支提交）→ GitHub PR 页面（含 diff）| 流程要求 |

> **录制提示**：S3 与 S9 是两条最能证明「真的接了数据库」的镜头，务必保留完整、不要剪辑过快。

### 8.4 PR 描述模板

```markdown
## 概述
基于 `main` 的特性分支，完成 README「技术要求」中 3 项需求 + 测试 + 文档，全部通过 AI Coding 提示词产出。

## 需求实现
- [x] 需求1 首页中英文切换（`web/qa-web/src/locales`，15 条文案，右上角下拉，localStorage 持久化）
- [x] 需求2 医生数据 MySQL 化（docker-compose + phpMyAdmin，MyBatis 接入，4 个 REST 接口，前后端联调）
- [x] 需求3 问诊用户账号密码登录（PRD 模板 + PRD + 拆分 ≤10 任务；BCrypt + JWT，独立登录注册页）

## 技术方案要点
- 后端：Spring Boot 3.5.7 / Java 17，新增 MyBatis XML 持久层 + `ApiResponse` 统一响应 + `@RestControllerAdvice`
- 鉴权：JJWT 0.12.x + `HandlerInterceptor` 白名单；密码 `BCryptPasswordEncoder`（仅引 spring-security-crypto）
- 前端：Vue3 + TS，新增 vue-i18n(v9) / axios；Vite proxy `/api` → `:8080`
- 数据库：MySQL 8 + phpMyAdmin，`t_doctor`（JSON 列存 specialties）/ `t_patient`

## 测试
- 31 条 curl 用例（含既有接口回归），见 `docs/API_TEST.md`
- 执行结果：`docs/TEST_REPORT.md`，通过率 ___%
- 一键回归：`bash scripts/api-test.sh`

## 文档
- `docs/IMPLEMENTATION_PLAN.md` 实现方案
- `docs/PRD_TEMPLATE.md`、`docs/PRD-patient-auth.md`、`docs/PRD_EXECUTION_PROMPTS.md`
- `docs/DESIGN-patient-auth.md`、`docs/API_TEST.md`、`docs/TEST_REPORT.md`、`docs/prompts.md`

## 演示视频
<附链接>

## 本地复现步骤
1. `docker compose up -d`
2. `cd server/qa-service-user && ./mvnw spring-boot:run`
3. `cd web/qa-web && npm install && npm run dev`
4. 访问 http://localhost:5173 ；phpMyAdmin http://localhost:8088

## 自检清单
- [x] `npm run build`（含 vue-tsc）通过
- [x] `./mvnw test` 通过
- [x] 既有接口 `/api/test/cors`、`/actuator/health` 无回归
```

### 8.5 合并前自检清单

| # | 检查项 | 命令 |
|---|---|---|
| C1 | 前端构建通过（TS strict） | `cd web/qa-web && npm run build` |
| C2 | 后端编译与测试通过 | `cd server/qa-service-user && ./mvnw test` |
| C3 | MyBatis 日志已关闭 `StdOutImpl`（决策 D8） | grep `log-impl` 于 `application.properties` |
| C4 | 无密码明文泄漏到任何 VO / 前端类型 | grep `password` 于 `dto/`、`DoctorVO`、`src/api/` |
| C5 | JWT 密钥非默认值 | grep `jwt.secret` |
| C6 | `.gitignore` 覆盖 `node_modules/`、`target/`、`*.log` | 若无根 `.gitignore` 需补（G6 关联） |
| C7 | `docker-compose.yml` 端口不与本机冲突 | `lsof -i :3306` / `:8088` |
| C8 | curl 全绿 | `bash scripts/api-test.sh` |
| C9 | docs 六份文档齐备 | `ls docs/` |
| C10 | `prompts.md` 已收录全部提示词 | 人工核对 §9 批次 |

---

## 9. AI Coding 提示词编排

### 9.1 编排原则

1. **先文档后代码**：需求3 要求「用 PRD 指导开发」，故 P3/P4 必须先于 P9/P10。
2. **先基建后业务**：容器 → 表 → 持久层 → Controller → 前端，任一步失败都在可控范围内。
3. **前后端分离、单批单一职责**：每批只产出一组文件，便于回退。
4. **每批必带上下文清单**：明确列出让 AI 读取的文件，避免它臆造路径。
5. **每批必带验收标准**：直接引用本文对应的 A× 条目，让 AI 自证。
6. **所有提示词实时沉淀**至 `docs/prompts.md`（README 交付物要求 3）。

### 9.2 提示词批次清单

| 批次 | 目标 | 输入上下文 | 产出文件 | 验收 |
|---|---|---|---|---|
| **P0** | 建立分支与任务视图 | README.md | 分支 `feat/ai-interview-delivery` | `git status` 在分支上 |
| **P1** | 依赖版本核对 | `server/qa-service-user/pom.xml`、`web/qa-web/package.json` | 一份「版本核对结论」写入本文 §2.4 备注 | 版本号可被 Maven/npm 解析 |
| **P2** | 编写本文方案 | README + 全仓结构 | `docs/IMPLEMENTATION_PLAN.md` | 章节齐全 |
| **P3** | PRD 模板 | `.asdm/toolsets/prd-builder/spec/feature-prd-spec.md` | `docs/PRD_TEMPLATE.md` | ≥10 章节 + 附录 A/B/C |
| **P4** | 特性 PRD | `docs/PRD_TEMPLATE.md`、`store/index.ts:74-92`、`Consultation.vue:4-49` | `docs/PRD-patient-auth.md` | A3-2、A3-3 |
| **P5** | 设计文档 | `docs/PRD-patient-auth.md`、§6 全节 | `docs/DESIGN-patient-auth.md` | 含表结构/契约/JWT 时序/前端状态流 |
| **P6** | 需求1 前端 i18n | `package.json`、`main.ts`、`AppHeader.vue`、`Home.vue` | `locales/*`、`LangSwitcher.vue`、改 3 文件 | A1-1 ~ A1-7 |
| **P7** | 需求2 容器与建表 | §5.2、§5.3、`doctor-user-list.json` | `docker-compose.yml`、`db/init/*.sql` | A2-1、A2-2 |
| **P8** | 需求2 后端持久层 | `pom.xml`、`application.properties`、`TestController.java` | `common/`、`exception/`、`entity/`、`dto/`、`handler/`、`mapper/`、`service/`、`controller/`、`resources/mapper/*.xml` | A2-3、A2-4 |
| **P9** | 需求2 前端联调 | `vite.config.ts`、`store/index.ts`、`Doctors.vue`、`App.vue`、`Consultation.vue:212-221` | `api/request.ts`、`api/doctor.ts`、改 5 文件 | A2-5 ~ A2-9 |
| **P10** | 需求3 后端鉴权 | §6.5/§6.6、`ApiResponse` | `config/`、`util/`、`entity/Patient`、`dto/`、`mapper/PatientMapper*`、`service/`、`controller/` | A3-4 ~ A3-8 |
| **P11** | 需求3 前端页面 | `DoctorLogin.vue`（范式参照）、`router/index.ts`、`Consultation.vue` | `PatientLogin.vue`、`api/patient.ts`、改 4 文件 | A3-9 ~ A3-12 |
| **P12** | 测试文档与脚本 | §7.3 用例矩阵 | `docs/API_TEST.md`、`scripts/api-test.sh` | A4-1 ~ A4-3 |
| **P13** | 执行测试并出报告 | `docs/API_TEST.md` | `docs/TEST_REPORT.md`（真实输出回填） | A4-4、A4-5 |
| **P14** | 提示词沉淀 | 上述 P0~P13 实际文本 | `docs/prompts.md` | C10 |
| **P15** | PR 与交付 | §8.4 模板 | PR → main | 自检 C1~C9 |

### 9.3 单批提示词模板

每批发给 AI 的提示词建议遵循以下结构（这也是 `docs/prompts.md` 的记录格式）：

````markdown
### P{编号}：{标题}

**背景**
{1-3 句说明为什么做这一步，引用本方案章节}

**请先阅读（上下文）**
- `/abs/path/fileA`
- `/abs/path/fileB`

**目标产出**
- [NEW] `/abs/path/newFileX` —— {职责}
- [MODIFY] `/abs/path/fileY` —— {改什么，不改什么}

**技术约束**
- {统一响应必须走 ApiResponse，不得裸返 Map}
- {VO 必须剔除 password}
- {不得引入 Spring Security 全量依赖}
- {TS strict：不留未使用变量}
- …

**验收标准**
- {引用 A×-× 条目，逐条可判定}

**完成后回报**
- 改动文件清单 + 关键 diff 摘要 + 自验结果
````

> **README 合规提示**：上述全部 P 批次必须以提示词形式驱动 AI 完成；人只能做「运行结果」与「微调」，不得直接编码。

---

## 10. 风险与工期排期

### 10.1 风险登记表

| # | 风险 | 影响 | 概率 | 缓解措施 |
|---|---|---|---|---|
| R1 | **本机 3306 端口已被占用**，docker 起不来 | 阻塞需求2 全部 | 中 | 改用 `3307:3306` 并同步改 `jdbc url`；启动前 `lsof -i :3306` 预检 |
| R2 | MyBatis XML 未被打包进 classpath | 启动报 `Invalid bound statement` | 高 | 已通过决策 D1 规避（放 `resources/mapper/`）。若 AI 仍放进 `src/main/java`，需在 `pom.xml` `<build><resources>` 补充包含规则 |
| R3 | `dependson` 只保证容器启动、MySQL 未就绪，Spring Boot 首启失败 | 中 | 高 | `docker-compose.yml` 已内置 `healthcheck` + `condition: service_healthy`（§5.2） |
| R4 | **`jjwt` 0.11/0.12 API 混写**导致编译失败 | 高 | 高 | P1 批次强制核对版本；提示词中明确「0.12.x 使用 `Jwts.builder()...signWith(key)` 与 `Jwts.parser().verifyWith(key).build().parseSignedClaims()`」 |
| R5 | **`tsconfig` strict + `noUnusedLocals`** 导致 AI 生成代码 `npm run build` 失败 | 中 | 高 | 每批前端提示词显式要求「不得留未使用变量/参数」；P6/P9/P11 完成后立即跑 build |
| R6 | `is_active` → `isActive` 未生效，前端拿到 `undefined` | 卡片「暂未开放」误判、在线诊室数为 0 | 中 | 必须开 `map-underscore-to-camel-case=true`；验收 A2-3 用 jq 断言 `isActive` 为 boolean |
| R7 | `specialties` 反序列化失败（JSON 串未转数组） | `v-for` 报错或标签异常 | 中 | 自定义 `TypeHandler` 并在 XML `<result>` 中显式声明；验收 A2-3 断言 array |
| R8 | 移除 `password` 后**医生登录失效** | 既有功能回归 | 中 | 决策 D5：`store/index.ts` 保留本地凭证表仅供 `loginDoctor` 比对；验收 A2-7 专测此路径 |
| R9 | **`Consultation.vue:215` 异步竞态**（首屏直开 `/consultation/:username`） | 医生未被选中且无提示 | 中 | §5.5.4 给出 `await` + `watch(route.params)` 双保险；验收 A2-8 要求测「首屏直开」和「页内跳转」两种路径 |
| R10 | CORS properties 与 Java Bean 双配置冲突，行为不可预期 | 联调偶发失败 | 中 | D9：删除 properties 的 CORS 段，保留并收窄 Java Bean 到 `http://localhost:5173` |
| R11 | 中文写入 MySQL **双重编码**（JDBC 读出乱码） | 数据不可读 | **已在 P8 实际发生并修复** | `db/init/*.sql` 开头加 `SET NAMES utf8mb4;`，配合属性 `characterEncoding=UTF-8`。根因：容器执行 initdb 脚本时客户端默认字符集是 latin1，会把文件的 UTF-8 中文按 latin1 解读后再转 utf8mb4 落库。**判定方法**：`SELECT HEX(name)`，正确 UTF-8 应为 `E5BCA0...`，若出现 `C3A5C2BC` 即为双重编码。注意 mysql 客户端直查时因往返转换会显示正常，**必须用 HEX 判定** |
| R12 | 时区导致 `created_at` 与本地差 8 小时 | 数据观感错误 | 低 | `serverTimezone=Asia/Shanghai` + 容器 `TZ=Asia/Shanghai` |
| R13 | **`'/api/doctors'` 被 JWT 拦截器误拦** | 首页/医生页全白 | 中 | 白名单含 `/api/doctors/**`；验收 T30/T31 专门反向验证 |
| R14 | JWT 密钥过短导致 `WeakKeyException` | 登录 500 | 中 | 密钥 ≥32 字节（256 bit）；`Keys.hmacShaKeyFor` 会直接抛错，启动自检即可发现 |
| R15 | AI 生成的 PRD 任务数 > 10 | 不符 PRD 约束 | 中 | 模板第 7 章明确上限；§6.3.2 已给出推荐的 10 条拆法，让 AI 参照 |
| R16 | 1 天工期紧张，文档与录制压缩 | 交付不完整 | 中 | 按 §10.2 排期执行；**视频 S3/S9 两段不可省** |

### 10.2 一天排期表（建议 8~10 小时）

| 时段 | 时长 | 批次 | 产出 | 里程碑 |
|---|---|---|---|---|
| T+0.0h | 0.5h | P0、P1 | 分支 + 依赖版本结论 | ✅ 可开工 |
| T+0.5h | 1.0h | P3、P4、P5 | PRD 模板 + 特性 PRD + 设计文档 | ✅ 需求3 文档前置完成（README 要求顺序） |
| T+1.5h | 0.5h | P7 | docker-compose + 建表 SQL | ✅ MySQL/phpMyAdmin 可访问 |
| T+2.0h | 1.5h | P8 | 后端 MyBatis 持久层 + 4 个医生接口 | ✅ curl 能拿到数据 |
| T+3.5h | 1.0h | P9 | 前端联调（api 层 + proxy + store + 三态 + 竞态） | ✅ 需求2 完成 |
| T+4.5h | 1.0h | P6 | 需求1 i18n 全套 | ✅ 需求1 完成 |
| T+5.5h | — | ☕ 缓冲 / 午饭 | — | — |
| T+6.5h | 1.5h | P10 | 需求3 后端鉴权 | ✅ 三个接口可用 |
| T+8.0h | 1.0h | P11 | 需求3 前端页面 + 守卫 | ✅ 需求3 完成 |
| T+9.0h | 0.8h | P12、P13 | API_TEST + 脚本 + 测试报告 | ✅ 测试要求完成 |
| T+9.8h | 0.7h | 录制视频 S1~S13 | 视频 | ✅ 交付1 |
| T+10.5h | 0.5h | P14、P15 | prompts.md + PR | ✅ 全部交付完成 |

> **压缩策略（若超时）**：优先牺牲 R16 中标注可选的视频段落（保留 S1/S3/S5/S8/S9/S10）；
> 其次合并 P12/P13（脚本与报告一次性产出）。**不要牺牲 C1/C2/C8 三项自检。**

### 10.3 降级方案（Docker 不可用时的兜底）

若面试环境无法运行 Docker：

| 替代 | 做法 |
|---|---|
| MySQL | 使用本机已安装的 MySQL / Homebrew `brew services start mysql`，手工执行 `db/init/*.sql` |
| 极端兜底 | `application.properties` 切到 **H2**（`jdbc:h2:mem:qalive`，需加 `com.h2database:h2` 依赖），建表改用 H2 方言（`VARCHAR` 替代 `JSON` 列） |
| phpMyAdmin | README 明确要求，故**不能跳过**；即使走本机 MySQL 也应另起 phpMyAdmin 容器（`docker run` 单容器方式）以满足要求 |

降级必须在 `README`/PR 描述中显式说明原因。

---

## 附录 A：方案涉及的全部新增/修改文件一览

**新增（NEW）**

```
docker-compose.yml
db/init/01-schema.sql
db/init/02-data.sql
scripts/api-test.sh
docs/IMPLEMENTATION_PLAN.md
docs/PRD_TEMPLATE.md
docs/PRD-patient-auth.md
docs/PRD_EXECUTION_PROMPTS.md
docs/DESIGN-patient-auth.md
docs/API_TEST.md
docs/TEST_REPORT.md
docs/prompts.md

server/qa-service-user/src/main/java/com/leansofx/qaserviceuser/
├── common/ApiResponse.java
├── exception/GlobalExceptionHandler.java
├── handler/JsonStringListTypeHandler.java
├── config/PasswordEncoderConfig.java
├── config/JwtInterceptor.java
├── config/WebConfig.java
├── util/JwtUtil.java
├── entity/Doctor.java
├── entity/Patient.java
├── dto/DoctorVO.java
├── dto/PatientVO.java
├── dto/RegisterRequest.java
├── dto/LoginRequest.java
├── dto/LoginResponse.java
├── mapper/DoctorMapper.java
├── mapper/PatientMapper.java
├── service/DoctorService.java
├── service/PatientService.java
├── controller/DoctorController.java
└── controller/PatientController.java

server/qa-service-user/src/main/resources/mapper/
├── DoctorMapper.xml
└── PatientMapper.xml

web/qa-web/src/
├── locales/index.ts
├── locales/zh-CN.ts
├── locales/en-US.ts
├── components/LangSwitcher.vue
├── views/PatientLogin.vue
├── api/request.ts
├── api/doctor.ts
└── api/patient.ts
```

**修改（MODIFY）**

```
server/qa-service-user/pom.xml
server/qa-service-user/src/main/resources/application.properties
server/qa-service-user/src/main/java/.../QaServiceUserApplication.java      # @MapperScan
server/qa-service-user/src/main/java/.../config/CorsConfig.java             # 收敛白名单

web/qa-web/package.json                    # vue-i18n / axios / @ant-design/icons-vue
web/qa-web/vite.config.ts                  # proxy
web/qa-web/index.html                      # title / lang
web/qa-web/src/main.ts                     # app.use(i18n)
web/qa-web/src/App.vue                     # 触发 loadDoctors()
web/qa-web/src/router/index.ts             # /patient/login + beforeEach 守卫
web/qa-web/src/store/index.ts              # loadDoctors / 患者登录态 / Doctor 类型
web/qa-web/src/components/AppHeader.vue    # LangSwitcher + 患者登录入口
web/qa-web/src/components/AppFooter.vue    # <a> → <router-link>（建议）
web/qa-web/src/views/Home.vue              # 15 处 $t()
web/qa-web/src/views/Doctors.vue           # 远端数据源 + 三态
web/qa-web/src/views/Consultation.vue      # 移除旧验证表单 + 竞态修复
```

**保持不变（只读参考）**

```
web/qa-web/src/views/About.vue             # 零改动
web/qa-web/src/views/DoctorRoom.vue        # 零改动
web/qa-web/src/views/DoctorLogin.vue       # 依赖决策 D5 保持零改动
web/qa-web/src/data/*.json                 # 保留（patient/question 仍为前端 mock；doctor 仅凭证比对用）
server/qa-service-question/**              # 不在本次范围
server/qa-service-statistic/**             # 空模块，不动
```

---

**文档版本**：v1.0 · PLANNED
**下一步**：按 [§9.2](#92-提示词批次清单) 的 P0 → P15 顺序，以提示词驱动 AI 分批执行。
