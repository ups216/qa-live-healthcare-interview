# Healthcare QA System - 接口测试报告

**项目名称**: Healthcare QA System (qa-service-user)  
**测试版本**: v1.0.0  
**测试日期**: 2026-04-22  
**测试工程师**: AI Assistant  
**测试环境**: Windows 11, JDK 17, MySQL 8.0 (Docker), Spring Boot 3.5.7  

---

## 1. 执行摘要

本次测试针对 `qa-service-user` 微服务的所有核心 RESTful API 进行了全面验证。测试覆盖了系统健康、医生管理、患者认证（双模式登录）以及安全性专项测试。

### 📊 测试结果概览

| 指标 | 数值 |
|------|------|
| **测试用例总数** | 12 |
| **通过 (Pass)** | 12 |
| **失败 (Fail)** | 0 |
| **暂缓 (Deferred)** | 2 (脱敏与锁定机制) |
| **总体通过率** | **100%** |

---

## 2. 详细测试结果

### 2.1 系统健康检查 (Actuator)

| ID | 测试项 | 预期结果 | 实际结果 | 状态 |
|----|--------|----------|----------|------|
| HC-01 | GET /actuator/health | status: UP, db: UP | status: UP, db: UP | ✅ Pass |

**结论**: 后端服务运行稳定，MySQL 数据库连接正常。

### 2.2 医生管理模块 (Doctors)

| ID | 测试项 | 预期结果 | 实际结果 | 状态 |
|----|--------|----------|----------|------|
| DOC-01 | GET /api/doctors/active | 返回活跃医生列表 (JSON Array) | 成功返回 4 位医生信息 | ✅ Pass |
| DOC-02 | POST /api/doctors/login | 返回医生详细信息 | 登录成功，返回完整对象 | ✅ Pass |

**结论**: 医生查询与认证功能符合预期。

### 2.3 患者认证模块 (Patients)

| ID | 测试项 | 预期结果 | 实际结果 | 状态 |
|----|--------|----------|----------|------|
| PAT-01 | POST /api/patients/register | 返回 patientId 和成功消息 | 注册成功，生成 UUID | ✅ Pass |
| PAT-02 | POST /api/patients/login (密码模式) | 返回患者详细信息 | 13800001234/1234 登录成功 | ✅ Pass |
| PAT-03 | POST /api/patients/verify (生日模式) | 返回患者详细信息 | 赵明/1985-03-15 验证成功 | ✅ Pass |

**结论**: 患者双模式登录及注册流程已全部打通，BCrypt 加密逻辑工作正常。

### 2.4 异常场景与安全测试

| ID | 测试项 | 预期结果 | 实际结果 | 状态 |
|----|--------|----------|----------|------|
| SEC-01 | 错误密码登录 | HTTP 401 Unauthorized | 正确返回 401 | ✅ Pass |
| SEC-02 | 重复手机号注册 | HTTP 409 Conflict | 正确返回 409 | ✅ Pass |
| SEC-03 | 不存在的用户验证 | HTTP 401 Unauthorized | 正确返回 401 | ✅ Pass |
| SEC-04 | SQL 注入攻击尝试 | HTTP 401 (拦截非法输入) | 未发生报错，安全拦截 | ✅ Pass |
| SEC-05 | 数据库密码哈希校验 | hash_prefix 为 $2a$10$ | 确认为 BCrypt 格式 | ✅ Pass |

**结论**: 系统具备基础的异常处理能力和安全防护意识。

### 2.5 暂缓处理项 (根据 PRD 要求)

| ID | 测试项 | 说明 | 状态 |
|----|--------|------|------|
| DEF-01 | 敏感信息脱敏 | 当前版本返回完整手机号，暂不脱敏 | ⏸️ Deferred |
| DEF-02 | 登录失败锁定 | 当前版本无 Redis 锁定逻辑 | ⏸️ Deferred |

---

## 3. 缺陷与风险记录

*   **无严重缺陷 (Critical Bugs)**: 本次测试未发现阻断性 Bug。
*   **风险提示**: 
    *   由于暂未实现“登录失败锁定”，生产环境可能存在暴力破解风险。
    *   手机号未脱敏可能在日志记录或前端展示时存在隐私泄露隐患。

---

## 4. 测试环境配置

*   **API Base URL**: `http://localhost:8080`
*   **Database**: `jdbc:mysql://localhost:3316/qa_live_healthcare`
*   **Test Tool**: Curl + Python JSON Formatter
*   **Data Source**: Dockerized MySQL with initial seed data (`02-patient-data.sql`)

---

## 6. 前端回归测试 (Frontend Regression)

**测试背景**: 移除本地 JSON 数据源 (`patient-user.json`, `doctor-user-list.json`)，验证前端 Store 是否能通过 API 正常驱动。

| ID | 测试项 | 预期结果 | 实际结果 | 状态 |
|----|--------|----------|----------|------|
| FE-01 | 医生列表加载 (`refreshActiveDoctors`) | 成功从 `/api/doctors/active` 获取并更新 state | 返回 4 位活跃医生，state 更新正常 | ✅ Pass |
| FE-02 | 患者密码登录 (`loginPatient`) | 调用 `/api/patients/login` 并更新 currentPatient | 13800001234 登录成功，状态同步 | ✅ Pass |
| FE-03 | 患者生日验证 (`verifyPatientByNameAndBirthday`) | 调用 `/api/patients/verify` 并更新 currentPatient | 赵明 验证成功，状态同步 | ✅ Pass |
| FE-04 | 跨域凭证携带 | fetch 请求包含 `credentials: 'include'` | 抓包确认 Cookie/Session 正常传递 | ✅ Pass |

**结论**: 前端代码重构后，已成功脱离本地 JSON 依赖，全面转向后端 API 驱动，交互逻辑符合预期。

---

## 7. 结论与建议

### 7.1 结论
`qa-service-user` 服务的核心功能（患者双模式登录、医生管理）已达到 **MVP (最小可行性产品)** 交付标准。前端已成功完成从本地 JSON 到后端 API 的架构迁移，全链路数据交互通畅。

### 7.2 建议
1.  **后续迭代**: 优先补充 Redis 缓存层以实现登录失败计数与账号锁定功能。
2.  **代码优化**: 建议在 DTO 层增加 `@JsonSerialize` 或自定义注解，以便在未来快速开启手机号脱敏功能。
3.  **自动化集成**: 建议将本次使用的 Curl 脚本转化为 JUnit 集成测试或 Postman Collection，纳入 CI/CD 流水线。

---

**审批签字**:  
技术负责人: \_\_\_\_\_\_\_\_\_\_  
产品经理: \_\_\_\_\_\_\_\_\_\_  
日期: 2026-04-22
