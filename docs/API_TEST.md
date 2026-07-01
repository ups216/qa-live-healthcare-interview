# API 接口测试文档

本文档描述 QA Live Healthcare 项目相关后端接口的 **curl 测试流程**，并指导 AI 按步骤执行测试、记录结果并生成测试报告。

**相关文件：**

| 文件 | 说明 |
|------|------|
| [run-api-tests.ps1](./run-api-tests.ps1) | Windows 一键测试脚本（14 用例） |
| [run-api-tests.sh](./run-api-tests.sh) | Linux/macOS/Git Bash 一键测试脚本 |
| [api-tests/curl-commands.sh](./api-tests/curl-commands.sh) | 单条 curl 命令参考 |
| [API_TEST_REPORT.md](./API_TEST_REPORT.md) | 测试报告（执行测试后生成/更新） |

---

## 1. 测试范围

当前已实现的接口均位于 **qa-service-user** 服务（默认端口 `8080`），主要覆盖以下需求：

| 需求 | 涉及接口 |
|------|---------|
| 医生数据 MySQL 存储 + 医生页面列表 | `GET /api/doctors` |
| 首页开放诊室（在线医生） | `GET /api/doctors?active=true` |
| 问诊页按用户名选择医生 | `GET /api/doctors/username/{username}` |
| 医生登录 | `POST /api/doctors/login` |
| 问诊用户登录 | `POST /api/patients/login` |
| 问诊用户注册 | `POST /api/patients/register` |
| 跨域与服务可用性（基础） | `GET /api/test/cors`、`POST /api/test/cors` |
| 服务健康检查 | `GET /actuator/health` |

---

## 2. 测试环境准备

### 2.1 前置条件

- 已安装 **curl**
- 已安装 **Docker**（推荐）或本机 **MySQL 8.0**
- 已安装 **JDK 17**、**Maven**

### 2.2 启动数据库

```bash
cd server
docker compose up -d
```

验证 MySQL 容器健康：

```bash
docker compose ps
```

- MySQL 端口：`3306`
- phpMyAdmin：http://localhost:8082（用户 `root`，密码 `root`）

### 2.3 启动后端服务

```bash
cd server/qa-service-user
./mvnw spring-boot:run
```

Windows：

```powershell
cd server\qa-service-user
.\mvnw.cmd spring-boot:run
```

### 2.4 环境变量（可选）

测试脚本中可统一设置基础地址：

```bash
# Linux / macOS
export BASE_URL=http://localhost:8080

# Windows PowerShell
$env:BASE_URL = "http://localhost:8080"
```

下文 curl 示例默认使用 `http://localhost:8080`。

---

## 3. 测试前检查

### 3.1 健康检查

```bash
curl -s -w "\nHTTP_STATUS:%{http_code}\n" http://localhost:8080/actuator/health
```

**预期结果：**

- HTTP 状态码：`200`
- 响应体包含 `"status":"UP"`

### 3.2 CORS 连通性

```bash
curl -s -w "\nHTTP_STATUS:%{http_code}\n" http://localhost:8080/api/test/cors
```

**预期结果：**

- HTTP 状态码：`200`
- 响应体包含 `"message":"CORS configuration is working!"`
- 响应体包含 `"service":"qa-service-user"`

---

## 4. 医生接口测试用例

### 4.1 获取全部医生列表

**用途：** 支撑前端 [医生页面](../web/qa-web/src/views/Doctors.vue) 展示医生卡片列表。

```bash
curl -s -w "\nHTTP_STATUS:%{http_code}\n" http://localhost:8080/api/doctors
```

**预期结果：**

| 检查项 | 预期 |
|--------|------|
| HTTP 状态码 | `200` |
| 响应格式 | JSON 数组 |
| 数组长度 | `5`（初始种子数据） |
| 单条记录字段 | 包含 `id`、`username`、`name`、`title`、`department`、`avatar`、`experience`、`specialties`、`isActive` |
| 安全性 | **不包含** `password` 字段 |

**字段示例：**

```json
{
  "id": "doc001",
  "username": "dr-zhang-wei",
  "name": "张伟医生",
  "title": "主任医师",
  "department": "心内科",
  "avatar": "https://...",
  "experience": "15年临床经验",
  "specialties": ["高血压", "冠心病", "心律失常"],
  "isActive": true
}
```

**Windows PowerShell 格式化输出（可选）：**

```powershell
curl.exe -s http://localhost:8080/api/doctors | ConvertFrom-Json | ConvertTo-Json -Depth 5
```

---

### 4.2 获取在线医生列表

**用途：** 支撑首页「开放诊室」仅展示 `isActive=true` 的医生。

```bash
curl -s -w "\nHTTP_STATUS:%{http_code}\n" "http://localhost:8080/api/doctors?active=true"
```

**预期结果：**

| 检查项 | 预期 |
|--------|------|
| HTTP 状态码 | `200` |
| 数组长度 | `4`（初始数据中 `doc004` 为离线） |
| 每条记录 | `isActive` 均为 `true` |

**验证无离线医生：**

```bash
# Linux / macOS（需安装 jq）
curl -s "http://localhost:8080/api/doctors?active=true" | jq '[.[] | select(.isActive == false)] | length'
# 预期输出：0
```

---

### 4.3 按用户名查询医生

**用途：** 支撑问诊页通过 URL 参数 `/consultation/{username}` 定位医生。

```bash
curl -s -w "\nHTTP_STATUS:%{http_code}\n" http://localhost:8080/api/doctors/username/dr-zhang-wei
```

**预期结果：**

| 检查项 | 预期 |
|--------|------|
| HTTP 状态码 | `200` |
| `username` | `dr-zhang-wei` |
| `name` | `张伟医生` |
| `isActive` | `true` |

**负面用例 — 医生不存在：**

```bash
curl -s -w "\nHTTP_STATUS:%{http_code}\n" http://localhost:8080/api/doctors/username/not-exist-user
```

**预期结果：**

- HTTP 状态码：`404`

---

### 4.4 医生登录（成功）

**用途：** 支撑前端 [医生登录页](../web/qa-web/src/views/DoctorLogin.vue)。

```bash
curl -s -w "\nHTTP_STATUS:%{http_code}\n" \
  -X POST http://localhost:8080/api/doctors/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"dr-zhang-wei\",\"password\":\"123456\"}"
```

**预期结果：**

| 检查项 | 预期 |
|--------|------|
| HTTP 状态码 | `200` |
| `username` | `dr-zhang-wei` |
| `name` | `张伟医生` |
| 安全性 | **不包含** `password` 字段 |

**Windows PowerShell 写法：**

```powershell
curl.exe -s -w "`nHTTP_STATUS:%{http_code}`n" `
  -X POST http://localhost:8080/api/doctors/login `
  -H "Content-Type: application/json" `
  -d '{\"username\":\"dr-zhang-wei\",\"password\":\"123456\"}'
```

---

### 4.5 医生登录（失败）

**负面用例 — 密码错误：**

```bash
curl -s -w "\nHTTP_STATUS:%{http_code}\n" \
  -X POST http://localhost:8080/api/doctors/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"dr-zhang-wei\",\"password\":\"wrong-password\"}"
```

**预期结果：**

- HTTP 状态码：`401`

**负面用例 — 用户不存在：**

```bash
curl -s -w "\nHTTP_STATUS:%{http_code}\n" \
  -X POST http://localhost:8080/api/doctors/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"unknown-user\",\"password\":\"123456\"}"
```

**预期结果：**

- HTTP 状态码：`401`

---

## 5. 问诊用户接口测试

### 5.1 患者登录（成功）

**用途：** 支撑前端 [问诊页](../web/qa-web/src/views/Consultation.vue) 用户名密码登录。

```bash
curl -s -w "\nHTTP_STATUS:%{http_code}\n" \
  -X POST http://localhost:8080/api/patients/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"zhaoming\",\"password\":\"123456\"}"
```

**预期结果：**

| 检查项 | 预期 |
|--------|------|
| HTTP 状态码 | `200` |
| `username` | `zhaoming` |
| `name` | `赵明` |
| 安全性 | **不包含** `password` 字段 |

---

### 5.2 患者登录（失败）

```bash
curl -s -w "\nHTTP_STATUS:%{http_code}\n" \
  -X POST http://localhost:8080/api/patients/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"zhaoming\",\"password\":\"wrong-password\"}"
```

**预期结果：** HTTP 状态码 `401`

---

### 5.3 患者注册（成功）

```bash
curl -s -w "\nHTTP_STATUS:%{http_code}\n" \
  -X POST http://localhost:8080/api/patients/register \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"test-patient-001\",\"password\":\"123456\",\"name\":\"测试用户\",\"birthday\":\"1992-01-01\",\"gender\":\"男\"}"
```

**预期结果：**

| 检查项 | 预期 |
|--------|------|
| HTTP 状态码 | `200` |
| `username` | `test-patient-001` |
| `name` | `测试用户` |

---

### 5.4 患者注册（失败 — 用户名重复）

```bash
curl -s -w "\nHTTP_STATUS:%{http_code}\n" \
  -X POST http://localhost:8080/api/patients/register \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"zhaoming\",\"password\":\"123456\",\"name\":\"重复用户\"}"
```

**预期结果：** HTTP 状态码 `400`

---

### 4.6 CORS POST 测试

```bash
curl -s -w "\nHTTP_STATUS:%{http_code}\n" \
  -X POST http://localhost:8080/api/test/cors \
  -H "Content-Type: application/json" \
  -d "{\"test\":\"hello\"}"
```

**预期结果：**

| 检查项 | 预期 |
|--------|------|
| HTTP 状态码 | `200` |
| `message` | `POST request with CORS is working!` |
| `receivedData.test` | `hello` |

---

## 6. 一键测试脚本

项目已提供可执行测试脚本，覆盖全部 **14** 条 curl 用例：

| 脚本 | 适用环境 | 用法 |
|------|---------|------|
| [run-api-tests.sh](./run-api-tests.sh) | Linux / macOS / Git Bash | `bash docs/run-api-tests.sh` |
| [run-api-tests.ps1](./run-api-tests.ps1) | Windows PowerShell | `powershell -ExecutionPolicy Bypass -File docs/run-api-tests.ps1` |
| [api-tests/curl-commands.sh](./api-tests/curl-commands.sh) | 手动逐条执行 | 复制单条 curl 命令运行 |

**Windows 推荐：**

```powershell
cd d:\workSpace\github\qa-live-healthcare-interview
powershell -ExecutionPolicy Bypass -File docs/run-api-tests.ps1
```

**Linux / macOS / Git Bash：**

```bash
cd /path/to/qa-live-healthcare-interview
chmod +x docs/run-api-tests.sh
./docs/run-api-tests.sh
```

**预期结果：** 14 条用例全部 `PASS`，脚本退出码为 `0`。

**用例与预期 HTTP 状态码：**

| 编号 | 用例 | 预期 |
|------|------|------|
| TC-01 | 健康检查 | 200 |
| TC-02 | CORS GET | 200 |
| TC-03 | 获取全部医生 | 200 |
| TC-04 | 获取在线医生 | 200 |
| TC-05 | 按用户名查询医生 | 200 |
| TC-06 | 医生不存在 | 404 |
| TC-07 | 医生登录成功 | 200 |
| TC-08 | 医生登录失败（密码错误） | 401 |
| TC-09 | 医生登录失败（用户不存在） | 401 |
| TC-10 | CORS POST | 200 |
| TC-11 | 患者登录成功 | 200 |
| TC-12 | 患者登录失败 | 401 |
| TC-13 | 患者注册成功 | 200 |
| TC-14 | 患者注册重复 | 400 |

脚本源码（bash 版）摘要：

```bash
#!/usr/bin/env bash
BASE_URL="${BASE_URL:-http://localhost:8080}"
# ... 依次执行 TC-01 ~ TC-14，比对 HTTP 状态码并输出 PASS/FAIL
```

完整实现见 [run-api-tests.sh](./run-api-tests.sh)。

---

## 7. AI 测试执行指引

请 AI 按以下步骤完成自动化测试并生成报告：

### 步骤 1：确认环境

1. 检查 `docker compose ps` 或本机 MySQL 是否可用
2. 确认 `qa-service-user` 已在 `8080` 端口运行
3. 若服务未启动，按第 2 节启动后再继续

### 步骤 2：逐条执行 curl 用例

按 **第 3、4 节** 顺序执行所有 curl 命令，记录：

- 用例编号
- 请求方法与 URL
- 实际 HTTP 状态码
- 实际响应摘要（关键字段）
- 是否符合预期（PASS / FAIL）

### 步骤 3：生成测试报告

在 `/docs` 目录创建测试报告，建议文件名：

```
docs/API_TEST_REPORT.md
```

报告须包含：

1. **测试概要**：测试时间、测试人/执行者、环境信息
2. **测试结果汇总表**（用例总数、通过数、失败数、通过率）
3. **用例明细**（每条用例的请求、预期、实际、结论）
4. **失败项分析**（如有）
5. **结论与建议**

### 步骤 4：测试报告模板

```markdown
# API 测试报告

## 测试概要

| 项目 | 内容 |
|------|------|
| 测试日期 | YYYY-MM-DD HH:mm |
| 测试环境 | Windows / macOS / Linux |
| 服务地址 | http://localhost:8080 |
| 数据库 | Docker MySQL / 本机 MySQL |
| 执行方式 | curl 手动 / 脚本自动 |

## 测试结果汇总

| 指标 | 数量 |
|------|------|
| 总用例数 | 14 |
| 通过 | |
| 失败 | |
| 跳过 | |
| 通过率 | % |

## 用例明细

| 编号 | 用例名称 | 方法 | URL | 预期状态码 | 实际状态码 | 结果 |
|------|---------|------|-----|-----------|-----------|------|
| TC-01 | 健康检查 | GET | /actuator/health | 200 | | |
| TC-02 | CORS GET | GET | /api/test/cors | 200 | | |
| TC-03 | 获取全部医生 | GET | /api/doctors | 200 | | |
| TC-04 | 获取在线医生 | GET | /api/doctors?active=true | 200 | | |
| TC-05 | 按用户名查询 | GET | /api/doctors/username/dr-zhang-wei | 200 | | |
| TC-06 | 医生不存在 | GET | /api/doctors/username/not-exist-user | 404 | | |
| TC-07 | 医生登录成功 | POST | /api/doctors/login | 200 | | |
| TC-08 | 医生登录失败-密码错误 | POST | /api/doctors/login | 401 | | |
| TC-09 | 医生登录失败-用户不存在 | POST | /api/doctors/login | 401 | | |
| TC-10 | CORS POST | POST | /api/test/cors | 200 | | |
| TC-11 | 患者登录成功 | POST | /api/patients/login | 200 | | |
| TC-12 | 患者登录失败 | POST | /api/patients/login | 401 | | |
| TC-13 | 患者注册成功 | POST | /api/patients/register | 200 | | |
| TC-14 | 患者注册重复 | POST | /api/patients/register | 400 | | |

## 失败项分析

（无则写「无」）

## 结论

（通过 / 部分通过 / 不通过，并说明原因）
```

---

## 8. 测试账号参考

### 医生账号

| 用户名 | 密码 | 姓名 | 在线状态 |
|--------|------|------|---------|
| dr-zhang-wei | 123456 | 张伟医生 | 在线 |
| dr-li-na | 123456 | 李娜医生 | 在线 |
| dr-wang-qiang | 123456 | 王强医生 | 在线 |
| dr-liu-min | 123456 | 刘敏医生 | **离线** |
| dr-chen-jie | 123456 | 陈杰医生 | 在线 |

### 问诊用户账号

| 用户名 | 密码 | 姓名 |
|--------|------|------|
| zhaoming | 123456 | 赵明 |
| sunli | 123456 | 孙丽 |
| zhoujie | 123456 | 周杰 |
| wufang | 123456 | 吴芳 |
| zhenghao | 123456 | 郑浩 |

---

## 9. 常见问题

| 现象 | 可能原因 | 处理方式 |
|------|---------|---------|
| `Unknown database 'qa_healthcare'` | 数据库未创建 | 启动 Docker 或确认 `application.properties` 中 `createDatabaseIfNotExist=true` 已配置 |
| 连接被拒绝 | 后端未启动 | 执行 `./mvnw spring-boot:run` |
| 返回空数组 `[]` | 种子数据未导入 | 检查 `data.sql` 是否执行，或通过 phpMyAdmin 查看 `doctors` 表 |
| curl 在 PowerShell 中 JSON 转义失败 | 引号转义问题 | 使用上文 PowerShell 专用示例，或改用 Git Bash |

---

## 10. 相关文档

- 项目说明：[README.md](../README.md)
- 数据库编排：[server/docker-compose.yml](../server/docker-compose.yml)
- 后端配置：[server/qa-service-user/src/main/resources/application.properties](../server/qa-service-user/src/main/resources/application.properties)
- 前端医生页：[web/qa-web/src/views/Doctors.vue](../web/qa-web/src/views/Doctors.vue)
