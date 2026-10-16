# AI Coding 提示词清单

> 本文记录本次交付过程中使用的全部提示词（README 交付物要求 3）。
> 所有代码、测试、文档均由这些提示词驱动 AI 产出，人工仅做运行验证与必要的手工微调。
>
> 说明：README 要求「不得直接编码」。本项目的做法是——由我在 AI 会话中组织提示词、
> AI 产出全部文件，人工只执行构建/测试/浏览器验证，并对 AI 产出做少量手工修正。

## 提示词模板

每一批提示词统一采用以下结构（便于复用与审计）：

````markdown
### P{编号}：{标题}

**背景**
{说明为什么做这一步，引用方案章节}

**请先阅读（上下文）**
- /绝对路径/文件A
- /绝对路径/文件B

**目标产出**
- [NEW] /绝对路径/新文件X —— {职责}
- [MODIFY] /绝对路径/文件Y —— {改什么，不改什么}

**技术约束**
- {约束 1}
- {约束 2}

**验收标准**
- {引用 IMPLEMENTATION_PLAN.md 的 A×-× 条目}

**完成后回报**
- 改动文件清单 + 关键 diff 摘要 + 自验结果
````

---

## P0 · 建立特性分支

```text
阅读 /Users/bhd/IdeaProjects/qa-live-healthcare-interview/README.md 的「分支要求」。
本项目已存在自建特性分支 testbhd20261015（基于 main，且已推送远端），
请确认该分支相对 main 的差异与提交记录，并说明后续工作应在此分支上进行。
不要创建新的嵌套分支。
```

**结果**：确认在 `testbhd20261015` 上继续，不新建分支。

---

## P1 · 依赖版本核对

```text
我需要为 qa-live-healthcare-interview 项目引入以下依赖，请逐一核对与
Spring Boot 3.5.7 + Java 17 的兼容版本，并说明核对依据（不要凭记忆给版本号）：
1. mybatis-spring-boot-starter（MyBatis XML 持久层）
2. mysql-connector-j
3. jjwt-api / jjwt-impl / jjwt-jackson（JWT）
4. spring-security-crypto（BCrypt）
请实际查询 Maven 仓库元数据（如 repo1.maven.org 的 maven-metadata.xml），
并特别检查 mybatis-spring-boot-starter 是否存在需要 Spring Boot 4 的新版本线
（若存在则必须排除，因为本项目是 Spring Boot 3.5.7）。
```

**结果**：`mybatis-spring-boot-starter` 4.x 依赖 Spring Boot 4 → 选用 **3.0.5**；
jjwt → **0.12.7**；mysql-connector-j 与 spring-security-crypto 由 parent 纳管。

```text
请核对前端依赖版本：
- vue-i18n：项目是 Vue 3.5，请查询 npm dist-tags 与 peerDependencies 确认应选哪个大版本，
  并注意 npm 安装时的弃用告警。
- axios 与 @ant-design/icons-vue：后者需与已安装的 ant-design-vue@4.2.6 配对，
  请查询 4.2.6 的 dependencies 确认。
```

**结果**：vue-i18n v9 已被官方标记弃用 → 改用 v11（`^11.1.10`，实际解析 11.4.14）；
axios `^1.20.0`；`@ant-design/icons-vue` `^7.0.1`。

---

## P2 · 编写实现方案

```text
分析 /Users/bhd/IdeaProjects/qa-live-healthcare-interview 项目代码，
然后结合 README.md 的需求部分给出需求实现方案，输出为 md 方案文档。
要求：逐条对应 README 的「需求列表 / 测试要求 / 交付物要求」；
每条结论须对照源码核验路径与行号；每个技术决策写清「为什么」；
明确标注需要新建/修改的文件路径；明确标注哪些依赖版本需在执行时核对。
```

**产出**：`docs/IMPLEMENTATION_PLAN.md`

---

## P3 · PRD 模板

```text
请阅读 .asdm/toolsets/prd-builder/spec/feature-prd-spec.md，
基于它裁剪出一份「PRD 文档模板」，输出到 docs/PRD_TEMPLATE.md。
要求：保留原 spec 的 10 个一级章节结构，不得增删一级章节；
去掉原 spec 中面向 AI 的「生成指令」类元内容；必须保留「任务数 ≤10」的约束；
新增面向本仓库的附录：A 技术栈约束速查、B 受影响文件清单模板、C API 契约清单模板；
新增「Out of Scope（明确不做）」为必填章节，用于约束 AI 的改动范围。
```

**产出**：`docs/PRD_TEMPLATE.md`

---

## P4 · 特性 PRD

```text
使用 docs/PRD_TEMPLATE.md 模板，为「为问诊用户提供用户名密码登录方式」编写 PRD，
输出到 docs/PRD-patient-auth.md。
背景（请先阅读这些文件确认现状）：
- web/qa-web/src/store/index.ts 的 verifyPatient（按姓名+生日查找，找不到就自动建档）
- web/qa-web/src/views/Consultation.vue（姓名+生日验证表单）
- web/qa-web/src/views/DoctorLogin.vue（UI 范式参照）
硬性要求：Feature ID 用 PATIENT-AUTH-001；Out of Scope 中明确「医生登录改造不做」
「历史患者数据不迁移」；Task Breakdown ≤10 个任务；
NFR 必须写清密码存储方式、token 机制、脱敏要求。
```

**产出**：`docs/PRD-patient-auth.md`

---

## P5 · 设计文档

```text
基于 docs/PRD-patient-auth.md，编写 docs/DESIGN-patient-auth.md。
必须包含：
1. 数据模型（t_patient 建表语句 + 字段选型理由 + 与既有 patient-user.json 的关系）
2. 后端分层设计（Controller/Service/Mapper 与跨层组件表）
3. Entity 与 VO 分离的安全设计说明
4. 注册、登录、鉴权拦截三段的时序图
5. 前端状态与存储设计（token 存哪里、登录态恢复的两条路径）
6. 关键取舍记录与安全清单
```

**产出**：`docs/DESIGN-patient-auth.md`

---

## P6 · 需求1 首页中英文切换

```text
请阅读 web/qa-web/src/views/Home.vue、src/components/AppHeader.vue、src/main.ts、package.json。

目标（README 需求1）：给首页添加中英文切换，右上角加语言切换下拉菜单，
选择「中文/English」时动态切换；语言资源文件必须放在 web/qa-web/src/locales 目录。
只需要处理首页本身，Header 导航与 Footer 保持中文。

产出：
- [NEW] src/locales/zh-CN.ts（中文源真值）
- [NEW] src/locales/en-US.ts（用 satisfies typeof zhCN 约束形状一致）
- [NEW] src/locales/index.ts（createI18n + localStorage 持久化 + 浏览器语言嗅探 + <html lang> 同步）
- [NEW] src/components/LangSwitcher.vue（a-select，选项 label 不翻译）
- [MODIFY] src/components/AppHeader.vue（在「医生登录」按钮右侧挂载，加 .header-actions 控制间距）
- [MODIFY] src/main.ts（app.use(i18n) 置于 app.use(Antd) 之前）
- [MODIFY] src/views/Home.vue（15 处文案替换为 $t(...)）
- [MODIFY] index.html（title 改为项目名）

技术约束：
- vue-i18n 用 v11，legacy: false
- Home.vue 第 86 行 <a-badge text="在线"> 是属性绑定，必须写 :text="$t(...)"，改插槽无效
- Home.vue 里 {{ statistics.xxx }} 的数字不翻译
- 医生姓名/职称/科室等来自数据库，属业务数据，不做国际化
- TS strict + noUnusedLocals，不得留未使用变量

验收：切 English 后 15 条文案即时变更（不刷新）；刷新后保持；Header/Footer 仍为中文；npm run build 通过
```

**产出**：`src/locales/*`、`LangSwitcher.vue`，并修改 `AppHeader.vue` / `Home.vue` / `main.ts` / `index.html`

---

## P7 · 需求2 基础设施

```text
在仓库根目录创建 docker-compose.yml 与 db/init/*.sql。

要求：
- mysql:8.0 + phpmyadmin，phpmyadmin 端口 8088
- mysql 必须带 healthcheck，phpmyadmin 用 depends_on: condition: service_healthy
  （只写 depends_on 不保证 MySQL 可连，是 Spring Boot 首启失败的头号原因）
- 数据卷持久化；初始化 SQL 挂到 /docker-entrypoint-initdb.d
- 建表 t_doctor（含 JSON 列 specialties、is_active）与 t_patient（username 唯一索引）
- t_patient.id 用 VARCHAR(32)，注意应用层生成的 id 不能超过该长度
- 种子数据必须与 web/qa-web/src/data/doctor-user-list.json 完全一致（5 位医生，doc004 离线）

关键：每个 .sql 文件开头必须写 SET NAMES utf8mb4;
否则容器 initdb 客户端默认 latin1，会把 UTF-8 中文按 latin1 解读后转存，
造成双重编码（mysql 客户端直查反而显示正常，必须用 SELECT HEX(name) 判定）。
```

**产出**：`docker-compose.yml`、`db/init/01-schema.sql`、`db/init/02-data.sql`

---

## P8 · 需求2 后端持久层

```text
请阅读 server/qa-service-user/pom.xml、application.properties、controller/TestController.java。

在 qa-service-user 中实现医生列表 API（数据源为 MySQL，MyBatis XML）：
- [MODIFY] pom.xml：新增 mybatis-spring-boot-starter 3.0.5、mysql-connector-j
- [MODIFY] application.properties：datasource + mybatis 配置
- [NEW] common/ApiResponse.java（{code,message,data,timestamp}）
- [NEW] exception/GlobalExceptionHandler.java + ErrorCode.java
- [NEW] entity/Doctor.java、dto/DoctorVO.java（VO 必须剔除 password）
- [NEW] handler/JsonStringListTypeHandler.java（JSON 列 ↔ List<String>）
- [NEW] mapper/DoctorMapper.java + resources/mapper/DoctorMapper.xml
- [NEW] service/DoctorService.java、controller/DoctorController.java
- [MODIFY] 启动类加 @MapperScan

接口：GET /api/doctors、/api/doctors/active、/api/doctors/{id}、/api/doctors/username/{username}
约束：
- XML 必须放 src/main/resources/mapper/（放 src/main/java 不会被打包）
- mybatis.configuration.map-underscore-to-camel-case=true（is_active → isActive）
- characterEncoding 必须写 UTF-8 而不是 utf8mb4（后者会让 JDBC 抛 Unsupported character encoding）
- 所有 Controller 返回 ApiResponse，不得裸返 Map
- 不存在的医生返回 404
```

**产出**：后端医生接口全部文件

---

## P9 · 需求2 前端联调

```text
请阅读 web/qa-web/vite.config.ts、src/store/index.ts、src/views/Doctors.vue、
src/views/Consultation.vue、src/App.vue。

把医生数据从本地 JSON 改为调用后端 API：
- [MODIFY] vite.config.ts：server.proxy['/api'] → http://localhost:8080
- [NEW] src/api/request.ts（axios 实例，baseURL '/api'，响应拦截器剥壳 ApiResponse.data）
- [NEW] src/api/doctor.ts（fetchDoctors / fetchActiveDoctors / fetchDoctorById / fetchDoctorByUsername）
- [MODIFY] src/store/index.ts：doctors 初值改 []，新增 loadDoctors()；
  Doctor 接口移除 password；保留一份本地凭证表仅供 DoctorLogin.vue 比对，使其零改动
- [MODIFY] src/App.vue：onMounted 触发 loadDoctors()
- [MODIFY] src/views/Doctors.vue：loading / error / empty / success 四态
- [MODIFY] src/views/Consultation.vue：修复竞态

关键约束：
- 必须在 store 层统一加载，不能只改 Doctors.vue —— 因为 Home.vue 的统计
  和 Consultation.vue 的医生下拉都消费同一个 doctors 数组
- Consultation.vue 第 215 行原本在 onMounted 里一次性同步读 getDoctorByUsername()，
  异步化后首屏直开会拿不到，必须改为 watch(store.state.doctors) + watch(route.params) 双保险
- 不要在同一元素上同时使用 v-for 和 v-else，用 <template v-else> 包一层
```

**产出**：前端 API 层、proxy、store 改造、Doctors 四态、竞态修复

---

## P10 · 需求3 后端鉴权

```text
实现问诊用户注册/登录与 JWT 鉴权（对应 docs/PRD-patient-auth.md）。
- [NEW] config/PasswordEncoderConfig.java（BCryptPasswordEncoder Bean）
- [NEW] util/JwtUtil.java（JJWT 0.12.7）
- [NEW] entity/Patient.java、dto/{PatientVO,RegisterRequest,LoginRequest,LoginResponse}.java
- [NEW] mapper/PatientMapper.java + resources/mapper/PatientMapper.xml
- [NEW] service/PatientService.java
- [NEW] controller/PatientController.java
- [NEW] config/JwtInterceptor.java + config/WebConfig.java

硬性约束：
- 只引 spring-security-crypto，禁止引入完整 spring-boot-starter-security
- JJWT 0.12.x 必须用 Jwts.parser().verifyWith(key).build().parseSignedClaims()，
  0.11 的 parserBuilder().setSigningKey() 已移除
- 密钥 >= 256 bit（Keys.hmacShaKeyFor 会自动校验）
- Entity 含 password 但绝不能作为返回值，一律转 PatientVO
- 白名单：/api/doctors/**、/api/patient/login、/api/patient/register、/api/test/**、/actuator/**
- 登录失败对外统一返回「用户名或密码错误」，不区分用户不存在与密码错误（防枚举），
  但内部 code 保留 1003/1004 便于排查
- 主键 id 长度不得超过 VARCHAR(32)
- 参数校验需要 @Valid + spring-boot-starter-validation
```

**产出**：后端鉴权全部文件

---

## P11 · 需求3 前端

```text
请阅读 src/views/DoctorLogin.vue（UI 范式参照）、src/views/Consultation.vue、src/router/index.ts。

实现患者登录注册页面与路由守卫：
- [NEW] src/api/patient.ts（register / login / fetchMe / logout）
- [NEW] src/views/PatientLogin.vue（a-tabs：登录 + 注册）
- [NEW] src/components/LoginForm.vue、RegisterForm.vue
- [MODIFY] src/router/index.ts：新增 /patient/login；beforeEach 对 /consultation 做登录校验，
  未登录跳 /patient/login?redirect=<原路径>
- [MODIFY] src/store/index.ts：loginPatient / logoutPatient / restorePatient
- [MODIFY] src/App.vue：onMounted 先 restorePatient() 再 loadDoctors()
- [MODIFY] src/views/Consultation.vue：移除「姓名+生日」表单，改为登录引导卡片
- [MODIFY] src/components/AppHeader.vue：已登录显示用户名+退出，未登录显示「用户登录」

约束：
- api 层 401 时不要直接 router.push，改为清 token + 广播事件，避免循环引用
- token 存 localStorage，保证刷新后仍登录
- 移除旧的 verifyPatient 自动建档逻辑
- TS strict，不得留未使用 import
```

**产出**：`PatientLogin.vue` 等前端文件

---

## P12 · 测试文档与脚本

```text
编写 docs/API_TEST.md 与 scripts/api-test.sh。
- API_TEST.md：给出可直接复制的 curl 命令，覆盖既有接口回归、医生接口、
  患者注册/登录/鉴权、白名单反向验证；含错误码对照表；
  末尾明确要求 AI「执行测试并把真实输出回填 TEST_REPORT.md，禁止编造」
- scripts/api-test.sh：一键回归，可重复执行（用户名带时间戳），
  失败时退出码 1；无 jq 时降级 python3

重要经验（请写进脚本注释与文档）：
1. 不要在 assert 参数里嵌套复杂引号的 $(...)，macOS bash 会错误分词导致
   实参个数错乱、断言标签错位，产生「假绿」。必须先赋变量再断言。
2. jq 对数组取长度/类型要用管道形式：.data | length、.data[0].specialties | type
```

**产出**：`docs/API_TEST.md`、`scripts/api-test.sh`

---

## P13 · 执行测试并生成报告

```text
按照 docs/API_TEST.md 执行测试，并生成 docs/TEST_REPORT.md。
要求：
1. 先确认 /actuator/health 为 UP
2. 执行 bash scripts/api-test.sh，完整保留真实输出
3. 用 docker exec 查询 t_patient，确认 password 前缀为 $2a$10$ 且长度为 60
4. 在浏览器中确认 E1~E9 端到端场景
5. 严禁编造响应内容，报告中每条响应都必须来自真实执行
6. 如实记录过程中遇到的失败与修复
```

**产出**：`docs/TEST_REPORT.md`

---

## P14 · 提示词沉淀

```text
把本次交付过程中使用过的全部提示词整理到 docs/prompts.md，
按 P0~P15 编号组织，每条注明：背景、输入上下文、目标产出、技术约束、验收标准。
```

**产出**：本文

---

## P15 · 自检与交付

```text
执行合并前自检：
- npm run build（含 vue-tsc -b）通过
- ./mvnw test 通过
- bash scripts/api-test.sh 全绿且可重复执行
- docs 目录下 8 份文档齐备
```

---

## 手工微调记录

README 允许「对 AI 生成的代码/文档进行手工调整」。本次的手工调整如下：

| # | 文件 | 调整内容 | 原因 |
|---|---|---|---|
| 1 | `db/init/*.sql` | 开头补 `SET NAMES utf8mb4;` | AI 初版未写，导致中文双重编码 |
| 2 | `application.properties` | `characterEncoding` 由 `utf8mb4` 改为 `UTF-8` | JDBC 抛 `Unsupported character encoding` |
| 3 | `PatientService.java` | id 生成改为 `pt-` + UUID 前 12 位 | 原 39 字符超出 `VARCHAR(32)` |
| 4 | `pom.xml` | 补 `spring-boot-starter-validation` | `@NotBlank` 编译不过 |
| 5 | `scripts/api-test.sh` | 重构为「先赋变量再断言」，修正 4 处 jq 表达式 | 消除嵌套引号分词问题与数组取长度写法错误 |
| 6 | `package.json` | `vue-i18n` 由 `^9.14.5` 改为 `^11.1.10` | v9 官方已弃用 |
| 7 | `docs/IMPLEMENTATION_PLAN.md` | 回填实际核对到的依赖版本与新增风险 R11 | 保持文档与实际一致 |
