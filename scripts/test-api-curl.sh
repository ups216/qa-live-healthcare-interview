#!/usr/bin/env bash
#
# 用 curl 测试患者登录相关及问诊页依赖的医生接口。
# 前置：MySQL + qa-service-user 已启动（默认 http://localhost:8080）
#
# 用法:
#   bash scripts/test-api-curl.sh
#   BASE_URL=http://127.0.0.1:8080 bash scripts/test-api-curl.sh
#
set -uo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
TMPDIR="${TMPDIR:-/tmp}"
BODY="${TMPDIR}/qa_api_test_body_$$.json"
REQ="${TMPDIR}/qa_api_test_req_$$.json"
cleanup() { rm -f "$BODY" "${BODY}.code" "$REQ" 2>/dev/null || true; }
trap cleanup EXIT

PASS=0
FAIL=0

http_request() {
  local method="$1"
  local path="$2"
  shift 2
  local url="${BASE_URL}${path}"
  local code
  code=$(curl -sS -o "$BODY" -w "%{http_code}" -X "$method" "$url" "$@") || {
    echo "FAIL curl 请求失败: $method $url"
    FAIL=$((FAIL + 1))
    return 1
  }
  echo "$code"
}

expect_eq() {
  local name="$1"
  local got="$2"
  local want="$3"
  if [[ "$got" == "$want" ]]; then
    echo "PASS  $name  (HTTP $got)"
    PASS=$((PASS + 1))
  else
    echo "FAIL  $name  期望 HTTP $want 实际 HTTP $got"
    if [[ -s "$BODY" ]]; then
      echo "      响应: $(head -c 400 "$BODY"; echo)"
    fi
    FAIL=$((FAIL + 1))
  fi
}

json_field() {
  local key="$1"
  local v=""
  if command -v python3 >/dev/null 2>&1; then
    v=$(python3 -c "import json,sys; d=json.load(open(sys.argv[1],encoding='utf-8')); print(d.get(sys.argv[2],'') or '')" "$BODY" "$key" 2>/dev/null || true)
  elif command -v python >/dev/null 2>&1; then
    v=$(python -c "import json,sys; d=json.load(open(sys.argv[1])); print(d.get(sys.argv[2],'') or '')" "$BODY" "$key" 2>/dev/null || true)
  elif command -v jq >/dev/null 2>&1; then
    v=$(jq -r ".$key // empty" "$BODY" 2>/dev/null || true)
  elif command -v node >/dev/null 2>&1; then
    v=$(node -e "
      const fs = require('fs');
      const j = JSON.parse(fs.readFileSync(process.argv[1], 'utf8'));
      const k = process.argv[2];
      process.stdout.write((j[k] != null && j[k] !== '') ? String(j[k]) : '');
    " "$BODY" "$key" 2>/dev/null || true)
  fi
  if [[ -z "$v" && -s "$BODY" ]]; then
    if [[ "$key" == "token" ]]; then
      v=$(sed -n 's/^.*\"token\"[[:space:]]*:[[:space:]]*\"\([^\"]*\)\".*$/\1/p' "$BODY" | head -1)
    elif [[ "$key" == "username" ]]; then
      v=$(sed -n 's/^.*\"username\"[[:space:]]*:[[:space:]]*\"\([^\"]*\)\".*$/\1/p' "$BODY" | head -1)
    fi
  fi
  printf '%s' "$v"
}

echo "== 基址: $BASE_URL =="

# ----- 医生（问诊页 loadDoctors） -----
code=$(http_request GET "/api/doctors" -H "Accept: application/json")
expect_eq "GET /api/doctors 列表" "$code" "200"

code=$(http_request GET "/api/doctors/dr-zhang-wei" -H "Accept: application/json")
expect_eq "GET /api/doctors/dr-zhang-wei 存在" "$code" "200"

code=$(http_request GET "/api/doctors/nobody-${RANDOM}" -H "Accept: application/json")
expect_eq "GET /api/doctors/{username} 不存在 -> 404" "$code" "404"

# ----- 患者：登录失败（统一 401，不区分用户是否存在） -----
code=$(http_request POST "/api/patients/login" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{"username":"zhaoming","password":"wrong-password"}')
expect_eq "POST /api/patients/login 错误密码 -> 401" "$code" "401"

code=$(http_request POST "/api/patients/login" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{"username":"nonexistent_user_xyz","password":"123456"}')
expect_eq "POST /api/patients/login 不存在用户 -> 401" "$code" "401"

# ----- 患者：登录成功 -----
code=$(http_request POST "/api/patients/login" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{"username":"zhaoming","password":"123456"}')
expect_eq "POST /api/patients/login 演示账号成功 -> 200" "$code" "200"
TOKEN="$(json_field token)"
if [[ -z "$TOKEN" ]]; then
  echo "FAIL  无法从登录响应解析 token（请安装 python3、python、jq 或 node；亦可依赖 sed 单行 JSON 回退）"
  FAIL=$((FAIL + 1))
else
  echo "PASS  登录响应含有效 token（长度 ${#TOKEN}）"
  PASS=$((PASS + 1))
fi

# ----- 患者：/me -----
code=$(http_request GET "/api/patients/me" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer ${TOKEN:-invalid}")
if [[ -n "$TOKEN" ]]; then
  expect_eq "GET /api/patients/me 带 Bearer -> 200" "$code" "200"
  uname="$(json_field username)"
  if [[ "$uname" == "zhaoming" ]]; then
    echo "PASS  /me 返回 username=zhaoming"
    PASS=$((PASS + 1))
  else
    echo "FAIL  /me username 期望 zhaoming 实际 ${uname:-<empty>}"
    FAIL=$((FAIL + 1))
  fi
else
  expect_eq "GET /api/patients/me（跳过：无 token）" "$code" "401"
fi

code=$(http_request GET "/api/patients/me" -H "Accept: application/json")
expect_eq "GET /api/patients/me 无 Authorization -> 401" "$code" "401"

code=$(http_request GET "/api/patients/me" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer not.a.valid.jwt.token")
expect_eq "GET /api/patients/me 无效 JWT -> 401" "$code" "401"

# ----- 患者：注册校验 -----
code=$(http_request POST "/api/patients/register" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{"username":"ab","password":"123456","name":"短用户名"}')
expect_eq "POST /api/patients/register 用户名过短 -> 400" "$code" "400"

code=$(http_request POST "/api/patients/register" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{"username":"validuser","password":"12345","name":"密码过短"}')
expect_eq "POST /api/patients/register 密码 <6 位 -> 400" "$code" "400"

# ----- 患者：注册成功（唯一用户名）与重复注册 -----
# 使用临时文件写入 ASCII JSON，避免 Git Bash/Windows 下 -d 内嵌中文或引号导致请求体非法（400）
NEW_USER="ct_${RANDOM}_$$"
printf '{"username":"%s","password":"123456","name":"CurlReg"}' "$NEW_USER" > "$REQ"
code=$(http_request POST "/api/patients/register" \
  -H "Content-Type: application/json; charset=UTF-8" \
  -H "Accept: application/json" \
  --data-binary "@${REQ}")
expect_eq "POST /api/patients/register 新用户成功 -> 200" "$code" "200"

printf '{"username":"%s","password":"123456","name":"Dup"}' "$NEW_USER" > "$REQ"
code=$(http_request POST "/api/patients/register" \
  -H "Content-Type: application/json; charset=UTF-8" \
  -H "Accept: application/json" \
  --data-binary "@${REQ}")
expect_eq "POST /api/patients/register 用户名重复 -> 409" "$code" "409"

printf '%s' '{"username":"zhaoming","password":"123456","name":"SeedDup"}' > "$REQ"
code=$(http_request POST "/api/patients/register" \
  -H "Content-Type: application/json; charset=UTF-8" \
  -H "Accept: application/json" \
  --data-binary "@${REQ}")
expect_eq "POST /api/patients/register 与种子冲突 -> 409" "$code" "409"

echo ""
echo "---------- 汇总 ----------"
echo "通过: $PASS  失败: $FAIL"
if [[ "$FAIL" -gt 0 ]]; then
  exit 1
fi
exit 0
