# API 测试文档（qa-service-user）

> 本文档描述本项目（医疗在线问诊系统）后端 `qa-service-user` 当前涉及的**所有接口**的测试流程，包含医生信息查询、医生登录、患者登录（首次自动建档）、JWT 鉴权与 CORS 测试。
> 关联的 PRD：`docs/PRD_用户名密码登录.md`。

---

## 1. 测试环境

| 项 | 说明 |
| --- | --- |
| 后端服务 | `qa-service-user`，默认地址 `http://localhost:8080` |
| 前端 Dev | `web/qa-web`，`npm run dev` 后访问 `http://localhost:5173`；Vite 已配置 `/api` 代理到 `:8080`，因此前端可用相对路径 `/api/...` |
| 数据库 | 通过 `docker-compose` 启动 MySQL（`qa_healthcare`）；需先执行 `sql/schema.sql` 与 `sql/data.sql` |
| 启动后端 | `cd server/qa-service-user && mvnw.cmd spring-boot:run` |

**测试方式**：以下示例使用绝对地址 `http://localhost:8080`，也可将 `/api/...` 直接用于前端已代理的环境。

---

## 2. 通用约定

- 请求头：`Content-Type: application/json`
- 登录成功后响应结构：
  ```json
  {
    "token": "<JWT>",
    "expiresIn": 7200,
    "role": "DOCTOR | PATIENT",
    "user": { "id": "...", "username": "...", "name": "..." }
  }
  ```
- **鉴权白名单（免 token）**：`/api/doctors/**`、`/api/auth/**`、`/api/test/**`
- **受保护接口**：非白名单的 `/api/**` 路径需携带 `Authorization: Bearer <token>`，缺失 / 过期 / 非法一律返回 `401`。当前业务受保护接口（问诊 / 诊室）尚未实现，拦截器已就绪，待接入后自动生效。

---

## 3. 接口清单

| 方法 | 路径 | 描述 | 是否需要登录 |
| --- | --- | --- | --- |
| GET | `/api/doctors` | 获取全部医生列表 | 否（白名单） |
| GET | `/api/doctors/active` | 获取在线（is_active=1）医生列表 | 否（白名单） |
| POST | `/api/auth/login` | 登录（医生：`type=doctor`；患者：`type=patient`） | 否（白名单，登录本身） |
| GET/POST | `/api/test/cors` | CORS 连通性测试 | 否（白名单） |
| 任意 | 非白名单 `/api/**` | 未来受保护业务接口（问诊 / 诊室） | 是（JWT，过期校验） |

---

## 4. 接口详细测试

### 4.1 获取全部医生 `GET /api/doctors`

```bash
curl http://localhost:8080/api/doctors
```

**预期**：`200 OK`，返回医生数组（5 条），每条含 `id / username / name / title / department / avatar / experience / specialties / isActive`。

**后端日志**（已加）：
```
[GET /api/doctors] 收到获取全部医生列表请求
[GET /api/doctors] 返回医生数量: 5
```

---

### 4.2 获取在线医生 `GET /api/doctors/active`

```bash
curl http://localhost:8080/api/doctors/active
```

**预期**：`200 OK`，仅返回 `isActive=true` 的医生（初始数据 4 条：`doc001/002/003/005`）。

---

### 4.3 医生登录 `POST /api/auth/login`（type=doctor）

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"type":"doctor","username":"dr-zhang-wei","password":"123456"}'
```

**预期成功**：`200 OK`，`role=DOCTOR`，返回 `token` 与 `user`。
**预期失败（错误密码）**：`401` → `{"message":"用户名或密码错误"}`。

> 医生密码在数据库中以 **MD5** 存储（`123456` → `e10adc3949ba59abbe56e057f20f883e`）；登录时后端对输入密码做 MD5 后比对。

---

### 4.4 患者登录 `POST /api/auth/login`（type=patient，首次自动建档）

**首次登录（自动建档）**：
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"type":"patient","name":"张三","birthday":"1990-05-20"}'
```

**预期**：`200 OK`，`role=PATIENT`，返回 `token` 与 `user`；**同时数据库 `patients` 表自动新增一条 `(name=张三, birthday=1990-05-20)` 记录**。
验证建档：
```bash
mysql> SELECT * FROM qa_healthcare.patients WHERE name='张三';
```

**再次登录（相同信息，不重复建档）**：
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"type":"patient","name":"张三","birthday":"1990-05-20"}'
```
**预期**：`200 OK` 正常登录；`patients` 表记录数不变（按 `(name, birthday)` 唯一约束命中已有记录）。

**边界（参数为空）**：`name` 或 `birthday` 缺失 → `400` → `{"message":"姓名和出生日期不能为空"}`。

> 患者身份以「姓名 + 出生日期」标识，**无密码**；首次输入任意姓名 + 生日即自动创建账户，再次相同信息直接登录。

---

### 4.5 JWT 鉴权（拦截器，只校验是否过期）

- **白名单接口**：`/api/doctors`、`/api/auth`、`/api/test` 无需 token，直接 `200`。
  ```bash
  curl http://localhost:8080/api/doctors      # 200，无需 Authorization
  ```
- **受保护接口（未来业务）**：需 `Authorization: Bearer <token>`。
  ```bash
  # 假设存在受保护接口 /api/consultation，携带合法 token
  curl http://localhost:8080/api/consultation \
    -H "Authorization: Bearer <token>"
  ```
  - 缺失 / 非法 token → `401`
  - token 过期（有效期 2 小时）→ `401`

- **当前验证拦截器的方法（可选）**：业务受保护接口尚未实现，可临时将 `/api/doctors` 从 `WebConfig` 白名单移除后，无 token 访问应返回 `401`，以此验证拦截器生效；验证后请还原白名单。

---

### 4.6 CORS 测试 `GET /api/test/cors`

```bash
curl http://localhost:8080/api/test/cors
```

**预期**：`200 OK`，返回：
```json
{ "message": "CORS configuration is working!", "timestamp": ..., "service": "qa-service-user" }
```

---

## 5. 测试账号与初始数据

- **医生**（密码统一 `123456`，库中为 MD5）：
  `dr-zhang-wei` / `dr-li-na` / `dr-wang-qiang` / `dr-liu-min` / `dr-chen-jie`
- **患者**：无需预置；任意「姓名 + 出生日期」首次即自动建档。

---

## 6. 端到端联调（经由前端页面）

1. 启动 MySQL（执行 `sql/schema.sql` + `sql/data.sql`）→ 启动后端 → 启动前端 `npm run dev`。
2. **医生登录**：首页点击「医生登录」→ 输入 `dr-zhang-wei` / `123456` → 调 `/api/auth/login`（type=doctor）→ 进入医生诊室页。
3. **患者登录**：点击顶部「问诊」/ 首页「立即问诊」/ 医生卡片「进入诊室」→ 输入任意「姓名 + 生日」→ 调 `/api/auth/login`（type=patient）→ 首次自动建档并进入问诊页。
4. 登录态（`token` / `role`）写入浏览器 `localStorage`（`qa_token` / `qa_role`），供后续受保护接口携带。

---

## 7. 常见问题

| 现象 | 排查 |
| --- | --- |
| 后端无接口调用日志 | 确认访问的是 `/doctors`（医生团队页）而非首页（首页医生卡片仍是静态/接口混合，但日志在 `/api/doctors`）；确认后端已启动且库已建表插数 |
| 登录返回 401 | 医生：用户名/密码错误（注意密码是 MD5 比对，初始密码 `123456`）；患者：`name`/`birthday` 不能为空 |
| 患者建档报唯一键冲突 | 同一 `(name, birthday)` 已存在，属正常（再次登录）；确认 `patients` 表为 `(name, birthday)` 唯一，而非 `username` 唯一 |
| 前端调接口 404 | 确认使用 `npm run dev`（Vite 代理生效）；不要直接访问 build 后的静态产物 |
| 医生登录后进入诊室提示「请先登录」 | 登录成功需写入 `store.currentDoctor`（已通过 `setCurrentDoctor` 处理），确认前端代码已更新 |
