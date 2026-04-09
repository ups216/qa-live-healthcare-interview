# 接口测试说明（curl）

本文档描述**面试需求涉及的后端接口**如何用 **curl** 做自动化/手工验证，并指导据此生成测试报告。

## 一、接口范围

| 模块 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 医生 | GET | `/api/doctors` | 医生列表（问诊页、医生页） |
| 医生 | GET | `/api/doctors/{username}` | 按用户名查医生 |
| 患者 | POST | `/api/patients/register` | 注册并返回 JWT |
| 患者 | POST | `/api/patients/login` | 登录并返回 JWT |
| 患者 | GET | `/api/patients/me` | 当前患者（`Authorization: Bearer`） |

基址默认：`http://localhost:8080`（可通过环境变量 `BASE_URL` 覆盖）。

---

## 二、环境前置

1. MySQL（如 `docker compose up -d mysql`）已就绪，库表含 `doctors`、`patients`（见 `docker/mysql/init/`）。
2. 启动 `qa-service-user`（默认端口 `8080`）。
3. 本机已安装 **curl**；自动化脚本解析 JWT 登录响应优先使用 **python3** / **python** / **jq** / **node**；皆无则对**单行 JSON**尝试 **sed** 回退（登录与 `/me` 的字段提取）。
4. 若注册相关用例出现 **400** 且无字段明细，多为 **请求体编码或引号** 问题；脚本已对注册步骤使用 **UTF-8 临时 JSON + `--data-binary @文件`**，并缩短随机用户名（如 `ct_${RANDOM}_$$`），在 Git Bash/Windows 下更稳。

---

## 三、自动化测试（推荐）

仓库提供一键脚本，覆盖上表全部路径的主要成功/失败场景：

```bash
# 在仓库根目录
bash scripts/test-api-curl.sh
```

自定义地址：

```bash
BASE_URL=http://127.0.0.1:8080 bash scripts/test-api-curl.sh
```

脚本行为概要：

- **医生**：列表 200；存在的 `dr-zhang-wei` 200；随机不存在的 username 404。
- **患者登录**：错误密码 401；不存在用户 401；演示账号 `zhaoming` / `123456` 200；解析 `token`。
- **`/me`**：带合法 Bearer 200 且 `username` 为 `zhaoming`；无头或非法 JWT 401。
- **注册**：用户名/密码校验失败 400；新随机用户注册 200；同用户名再次注册 409；与种子冲突的 `zhaoming` 再注册 409。

退出码：`0` 表示全部通过，`1` 表示存在失败（终端会打印 `FAIL` 与摘要）。

### 生成测试报告（给 AI / 人工）

1. 执行并将完整输出保存为报告正文，例如：
   ```bash
   bash scripts/test-api-curl.sh 2>&1 | tee docs/test-report-$(date +%Y%m%d-%H%M%S).log
   ```
2. 报告中应包含：执行时间、`BASE_URL`、脚本打印的 **PASS/FAIL 行**、最后一行 **通过/失败统计**、退出码。
3. 若有 `FAIL`：根据脚本输出的期望/实际 HTTP 状态与响应片段排查服务、数据库或网络。

---

## 四、手工 curl 清单（与脚本等价场景）

以下示例仅供参考；**`$ACCESS_TOKEN` 需用登录响应中的 `token` 替换**。

### 1. 医生列表

```bash
curl -sS -w "\nHTTP:%{http_code}\n" \
  -H "Accept: application/json" \
  "http://localhost:8080/api/doctors"
```

### 2. 按用户名查医生

```bash
curl -sS -w "\nHTTP:%{http_code}\n" \
  -H "Accept: application/json" \
  "http://localhost:8080/api/doctors/dr-zhang-wei"
```

### 3. 患者登录（演示账号）

```bash
curl -sS -w "\nHTTP:%{http_code}\n" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{"username":"zhaoming","password":"123456"}' \
  "http://localhost:8080/api/patients/login"
```

### 4. 当前用户

```bash
export ACCESS_TOKEN='在此粘贴 token'

curl -sS -w "\nHTTP:%{http_code}\n" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  "http://localhost:8080/api/patients/me"
```

### 5. 注册新用户

```bash
curl -sS -w "\nHTTP:%{http_code}\n" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{"username":"myuser01","password":"123456","name":"测试"}' \
  "http://localhost:8080/api/patients/register"
```

更多字段与演示账号见 `docs/patient-api.md`。

---

## 五、相关文件

| 文件 | 说明 |
|------|------|
| `scripts/test-api-curl.sh` | curl 自动化用例 |
| `docs/patient-api.md` | 患者 API 约定与演示账号表 |

---

## 六、让 AI 根据本文档完成测试并写报告时的提示建议

将以下内容连同环境说明一并提供给 AI：

1. 「已启动 MySQL 与 `qa-service-user`；在仓库根目录执行 `bash scripts/test-api-curl.sh`。」
2. 「把终端完整输出整理为测试报告：每个用例 PASS/FAIL、HTTP 状态、失败原因、汇总统计。」
3. 「若退出码非 0，根据 FAIL 项给出修复建议（数据库、端口、表结构、种子数据）。」
