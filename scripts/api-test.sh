#!/usr/bin/env bash
#
# QA Live Healthcare —— 接口回归测试脚本
#
# 覆盖 README「测试要求」中涉及的全部后端接口：
#   - 既有接口回归：/api/test/cors、/actuator/health
#   - 需求2 医生接口：/api/doctors/**
#   - 需求3 患者鉴权：/api/patient/register | login | me
#
# 用法：
#   bash scripts/api-test.sh                            # 默认 http://localhost:8080
#   BASE_URL=http://localhost:8080 bash scripts/api-test.sh
#
# 设计约定（重要）：
#   1. 每条 curl 结果都"先赋给变量、再断言"，绝不在 assert 的参数里嵌套复杂引号的 $(...)。
#      原因：在 macOS bash 上，$(req ... -d "{\"k\":\"$v\"}") 这类嵌套会被错误分词，
#      导致实参个数错乱、断言标签错位（曾因此产生大量假绿）。
#   2. 断言失败只记录不中断，最后统一汇总；
#   3. 注册/登录使用带时间戳的用户名，脚本可重复执行，不会撞唯一索引；
#   4. 存在失败项时以退出码 1 结束，便于后续接入 CI。
#
set -uo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"

PASS=0
FAIL=0
FAILED_CASES=()

GREEN='\033[32m'
RED='\033[31m'
YELLOW='\033[33m'
RESET='\033[0m'

TMP_DIR="$(mktemp -d)"
cleanup() { rm -rf "$TMP_DIR"; }
trap cleanup EXIT

################################## 工具 ##################################

# 优先 jq；无 jq 时降级 python3
JSON_TOOL="none"
if command -v jq >/dev/null 2>&1; then
  JSON_TOOL="jq"
elif command -v python3 >/dev/null 2>&1; then
  JSON_TOOL="python3"
fi

# jq_get <表达式> <响应文件>
# 支持的表达式：a.b.c / a[0].b / .length / .type / .data[0].id 等基本形式
jq_get() {
  local expr="$1" file="$2"
  case "$JSON_TOOL" in
    jq) jq -r "$expr" < "$file" 2>/dev/null ;;
    python3)
      JQ_FILE="$file" JQ_EXPR="$expr" python3 -c '
import json, os, re, sys
import json, os, re, sys
try:
    with open(os.environ["JQ_FILE"], encoding="utf-8") as f:
        cur = json.load(f)
except Exception:
    print(""); sys.exit(0)
expr = os.environ.get("JQ_EXPR", "").strip()
# 管道与 jq 等价地把值传给 length/type，此处退化成同一套 token 解析
expr = expr.replace("|", ".")
if expr.startswith("."):
    expr = expr[1:]
# 拆分为 token：key 或 [index]
tokens = []
for m in re.finditer(r"\[(-?\d+)\]|([^.\[\]]+)", expr):
    if m.group(1) is not None:
        tokens.append(int(m.group(1)))
    elif m.group(2):
        tokens.append(m.group(2))
try:
    for t in tokens:
        if isinstance(t, int):
            cur = cur[t]
        elif t == "length":
            cur = len(cur)
        elif t == "type":
            cur = {"dict": "object", "list": "array", "str": "string",
                   "int": "number", "float": "number", "bool": "boolean"}[type(cur).__name__]
        else:
            cur = cur[t]
except Exception:
    cur = None
if isinstance(cur, bool):
    print("true" if cur else "false")
elif cur is None:
    print("null")
elif isinstance(cur, (dict, list)):
    print(json.dumps(cur, ensure_ascii=False))
else:
    print(cur)
' 2>/dev/null ;;
    *) echo "" ;;
  esac
}

# req <响应文件> <curl 参数...>  —— 回显 HTTP 状态码，响应体写入文件
req() {
  local out_file="$1"
  shift
  local code
  code="$(curl -s --max-time 10 -o "$out_file" -w '%{http_code}' "$@" 2>/dev/null)"
  printf '%s' "$code"
}

# assert_eq <实际> <期望> <用例ID>
assert_eq() {
  local actual="$1" expected="$2" case_id="$3"
  if [ "$actual" = "$expected" ]; then
    PASS=$((PASS + 1))
    printf "  ${GREEN}PASS${RESET} %-40s actual=%s\n" "$case_id" "$actual"
  else
    FAIL=$((FAIL + 1))
    FAILED_CASES+=("$case_id")
    printf "  ${RED}FAIL${RESET} %-40s expected=%s actual=%s\n" "$case_id" "$expected" "$actual"
  fi
}

# assert_ne <实际> <不允许出现的值> <用例ID>
assert_ne() {
  local actual="$1" unexpected="$2" case_id="$3"
  if [ "$actual" != "$unexpected" ]; then
    PASS=$((PASS + 1))
    printf "  ${GREEN}PASS${RESET} %-40s actual=%s\n" "$case_id" "$actual"
  else
    FAIL=$((FAIL + 1))
    FAILED_CASES+=("$case_id")
    printf "  ${RED}FAIL${RESET} %-40s unexpected=%s actual=%s\n" "$case_id" "$unexpected" "$actual"
  fi
}

section() { printf "\n${YELLOW}── %s ──${RESET}\n" "$1"; }

CT='Content-Type: application/json'

############################### 前置检查 ###############################

section "环境检查"

HEALTH="$TMP_DIR/health.json"
CODE="$(req "$HEALTH" "$BASE_URL/actuator/health")"

if [ "$CODE" != "200" ]; then
  printf "  ${RED}后端不可达或异常: %s (HTTP %s)${RESET}\n" "$BASE_URL" "$CODE"
  printf "  请先启动后端: cd server/qa-service-user && ./mvnw spring-boot:run\n"
  exit 2
fi

assert_eq "$(jq_get '.status' "$HEALTH")" "UP" "T04-actuator-health"
assert_eq "$(jq_get '.components.db.status' "$HEALTH")" "UP" "T04b-datasource-up"
printf "  ${YELLOW}INFO${RESET} JSON 引擎: %s\n" "$JSON_TOOL"

############################ 既有接口回归 ############################

section "既有接口回归（防回归基线）"

F="$TMP_DIR/cors_get.json"
CODE="$(req "$F" "$BASE_URL/api/test/cors")"
assert_eq "$CODE" "200" "T01-GET-test-cors"
assert_eq "$(jq_get '.service' "$F")" "qa-service-user" "T01b-test-cors-payload"

F="$TMP_DIR/cors_post.json"
CODE="$(req "$F" -X POST "$BASE_URL/api/test/cors" -H "$CT" -d '{"a":1}')"
assert_eq "$CODE" "200" "T02-POST-test-cors"

F="$TMP_DIR/cors_opt.json"
CODE="$(req "$F" -X OPTIONS "$BASE_URL/api/test/cors")"
assert_eq "$CODE" "200" "T03-OPTIONS-test-cors"

######################## 需求2 · 医生接口 ########################

section "需求2 · 医生接口（MySQL 数据源）"

F="$TMP_DIR/doctors.json"
CODE="$(req "$F" "$BASE_URL/api/doctors")"
assert_eq "$CODE" "200" "T05-doctors-http-200"
assert_eq "$(jq_get '.data | length' "$F")" "5" "T05b-doctors-count-5"
assert_eq "$(jq_get '.data[0].id' "$F")" "doc001" "T05c-first-doctor-id"
assert_eq "$(jq_get '.data[0].password' "$F")" "null" "T06a-no-password-field"
assert_eq "$(jq_get '.data[0].specialties | type' "$F")" "array" "T06b-specialties-is-array"
assert_eq "$(jq_get '.data[0].isActive | type' "$F")" "boolean" "T06c-isActive-is-boolean"
assert_eq "$(jq_get '.data[0].name' "$F")" "张伟医生" "T06d-chinese-not-mojibake"

F="$TMP_DIR/doctors_active.json"
CODE="$(req "$F" "$BASE_URL/api/doctors/active")"
assert_eq "$CODE" "200" "T07-active-http-200"
assert_eq "$(jq_get '.data | length' "$F")" "4" "T07b-active-count-4"

F="$TMP_DIR/doctor_001.json"
CODE="$(req "$F" "$BASE_URL/api/doctors/doc001")"
assert_eq "$CODE" "200" "T08-by-id-200"
assert_eq "$(jq_get '.data.name' "$F")" "张伟医生" "T08b-by-id-name"
assert_eq "$(jq_get '.data.experience' "$F")" "15年临床经验" "T08c-by-id-experience"

F="$TMP_DIR/doctor_uname.json"
CODE="$(req "$F" "$BASE_URL/api/doctors/username/dr-zhang-wei")"
assert_eq "$CODE" "200" "T10-by-username-200"
assert_eq "$(jq_get '.data.username' "$F")" "dr-zhang-wei" "T10b-by-username-value"

F="$TMP_DIR/nf1.json"
CODE="$(req "$F" "$BASE_URL/api/doctors/not-exist")"
assert_eq "$CODE" "404" "T09-by-id-notfound-404"

F="$TMP_DIR/nf2.json"
CODE="$(req "$F" "$BASE_URL/api/doctors/username/nobody")"
assert_eq "$CODE" "404" "T11-by-username-notfound-404"

F="$TMP_DIR/blank.json"
CODE="$(req "$F" "$BASE_URL/api/doctors/%20")"
assert_ne "$CODE" "500" "T12-blank-id-no-500"

F="$TMP_DIR/sqli.json"
CODE="$(req "$F" "$BASE_URL/api/doctors/a%27b")"
assert_ne "$CODE" "500" "T13-special-char-no-500"

######################## 需求3 · 患者注册 ########################

section "需求3 · 患者注册"

TS="$(date +%s)"
NEW_USER="pt${TS}"

F="$TMP_DIR/reg_ok.json"
DATA="{\"username\":\"$NEW_USER\",\"password\":\"secret123\",\"name\":\"测试用户\"}"
CODE="$(req "$F" -X POST "$BASE_URL/api/patient/register" -H "$CT" -d "$DATA")"
assert_eq "$CODE" "201" "T14-register-created"
assert_eq "$(jq_get '.data.username' "$F")" "$NEW_USER" "T14b-register-returns-username"
assert_eq "$(jq_get '.data.password' "$F")" "null" "T14c-register-no-password"

F="$TMP_DIR/reg_dup.json"
DATA="{\"username\":\"$NEW_USER\",\"password\":\"secret123\",\"name\":\"重复用户\"}"
CODE="$(req "$F" -X POST "$BASE_URL/api/patient/register" -H "$CT" -d "$DATA")"
assert_eq "$CODE" "400" "T15-register-duplicate-400"
assert_eq "$(jq_get '.code' "$F")" "1001" "T15b-duplicate-error-code"

F="$TMP_DIR/reg_miss_u.json"
DATA='{"password":"secret123","name":"缺用户名"}'
CODE="$(req "$F" -X POST "$BASE_URL/api/patient/register" -H "$CT" -d "$DATA")"
assert_eq "$CODE" "400" "T16-missing-username-400"

F="$TMP_DIR/reg_miss_p.json"
DATA="{\"username\":\"${NEW_USER}c\",\"name\":\"缺密码\"}"
CODE="$(req "$F" -X POST "$BASE_URL/api/patient/register" -H "$CT" -d "$DATA")"
assert_eq "$CODE" "400" "T17-missing-password-400"

F="$TMP_DIR/reg_empty.json"
DATA='{}'
CODE="$(req "$F" -X POST "$BASE_URL/api/patient/register" -H "$CT" -d "$DATA")"
assert_eq "$CODE" "400" "T18-empty-body-400"
assert_eq "$(jq_get '.code' "$F")" "1002" "T18b-validation-error-code"

LONG_USER="$(printf 'u%.0s' $(seq 1 80))"
F="$TMP_DIR/reg_long.json"
DATA="{\"username\":\"$LONG_USER\",\"password\":\"secret123\",\"name\":\"超长用户名\"}"
CODE="$(req "$F" -X POST "$BASE_URL/api/patient/register" -H "$CT" -d "$DATA")"
assert_eq "$CODE" "400" "T20-too-long-username-400"

###################### 需求3 · 患者登录与 JWT ######################

section "需求3 · 患者登录与 JWT"

F="$TMP_DIR/login_ok.json"
DATA="{\"username\":\"$NEW_USER\",\"password\":\"secret123\"}"
CODE="$(req "$F" -X POST "$BASE_URL/api/patient/login" -H "$CT" -d "$DATA")"
assert_eq "$CODE" "200" "T21-login-success-200"

TOKEN="$(jq_get '.data.token' "$F")"
EXPIRES="$(jq_get '.data.expiresIn' "$F")"
assert_ne "$TOKEN" "" "T21b-token-not-empty"
assert_ne "$EXPIRES" "0" "T21b2-expiresIn-positive"
assert_eq "$(printf '%s' "$TOKEN" | awk -F'.' '{print NF}')" "3" "T21c-token-is-three-part-jwt"

F="$TMP_DIR/login_bad.json"
DATA="{\"username\":\"$NEW_USER\",\"password\":\"wrongpwd\"}"
CODE="$(req "$F" -X POST "$BASE_URL/api/patient/login" -H "$CT" -d "$DATA")"
assert_eq "$CODE" "401" "T22-wrong-password-401"
CODE_BAD="$(jq_get '.code' "$F")"
MSG_BAD="$(jq_get '.message' "$F")"

F="$TMP_DIR/login_nouser.json"
DATA='{"username":"nosuchuser","password":"secret123"}'
CODE="$(req "$F" -X POST "$BASE_URL/api/patient/login" -H "$CT" -d "$DATA")"
assert_eq "$CODE" "401" "T23-nonexistent-user-401"
CODE_NOUSER="$(jq_get '.code' "$F")"
MSG_NOUSER="$(jq_get '.message' "$F")"

assert_eq "$MSG_NOUSER" "$MSG_BAD" "T23b-identical-message-anti-enumeration"
assert_ne "$CODE_BAD" "$CODE_NOUSER" "T23c-internal-codes-differ"

F="$TMP_DIR/login_empty.json"
DATA="{\"username\":\"$NEW_USER\",\"password\":\"\"}"
CODE="$(req "$F" -X POST "$BASE_URL/api/patient/login" -H "$CT" -d "$DATA")"
assert_eq "$CODE" "400" "T24-empty-password-400"

################### 需求3 · /api/patient/me 鉴权矩阵 ###################

section "需求3 · /api/patient/me 鉴权矩阵"

F="$TMP_DIR/me_ok.json"
CODE="$(req "$F" "$BASE_URL/api/patient/me" -H "Authorization: Bearer $TOKEN")"
assert_eq "$CODE" "200" "T25-me-with-token-200"
assert_eq "$(jq_get '.data.username' "$F")" "$NEW_USER" "T25b-me-correct-user"
assert_eq "$(jq_get '.data.password' "$F")" "null" "T25c-me-no-password"

F="$TMP_DIR/me_none.json"
CODE="$(req "$F" "$BASE_URL/api/patient/me")"
assert_eq "$CODE" "401" "T26-me-no-token-401"

F="$TMP_DIR/me_tamper.json"
CODE="$(req "$F" "$BASE_URL/api/patient/me" -H "Authorization: Bearer ${TOKEN}x")"
assert_eq "$CODE" "401" "T27-me-tampered-token-401"

F="$TMP_DIR/me_noprefix.json"
CODE="$(req "$F" "$BASE_URL/api/patient/me" -H "Authorization: $TOKEN")"
assert_eq "$CODE" "401" "T28-me-missing-bearer-401"

F="$TMP_DIR/me_malformed.json"
CODE="$(req "$F" "$BASE_URL/api/patient/me" -H "Authorization: Bearer invalid.jwt.value")"
assert_eq "$CODE" "401" "T29-me-malformed-token-401"

##################### 白名单反向验证（防误拦截） #####################

section "白名单反向验证（防止误拦截）"

F="$TMP_DIR/wl1.json"
CODE="$(req "$F" "$BASE_URL/api/doctors")"
assert_eq "$CODE" "200" "T30-doctors-public-no-token"

F="$TMP_DIR/wl2.json"
CODE="$(req "$F" "$BASE_URL/api/doctors/active")"
assert_eq "$CODE" "200" "T30b-active-public-no-token"

F="$TMP_DIR/wl3.json"
DATA="{\"username\":\"$NEW_USER\",\"password\":\"secret123\"}"
CODE="$(req "$F" -X POST "$BASE_URL/api/patient/login" -H "$CT" -d "$DATA")"
assert_eq "$CODE" "200" "T31-login-public-no-token"

################################ 汇总 ################################

printf "\n${YELLOW}════════════════════════════════════════${RESET}\n"
TOTAL=$((PASS + FAIL))
printf "总计 %d 项 | ${GREEN}通过 %d${RESET} | ${RED}失败 %d${RESET}\n" "$TOTAL" "$PASS" "$FAIL"
printf "通过率：%s%%\n" "$(( TOTAL > 0 ? PASS * 100 / TOTAL : 0 ))"

if [ "$FAIL" -gt 0 ]; then
  printf "\n失败用例：\n"
  for c in "${FAILED_CASES[@]}"; do
    printf "  - %s\n" "$c"
  done
  exit 1
fi

printf "\n${GREEN}全部用例通过${RESET}\n"
exit 0
