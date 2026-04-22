#!/bin/bash
# API接口测试脚本
# 基于 docs/PRD_PATIENT_LOGIN.md 测试规范

# 配置
BASE_URL="http://localhost:8080"

echo "========================================"
echo "  QA Live Healthcare API 测试脚本"
echo "========================================"
echo ""

# ========================================
# 1. 获取医生列表接口测试
# ========================================
echo "----------------------------------------"
echo "【测试1】获取医生列表接口"
echo "----------------------------------------"
echo "请求: GET $BASE_URL/api/doctors"
echo ""

response=$(curl -s -w "\n%{http_code}" "$BASE_URL/api/doctors")
http_code=$(echo "$response" | tail -n 1)
body=$(echo "$response" | sed '$d')

echo "HTTP状态码: $http_code"
echo "响应内容:"
echo "$body" | head -50
echo ""

if [ "$http_code" = "200" ]; then
    echo "✅ 测试通过 - 成功获取医生列表"
else
    echo "❌ 测试失败 - HTTP状态码: $http_code"
fi
echo ""

# ========================================
# 2. 患者登录接口测试
# ========================================
echo "========================================"
echo "【测试2】患者登录接口测试"
echo "========================================"
echo ""

# 测试数据 (基于 PRD 12.1 测试账号)
# 密码均为: 123456

# TC-PL-01: 正确用户名密码登录
echo "----------------------------------------"
echo "【TC-PL-01】正确用户名密码登录"
echo "----------------------------------------"
echo "请求: POST $BASE_URL/api/patients/login"
echo "参数: username=patient001, password=123456"
echo ""

response=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/patients/login" \
    -H "Content-Type: application/json" \
    -d '{"username":"patient001","password":"123456"}')
http_code=$(echo "$response" | tail -n 1)
body=$(echo "$response" | sed '$d')

echo "HTTP状态码: $http_code"
echo "响应内容:"
echo "$body"
echo ""

if [ "$http_code" = "200" ]; then
    echo "✅ TC-PL-01 测试通过 - 登录成功"
else
    echo "❌ TC-PL-01 测试失败 - HTTP状态码: $http_code"
fi
echo ""

# TC-PL-02: 错误密码登录
echo "----------------------------------------"
echo "【TC-PL-02】错误密码登录"
echo "----------------------------------------"
echo "请求: POST $BASE_URL/api/patients/login"
echo "参数: username=patient001, password=wrongpassword"
echo ""

response=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/patients/login" \
    -H "Content-Type: application/json" \
    -d '{"username":"patient001","password":"wrongpassword"}')
http_code=$(echo "$response" | tail -n 1)
body=$(echo "$response" | sed '$d')

echo "HTTP状态码: $http_code"
echo "响应内容:"
echo "$body"
echo ""

if [ "$http_code" = "401" ]; then
    echo "✅ TC-PL-02 测试通过 - 密码错误返回401"
else
    echo "❌ TC-PL-02 测试失败 - HTTP状态码: $http_code"
fi
echo ""

# TC-PL-03: 不存在用户登录
echo "----------------------------------------"
echo "【TC-PL-03】不存在用户登录"
echo "----------------------------------------"
echo "请求: POST $BASE_URL/api/patients/login"
echo "参数: username=nonexist, password=123456"
echo ""

response=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/patients/login" \
    -H "Content-Type: application/json" \
    -d '{"username":"nonexist","password":"123456"}')
http_code=$(echo "$response" | tail -n 1)
body=$(echo "$response" | sed '$d')

echo "HTTP状态码: $http_code"
echo "响应内容:"
echo "$body"
echo ""

if [ "$http_code" = "401" ]; then
    echo "✅ TC-PL-03 测试通过 - 用户不存在返回401"
else
    echo "❌ TC-PL-03 测试失败 - HTTP状态码: $http_code"
fi
echo ""

# TC-PL-04: 空用户名登录
echo "----------------------------------------"
echo "【TC-PL-04】空用户名登录"
echo "----------------------------------------"
echo "请求: POST $BASE_URL/api/patients/login"
echo "参数: username=(空), password=123456"
echo ""

response=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/patients/login" \
    -H "Content-Type: application/json" \
    -d '{"username":"","password":"123456"}')
http_code=$(echo "$response" | tail -n 1)
body=$(echo "$response" | sed '$d')

echo "HTTP状态码: $http_code"
echo "响应内容:"
echo "$body"
echo ""

if [ "$http_code" = "400" ] || [ "$http_code" = "401" ]; then
    echo "✅ TC-PL-04 测试通过 - 空用户名被拒绝"
else
    echo "❌ TC-PL-04 测试失败 - HTTP状态码: $http_code"
fi
echo ""

# TC-PL-05: 空密码登录
echo "----------------------------------------"
echo "【TC-PL-05】空密码登录"
echo "----------------------------------------"
echo "请求: POST $BASE_URL/api/patients/login"
echo "参数: username=patient001, password=(空)"
echo ""

response=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/patients/login" \
    -H "Content-Type: application/json" \
    -d '{"username":"patient001","password":""}')
http_code=$(echo "$response" | tail -n 1)
body=$(echo "$response" | sed '$d')

echo "HTTP状态码: $http_code"
echo "响应内容:"
echo "$body"
echo ""

if [ "$http_code" = "400" ] || [ "$http_code" = "401" ]; then
    echo "✅ TC-PL-05 测试通过 - 空密码被拒绝"
else
    echo "❌ TC-PL-05 测试失败 - HTTP状态码: $http_code"
fi
echo ""

# 测试其他有效账号
echo "----------------------------------------"
echo "【扩展测试】测试其他有效账号"
echo "----------------------------------------"
for patient in "patient002" "patient003" "patient004" "patient005"; do
    echo "测试账号: $patient / 123456"
    response=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/patients/login" \
        -H "Content-Type: application/json" \
        -d "{\"username\":\"$patient\",\"password\":\"123456\"}")
    http_code=$(echo "$response" | tail -n 1)
    
    if [ "$http_code" = "200" ]; then
        echo "✅ $patient 登录成功"
    else
        echo "❌ $patient 登录失败 (HTTP $http_code)"
    fi
    echo ""
done

echo "========================================"
echo "  测试完成"
echo "========================================"
