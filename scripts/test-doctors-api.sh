#!/usr/bin/env bash
# 医生列表 API 测试脚本（GET /api/doctors）
# 使用方式：bash scripts/test-doctors-api.sh
# 前置条件：MySQL 与 qa-service-user 已启动（先 docker-compose up -d，再在 server/qa-service-user 下运行 ./mvnw spring-boot:run）

set -e
BASE_URL="${BASE_URL:-http://localhost:8080}"
ENDPOINT="${BASE_URL}/api/doctors"

echo "=== 测试 GET $ENDPOINT ==="

# 1. 校验 HTTP 状态码为 200
HTTP_CODE=$(curl -s -o /tmp/doctors_response.json -w "%{http_code}" "$ENDPOINT")
if [ "$HTTP_CODE" != "200" ]; then
  echo "失败：期望 HTTP 200，实际 $HTTP_CODE"
  exit 1
fi
echo "HTTP 状态码: $HTTP_CODE"

# 2. 校验响应体为非空且为合法 JSON（无 jq 时用 grep 简单判断）
if [ ! -s /tmp/doctors_response.json ]; then
  echo "失败：响应体为空"
  exit 1
fi
if ! grep -q '^\[' /tmp/doctors_response.json; then
  echo "失败：响应体应为 JSON 数组（以 [ 开头）"
  exit 1
fi
echo "响应体为合法 JSON 数组"

# 3. 可选：若有 jq 则校验不含 password 且为数组
if command -v jq &>/dev/null; then
  if jq -e 'type == "array"' /tmp/doctors_response.json >/dev/null 2>&1; then
    echo "jq 校验：类型为 array，通过"
  fi
  if jq -e '[.[] | has("password")] | any' /tmp/doctors_response.json >/dev/null 2>&1; then
    echo "失败：响应中不应包含 password 字段"
    exit 1
  fi
  echo "jq 校验：未包含 password，通过"
fi

echo "=== 全部检查通过 ==="
