# API 测试报告

> 本报告依据 [API_TEST.md](./API_TEST.md) 第 7 节「AI 测试执行指引」生成。

## 测试概要

| 项目 | 内容 |
|------|------|
| 测试日期 | 2026-07-01 10:47 (UTC+8) |
| 测试环境 | Windows 10 |
| 服务地址 | http://localhost:8080 |
| 被测服务 | qa-service-user |
| 数据库 | MySQL（qa_healthcare） |
| 执行方式 | 自动化脚本 `docs/run-api-tests.ps1` |
| 参考文档 | [API_TEST.md](./API_TEST.md) |

### 环境确认（对应 API_TEST.md 步骤 1）

| 检查项 | 结果 |
|--------|------|
| qa-service-user 端口 8080 可访问 | 通过 |
| `/actuator/health` 返回 UP | 通过 |
| MySQL 数据可查询（医生/患者接口有数据） | 通过 |

---

## 测试结果汇总

| 指标 | 数量 |
|------|------|
| 总用例数 | 14 |
| 通过 | 14 |
| 失败 | 0 |
| 跳过 | 0 |
| **通过率** | **100%** |

---

## 用例明细

| 编号 | 用例名称 | 方法 | URL | 预期状态码 | 实际状态码 | 响应摘要 | 结果 |
|------|---------|------|-----|-----------|-----------|---------|------|
| TC-01 | 健康检查 | GET | `/actuator/health` | 200 | 200 | `status: UP` | PASS |
| TC-02 | CORS GET | GET | `/api/test/cors` | 200 | 200 | `message: CORS configuration is working!` | PASS |
| TC-03 | 获取全部医生 | GET | `/api/doctors` | 200 | 200 | JSON 数组，5 条记录；含 `id/name/isActive`，无 `password` | PASS |
| TC-04 | 获取在线医生 | GET | `/api/doctors?active=true` | 200 | 200 | JSON 数组，4 条记录；`doc004` 离线已过滤 | PASS |
| TC-05 | 按用户名查询医生 | GET | `/api/doctors/username/dr-zhang-wei` | 200 | 200 | `username: dr-zhang-wei`，`name: 张伟医生` | PASS |
| TC-06 | 医生不存在 | GET | `/api/doctors/username/not-exist-user` | 404 | 404 | 错误响应，医生未找到 | PASS |
| TC-07 | 医生登录成功 | POST | `/api/doctors/login` | 200 | 200 | 返回医生信息，不含 `password` | PASS |
| TC-08 | 医生登录失败-密码错误 | POST | `/api/doctors/login` | 401 | 401 | `Invalid credentials` | PASS |
| TC-09 | 医生登录失败-用户不存在 | POST | `/api/doctors/login` | 401 | 401 | `Invalid credentials` | PASS |
| TC-10 | CORS POST | POST | `/api/test/cors` | 200 | 200 | `message: POST request with CORS is working!` | PASS |
| TC-11 | 患者登录成功 | POST | `/api/patients/login` | 200 | 200 | `username: zhaoming`，`name: 赵明` | PASS |
| TC-12 | 患者登录失败 | POST | `/api/patients/login` | 401 | 401 | `Invalid credentials` | PASS |
| TC-13 | 患者注册成功 | POST | `/api/patients/register` | 200 | 200 | 动态用户名注册成功，返回新患者信息 | PASS |
| TC-14 | 患者注册重复 | POST | `/api/patients/register` | 400 | 400 | 用户名 `zhaoming` 已存在 | PASS |

### 需求覆盖映射

| 面试需求 | 对应用例 | 结论 |
|---------|---------|------|
| 医生数据 MySQL + 医生页列表 | TC-03 | 通过 |
| 首页开放诊室（在线医生） | TC-04 | 通过 |
| 问诊页按医生用户名定位 | TC-05、TC-06 | 通过 |
| 医生登录 | TC-07～TC-09 | 通过 |
| 问诊用户用户名密码登录 | TC-11、TC-12 | 通过 |
| 问诊用户注册 | TC-13、TC-14 | 通过 |
| 服务可用性 / 跨域 | TC-01、TC-02、TC-10 | 通过 |

---

## 失败项分析

无。

---

## 结论与建议

### 结论

**全部通过。** 依据 `API_TEST.md` 定义的 14 条 curl 用例均已执行，HTTP 状态码与预期一致，需求相关接口（医生列表、医生登录、患者登录/注册）均可正常工作。

### 复现命令

```powershell
# Windows
powershell -ExecutionPolicy Bypass -File docs/run-api-tests.ps1
```

```bash
# Linux / macOS / Git Bash
bash docs/run-api-tests.sh
```

### 建议

1. 将 `run-api-tests.ps1` / `run-api-tests.sh` 纳入 CI 流水线，在合并前自动执行。
2. 后续新增接口时，同步更新 `API_TEST.md`、`api-tests/curl-commands.sh` 及本报告。
3. Windows 下 POST 请求建议使用脚本中的「临时文件 + `--data-binary`」方式，避免 PowerShell 直接传 JSON 导致 400。

---

## 附录：脚本执行原始输出

```
========================================
QA Live Healthcare API Tests
========================================
BASE_URL: http://localhost:8080
Register test user: test-patient-1782874053
[PASS] TC-01 - Health check (expected HTTP 200, got HTTP 200)
[PASS] TC-02 - CORS GET (expected HTTP 200, got HTTP 200)
[PASS] TC-03 - List all doctors (expected HTTP 200, got HTTP 200)
[PASS] TC-04 - List active doctors (expected HTTP 200, got HTTP 200)
[PASS] TC-05 - Get doctor by username (expected HTTP 200, got HTTP 200)
[PASS] TC-06 - Doctor not found (expected HTTP 404, got HTTP 404)
[PASS] TC-07 - Doctor login success (expected HTTP 200, got HTTP 200)
[PASS] TC-08 - Doctor login wrong password (expected HTTP 401, got HTTP 401)
[PASS] TC-09 - Doctor login unknown user (expected HTTP 401, got HTTP 401)
[PASS] TC-10 - CORS POST (expected HTTP 200, got HTTP 200)
[PASS] TC-11 - Patient login success (expected HTTP 200, got HTTP 200)
[PASS] TC-12 - Patient login failure (expected HTTP 401, got HTTP 401)
[PASS] TC-13 - Patient register success (expected HTTP 200, got HTTP 200)
[PASS] TC-14 - Patient register duplicate (expected HTTP 400, got HTTP 400)

========================================
Summary
========================================
Total:  14
Passed: 14
Failed: 0
Pass rate: 100%

All tests passed.
```
