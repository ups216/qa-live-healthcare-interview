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