# API 接口测试说明

## 1. 文档目的

本文档用于说明当前项目所有核心接口的 `curl` 测试方法，覆盖：

- `qa-service-user` 用户服务，端口 `8080`
- `qa-service-question` 问题服务，端口 `8081`

文档同时约束测试执行顺序，便于按同一套流程完成联调和回归。

## 2. 测试前提

### 2.1 服务准备

需要先启动以下服务：

1. `server/qa-service-user`
2. `server/qa-service-question`
3. MySQL

### 2.2 环境检查

执行以下命令确认端口可访问：

```powershell
Test-NetConnection -ComputerName localhost -Port 8080
Test-NetConnection -ComputerName localhost -Port 8081
```

### 2.3 Windows 使用说明

在 PowerShell 中，`curl` 默认是 `Invoke-WebRequest` 的别名，不是真正的 curl。

测试时必须使用：

```powershell
curl.exe
```

另外，`POST` JSON 请求体在 PowerShell 下直接内联容易被转义破坏。当前项目建议统一采用“先写临时 JSON 文件，再通过 `--data-binary @file` 发送”的方式。

示例：

```powershell
$bodyPath = Join-Path $env:TEMP 'qa_api_body.json'
Set-Content -Path $bodyPath -Value '{"username":"demo","password":"123456"}' -NoNewline -Encoding utf8
curl.exe -X POST "http://localhost:8080/api/patients/login" `
  -H "Content-Type: application/json" `
  --data-binary "@$bodyPath"
```

## 3. 测试范围

### 3.1 用户服务接口

- `GET /api/doctors`
- `POST /api/doctors/login`
- `POST /api/patients/register`
- `POST /api/patients/login`

### 3.2 问题服务接口

- `GET /api/questions`
- `POST /api/questions`
- `POST /api/questions/{id}/answer`
- `POST /api/questions/{id}/mark-answered`
- `GET /api/questions/summary`

## 4. 测试数据

### 4.1 固定医生账号

```text
username: dr-zhang-wei
password: 123456
doctorId: doc001
```

### 4.2 动态患者账号

患者注册建议使用时间戳拼接用户名，避免重复：

```text
username: api_test_<timestamp>
password: 123456
displayName: API-Test-<timestamp>
```

## 5. 测试流程

### 5.1 查询全部医生

```powershell
curl.exe "http://localhost:8080/api/doctors"
```

预期：

- 返回 `200`
- 返回医生列表
- 列表长度大于 0

### 5.2 查询在线医生

```powershell
curl.exe "http://localhost:8080/api/doctors?activeOnly=true"
```

预期：

- 返回 `200`
- 返回仅在线医生
- 数量小于等于全部医生数量

### 5.3 医生登录成功

```powershell
$bodyPath = Join-Path $env:TEMP 'doctor_login_success.json'
Set-Content -Path $bodyPath -Value '{"username":"dr-zhang-wei","password":"123456"}' -NoNewline -Encoding utf8
curl.exe -X POST "http://localhost:8080/api/doctors/login" `
  -H "Content-Type: application/json" `
  --data-binary "@$bodyPath"
```

预期：

- 返回 `200`
- 返回医生信息
- 返回 `id=doc001`

### 5.4 医生登录失败

```powershell
$bodyPath = Join-Path $env:TEMP 'doctor_login_fail.json'
Set-Content -Path $bodyPath -Value '{"username":"dr-zhang-wei","password":"wrong-password"}' -NoNewline -Encoding utf8
curl.exe -i -X POST "http://localhost:8080/api/doctors/login" `
  -H "Content-Type: application/json" `
  --data-binary "@$bodyPath"
```

预期：

- 返回 `401`

### 5.5 患者注册成功

将 `<timestamp>` 替换成实际值：

```powershell
$bodyPath = Join-Path $env:TEMP 'patient_register.json'
Set-Content -Path $bodyPath -Value '{"username":"api_test_<timestamp>","password":"123456","displayName":"API-Test-<timestamp>"}' -NoNewline -Encoding utf8
curl.exe -X POST "http://localhost:8080/api/patients/register" `
  -H "Content-Type: application/json" `
  --data-binary "@$bodyPath"
```

预期：

- 返回 `200`
- 返回患者信息
- 返回患者 `id`

### 5.6 患者重复注册

重复执行上一步相同请求。

预期：

- 返回 `409`

### 5.7 患者登录成功

```powershell
$bodyPath = Join-Path $env:TEMP 'patient_login_success.json'
Set-Content -Path $bodyPath -Value '{"username":"api_test_<timestamp>","password":"123456"}' -NoNewline -Encoding utf8
curl.exe -X POST "http://localhost:8080/api/patients/login" `
  -H "Content-Type: application/json" `
  --data-binary "@$bodyPath"
```

预期：

- 返回 `200`
- 返回与注册一致的患者 `id`

### 5.8 患者登录失败

```powershell
$bodyPath = Join-Path $env:TEMP 'patient_login_fail.json'
Set-Content -Path $bodyPath -Value '{"username":"api_test_<timestamp>","password":"wrong-password"}' -NoNewline -Encoding utf8
curl.exe -i -X POST "http://localhost:8080/api/patients/login" `
  -H "Content-Type: application/json" `
  --data-binary "@$bodyPath"
```

预期：

- 返回 `401`

### 5.9 查询患者问题列表（创建前）

将 `<patientId>` 替换为患者注册返回的 ID：

```powershell
curl.exe "http://localhost:8081/api/questions?patientId=<patientId>"
```

预期：

- 返回 `200`
- 若是新注册患者，列表为空

### 5.10 患者创建问题

将 `<patientId>`、`<displayName>` 替换为实际值：

```powershell
$bodyPath = Join-Path $env:TEMP 'question_create_1.json'
Set-Content -Path $bodyPath -Value '{"patientId":"<patientId>","patientName":"<displayName>","doctorId":"doc001","doctorName":"张伟医生","question":"API测试问题1"}' -NoNewline -Encoding utf8
curl.exe -X POST "http://localhost:8081/api/questions" `
  -H "Content-Type: application/json" `
  --data-binary "@$bodyPath"
```

预期：

- 返回 `200`
- 返回新问题 `id`
- 返回 `status=pending`

### 5.11 患者再次创建问题

```powershell
$bodyPath = Join-Path $env:TEMP 'question_create_2.json'
Set-Content -Path $bodyPath -Value '{"patientId":"<patientId>","patientName":"<displayName>","doctorId":"doc001","doctorName":"张伟医生","question":"API测试问题2"}' -NoNewline -Encoding utf8
curl.exe -X POST "http://localhost:8081/api/questions" `
  -H "Content-Type: application/json" `
  --data-binary "@$bodyPath"
```

预期：

- 返回 `200`
- 返回第二条问题 `id`

### 5.12 查询患者问题列表（创建后）

```powershell
curl.exe "http://localhost:8081/api/questions?patientId=<patientId>"
```

预期：

- 返回 `200`
- 列表数量增加

### 5.13 查询医生待处理问题

```powershell
curl.exe "http://localhost:8081/api/questions?doctorId=doc001&status=pending"
```

预期：

- 返回 `200`
- 能查到刚创建的待处理问题

### 5.14 医生回复问题

将 `<questionId>` 替换为第一条问题 ID：

```powershell
$bodyPath = Join-Path $env:TEMP 'question_answer.json'
Set-Content -Path $bodyPath -Value '{"answer":"API测试回复1"}' -NoNewline -Encoding utf8
curl.exe -X POST "http://localhost:8081/api/questions/<questionId>/answer" `
  -H "Content-Type: application/json" `
  --data-binary "@$bodyPath"
```

预期：

- 返回 `200`
- 返回 `status=answered`

### 5.15 标记问题已答

将 `<questionId>` 替换为第二条问题 ID：

```powershell
curl.exe -X POST "http://localhost:8081/api/questions/<questionId>/mark-answered"
```

预期：

- 返回 `200`
- 返回 `status=answered`

### 5.16 查询医生已答问题

```powershell
curl.exe "http://localhost:8081/api/questions?doctorId=doc001&status=answered"
```

预期：

- 返回 `200`
- 可看到刚处理完的问题

### 5.17 查询问题汇总

```powershell
curl.exe "http://localhost:8081/api/questions/summary"
```

预期：

- 返回 `200`
- 返回 `totalQuestions`、`pendingQuestions`、`answeredQuestions`

## 6. 执行顺序建议

推荐按以下顺序执行，避免前置数据缺失：

1. 查询医生列表
2. 医生登录
3. 患者注册
4. 患者登录
5. 患者查询问题列表
6. 创建两条问题
7. 医生查询待处理问题
8. 回复第一条问题
9. 标记第二条问题已答
10. 查询患者问题列表、医生已答列表、问题汇总

## 7. 注意事项

- 由于测试会写入数据库，重复执行会持续累积问题数据，这是当前项目的预期行为。
- 患者注册接口不能使用同一个用户名重复调用，否则会返回 `409`。
- 如需重复执行全流程，必须更换新的测试用户名。
- PowerShell 中请始终使用 `curl.exe`，不要使用别名 `curl`。
