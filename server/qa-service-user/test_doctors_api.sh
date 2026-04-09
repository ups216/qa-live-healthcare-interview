#!/usr/bin/env bash
# =============================================================
# test_doctors_api.sh
# QA Medical — Doctors API curl 测试用例脚本
# 服务: qa-service-user  Base URL: http://localhost:8080/api
# =============================================================

set -euo pipefail

# ── 配置 ──────────────────────────────────────────────────────
BASE_URL="${BASE_URL:-http://localhost:8080/api}"
PASS=0
FAIL=0
TOTAL=0

# ── 颜色 ──────────────────────────────────────────────────────
GREEN="\033[0;32m"
RED="\033[0;31m"
YELLOW="\033[1;33m"
CYAN="\033[0;36m"
RESET="\033[0m"

# ── 工具函数 ──────────────────────────────────────────────────

# 打印分隔线
separator() {
  echo -e "${CYAN}──────────────────────────────────────────────────────${RESET}"
}

# run_test <TEST_ID> <DESC> <EXPECTED_HTTP_CODE> <CURL_ARGS...>
run_test() {
  local test_id="$1"; shift
  local desc="$1";    shift
  local expected="$1"; shift

  TOTAL=$((TOTAL + 1))
  separator
  echo -e "${YELLOW}[${test_id}]${RESET} ${desc}"
  echo -e "  Expected HTTP: ${expected}"

  # 执行 curl，写 body 到临时文件，取 HTTP 状态码
  local body_file
  body_file=$(mktemp)
  local actual_code
  actual_code=$(curl -s -o "$body_file" -w "%{http_code}" "$@")
  local body
  body=$(cat "$body_file")
  rm -f "$body_file"

  echo -e "  Actual   HTTP: ${actual_code}"

  # 美化输出 JSON（python3 兜底）
  local pretty
  pretty=$(echo "$body" | python3 -m json.tool 2>/dev/null || echo "$body")
  echo -e "  Response Body:\n$(echo "$pretty" | sed 's/^/    /')"

  if [[ "$actual_code" == "$expected" ]]; then
    echo -e "  ${GREEN}✓ PASS${RESET}"
    PASS=$((PASS + 1))
  else
    echo -e "  ${RED}✗ FAIL  (expected=${expected}, got=${actual_code})${RESET}"
    FAIL=$((FAIL + 1))
  fi
}

# assert_field <TEST_ID> <DESC> <JSON_STRING> <FIELD_PATH> <EXPECTED_VALUE>
# 使用 python3 做简单 JSON 断言
assert_field() {
  local test_id="$1"
  local desc="$2"
  local json="$3"
  local field="$4"   # 支持简单路径，如 .code 或 .data[0].id
  local expected="$5"

  TOTAL=$((TOTAL + 1))
  local actual
  actual=$(echo "$json" | python3 -c \
    "import sys,json; d=json.load(sys.stdin); print(eval('d${field}'))" 2>/dev/null || echo "__PARSE_ERROR__")

  if [[ "$actual" == "$expected" ]]; then
    echo -e "  ${GREEN}✓ ASSERT PASS${RESET} [${test_id}] ${desc}: ${field} == '${expected}'"
    PASS=$((PASS + 1))
  else
    echo -e "  ${RED}✗ ASSERT FAIL${RESET} [${test_id}] ${desc}: ${field} expected='${expected}' got='${actual}'"
    FAIL=$((FAIL + 1))
  fi
}

# =============================================================
echo ""
echo -e "${CYAN}╔══════════════════════════════════════════════════════╗${RESET}"
echo -e "${CYAN}║     QA Medical — Doctors API 自动化测试               ║${RESET}"
echo -e "${CYAN}║     Target: ${BASE_URL}${RESET}"
echo -e "${CYAN}╚══════════════════════════════════════════════════════╝${RESET}"
echo ""

# =============================================================
# TC-01  GET /doctors — 查询全部医生列表
# =============================================================
RESPONSE=$(curl -s -w "\n%{http_code}" \
  -H "Accept: application/json" \
  "${BASE_URL}/doctors")
BODY=$(echo "$RESPONSE" | head -n -1)
CODE=$(echo "$RESPONSE" | tail -n 1)

TOTAL=$((TOTAL + 1))
separator
echo -e "${YELLOW}[TC-01]${RESET} GET /doctors — 查询全部医生列表"
echo -e "  Expected HTTP: 200"
echo -e "  Actual   HTTP: ${CODE}"
echo -e "  Response Body:\n$(echo "$BODY" | python3 -m json.tool 2>/dev/null | sed 's/^/    /')"
if [[ "$CODE" == "200" ]]; then
  echo -e "  ${GREEN}✓ PASS${RESET}"
  PASS=$((PASS + 1))
else
  echo -e "  ${RED}✗ FAIL${RESET}"
  FAIL=$((FAIL + 1))
fi
assert_field "TC-01-A" "响应 code 字段为 200"  "$BODY" "['code']"        "200"
assert_field "TC-01-B" "返回医生总数为 5"       "$BODY" "['data'].__len__()" "5"

# =============================================================
# TC-02  GET /doctors?isActive=true — 仅查询在线医生
# =============================================================
RESPONSE=$(curl -s -w "\n%{http_code}" \
  -H "Accept: application/json" \
  "${BASE_URL}/doctors?isActive=true")
BODY=$(echo "$RESPONSE" | head -n -1)
CODE=$(echo "$RESPONSE" | tail -n 1)

TOTAL=$((TOTAL + 1))
separator
echo -e "${YELLOW}[TC-02]${RESET} GET /doctors?isActive=true — 仅查询在线医生"
echo -e "  Expected HTTP: 200"
echo -e "  Actual   HTTP: ${CODE}"
echo -e "  Response Body:\n$(echo "$BODY" | python3 -m json.tool 2>/dev/null | sed 's/^/    /')"
if [[ "$CODE" == "200" ]]; then
  echo -e "  ${GREEN}✓ PASS${RESET}"
  PASS=$((PASS + 1))
else
  echo -e "  ${RED}✗ FAIL${RESET}"
  FAIL=$((FAIL + 1))
fi
assert_field "TC-02-A" "响应 code 字段为 200"       "$BODY" "['code']"                        "200"
assert_field "TC-02-B" "在线医生数量为 4"             "$BODY" "['data'].__len__()"              "4"
assert_field "TC-02-C" "第一条记录 isActive 为 True" "$BODY" "['data'][0]['isActive']"         "True"

# =============================================================
# TC-03  GET /doctors?isActive=false — 仅查询离线医生
# =============================================================
RESPONSE=$(curl -s -w "\n%{http_code}" \
  -H "Accept: application/json" \
  "${BASE_URL}/doctors?isActive=false")
BODY=$(echo "$RESPONSE" | head -n -1)
CODE=$(echo "$RESPONSE" | tail -n 1)

TOTAL=$((TOTAL + 1))
separator
echo -e "${YELLOW}[TC-03]${RESET} GET /doctors?isActive=false — 仅查询离线医生"
echo -e "  Expected HTTP: 200"
echo -e "  Actual   HTTP: ${CODE}"
echo -e "  Response Body:\n$(echo "$BODY" | python3 -m json.tool 2>/dev/null | sed 's/^/    /')"
if [[ "$CODE" == "200" ]]; then
  echo -e "  ${GREEN}✓ PASS${RESET}"
  PASS=$((PASS + 1))
else
  echo -e "  ${RED}✗ FAIL${RESET}"
  FAIL=$((FAIL + 1))
fi
assert_field "TC-03-A" "响应 code 字段为 200"        "$BODY" "['code']"               "200"
assert_field "TC-03-B" "离线医生数量为 1"              "$BODY" "['data'].__len__()"    "1"
assert_field "TC-03-C" "离线医生 username 正确"        "$BODY" "['data'][0]['username']" "dr-liu-min"

# =============================================================
# TC-04  GET /doctors/{id} — 按 ID 查询医生详情（正常）
# =============================================================
RESPONSE=$(curl -s -w "\n%{http_code}" \
  -H "Accept: application/json" \
  "${BASE_URL}/doctors/doc001")
BODY=$(echo "$RESPONSE" | head -n -1)
CODE=$(echo "$RESPONSE" | tail -n 1)

TOTAL=$((TOTAL + 1))
separator
echo -e "${YELLOW}[TC-04]${RESET} GET /doctors/doc001 — 按 ID 查询医生详情"
echo -e "  Expected HTTP: 200"
echo -e "  Actual   HTTP: ${CODE}"
echo -e "  Response Body:\n$(echo "$BODY" | python3 -m json.tool 2>/dev/null | sed 's/^/    /')"
if [[ "$CODE" == "200" ]]; then
  echo -e "  ${GREEN}✓ PASS${RESET}"
  PASS=$((PASS + 1))
else
  echo -e "  ${RED}✗ FAIL${RESET}"
  FAIL=$((FAIL + 1))
fi
assert_field "TC-04-A" "data.id 正确"         "$BODY" "['data']['id']"         "doc001"
assert_field "TC-04-B" "data.name 正确"       "$BODY" "['data']['name']"       "张伟医生"
assert_field "TC-04-C" "data.department 正确" "$BODY" "['data']['department']" "心内科"
assert_field "TC-04-D" "password 不在响应中"  "$BODY" "'password' not in d.get('data',{})" "True"

# =============================================================
# TC-05  GET /doctors/{id} — 查询不存在的 ID（异常）
# =============================================================
run_test "TC-05" \
  "GET /doctors/doc999 — 查询不存在的 ID，期望 404" \
  "404" \
  -H "Accept: application/json" \
  "${BASE_URL}/doctors/doc999"

# =============================================================
# TC-06  GET /doctors/u/{username} — 按 username 查询（正常）
# =============================================================
RESPONSE=$(curl -s -w "\n%{http_code}" \
  -H "Accept: application/json" \
  "${BASE_URL}/doctors/u/dr-li-na")
BODY=$(echo "$RESPONSE" | head -n -1)
CODE=$(echo "$RESPONSE" | tail -n 1)

TOTAL=$((TOTAL + 1))
separator
echo -e "${YELLOW}[TC-06]${RESET} GET /doctors/u/dr-li-na — 按 username 查询医生"
echo -e "  Expected HTTP: 200"
echo -e "  Actual   HTTP: ${CODE}"
echo -e "  Response Body:\n$(echo "$BODY" | python3 -m json.tool 2>/dev/null | sed 's/^/    /')"
if [[ "$CODE" == "200" ]]; then
  echo -e "  ${GREEN}✓ PASS${RESET}"
  PASS=$((PASS + 1))
else
  echo -e "  ${RED}✗ FAIL${RESET}"
  FAIL=$((FAIL + 1))
fi
assert_field "TC-06-A" "data.username 正确"    "$BODY" "['data']['username']"   "dr-li-na"
assert_field "TC-06-B" "data.department 正确"  "$BODY" "['data']['department']" "儿科"
assert_field "TC-06-C" "specialties 为列表"    "$BODY" "isinstance(d['data']['specialties'], list)" "True"

# =============================================================
# TC-07  GET /doctors/u/{username} — 查询不存在的 username（异常）
# =============================================================
run_test "TC-07" \
  "GET /doctors/u/dr-unknown — 查询不存在的 username，期望 404" \
  "404" \
  -H "Accept: application/json" \
  "${BASE_URL}/doctors/u/dr-unknown"

# =============================================================
# TC-08  响应头验证 — Content-Type 必须为 application/json
# =============================================================
separator
TOTAL=$((TOTAL + 1))
echo -e "${YELLOW}[TC-08]${RESET} 响应头 Content-Type 验证"
CT=$(curl -s -I "${BASE_URL}/doctors" | grep -i "^content-type" | tr -d '\r')
echo -e "  Content-Type Header: ${CT}"
if echo "$CT" | grep -qi "application/json"; then
  echo -e "  ${GREEN}✓ PASS${RESET}"
  PASS=$((PASS + 1))
else
  echo -e "  ${RED}✗ FAIL — Content-Type 不含 application/json${RESET}"
  FAIL=$((FAIL + 1))
fi

# =============================================================
# TC-09  CORS 头验证 — 允许跨域
# =============================================================
separator
TOTAL=$((TOTAL + 1))
echo -e "${YELLOW}[TC-09]${RESET} CORS 响应头验证（Access-Control-Allow-Origin）"
CORS=$(curl -s -I -H "Origin: http://localhost:5173" "${BASE_URL}/doctors" \
       | grep -i "access-control-allow-origin" | tr -d '\r')
echo -e "  CORS Header: ${CORS:-<not found>}"
if [[ -n "$CORS" ]]; then
  echo -e "  ${GREEN}✓ PASS${RESET}"
  PASS=$((PASS + 1))
else
  echo -e "  ${RED}✗ FAIL — 缺少 Access-Control-Allow-Origin 响应头${RESET}"
  FAIL=$((FAIL + 1))
fi

# =============================================================
# TC-10  非法 isActive 参数 — 服务应返回 400 或容错处理
# =============================================================
run_test "TC-10" \
  "GET /doctors?isActive=invalid — 非法参数，期望 400" \
  "400" \
  -H "Accept: application/json" \
  "${BASE_URL}/doctors?isActive=invalid"

# =============================================================
# 汇总
# =============================================================
separator
echo ""
echo -e "${CYAN}╔══════════════════════════════════════════════╗${RESET}"
echo -e "${CYAN}║               测试结果汇总                    ║${RESET}"
echo -e "${CYAN}╠══════════════════════════════════════════════╣${RESET}"
echo -e "${CYAN}║${RESET}  Total : ${TOTAL}"
echo -e "${CYAN}║${RESET}  ${GREEN}Pass  : ${PASS}${RESET}"
echo -e "${CYAN}║${RESET}  ${RED}Fail  : ${FAIL}${RESET}"
echo -e "${CYAN}╚══════════════════════════════════════════════╝${RESET}"
echo ""

if [[ $FAIL -gt 0 ]]; then
  exit 1
else
  exit 0
fi
