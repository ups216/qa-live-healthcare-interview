# 患者账号 API（qa-service-user）

Base URL 示例：`http://localhost:8080`（与 `vite` 开发代理 `/api` 一致时，前端使用相对路径 `/api/patients/...`）。

## 环境

- MySQL 需已执行 `docker/mysql/init/02_patients.sql` 建表；**首次**启动应用且 `patients` 表为空时，会由 `PatientDataInitializer` 写入演示患者（密码均为 `123456`）。
- 若本地库是旧 volume、没有 `patients` 表，需重建 volume 或手动执行该 SQL。
- JWT 密钥：`application.properties` 中 `qa.jwt.secret`（或通过环境变量 `JWT_SECRET` 覆盖），长度须 ≥ 32 字节（UTF-8）。

---

## 注册

```http
POST /api/patients/register
Content-Type: application/json
```

**Body 示例**

```json
{
  "username": "newuser01",
  "password": "123456",
  "name": "新用户",
  "birthday": "1990-01-01",
  "phone": "",
  "gender": ""
}
```

**成功 200**：返回 `token`、`expiresIn`（秒）、`patient`（无密码字段）。

**失败**：`409` 用户名已占用；`400` 校验失败。

### curl

```bash
curl -s -X POST "http://localhost:8080/api/patients/register" \
  -H "Content-Type: application/json" \
  -d '{"username":"newuser01","password":"123456","name":"新用户"}'
```

---

## 登录

```http
POST /api/patients/login
Content-Type: application/json
```

**Body 示例**

```json
{
  "username": "zhaoming",
  "password": "123456"
}
```

**成功 200**：同注册，返回 JWT 与患者信息。

**失败**：`401`，消息体含 Spring 默认 `message` 字段（如 `AUTH_INVALID_CREDENTIALS`）；前端统一展示「用户名或密码错误」。

### curl

```bash
curl -s -X POST "http://localhost:8080/api/patients/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"zhaoming","password":"123456"}'
```

将响应中的 `token` 复制到下面 `ACCESS_TOKEN`。

---

## 当前用户（需鉴权）

```http
GET /api/patients/me
Authorization: Bearer <ACCESS_TOKEN>
```

**成功 200**：患者 DTO（无密码）。

**失败**：`401`（未带 Token、无效或过期）。

### curl

```bash
export ACCESS_TOKEN='粘贴登录返回的 token'

curl -s "http://localhost:8080/api/patients/me" \
  -H "Authorization: Bearer $ACCESS_TOKEN"
```

---

## 演示账号（种子数据）

| 用户名    | 密码   | 说明   |
|-----------|--------|--------|
| zhaoming  | 123456 | 对应 id `patient001` |
| sunli     | 123456 | `patient002` |
| zhoujie   | 123456 | `patient003` |
| wufang    | 123456 | `patient004` |
| zhenghao  | 123456 | `patient005` |

前端静态 `question-list.json` 中历史问题使用上述 `patient00x` id；**新注册用户**为新 id，仅能看到自己提交的问题。

---

## 自动化 curl 测试

仓库根目录执行：

```bash
bash scripts/test-api-curl.sh
```

测试范围包含本页所列患者接口，以及问诊页依赖的 `GET /api/doctors`、`GET /api/doctors/{username}`。流程与报告模板见 **`docs/API_TEST.md`**。
