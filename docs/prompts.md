# 提示词记录（Prompts）

本文档记录完成 QA Live Healthcare 面试项目过程中，向 AI 发出的全部提示词，按时间顺序排列。

---

## 1. 项目结构说明

```
在该项目目录中，web目录是前端部分，server目录是后端部分
```

**用途：** 说明项目目录结构，为后续前后端分工开发提供上下文。

---

## 2. 首页中英文切换（需求 1）

```
给web端的首页添加中英文切换能力，在页面右上角添加语言切换下拉菜单，在用户选择 中文/English 选项时动态切换页面显示内容到对应语言。中英文语言资源文件需要保存在 web/qa-web/src/locales 目录中。只需要处理首页本身，无需处理其他页面。
```

**用途：** 实现首页 i18n，使用 vue-i18n，语言文件存放于 `src/locales`。

**主要产出：**
- `web/qa-web/src/locales/zh-CN.ts`、`en.ts`、`index.ts`
- `Home.vue` 文案国际化
- `LangSwitcher` 语言切换组件

---

## 3. 医生数据迁移至 MySQL（需求 2 - 整体）

```
当前应用的数据全部使用json文件的方式在前端项目中存储，请将医生相关的数据(D:\workSpace\github\qa-live-healthcare-interview\web\qa-web\src\data\doctor-user-list.json)使用mysql数据库存储。
```

**用途：** 将医生数据从前端 JSON 迁移到 MySQL，并完成前后端联调。

**主要产出：**
- 后端 `Doctor` 实体、API
- 前端 `api/doctor.ts`、`store` 改造
- 数据库初始化脚本

---

## 4. Docker 数据库环境（需求 2 - 子项）

```
在server后端目录中，创建 docker-compose.yml 用于创建mysql数据库，并挂接phpmyadmin作为数据库服务器管理界面
```

**用途：** 在 `server/` 目录创建 MySQL + phpMyAdmin 编排文件。

**主要产出：**
- `server/docker-compose.yml`
- `server/qa-service-user/src/main/resources/db/init.sql`

---

## 5. 医生列表 API（需求 2 - 子项）

```
在 qa-service-user 服务中创建可以支持前端 医生页面 显示医生列表所需要的api
```

**用途：** 实现 `GET /api/doctors` 等医生相关 REST 接口。

**主要产出：**
- `DoctorController`、`DoctorService`、`DoctorRepository`

---

## 6. 数据库连接错误排查

```
2026-07-01T10:26:29.456+08:00 ERROR 5680 --- [qa-service-user] [           main] o.h.engine.jdbc.spi.SqlExceptionHelper   : Unknown database 'qa_healthcare'
...（完整 Spring Boot 启动错误日志）
```

**用途：** 排查并修复 `qa_healthcare` 数据库不存在导致的启动失败。

**主要产出：**
- `application.properties` 增加 `createDatabaseIfNotExist=true`
- `ddl-auto=update`、`data.sql` 自动初始化

---

## 7. API 测试文档（测试要求 - 文档）

```
在/docs/目录下创建API_TEST.md 用于描述以上接口测试流程
```

**用途：** 编写接口 curl 测试流程文档。

**主要产出：**
- `docs/API_TEST.md`

---

## 8. 问诊用户登录（需求 3 - 功能）

```
为问诊用户提供用户名密码登录方式
```

**用途：** 将问诊页从「姓名+生日」验证改为用户名密码登录/注册，数据存 MySQL。

**主要产出：**
- `Patient` 实体、`PatientController`
- `Consultation.vue` 登录/注册 Tab
- `api/patient.ts`

---

## 9. PRD 文档模版（需求 3 - 文档）

```
在 /docs 目录中编写规范的PRD文档模版
```

**用途：** 创建可复用的产品需求文档模版，供 AI 编写特性 PRD。

**主要产出：**
- `docs/PRD_TEMPLATE.md`

---

## 10. 编写特性 PRD（需求 3 - 文档）

```
根据PRD_TEMPLATE.md模版编写此特性的PRD文档
```

**用途：** 按模版编写「问诊用户用户名密码登录」特性的完整 PRD。

**主要产出：**
- `docs/prd/PRD-003-问诊用户登录.md`

---

## 11. curl 接口测试（测试要求 - 实现）

```
使用 curl 构建对于以上需求中所涉及的所有接口的测试
```

**用途：** 为全部后端接口编写可执行的 curl 测试脚本。

**主要产出：**
- `docs/run-api-tests.sh`
- `docs/run-api-tests.ps1`
- `docs/api-tests/curl-commands.sh`

---

## 12. 执行测试并生成报告（测试要求 - 执行）

```
提供 /docs/API_TEST.md 用于描述以上接口测试流程，并使用这份文档完成测试并生成测试报告
```

**用途：** 按 `API_TEST.md` 执行测试，输出测试报告。

**主要产出：**
- `docs/API_TEST_REPORT.md`（14/14 用例通过）

---

## 13. 按 PRD 完成开发（需求 3 - 开发）

```
使用此PRD文档完成特性开发，如果需要进行拆分请在PRD文档中提供拆分步骤
```

**用途：** 依据 PRD-003 按实现拆分步骤完成问诊用户登录特性开发与验收。

**主要产出：**
- PRD 第 9 节 10 步拆分全部完成
- 验收标准 AC-001～AC-009 通过

---

## 14. UI 布局优化

```
中英文切换按钮遮挡住了一个搜索框，请优化
```

**用途：** 修复首页语言切换器与页头「医生登录」按钮重叠的布局问题。

**主要产出：**
- `LangSwitcher.vue` 移入 `AppHeader.vue`
- 移除 `Home.vue` 中 `position: fixed` 的定位方式

---

## 15. 提示词归档（交付要求）

```
/docs/prompts.md 中列出在这个过程中你所使用的所有提示词
```

**用途：** 汇总面试过程中使用的全部 AI 提示词，作为交付物之一。

**主要产出：**
- 本文档 `docs/prompts.md`

---

## 提示词使用统计

| 类别 | 数量 |
|------|------|
| 功能开发 | 6 |
| 文档编写 | 4 |
| 测试相关 | 2 |
| 问题排查 | 1 |
| 项目说明 / 交付 | 2 |
| **合计** | **15** |

---

## 建议的复用提示词模板

后续类似需求可参考以下提示词结构：

### 功能开发

```
在 [模块/目录] 中实现 [功能描述]。
技术要求：[技术栈/约束]。
只需要处理 [范围]，无需处理 [排除范围]。
```

### 文档编写

```
在 /docs 目录中编写 [文档类型]，用于 [用途说明]。
请参考 [已有文件] 的结构与风格。
```

### 测试

```
使用 curl 构建对于 [需求范围] 所涉及的所有接口的测试。
提供测试文档描述流程，并执行测试生成测试报告。
```

### 按 PRD 开发

```
请阅读 docs/prd/PRD-XXX.md，严格按照 PRD 中「实现拆分」章节顺序完成开发。
每完成一步对照验收标准自检，完成后更新 API_TEST.md 并生成测试报告。
```
