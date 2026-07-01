#!/usr/bin/env bash
# QA Live Healthcare - API curl 测试脚本
# 用法: ./docs/run-api-tests.sh
# 环境变量: BASE_URL (默认 http://localhost:8080)

set -u

BASE_URL="${BASE_URL:-http://localhost:8080}"
REGISTER_USER="test-patient-$(date +%s)"

PASS=0
FAIL=0
SKIP=0

print_header() {
  echo ""
  echo "========================================"
  echo "$1"
  echo "========================================"
}

# run_test <id> <name> <expected_code> <curl_args...>
run_test() {
  local id="$1"
  local name="$2"
  local expected="$3"
  shift 3

  local body_file
  body_file=$(mktemp)
  local http_code
  http_code=$(curl -s -o "$body_file" -w "%{http_code}" "$@")

  local result="PASS"
  if [ "$http_code" != "$expected" ]; then
    result="FAIL"
    FAIL=$((FAIL + 1))
  else
    PASS=$((PASS + 1))
  fi

  printf "[%s] %s - %s (expected HTTP %s, got HTTP %s)\n" "$result" "$id" "$name" "$expected" "$http_code"
  if [ "$result" = "FAIL" ]; then
    echo "  Response: $(head -c 500 "$body_file")"
  fi
  rm -f "$body_file"
}

print_header "QA Live Healthcare API Tests"
echo "BASE_URL: $BASE_URL"
echo "Register test user: $REGISTER_USER"

# --- TC-01: Health Check ---
run_test "TC-01" "健康检查" "200" \
  "$BASE_URL/actuator/health"

# --- TC-02: CORS GET ---
run_test "TC-02" "CORS GET" "200" \
  "$BASE_URL/api/test/cors"

# --- TC-03: Get All Doctors ---
run_test "TC-03" "获取全部医生列表" "200" \
  "$BASE_URL/api/doctors"

# --- TC-04: Get Active Doctors ---
run_test "TC-04" "获取在线医生列表" "200" \
  "$BASE_URL/api/doctors?active=true"

# --- TC-05: Get Doctor By Username ---
run_test "TC-05" "按用户名查询医生" "200" \
  "$BASE_URL/api/doctors/username/dr-zhang-wei"

# --- TC-06: Doctor Not Found ---
run_test "TC-06" "医生不存在" "404" \
  "$BASE_URL/api/doctors/username/not-exist-user"

# --- TC-07: Doctor Login Success ---
run_test "TC-07" "医生登录成功" "200" \
  -X POST "$BASE_URL/api/doctors/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"dr-zhang-wei","password":"123456"}'

# --- TC-08: Doctor Login Failure (wrong password) ---
run_test "TC-08" "医生登录失败-密码错误" "401" \
  -X POST "$BASE_URL/api/doctors/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"dr-zhang-wei","password":"wrong-password"}'

# --- TC-09: Doctor Login Failure (unknown user) ---
run_test "TC-09" "医生登录失败-用户不存在" "401" \
  -X POST "$BASE_URL/api/doctors/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"unknown-user","password":"123456"}'

# --- TC-10: CORS POST ---
run_test "TC-10" "CORS POST" "200" \
  -X POST "$BASE_URL/api/test/cors" \
  -H "Content-Type: application/json" \
  -d '{"test":"hello"}'

# --- TC-11: Patient Login Success ---
run_test "TC-11" "患者登录成功" "200" \
  -X POST "$BASE_URL/api/patients/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"zhaoming","password":"123456"}'

# --- TC-12: Patient Login Failure ---
run_test "TC-12" "患者登录失败" "401" \
  -X POST "$BASE_URL/api/patients/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"zhaoming","password":"wrong-password"}'

# --- TC-13: Patient Register Success ---
run_test "TC-13" "患者注册成功" "200" \
  -X POST "$BASE_URL/api/patients/register" \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"$REGISTER_USER\",\"password\":\"123456\",\"name\":\"测试用户\",\"birthday\":\"1992-01-01\",\"gender\":\"男\"}"

# --- TC-14: Patient Register Duplicate ---
run_test "TC-14" "患者注册失败-用户名重复" "400" \
  -X POST "$BASE_URL/api/patients/register" \
  -H "Content-Type: application/json" \
  -d '{"username":"zhaoming","password":"123456","name":"重复用户"}'

print_header "Summary"
TOTAL=$((PASS + FAIL + SKIP))
echo "Total:  $TOTAL"
echo "Passed: $PASS"
echo "Failed: $FAIL"
echo "Skipped: $SKIP"
if [ "$TOTAL" -gt 0 ]; then
  echo "Pass rate: $(( PASS * 100 / TOTAL ))%"
fi

if [ "$FAIL" -gt 0 ]; then
  echo ""
  echo "Some tests failed. Ensure MySQL and qa-service-user are running."
  exit 1
fi

echo ""
echo "All tests passed."
exit 0
