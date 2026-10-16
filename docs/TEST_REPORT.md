# API 测试报告

> 本报告的响应数据全部来自**真实执行**，未做任何编造。
> 测试流程文档：`docs/API_TEST.md`；一键脚本：`scripts/api-test.sh`

| 项 | 内容 |
|---|---|
| 执行日期 | 2026-10-15 |
| 被测分支 | `testbhd20261015`（README 要求的自建特性分支） |
| 被测服务 | `qa-service-user` :8080（Spring Boot 3.5.7 / Java 17） |
| 数据库 | MySQL 8.0（Docker `qa-live-mysql`）+ phpMyAdmin :8088 |
| 前端 | `web/qa-web`（Vue 3.5 + Vite 5.4） |
| 操作系统 | macOS（darwin） |
| 总体结论 | ✅ **全部通过** |

---

## 1. 环境准备

```bash
docker compose up -d
docker compose ps
cd server/qa-service-user && ./mvnw spring-boot:run
```

| 容器 | 状态 |
|---|---|
| `qa-live-mysql` | healthy |
| `qa-live-phpmyadmin` | started |

健康检查实际输出：

```json
{"status":"UP","components":{"db":{"status":"UP","details":{"database":"MySQL","validationQuery":"isValid()"}},"diskSpace":{"status":"UP", ...}}}
```

---

## 2. 自动化测试汇总

执行：`bash scripts/api-test.sh`（JSON 解析引擎：jq）

```
总计 53 项 | 通过 53 | 失败 0
通过率：100%
全部用例通过
```

脚本退出码 `0`。为验证幂等性连续执行 3 次，结果一致（53/53 通过）。

### 2.1 分模块明细

| 模块 | 结果 |
|---|---|
| 环境检查（2 项） | ✅ 全通过 |
| 既有接口回归（4 项） | ✅ 全通过 |
| 需求2 · 医生接口（15 项） | ✅ 全通过 |
| 需求3 · 患者注册（10 项） | ✅ 全通过 |
| 需求3 · 患者登录与 JWT（9 项） | ✅ 全通过 |
| 需求3 · `/api/patient/me` 鉴权矩阵（8 项） | ✅ 全通过 |
| 白名单反向验证（3 项） | ✅ 全通过 |
| **合计 53 项** | ✅ **53 通过 / 0 失败** |

### 2.2 关键断言的实际输出

| 用例 | 断言内容 | 实测值 |
|---|---|---|
| T04b-datasource-up | 数据源连通 | `UP` |
| T05b-doctors-count-5 | 全量医生数 | `5` |
| T06a-no-password-field | 响应不含 password | `null` |
| T06b-specialties-is-array | specialties 为数组 | `array` |
| T06c-isActive-isBoolean | isActive 为布尔 | `boolean` |
| T06d-chinese-not-mojibake | 中文无双重编码 | `张伟医生` |
| T07b-active-count-4 | 在线医生数（排除 doc004） | `4` |
| T08c-by-id-experience | 中文业务字段正确 | `15年临床经验` |
| T14-register-created | 注册成功 | HTTP `201` |
| T15b-duplicate-error-code | 用户名重复 | `1001` |
| T18b-validation-error-code | 参数校验失败 | `1002` |
| T20-too-long-username-400 | 超长用户名被拒 | HTTP `400` |
| T21-login-success-200 | 登录成功 | HTTP `200` |
| T21b2-expiresIn-positive | token 有效期 | `86400000` ms |
| T21c-token-is-three-part-jwt | token 为三段式 JWT | `3` |
| T23b-identical-message-anti-enumeration | 防用户名枚举 | 两次均返回 `用户名或密码错误` |
| T23c-internal-codes-differ | 内部码仍可区分 | `1004` vs `1003` |
| T25b-me-correct-user | token 解析出正确用户 | 与注册用户名一致 |
| T26 ~ T29 | 无 token / 篡改 / 缺 Bearer / 非法 token | 全部 `401` |
| T30 / T31 | 白名单接口未被误拦截 | 全部 `200` |

---

## 3. 安全性专项校验

### 3.1 密码存储（不只看接口返回）

```bash
docker exec qa-live-mysql mysql -uroot -proot123456 -D qa_live \
  -e "SELECT username, LEFT(password,7) AS prefix, LENGTH(password) AS len FROM t_patient;"
```

实测结果（节选）：

| username | password 前缀 | 长度 |
|---|---|---|
| `e2e1792051589` | `$2a$10$` | 60 |
| `pt1792048631` | `$2a$10$` | 60 |
| `pt1792053061` | `$2a$10$` | 60 |
| `pt1792053218` | `$2a$10$` | 60 |
| `pt1792053227` | `$2a$10$` | 60 |

聚合校验：`COUNT = 8`，`MIN(LENGTH(password)) = 60`，前缀一致为 `$2a$10$`。

**结论**：密码全部以 BCrypt（cost factor 10）加盐存储，**无明文、无弱哈希**。

### 3.2 敏感字段不下发

- `/api/doctors` 响应首条的字段集合为
  `avatar, department, experience, id, isActive, name, specialties, title, username`
  —— **不含 password**；
- `/api/patient/register`、`/api/patient/me` 同样不含 password（T14c、T25c 断言结果均为 `null`）。

### 3.3 防用户名枚举

账户不存在（内部 code 1003）与密码错误（内部 code 1004）对外返回**完全一致**的文案
「用户名或密码错误」；内部保留不同 code 以便日志排查，兼顾安全性与可观测性。

---

## 4. 端到端验证（浏览器实测）

除接口测试外，在真实浏览器中完成了全链路确认：

| # | 场景 | 实测结果 |
|---|---|---|
| E1 | 未登录访问 `/consultation` | ✅ 自动跳转 `/patient/login?redirect=/consultation` |
| E2 | UI 注册 → 自动登录 | ✅ 跳回 `/consultation`，登录态写入 localStorage |
| E3 | 刷新浏览器 | ✅ 仍为登录态，头部显示用户名 |
| E4 | 首页切换 English | ✅ hero / 统计 / 开放诊室文案**即时**切换（未刷新），`<html lang>` 同步为 `en-US` |
| E5 | 语言持久化 | ✅ 刷新后仍为 English；切回中文后同样保持 |
| E6 | 翻译范围边界 | ✅ English 下导航「首页/问诊/医生/关于」与页脚**仍为中文**，符合「仅处理首页」要求 |
| E7 | 医生登录无回归 | ✅ `dr-zhang-wei / 123456` 登录成功进入诊室，诊室信息来自后端数据库 |
| E8 | 退出登录 | ✅ 清除 localStorage token，头部恢复「用户登录」 |
| E9 | 退出后再访问 `/consultation` | ✅ 再次被重定向到登录页 |

> 浏览器控制台仅有一条 `ERR_TUNNEL_CONNECTION_FAILED` —— 加载外部 Logo 图片（pexels.com）
> 受网络环境限制失败，**与应用代码无关**。

---

## 5. 构建与静态检查

| 检查项 | 命令 | 结果 |
|---|---|---|
| 前端类型检查 + 构建 | `npm run build`（含 `vue-tsc -b`） | ✅ BUILD SUCCESS，0 error |
| 后端编译 + 单测 | `./mvnw test` | ✅ Tests run: 1, Failures: 0, Errors: 0 |
| Shell 脚本语法 | `bash -n scripts/api-test.sh` | ✅ 无错误 |

---

## 6. 失败记录与分析

本次最终执行**无失败用例**。

实现过程中曾出现以下问题，均已修复并归档，供复现时避坑：

| # | 问题 | 根因 | 修复 |
|---|---|---|---|
| 1 | 医生数据中文显示为乱码 | 容器执行 initdb 脚本时客户端默认 latin1，把 UTF-8 中文按 latin1 解读后转存 utf8mb4，造成**双重编码**；mysql 客户端直查因往返转换反而显示正常 | `db/init/*.sql` 开头加 `SET NAMES utf8mb4;` 并清卷重建。判定用 `SELECT HEX(name)`：正确为 `E5BCA0...`，双重编码为 `C3A5C2BC...` |
| 2 | JDBC 抛 `Unsupported character encoding 'utf8mb4'` | `utf8mb4` 是 MySQL 服务端字符集名，JDBC 参数须写 Java 字符集名 | 改为 `characterEncoding=UTF-8` |
| 3 | 注册返回 HTTP 500（Data truncation: id too long） | 生成的 id 为 `patient` + UUID(32) = 39 字符，超出 `VARCHAR(32)` | 改为 `pt-` + UUID 前 12 位 = 15 字符 |
| 4 | `@NotBlank` 编译报「找不到符号」 | `jakarta.validation` 需要额外 starter | 新增 `spring-boot-starter-validation` |
| 5 | 测试脚本出现"假绿"（FAIL 被记成 PASS、标签错位） | 在 `assert_eq` 参数中嵌套复杂引号的 `$(...)`，macOS bash 下被错误分词导致实参个数错乱 | 改为「先赋变量再断言」；详见 `API_TEST.md` §7 |
| 6 | 4 项契约断言恒返回空 | jq 对数组使用 `.data.length` 返回 null | 改用管道形式 `.data \| length`、`.data[0].specialties \| type` |
| 7 | `mybatis-spring-boot-starter` 版本选型 | 现有 4.x 线依赖 Spring Boot 4，与本项目 3.5.7 不兼容 | 选用 3.0.5 |
| 8 | `vue-i18n@9` 安装时告警已废弃 | v9 / v10 官方不再维护 | 改用 v11 稳定线（`^11.1.10`，实际解析 11.4.14），API 兼容 |

---

## 7. 遗留问题与后续建议

| # | 项 | 说明 |
|---|---|---|
| 1 | 医生登录仍为前端本地凭证比对 | 受范围限制（需求3 仅要求「问诊用户」登录）与安全约束（VO 不下发 password）。后续应新增 `/api/doctor/login` 做服务端鉴权，并移除 `store/index.ts` 中的本地凭证表 |
| 2 | 历史患者数据未迁移 | `patient-user.json` 的 5 位患者无凭证字段；刻意不批量创建弱口令账户 |
| 3 | 问诊/提问数据仍在前端 mock | `question-list.json` 未服务端化，不在本次范围内 |
| 4 | 未引入 CI | 脚本退出码已规范（`0` / `1`），可直接接入 |
| 5 | 演示环境使用 HTTP | 生产须切换 HTTPS 以保护 token 与密码传输 |

---

## 8. 结论

- **接口层**：53 项 curl 用例全部通过，覆盖既有接口回归、需求2 医生接口、需求3 患者注册/登录/鉴权全链路，以及拦截器白名单的反向验证；
- **安全层**：密码 BCrypt 存储、敏感字段不下发、防用户名枚举均已实测确认；
- **交互层**：9 项浏览器端到端场景全部符合预期，含语言切换的即时生效、持久化与范围边界；
- **质量现状**：前后端构建与既有单测均通过，无回归。

**测试结论：通过，可交付。**
