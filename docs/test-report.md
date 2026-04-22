# 测试报告

> QA Healthcare 系统接口测试报告
> 测试日期：2026-04-23
> 测试环境：Windows 11 / JDK 26 / MySQL 8.0 / Spring Boot 3.5.7 / Node.js 20

---

## 测试概述

本次测试覆盖面试需求1（首页国际化）、需求2（医生数据后端化）和需求3（患者用户名密码登录）所涉及的所有后端 API 接口。

### 测试范围

| 模块 | 接口数量 | 说明 |
|------|----------|------|
| 基础测试 | 2 | CORS、健康检查 |
| 患者接口 | 6 | 登录、注册、查询 |
| 医生接口 | 8 | 登录、列表、查询 |
| **合计** | **16** | |

### 测试工具

- PowerShell Invoke-RestMethod
- 自动化测试脚本：`test-api.ps1`

---

## 测试结果总览

| 状态 | 数量 |
|------|------|
| ✅ PASS | 16 |
| ❌ FAIL | 0 |
| **通过率** | **100%** |

---

## 详细测试结果

### 1. 基础测试接口

| # | 测试用例 | 方法 | URL | 预期状态码 | 实际状态码 | 结果 |
|---|---------|------|-----|-----------|-----------|------|
| 1.1 | CORS 测试 GET | GET | /api/test/cors | 200 | 200 | ✅ PASS |
| 1.2 | 健康检查 | GET | /actuator/health | 200 | 200 | ✅ PASS |

**1.1 CORS GET 响应**：
```json
{"service":"qa-service-user","message":"CORS configuration is working!","timestamp":1776876306038}
```

**1.2 健康检查响应**：
```json
{"status":"UP","components":{"db":{"status":"UP","details":{"database":"MySQL","validationQuery":"isValid()"}},"diskSpace":{"status":"UP"},"ping":{"status":"UP"},"ssl":{"status":"UP"}}}
```

### 2. 患者接口

| # | 测试用例 | 方法 | URL | 预期状态码 | 实际状态码 | 结果 |
|---|---------|------|-----|-----------|-----------|------|
| 2.1 | 患者登录-成功 | POST | /api/patient/login | 200 | 200 | ✅ PASS |
| 2.2 | 患者登录-错误密码 | POST | /api/patient/login | 401 | 401 | ✅ PASS |
| 2.3 | 患者登录-不存在用户 | POST | /api/patient/login | 401 | 401 | ✅ PASS |
| 2.4 | 患者注册-成功 | POST | /api/patient/register | 200 | 200 | ✅ PASS |
| 2.5 | 患者注册-用户名重复 | POST | /api/patient/register | 400 | 400 | ✅ PASS |
| 2.6 | 获取患者信息 | GET | /api/patient/patient001 | 200 | 200 | ✅ PASS |
| 2.7 | 获取患者信息-不存在 | GET | /api/patient/nonexistent | 404 | 404 | ✅ PASS |

**2.1 患者登录成功响应**：
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "id": "patient001",
    "username": "patient-zhao-ming",
    "name": "赵明",
    "birthday": "1985-03-15",
    "phone": "138****1234",
    "gender": "男"
  }
}
```

**2.2 患者登录-错误密码响应**：
```json
{"code": 401, "message": "用户名或密码错误", "data": null}
```

**2.3 患者登录-不存在用户响应**：
```json
{"code": 401, "message": "用户名或密码错误", "data": null}
```

**2.4 患者注册成功响应**：
```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "id": "patient1776876307324",
    "username": "test-user-api-001",
    "name": "API Test User",
    "birthday": "1995-01-01",
    "phone": "13800001111",
    "gender": "male"
  }
}
```

**2.5 患者注册-用户名重复响应**：
```json
{"code": 400, "message": "该用户名已被注册", "data": null}
```

**2.7 获取患者信息-不存在响应**：
```json
{"code": 404, "message": "患者不存在", "data": null}
```

### 3. 医生接口

| # | 测试用例 | 方法 | URL | 预期状态码 | 实际状态码 | 结果 |
|---|---------|------|-----|-----------|-----------|------|
| 3.1 | 医生登录-成功 | POST | /api/doctor/login | 200 | 200 | ✅ PASS |
| 3.2 | 医生登录-错误密码 | POST | /api/doctor/login | 401 | 401 | ✅ PASS |
| 3.3 | 医生登录-不存在用户 | POST | /api/doctor/login | 401 | 401 | ✅ PASS |
| 3.4 | 医生列表 | GET | /api/doctor/list | 200 | 200 | ✅ PASS |
| 3.5 | 在线医生列表 | GET | /api/doctor/active | 200 | 200 | ✅ PASS |
| 3.6 | 按ID查医生 | GET | /api/doctor/doc001 | 200 | 200 | ✅ PASS |
| 3.7 | 按用户名查医生 | GET | /api/doctor/username/dr-zhang-wei | 200 | 200 | ✅ PASS |
| 3.8 | 医生不存在 | GET | /api/doctor/nonexistent | 404 | 404 | ✅ PASS |

**3.1 医生登录成功响应**：
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "id": "doc001",
    "username": "dr-zhang-wei",
    "name": "张伟医生",
    "title": "主任医师",
    "department": "心内科",
    "avatar": "https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400",
    "experience": "15年临床经验",
    "isActive": true,
    "specialties": ["高血压", "冠心病", "心律失常"]
  }
}
```

**3.2 医生登录-错误密码响应**：
```json
{"code": 401, "message": "用户名或密码错误", "data": null}
```

**3.3 医生登录-不存在用户响应**：
```json
{"code": 401, "message": "用户名或密码错误", "data": null}
```

**3.4 医生列表响应**（5条记录）：
- doc001 - 张伟医生 - 心内科 - isActive: true
- doc002 - 李娜医生 - 儿科 - isActive: true
- doc003 - 王强医生 - 骨科 - isActive: true
- doc004 - 刘敏医生 - 妇产科 - isActive: false
- doc005 - 陈杰医生 - 消化内科 - isActive: true

**3.5 在线医生列表响应**（4条，排除 isActive=false 的刘敏医生）

**3.8 医生不存在响应**：
```json
{"code": 404, "message": "医生不存在", "data": null}
```

---

## 前端联调验证

### 需求1：首页中英文切换

| # | 验证项 | 状态 | 说明 |
|---|--------|------|------|
| F1.1 | vue-i18n 集成 | ✅ | main.ts 中 app.use(i18n) |
| F1.2 | 语言资源文件 | ✅ | locales/zh.ts + locales/en.ts |
| F1.3 | 语言切换下拉菜单 | ✅ | AppHeader.vue 右上角下拉菜单 |
| F1.4 | localStorage 语言持久化 | ✅ | 切换后保存到 localStorage |
| F1.5 | 首页文本国际化 | ✅ | Home.vue 全部使用 t() 函数 |
| F1.6 | 其他页面不受影响 | ✅ | 仅 Home.vue 使用 i18n |

### 需求2：医生数据后端化

| # | 验证项 | 状态 | 说明 |
|---|--------|------|------|
| F2.1 | docker-compose.yml | ✅ | MySQL 8.0 + phpMyAdmin 配置完整 |
| F2.2 | 数据库初始化 SQL | ✅ | 01-schema.sql + 02-data.sql |
| F2.3 | DoctorController API | ✅ | 5个接口：login/list/active/{id}/username/{username} |
| F2.4 | 前端 store.loadDoctors() | ✅ | 从 /api/doctor/list 加载数据 |
| F2.5 | 移除 JSON 导入 | ✅ | 不再 import doctor-user-list.json |
| F2.6 | App.vue 启动加载 | ✅ | onMounted 调用 loadDoctors() |
| F2.7 | 医生登录后端化 | ✅ | loginDoctor 调用 /api/doctor/login |
| F2.8 | Vite 代理配置 | ✅ | /api → localhost:8080 |

### 需求3：患者用户名密码登录

| # | 验证项 | 状态 | 说明 |
|---|--------|------|------|
| F3.1 | 数据库 patient 表增加 username/password | ✅ | 03-migrate-patient.sql |
| F3.2 | BCrypt 密码加密 | ✅ | spring-security-crypto 依赖 |
| F3.3 | PatientController 登录接口 | ✅ | POST /api/patient/login |
| F3.4 | PatientController 注册接口 | ✅ | POST /api/patient/register |
| F3.5 | 前端登录表单改造 | ✅ | Consultation.vue 用户名+密码 |
| F3.6 | 前端注册表单 | ✅ | Consultation.vue 注册模式切换 |
| F3.7 | localStorage 登录持久化 | ✅ | 患者登录状态保存/恢复 |
| F3.8 | 密码初始化接口 | ✅ | GET /api/patient/init-passwords |

---

## 交付物检查

| # | 交付物 | 路径 | 状态 |
|---|--------|------|------|
| 1 | PRD 模板 | /docs/PRD-template.md | ✅ |
| 2 | PRD 文档 | /docs/PRD-patient-login.md | ✅ |
| 3 | API 测试文档 | /docs/API_TEST.md | ✅ |
| 4 | 测试报告 | /docs/test-report.md | ✅ 本文档 |
| 5 | Prompts 记录 | /docs/prompts.md | ✅ |
| 6 | docker-compose.yml | /docker-compose.yml | ✅ |
| 7 | 数据库初始化脚本 | /server/init-db/*.sql | ✅ |
| 8 | 后端代码 | /server/qa-service-user/src/ | ✅ |
| 9 | 前端代码 | /web/qa-web/src/ | ✅ |

---

## 结论

所有 16 个 API 接口测试均通过，前端三个需求的联调验证项全部通过。系统功能符合面试需求规格。
