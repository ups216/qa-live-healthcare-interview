# 接口测试报告（API_TEST_REPORT）

- **测试日期**：2026-10-19
- **测试依据**：`docs/API_TEST.md`
- **被测服务**：`qa-service-user`（`http://localhost:8080`）
- **测试范围**：除 `TestController`（`/api/test/cors`）外的本项目所有接口
- **测试方法**：通过 `curl` 直接调用接口（请求体以文件方式 `--data @file` 传入，规避 PowerShell 对含双引号 JSON 的内联传参破坏），核对返回是否符合预期
- **判定标准**：接口按预期返回即记为 **PASS**

> 结论：所有被测接口均按预期返回，全部 **PASS**。

---

## 1. 测试结论汇总

| 序号 | 用例 | 接口 / 方法 | 预期结果 | 实际结果 | 结论 |
| --- | --- | --- | --- | --- | --- |
| 1 | 获取全部医生 | `GET /api/doctors` | 200，返回 5 位医生 | 200，返回 5 位医生（字段齐全） | **PASS** |
| 2 | 获取在线医生 | `GET /api/doctors/active` | 200，返回 `isActive=true` 的医生 | 200，返回 4 位在线医生 | **PASS** |
| 3 | 医生登录（正确凭证） | `POST /api/auth/login` type=doctor | 200，返回 token / role=DOCTOR / user | 200，返回 token、expiresIn、role=DOCTOR、user | **PASS** |
| 4 | 医生登录（错误密码） | `POST /api/auth/login` type=doctor | 401，`message=用户名或密码错误` | 401，`{"message":"用户名或密码错误"}` | **PASS** |
| 5 | 患者登录（首次，自动建档） | `POST /api/auth/login` type=patient | 200，返回 token / role=PATIENT / user；库新增记录 | 200，role=PATIENT，user.id 由库生成 | **PASS** |
| 6 | 患者登录（再次，相同信息） | `POST /api/auth/login` type=patient | 200，返回同一 user（不重复建档） | 200，**token 与 user.id 与首次完全相同** | **PASS** |
| 7 | 白名单免鉴权 | `GET /api/doctors`（无 token） | 200，无需 Authorization | 200，无需 token 直接返回 | **PASS** |
| — | TestController | `GET /api/test/cors` | — | 按需求**跳过** | **SKIP** |

---

## 2. 详细测试结果

### 用例 1：获取全部医生 `GET /api/doctors`
- **请求**：`GET http://localhost:8080/api/doctors`
- **预期**：`200 OK`，返回医生数组（5 条），含 `id / username / name / title / department / avatar / experience / specialties / isActive`。
- **实际**：`HTTP 200`，返回 5 条，示例片段：
  ```json
  [
    {"id":"doc001","username":"dr-zhang-wei","name":"张伟医生","title":"主任医师","department":"心内科","isActive":true},
    {"id":"doc002","username":"dr-li-na","name":"李娜医生","title":"副主任医师","department":"儿科","isActive":true},
    {"id":"doc003","username":"dr-wang-qiang","name":"王强医生","title":"主治医师","department":"骨科","isActive":true},
    {"id":"doc004","username":"dr-liu-min","name":"刘敏医生","title":"主任医师","department":"妇产科","isActive":false},
    {"id":"doc005","username":"dr-chen-jie","name":"陈杰医生","title":"副主任医师","department":"消化内科","isActive":true}
  ]
  ```
- **结论**：**PASS**。

### 用例 2：获取在线医生 `GET /api/doctors/active`
- **请求**：`GET http://localhost:8080/api/doctors/active`
- **预期**：`200 OK`，仅返回 `isActive=true` 的医生（4 条）。
- **实际**：`HTTP 200`，返回 4 条（`doc001 / doc002 / doc003 / doc005`），`doc004`（刘敏医生，`isActive=false`）被正确排除。
- **结论**：**PASS**。

### 用例 3：医生登录（正确凭证） `POST /api/auth/login`
- **请求体**：
  ```json
  {"type":"doctor","username":"dr-zhang-wei","password":"123456"}
  ```
- **预期**：`200 OK`，响应含 `token / expiresIn(7200) / role=DOCTOR / user`。
- **实际**：`HTTP 200`，返回：
  ```json
  {
    "token":"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkci16aGFuZy13ZWkiLCJyb2xlIjoiRE9DVE9SIiwiaWF0IjoxNzkyNDA4MDU1LCJleHAiOjE3OTI0MTUyNTV9...",
    "expiresIn":7200,
    "role":"DOCTOR",
    "user":{"id":"doc001","username":"dr-zhang-wei","name":"张伟医生"}
  }
  ```
  密码在服务端以 MD5 比对通过。
- **结论**：**PASS**。

### 用例 4：医生登录（错误密码） `POST /api/auth/login`
- **请求体**：`{"type":"doctor","username":"dr-zhang-wei","password":"wrong"}`
- **预期**：`401`，`{"message":"用户名或密码错误"}`。
- **实际**：`HTTP 401`，返回 `{"message":"用户名或密码错误"}`。
- **结论**：**PASS**。

### 用例 5：患者登录（首次，自动建档） `POST /api/auth/login`
- **请求体**：`{"type":"patient","name":"APITestUser","birthday":"2000-01-01"}`
- **预期**：`200 OK`，返回 `token / expiresIn / role=PATIENT / user`；数据库 `patients` 表按 `(name, birthday)` 自动新增一条。
- **实际**：`HTTP 200`，返回：
  ```json
  {
    "token":"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBUElUZXN0VXNlciIsInJvbGUiOiJQQVRJRU5UIiwiaWF0IjoxNzkyNDA4MDU2LCJleHAiOjE3OTI0MTUyNTZ9...",
    "expiresIn":7200,
    "role":"PATIENT",
    "user":{"id":"d0d86c67d333458ea795e89a8e095402","username":"APITestUser","name":"APITestUser"}
  }
  ```
  `user.id` 为数据库生成的 UUID，表明已自动建档。
- **结论**：**PASS**。

### 用例 6：患者登录（再次，相同信息） `POST /api/auth/login`
- **请求体**：同用例 5（相同 `name` + `birthday`）。
- **预期**：`200 OK`，返回的 `user.id` 与首次一致（按唯一键 `(name, birthday)` 命中已有记录，不重复建档）。
- **实际**：`HTTP 200`，**返回的 `token` 与 `user.id`（`d0d86c67d333458ea795e89a8e095402`）与用例 5 完全相同**，证明未重复创建账户。
- **结论**：**PASS**。

### 用例 7：白名单免鉴权（JWT 拦截器）
- **请求**：`GET http://localhost:8080/api/doctors`（不携带 `Authorization`）。
- **预期**：`200 OK`，白名单 `/api/doctors`、`/api/auth`、`/api/test` 无需 token。
- **实际**：`HTTP 200`，无需 token 正常返回（见用例 1/2）。
- **结论**：**PASS**。
- 补充：非白名单的 `/api/**` 未来受保护接口需携带 `Authorization: Bearer <token>`，缺失 / 过期 / 非法返回 `401`（拦截器已就绪，待问诊 / 诊室业务接口接入后自动生效）。

---

## 3. 备注

1. **TestController 跳过**：依据需求，未对 `GET/POST /api/test/cors` 执行测试。
2. **关于早期 `400` 的说明（已澄清）**：测试初期以 PowerShell 内联 / 变量方式传 JSON（`curl -d $var` 或 `-d '{"..."}'`）调用登录接口时，因 PowerShell 对含双引号的字符串在命令行参数传递中被破坏，后端收到的请求体无法解析为 `LoginRequest`，从而返回 `400`（空对象 `{}` 因无属性而能成功，带字段则失败，正是此特征）。改用 `--data @file`（从文件读取请求体）后，所有登录接口均正常返回 `200/401`。**后端实现本身无问题**，与需求方端到端验证结果一致。
3. **测试账号**：医生 `dr-zhang-wei / 123456`（密码库中为 MD5）；患者任意「姓名 + 出生日期」，首次自动建档。

---

## 4. 总体结论

除明确跳过的 `TestController` 外，本项目涉及的全部接口均**按预期返回，测试通过（PASS）**。医生信息查询、医生登录（MD5 校验）、患者登录（姓名 + 生日、首次自动建档、再次登录不重复建档）、JWT 白名单免鉴权均工作正常。
