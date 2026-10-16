# PRD 文档模板

> 本模板由 `.asdm/toolsets/prd-builder/spec/feature-prd-spec.md` 裁剪而来，
> 去掉了原 spec 中面向 AI 的「生成指令」元内容，补上了本仓库的技术栈约束，
> 使其同时适合**人阅读**与**AI 编写/消费**。
> 使用方式：复制本文件 → 替换 `<...>` 占位符 → 保留全部章节标题。

## 语言约定

- 全文使用**同一种语言**书写（推荐中文）。
- 术语首次出现时给出中英对照，后续统一。
- 避免模糊表述（"较快"、"友好"），一律写可判定/可度量的描述。

---

# Feature PRD: <特性名称>

**Feature ID**：<唯一 ID，如 `PATIENT-AUTH-001`；需与已有 PRD 不重复>
**Created Date**：<YYYY-MM-DD>
**Status**：PLANNED | IN PROGRESS | COMPLETED | CANCELLED
**Language**：zh
**Author**：<作者 / AI 生成时填 AI>

---

## 1. Overview

### 1.1 Feature Summary

简要说明本特性：做什么、为什么需要、谁受益。控制在 3~5 句。

- **做什么**：
- **为什么需要**：（必须指向一个具体的现状痛点，并给出代码/文件证据）
- **谁受益**：

### 1.2 Objectives

列出可验证的目标，每条一句话。

- Objective 1：
- Objective 2：

### 1.3 Out of Scope（明确不做）

> 本节为必填。显式声明不在范围内的项，可有效防止 AI 自由发挥扩大改动面。

- 不在范围内：
- 本期不实现但已识别的后续项：

---

## 2. User Stories

每个 Story 必须包含可判定的 Acceptance Criteria（建议 2~5 条）。

### Story 1

**As a** <用户角色>
**I want to** <动作>
**So that** <价值>

**Acceptance Criteria**：

- [ ] Criterion 1
- [ ] Criterion 2

### Story 2

（按需增删，建议 3~6 个）

---

## 3. Functional Requirements

### Requirement 1

- **ID**：REQ-001
- **Description**：
- **Priority**：High | Medium | Low
- **Related Stories**：Story 1
- **Acceptance**：（如何验证该需求已实现）

### Requirement 2

（按需增删）

---

## 4. Non-Functional Requirements

### 4.1 Performance

- （给出可度量的指标，如 P95 < 300ms）

### 4.2 Security

> 涉及账号/密码/token 的特性必须填写本节。

- 密码存储方式：
- 令牌机制与有效期：
- 对外暴露字段的脱敏要求：
- 错误信息是否会泄漏内部信息：

### 4.3 Scalability

- （数据量/并发量的预期与应对）

### 4.4 Reliability

- （失败降级、幂等、可观测性）

---

## 5. Technical Requirements

### 5.1 Architecture Considerations

- 分层职责边界：
- 对既有代码的影响面：

### 5.2 Dependencies

- 内部依赖：（模块/类/数据表）
- 外部依赖：（三方库及版本，必须已在 `pom.xml` / `package.json` 中核对）

### 5.3 Constraints

- （技术栈版本约束、环境约束、兼容性约束）

---

## 6. Success Criteria

必须是可度量/可判定的。

- Success criterion 1：
- Success criterion 2：

---

## 7. Task Breakdown

> **硬性约束：任务数 ≤ 10。** 若超过 10，必须拆分为多个子特性并各自编写 PRD。

每个任务的粒度：人类开发者 1~2 小时 / AI 5~10 分钟；尽量可并行；每个任务必须有独立的验收方式。

| # | 任务 | 产出文件 | 依赖 | 验收方式 |
|---|---|---|---|---|
| 1 | | | — | |
| 2 | | | 1 | |

---

## 8. Implementation Notes

实现时需要特别注意的细节（易错点、副作用、迁移顺序等）。

- Note 1：
- Note 2：

---

## 9. Risks and Mitigations

### Risk 1

- **Description**：
- **Impact**：High | Medium | Low
- **Mitigation**：

### Risk 2

（按需增删，建议 ≥3 条）

---

## 10. Appendix

### 10.1 References

- （源码相对路径 + 行号，便于 AI 精确定位）

### 10.2 Glossary

- Term：Definition

### 10.3 API 契约清单

> 本节为模板面向本仓库新增。凡涉及接口的特性必须填写。

| 方法 | 路径 | 鉴权 | 请求体 | 响应 `data` | 错误码 |
|---|---|---|---|---|---|
| | | | | | |

### 10.4 受影响文件清单

> 本节为模板面向本仓库新增。

| 文件 | 动作（NEW/MODIFY） | 说明 |
|---|---|---|
| | | |

---

## 附录 A：本仓库技术栈约束速查

> AI 编写 PRD 时必须遵守以下约束，不得引入约束外的框架。

**后端**

| 项 | 约定 |
|---|---|
| 框架 | Spring Boot 3.5.7 / Java 17 / Maven（模块内 `mvnw`） |
| 服务 | `qa-service-user`（`com.leansofx.qaserviceuser`，端口 8080，上下文路径 `/`） |
| 持久层 | MyBatis XML：`Mapper.java` + `src/main/resources/mapper/*.xml` |
| 数据库 | MySQL 8（`docker-compose.yml`），`qa_live` 库，utf8mb4 |
| 响应封装 | 统一 `ApiResponse<T>{code,message,data,timestamp}`，**禁止 Controller 裸返 Map** |
| 异常处理 | `@RestControllerAdvice` 统一兜底 |
| 鉴权 | JJWT 0.12.x + `HandlerInterceptor`，白名单机制 |
| 密码 | `BCryptPasswordEncoder`（仅 `spring-security-crypto`，**禁止引入完整 Spring Security**） |

**前端**

| 项 | 约定 |
|---|---|
| 框架 | Vue 3.5 + TypeScript 5.5 + Vite 5.4 + vue-router 4.6 |
| UI | Ant Design Vue 4.2.6（含 `@ant-design/icons-vue` 7.x） |
| 状态 | 手写 `reactive` 单例 `src/store/index.ts`（**无 Pinia**） |
| HTTP | `src/api/request.ts`（axios，`baseURL='/api'`，拦截器注入 Bearer） |
| i18n | `vue-i18n` 9.x，语言包在 `src/locales/` |
| 类型检查 | `strict` + `noUnusedLocals` + `noUnusedParameters`；`npm run build` 含 `vue-tsc -b` |

---

## 附录 B：AI 使用本模板的规范

1. **不得增删一级章节**（`## 1.` ~ `## 10.`），但可在章节内增删条目。
2. **Task Breakdown 必须 ≤ 10 条**；超出则先拆分特性。
3. References 必须给出**可打开的仓库相对路径**，不使用泛指描述。
4. Out of Scope 必须填写，用于约束 AI 的改动范围。
5. 完成后用勾选清单自评：全文语言一致 / 章节齐全 / 任务数合规 / 验收项可判定。
