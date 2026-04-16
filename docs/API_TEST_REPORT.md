# API 接口测试报告

## 1. 测试信息

| 字段 | 内容 |
| --- | --- |
| 测试日期 | 2026-04-16 |
| 测试方式 | `curl.exe` 手工接口联调 |
| 测试环境 | 本地 Windows + MySQL + `qa-service-user` + `qa-service-question` |
| 用户服务地址 | `http://localhost:8080` |
| 问题服务地址 | `http://localhost:8081` |
| 依据文档 | `docs/API_TEST.md` |

## 2. 本次测试数据

### 2.1 医生账号

```text
username: dr-zhang-wei
password: 123456
doctorId: doc001
```

### 2.2 本次新增患者账号

```text
username: api_test_20260416133805
password: 123456
patientId: patient1776317886285
displayName: API-Test-20260416133805
```

### 2.3 本次新增问题

```text
questionId1: q1776317886614
questionId2: q1776317886673
```

## 3. 测试结果汇总

| 编号 | 接口 | 场景 | 预期状态码 | 实际状态码 | 结果 |
| --- | --- | --- | --- | --- | --- |
| T01 | `GET /api/doctors` | 查询全部医生 | 200 | 200 | 通过 |
| T02 | `GET /api/doctors?activeOnly=true` | 查询在线医生 | 200 | 200 | 通过 |
| T03 | `POST /api/doctors/login` | 医生登录成功 | 200 | 200 | 通过 |
| T04 | `POST /api/doctors/login` | 医生登录失败 | 401 | 401 | 通过 |
| T05 | `POST /api/patients/register` | 患者注册成功 | 200 | 200 | 通过 |
| T06 | `POST /api/patients/register` | 患者重复注册 | 409 | 409 | 通过 |
| T07 | `POST /api/patients/login` | 患者登录成功 | 200 | 200 | 通过 |
| T08 | `POST /api/patients/login` | 患者登录失败 | 401 | 401 | 通过 |
| T09 | `GET /api/questions?patientId=...` | 新患者查询问题列表 | 200 | 200 | 通过 |
| T10 | `POST /api/questions` | 创建第一条问题 | 200 | 200 | 通过 |
| T11 | `POST /api/questions` | 创建第二条问题 | 200 | 200 | 通过 |
| T12 | `GET /api/questions?patientId=...` | 创建后查询患者问题 | 200 | 200 | 通过 |
| T13 | `GET /api/questions?doctorId=doc001&status=pending` | 查询医生待处理问题 | 200 | 200 | 通过 |
| T14 | `POST /api/questions/{id}/answer` | 回复问题 | 200 | 200 | 通过 |
| T15 | `POST /api/questions/{id}/mark-answered` | 标记已答 | 200 | 200 | 通过 |
| T16 | `GET /api/questions?doctorId=doc001&status=answered` | 查询医生已答问题 | 200 | 200 | 通过 |
| T17 | `GET /api/questions/summary` | 查询汇总数据 | 200 | 200 | 通过 |

## 4. 关键返回结果

### 4.1 医生列表

- 全部医生数量：`5`
- 在线医生数量：`4`

说明：

- `activeOnly=true` 过滤行为符合预期。

### 4.2 医生登录

- 成功登录医生 ID：`doc001`
- 用户名：`dr-zhang-wei`
- 失败场景状态码：`401`

说明：

- 登录成功和失败分支都符合预期。

### 4.3 患者注册与登录

- 注册成功患者 ID：`patient1776317886285`
- 登录成功返回 ID：`patient1776317886285`
- 重复注册状态码：`409`
- 错误密码登录状态码：`401`

说明：

- 注册、登录、重复注册拦截和登录失败分支均符合预期。

### 4.4 问题创建与流转

- 创建前患者问题数量：`0`
- 创建后患者问题数量：`2`
- 医生待处理问题数量：`2`
- 第一条问题回复后状态：`answered`
- 第二条问题标记已答后状态：`answered`

说明：

- 创建、查询、回复、状态流转均符合预期。

### 4.5 汇总接口

- `totalQuestions = 12`
- `pendingQuestions = 2`
- `answeredQuestions = 10`

说明：

- 汇总数据返回正常。
- 由于数据库保留历史测试数据，所以总量会随着重复测试持续增长，这是当前实现的正常现象。

## 5. 测试结论

本次按 `docs/API_TEST.md` 执行的真实 `curl.exe` 联调结果显示：

- 当前项目核心接口均可访问。
- 患者注册、登录、提问主流程可用。
- 医生登录、查看待处理问题、回复问题、标记已答主流程可用。
- 关键失败分支返回码正确：
  - 医生错误密码：`401`
  - 患者错误密码：`401`
  - 患者重复注册：`409`

综合结论：

- 当前项目核心接口测试通过，可以支撑现阶段前端联调和演示。

## 6. 测试中发现的执行注意点

- 在 PowerShell 中必须使用 `curl.exe`，不能直接使用 `curl` 别名。
- `POST` JSON 请求建议通过临时文件配合 `--data-binary @file` 发送，否则请求体可能被 PowerShell 转义破坏，导致接口返回 `400`。
- 当前系统不会自动清理测试数据，重复执行会持续增加 `questions` 表记录数。
