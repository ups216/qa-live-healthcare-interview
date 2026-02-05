#!/usr/bin/env bash
# 医生列表 API 测试脚本（GET /api/doctors）
# 使用方式：先启动 MySQL（docker-compose up -d）与 qa-service-user，再执行 bash scripts/test-doctor-api.sh
# 可选依赖：jq（用于校验 JSON 结构），无 jq 时仅校验状态码与 body 非空

set -e

BASE_URL="${BASE_URL:-http://localhost:8080}"
ENDPOINT="${BASE_URL}/api/doctors"

echo "=== 测试 GET ${ENDPOINT} ==="

# 1. 校验 HTTP 状态码为 200
HTTP_CODE=$(curl -s -o /tmp/doctors_response.json -w "%{http_code}" "$ENDPOINT")
if [ "$HTTP_CODE" != "200" ]; then
  echo "FAIL: 期望 HTTP 200，实际 ${HTTP_CODE}"
  exit 1
fi
echo "OK: HTTP 200"

# 2. 校验 body 非空
if [ ! -s /tmp/doctors_response.json ]; then
  echo "FAIL: 响应 body 为空"
  exit 1
fi
echo "OK: 响应 body 非空"

# 3. 可选：使用 jq 校验为数组、不含 password（空数组时跳过首项检查）
if command -v jq &>/dev/null; then
  if ! jq -e 'type == "array"' /tmp/doctors_response.json >/dev/null 2>&1; then
    echo "FAIL: 响应不是 JSON 数组"
    exit 1
  fi
  LEN=$(jq 'length' /tmp/doctors_response.json)
  echo "OK: JSON 数组，长度 ${LEN}"
  if [ "$LEN" -gt 0 ]; then
    if jq -e '.[0] | has("password")' /tmp/doctors_response.json >/dev/null 2>&1; then
      echo "WARN: 响应中含 password 字段，建议列表 API 不返回"
    else
      echo "OK: 响应中无 password 字段"
    fi
  fi
else
  echo "（未安装 jq，跳过 JSON 结构校验；可安装 jq 后重新执行）"
fi

echo "=== 全部检查通过 ==="
