# API 接口测试文档

> 本文用于描述本项目的接口测试流程，并可**直接指导 AI 完成测试并生成测试报告**。
> 所有用例均以 `curl` 构建，覆盖 README「测试要求」中涉及的全部接口。

## 1. 环境准备

### 1.1 依赖

| 工具 | 必需 | 说明 |
|---|---|---|
| `curl` | ✅ | 执行用例；macOS 自带 |
| `jq` | ⭕ 推荐 | 解析 JSON；未安装时一键脚本自动降级为 `python3` |
| `python3` | ⭕ | 仅在无 `jq` 时需要 |
| Docker | ✅ | 提供 MySQL 与 phpMyAdmin |

```bash
which curl jq docker        # 确认可用
brew install jq             # 如需（macOS）
```

### 1.2 启动被测环境

```bash
# 1) 启动 MySQL + phpMyAdmin
docker compose up -d
docker compose ps           # 两个容器均应为 healthy

# 2) 启动后端 qa-service-user（:8080）
cd server/qa-service-user
export JAVA_HOME=/opt/homebrew/opt/openjdk@17   # macOS Homebrew；按本机 JDK 调整
./mvnw spring-boot:run

# 3) 自检
curl -s http://localhost:8080/actuator/health | jq
# 期望：{"status":"UP", ...}，且 components.db.status == "UP"
```

> **初始化 SQL 只在数据卷为空的首次启动执行**。若修改了 `db/init/*.sql`，
> 需先 `docker compose down -v` 清卷再 `up -d`。

### 1.3 变量约定

```bash
BASE=http://localhost:8080
CT='Content-Type: application/json'
```

## 2. 一键回归（推荐）

```bash
bash scripts/api-test.sh
```

- 自动执行全部用例并以 `PASS/FAIL` 逐项输出
- 用户名使用时间戳，可**重复执行**、不受唯一索引影响
- 全部通过退出码 `0`；存在失败退出码 `1`（便于接入 CI）
- 无 `jq` 时自动降级 `python3`，无需改动脚本

## 3. 手工 curl 用例集

> 每条均给出可直接复制的命令。HTTP 状态码通过 `-w "%{http_code}"` 输出。

### 3.1 既有接口回归（防回归基线）

```bash
# T01 GET
curl -s -w "\nHTTP:%{http_code}\n" $BASE/api/test/cors

# T02 POST
curl -s -w "\nHTTP:%{http_code}\n" -X POST $BASE/api/test/cors -H "$CT" -d '{"a":1}'

# T03 OPTIONS
curl -s -o /dev/null -w "HTTP:%{http_code}\n" -X OPTIONS $BASE/api/test/cors

# T04 健康检查（含数据源连通性）
curl -s $BASE/actuator/health | jq '{status, db: .components.db.status}'
```

| 用例 | 期望 |
|---|---|
| T01 / T02 / T03 | HTTP 200 |
| T04 | `status=UP`，`db.status=UP` |

### 3.2 需求2 · 医生接口

```bash
# T05 全量列表
curl -s $BASE/api/doctors | jq '.data | length'                 # 期望 5

# T06 契约校验
curl -s $BASE/api/doctors | jq '.data[0] | has("password")'     # false
curl -s $BASE/api/doctors | jq '.data[0].specialties | type'    # "array"
curl -s $BASE/api/doctors | jq '.data[0].isActive | type'       # "boolean"
curl -s $BASE/api/doctors | jq -r '.data[0].name'               # 张伟医生

# T07 在线医生
curl -s $BASE/api/doctors/active | jq '.data | length'          # 期望 4（doc004 离线）

# T08 / T10 精确查询
curl -s $BASE/api/doctors/doc001 | jq '.data.name'                    # 张伟医生
curl -s $BASE/api/doctors/username/dr-zhang-wei | jq '.data.username' # dr-zhang-wei

# T09 / T11 不存在 → 404
curl -s -o /dev/null -w "HTTP:%{http_code}\n" $BASE/api/doctors/not-exist
curl -s -o /dev/null -w "HTTP:%{http_code}\n" $BASE/api/doctors/username/nobody

# T12 / T13 边界：不得返回 500
curl -s -o /dev/null -w "HTTP:%{http_code}\n" "$BASE/api/doctors/%20"
curl -s -o /dev/null -w "HTTP:%{http_code}\n" "$BASE/api/doctors/a%27b"
```

> **中文乱码判定方法**：`.data[0].name` 若出现 `å¼ ä¼Ÿ` 形态即为「双重编码」。
> 根因是 initdb 脚本以 latin1 读入 UTF-8 文件，修复方式见 `db/init/*.sql` 顶部的 `SET NAMES utf8mb4;`。
> 注意：**mysql 客户端直查会因往返转换而显示正常，必须用 `SELECT HEX(name)` 判定**。

### 3.3 需求3 · 患者注册

```bash
U="pt$(date +%s)"

# T14 注册成功（HTTP 201）
curl -s -w "\nHTTP:%{http_code}\n" -X POST $BASE/api/patient/register \
  -H "$CT" -d "{\"username\":\"$U\",\"password\":\"secret123\",\"name\":\"测试用户\"}"

# T15 用户名重复 → 400 / code 1001
curl -s -X POST $BASE/api/patient/register \
  -H "$CT" -d "{\"username\":\"$U\",\"password\":\"secret123\",\"name\":\"重复\"}" | jq '.code'

# T16 / T17 / T18 参数校验 → 400 / code 1002
curl -s -o /dev/null -w "HTTP:%{http_code}\n" -X POST $BASE/api/patient/register -H "$CT" -d '{"password":"secret123","name":"缺用户名"}'
curl -s -o /dev/null -w "HTTP:%{http_code}\n" -X POST $BASE/api/patient/register -H "$CT" -d "{\"username\":\"${U}c\",\"name\":\"缺密码\"}"
curl -s -o /dev/null -w "HTTP:%{http_code}\n" -X POST $BASE/api/patient/register -H "$CT" -d '{}'

# T20 超长用户名 → 400，且不产生脏数据
LONG=$(printf 'u%.0s' $(seq 1 80))
curl -s -o /dev/null -w "HTTP:%{http_code}\n" -X POST $BASE/api/patient/register \
  -H "$CT" -d "{\"username\":\"$LONG\",\"password\":\"secret123\",\"name\":\"超长\"}"
```

**验证密码以 BCrypt 存储**（不能只信接口返回）：

```bash
docker exec qa-live-mysql mysql -uroot -proot123456 -D qa_live -N \
  -e "SELECT username, LEFT(password,7), LENGTH(password) FROM t_patient WHERE username='$U';"
# 期望：前缀 $2a$10$ ，长度 60
```

### 3.4 需求3 · 患者登录与 JWT

```bash
# T21 登录成功 → 200 + token
TOKEN=$(curl -s -X POST $BASE/api/patient/login -H "$CT" \
  -d "{\"username\":\"$U\",\"password\":\"secret123\"}" | jq -r '.data.token')

# token 应为三段式 JWT
echo "$TOKEN" | awk -F'.' '{print NF}'      # 期望 3

# T22 密码错误 → 401
curl -s -o /dev/null -w "HTTP:%{http_code}\n" -X POST $BASE/api/patient/login \
  -H "$CT" -d "{\"username\":\"$U\",\"password\":\"wrongpwd\"}"

# T23 用户不存在 → 401
curl -s -o /dev/null -w "HTTP:%{http_code}\n" -X POST $BASE/api/patient/login \
  -H "$CT" -d '{"username":"nosuchuser","password":"secret123"}'

# T23b 防用户名枚举：两种失败场景的对外 message 必须一致
curl -s -X POST $BASE/api/patient/login -H "$CT" -d "{\"username\":\"$U\",\"password\":\"wrongpwd\"}" | jq -r '.message'
curl -s -X POST $BASE/api/patient/login -H "$CT" -d '{"username":"nosuchuser","password":"secret123"}' | jq -r '.message'
# 期望：两次输出均为「用户名或密码错误」

# T24 空密码 → 400
curl -s -o /dev/null -w "HTTP:%{http_code}\n" -X POST $BASE/api/patient/login \
  -H "$CT" -d "{\"username\":\"$U\",\"password\":\"\"}"
```

### 3.5 需求3 · `/api/patient/me` 鉴权矩阵

```bash
# T25 携带有效 token → 200
curl -s -w "\nHTTP:%{http_code}\n" $BASE/api/patient/me -H "Authorization: Bearer $TOKEN"

# T26 无 token → 401
curl -s -o /dev/null -w "HTTP:%{http_code}\n" $BASE/api/patient/me

# T27 篡改 token → 401
curl -s -o /dev/null -w "HTTP:%{http_code}\n" $BASE/api/patient/me -H "Authorization: Bearer ${TOKEN}x"

# T28 缺少 Bearer 前缀 → 401
curl -s -o /dev/null -w "HTTP:%{http_code}\n" $BASE/api/patient/me -H "Authorization: $TOKEN"

# T29 格式非法的 token → 401
curl -s -o /dev/null -w "HTTP:%{http_code}\n" $BASE/api/patient/me -H "Authorization: Bearer invalid.jwt.value"
```

| 用例 | 期望 HTTP | 期望 code |
|---|---|---|
| T25 | 200 | 0 |
| T26 | 401 | 1005 |
| T27 | 401 | 1006 |
| T28 | 401 | 1005 |
| T29 | 401 | 1006 |

### 3.6 白名单反向验证（防止误拦截）

拦截器出错的代价远高于漏判，因此必须反向验证公开接口**不带 token 也能访问**：

```bash
# T30 医生接口匿名可访问
curl -s -o /dev/null -w "HTTP:%{http_code}\n" $BASE/api/doctors          # 200
curl -s -o /dev/null -w "HTTP:%{http_code}\n" $BASE/api/doctors/active   # 200

# T31 登录接口本身必须匿名可访问，否则用户永远拿不到 token
curl -s -o /dev/null -w "HTTP:%{http_code}\n" -X POST $BASE/api/patient/login \
  -H "$CT" -d "{\"username\":\"$U\",\"password\":\"secret123\"}"          # 200
```

## 4. 错误码对照表

| code | 含义 | HTTP |
|---|---|---|
| 0 | 成功 | 200 |
| 1001 | 用户名已存在 | 400 |
| 1002 | 参数校验失败 | 400 |
| 1003 | 用户不存在（内部码，对外与 1004 同文案） | 401 |
| 1004 | 密码错误 | 401 |
| 1005 | token 缺失 | 401 |
| 1006 | token 无效或已过期 | 401 |
| 1010 | 医生不存在 | 404 |
| 5000 | 服务器内部错误 | 500 |

## 5. 端到端手工验证（浏览器）

接口测试之外，以下链路需在浏览器中确认：

| # | 场景 | 期望 |
|---|---|---|
| E1 | 未登录访问 `/consultation` | 重定向到 `/patient/login?redirect=/consultation` |
| E2 | 注册 → 自动登录 | 跳转回 `/consultation`，头部显示用户名 |
| E3 | 刷新浏览器 | 仍保持登录（旧实现此处会丢失） |
| E4 | 首页右上角切换 English | 首页文案即时切换；导航/页脚仍为中文 |
| E5 | 刷新后 | 语言保持 English；切回中文后同样保持 |
| E6 | 医生页 `/doctors` | 5 张卡片来自后端；`doc004` 显示「暂未开放」 |
| E7 | 停掉后端刷新医生页 | 显示错误态与「重试」按钮（而非静态数据） |
| E8 | 医生登录 `dr-zhang-wei / 123456` | 仍能正常登录进诊室 |

## 6. 指导 AI 生成测试报告

> 以下内容供 AI 阅读并执行。

**任务**：执行本文档描述的测试，并产出 `docs/TEST_REPORT.md`。

**执行要求**：

1. 按 §1.2 启动环境，确认 `/actuator/health` 为 `UP`；
2. 执行 `bash scripts/api-test.sh`，**完整保留真实输出**；
3. 执行 §3.3 的 BCrypt 数据库校验，把真实查询结果贴入报告；
4. 按 §5 在浏览器中确认 E1~E8，并记录实际观察到的结果；
5. **严禁编造响应内容** —— 报告中出现的每一条响应都必须来自真实执行；
6. 若有失败项，如实记录，并在「失败分析」中给出根因与修复状态。

**报告结构**：见 `docs/TEST_REPORT.md` 模板。

## 7. 编写 curl 用例的经验教训

实现过程中踩到的坑，供复用本文时参考：

1. **不要在 shell 里嵌套复杂引号的 `$(...)`**
   `assert_eq "$(req ... -d "{\"k\":\"$v\"}")" "201" "T14"` 在 macOS bash 下会被错误分词，
   导致实参个数错乱、断言标签错位，进而产生"假绿"（FAIL 被记成 PASS）。
   **正确写法**：先赋变量再断言 —— `CODE="$(req ... -d "$DATA")"; assert_eq "$CODE" "201" "T14"`。
2. **jq 对数组取长度/类型要写在管道后**：`.data | length`、`.data[0].specialties | type`；
   写成 `.data.length` 对数组会返回 `null`。
3. **断言必须带上唯一标签**，否则输出无法定位到具体用例。
