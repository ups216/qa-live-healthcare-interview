# API 接口测试文档

> 本文档描述 `qa-service-user` 服务所有接口的 curl 测试方法，可用于指导 AI 或手工执行测试。

---

## 1. 测试环境

| 项目       | 说明                                    |
| ---------- | --------------------------------------- |
| 操作系统   | macOS (Darwin 26.3)                     |
| 后端框架   | Spring Boot 3.5.7 + MyBatis-Plus 3.5.7 |
| 数据库     | MySQL 8.0 (Docker 容器, 端口 3307)      |
| 后端端口   | 8081                                    |
| 基础 URL   | `http://localhost:8081`                 |
| 测试工具   | curl                                    |

---

## 2. 接口列表

| 序号 | 方法   | 路径                       | 说明             |
| ---- | ------ | -------------------------- | ---------------- |
| 1    | GET    | /api/doctors               | 获取全部医生列表 |
| 2    | GET    | /api/doctors?status=active | 获取在线医生列表 |
| 3    | GET    | /api/doctors/{username}    | 获取单个医生详情 |
| 4    | POST   | /api/auth/login            | 患者登录         |

---

## 3. 测试用例

### TC-001: 获取全部医生列表

**接口**: `GET /api/doctors`

**说明**: 查询数据库中所有医生记录，不做任何筛选。

**curl 命令**:

```bash
curl -s -X GET 'http://localhost:8081/api/doctors' \
  -H 'Content-Type: application/json' | python3 -m json.tool
  ```
  预期结果:
HTTP 状态码: 200
响应体 code 为 200
data 数组包含 5 条医生记录
每条记录包含字段: id, username, realName, title, department, avatar, yearsOfExperience, specialty(数组), status
中文数据正常显示（无乱码）
预期响应示例:
  {
  "code": 200,
  "message": "success",
  "data": [
  {
  "id": 1,
  "username": "dr-zhang-wei",
  "password": null,
  "realName": "张伟医生",
  "title": "主任医师",
  "department": "心内科",
  "avatar": "https://images.pexels.com/photos/5215024/...",
  "yearsOfExperience": 15,
  "specialty": ["高血压", "冠心病", "心律失常"],
  "status": "active",
  "createdAt": "2026-10-25T...",
  "updatedAt": "2026-10-25T..."
  }
  ]
  }
### TC-002: 获取在线医生列表（按状态筛选）
接口: GET /api/doctors?status=active
说明: 仅返回 status=active 的在线医生。
curl 命令:
curl -s -X GET 'http://localhost:8081/api/doctors?status=active' \
-H 'Content-Type: application/json' | python3 -m json.tool

HTTP 状态码: 200
data 数组包含 4 条医生记录
刘敏医生（dr-liu-min, status=inactive）不应出现在结果中
所有返回记录的 status 字段均为 "active"
### TC-003: 获取单个医生详情
接口: GET /api/doctors/{username}
说明: 根据用户名查询单个医生的完整信息。
curl 命令:
curl -s -X GET 'http://localhost:8081/api/doctors/dr-zhang-wei' \
-H 'Content-Type: application/json' | python3 -m json.tool
预期结果:
HTTP 状态码: 200
data 为单个对象（非数组）
data.username 为 "dr-zhang-wei"
data.realName 为 "张伟医生"
data.department 为 "心内科"
data.specialty 为 ["高血压", "冠心病", "心律失常"
### TC-004: 查询不存在的医生
接口: GET /api/doctors/nonexistent
说明: 查询一个不存在的医生用户名。
curl 命令
curl -s -X GET 'http://localhost:8081/api/doctors/nonexistent' \
-H 'Content-Type: application/json' | python3 -m json.tool
预期结果:
HTTP 状态码: 200
响应体 code 为 404
message 为 "医生不存在"
data 为 null
预期响应:
{
"code": 404,
"message": "医生不存在",
"data": null
}
### TC-005: 者登录 — 正确凭据（成功）
口: POST /api/auth/login
说明: 使用正确的用户名和密码登录，预期返回 JWT token。
curl 命令:
curl -s -X POST 'http://localhost:8081/api/auth/login' \
-H 'Content-Type: application/json' \
-d '{
"username": "admin",
"password": "123456"
}' | python3 -m json.tool

预期结果:
HTTP 状态码: 200
响应体 code 为 200
message 为 "登录成功"
data.token 为有效的 JWT 字符串（以 eyJ 开头）
data.user.id 为 1
data.user.username 为 "admin"
data.user.realName 为 "管理员"
data.user.phone 为 "138****0000"
预期响应示例:
{
"code": 200,
"message": "登录成功",
"data": {
"token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIs...",
"user": {
"id": 1,
"username": "admin",
"realName": "管理员",
"phone": "138****0000"
}
}
}

### TC-006: 患者登录 — 密码错误（失败）
接口: POST /api/auth/login
说明: 使用正确的用户名但错误的密码登录，预期返回 401。
curl 命令:
curl -s -X POST 'http://localhost:8081/api/auth/login' \
-H 'Content-Type: application/json' \
-d '{
"username": "admin",
"password": "wrongpassword"
}' | python3 -m json.tool
预期结果:
HTTP 状态码: 200
响应体 code 为 401
message 为 "用户名或密码错误"
data 为 null
预期响应:
{
"code": 401,
"message": "用户名或密码错误",
"data": null
}

### TC-007: 患者登录 — 用户不存在（失败）
接口: POST /api/auth/login
说明: 使用不存在的用户名登录，预期返回 401（与密码错误保持一致，不泄露用户是否存在）。
curl 命令:
curl -s -X POST 'http://localhost:8081/api/auth/login' \
-H 'Content-Type: application/json' \
-d '{
"username": "nonexistent_user",
"password": "123456"
}' | python3 -m json.tool
预期结果:
HTTP 状态码: 200
响应体 code 为 401
message 为 "用户名或密码错误"
data 为 null
## 4. 一键执行脚本
将以下脚本保存为 test-api.sh 并执行，可自动化运行所有测试用例：
#!/bin/bash
# API 接口自动化测试脚本
# 用法: bash docs/test-api.sh

BASE_URL="http://localhost:8081"
PASS=0
FAIL=0

run_test() {
local test_name="$1"
local expected_code="$2"
local response="$3"

actual_code=$(echo "$response" | python3 -c "import sys,json; print(json.load(sys.stdin).get('code',''))" 2>/dev/null)

if [ "$actual_code" = "$expected_code" ]; then
echo "✅ PASS: $test_name (code=$actual_code)"
PASS=$((PASS + 1))
else
echo "❌ FAIL: $test_name (expected code=$expected_code, got code=$actual_code)"
FAIL=$((FAIL + 1))
fi
}

echo "========================================="
echo "  QA Healthcare API 接口测试"
echo "  Base URL: $BASE_URL"
echo "  Time: $(date '+%Y-%m-%d %H:%M:%S')"
echo "========================================="
echo ""

# TC-001: 获取全部医生列表
echo "--- TC-001: GET /api/doctors ---"
resp=$(curl -s -X GET "$BASE_URL/api/doctors" -H 'Content-Type: application/json')
run_test "TC-001 获取全部医生列表" "200" "$resp"

# TC-002: 获取在线医生列表
echo "--- TC-002: GET /api/doctors?status=active ---"
resp=$(curl -s -X GET "$BASE_URL/api/doctors?status=active" -H 'Content-Type: application/json')
run_test "TC-002 获取在线医生列表" "200" "$resp"

# TC-003: 获取单个医生详情
echo "--- TC-003: GET /api/doctors/dr-zhang-wei ---"
resp=$(curl -s -X GET "$BASE_URL/api/doctors/dr-zhang-wei" -H 'Content-Type: application/json')
run_test "TC-003 获取单个医生详情" "200" "$resp"

# TC-004: 查询不存在的医生
echo "--- TC-004: GET /api/doctors/nonexistent ---"
resp=$(curl -s -X GET "$BASE_URL/api/doctors/nonexistent" -H 'Content-Type: application/json')
run_test "TC-004 查询不存在的医生" "404" "$resp"

# TC-005: 患者登录 — 正确凭据
echo "--- TC-005: POST /api/auth/login (正确) ---"
resp=$(curl -s -X POST "$BASE_URL/api/auth/login" \
-H 'Content-Type: application/json' \
-d '{"username":"admin","password":"123456"}')
run_test "TC-005 患者登录(正确凭据)" "200" "$resp"

# TC-006: 患者登录 — 密码错误
echo "--- TC-006: POST /api/auth/login (错密) ---"
resp=$(curl -s -X POST "$BASE_URL/api/auth/login" \
-H 'Content-Type: application/json' \
-d '{"username":"admin","password":"wrongpassword"}')
run_test "TC-006 患者登录(密码错误)" "401" "$resp"

# TC-007: 患者登录 — 用户不存在
echo "--- TC-007: POST /api/auth/login (不存在) ---"
resp=$(curl -s -X POST "$BASE_URL/api/auth/login" \
-H 'Content-Type: application/json' \
-d '{"username":"nonexistent","password":"123456"}')
run_test "TC-007 患者登录(用户不存在)" "401" "$resp"

echo ""
echo "========================================="
echo "  测试结果: 通过 $PASS / 失败 $FAIL / 总计 $((PASS + FAIL))"
echo "========================================="

if [ "$FAIL" -gt 0 ]; then
exit 1
else
echo "  🎉 全部通过！"
exit 0
fi
## 5. 前置条件
执行测试前，请确保以下服务已启动：
# 1. 启动 Docker 容器（MySQL + phpMyAdmin）
docker-compose up -d

# 2. 等待 MySQL 就绪（约 10 秒）
docker exec -it qa-mysql mysqladmin ping -uroot -proot --wait=30

# 3. 启动后端服务
cd server/qa-service-user
./mvnw spring-boot:run

# 4. 确认后端已启动（端口 8081）
curl -s http://localhost:8081/actuator/health | python3 -m json.tool


