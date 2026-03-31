#!/bin/bash
# API Test Script for Doctors List
# 用于测试医生列表页面的 API 接口

BASE_URL="http://localhost:8080/api"

echo "========================================"
echo "  医生列表 API 测试脚本"
echo "========================================"
echo ""

# 测试 1: 获取所有医生（中文）
echo ">>> 测试 1: 获取所有医生（中文）"
echo "URL: ${BASE_URL}/doctors?locale=zh-CN"
echo ""
curl -s -X GET "${BASE_URL}/doctors?locale=zh-CN" | jq '.'
echo ""
echo "----------------------------------------"
echo ""

# 测试 2: 获取所有医生（英文）
echo ">>> 测试 2: 获取所有医生（英文）"
echo "URL: ${BASE_URL}/doctors?locale=en-US"
echo ""
curl -s -X GET "${BASE_URL}/doctors?locale=en-US" | jq '.'
echo ""
echo "----------------------------------------"
echo ""

# 测试 3: 获取活跃医生（中文）
echo ">>> 测试 3: 获取活跃医生（中文）"
echo "URL: ${BASE_URL}/doctors/active?locale=zh-CN"
echo ""
curl -s -X GET "${BASE_URL}/doctors/active?locale=zh-CN" | jq '.'
echo ""
echo "----------------------------------------"
echo ""

# 测试 4: 获取活跃医生（英文）
echo ">>> 测试 4: 获取活跃医生（英文）"
echo "URL: ${BASE_URL}/doctors/active?locale=en-US"
echo ""
curl -s -X GET "${BASE_URL}/doctors/active?locale=en-US" | jq '.'
echo ""
echo "----------------------------------------"
echo ""

# 测试 5: 根据用户名获取医生（中文）
echo ">>> 测试 5: 根据用户名获取医生 dr-wang-qiang（中文）"
echo "URL: ${BASE_URL}/doctors/dr-wang-qiang?locale=zh-CN"
echo ""
curl -s -X GET "${BASE_URL}/doctors/dr-wang-qiang?locale=zh-CN" | jq '.'
echo ""
echo "----------------------------------------"
echo ""

# 测试 6: 根据用户名获取医生（英文）
echo ">>> 测试 6: 根据用户名获取医生 dr-wang-qiang（英文）"
echo "URL: ${BASE_URL}/doctors/dr-wang-qiang?locale=en-US"
echo ""
curl -s -X GET "${BASE_URL}/doctors/dr-wang-qiang?locale=en-US" | jq '.'
echo ""
echo "----------------------------------------"
echo ""

# 测试 7: 获取不存在的医生（404 测试）
echo ">>> 测试 7: 获取不存在的医生（404 测试）"
echo "URL: ${BASE_URL}/doctors/dr-not-exist?locale=zh-CN"
echo ""
curl -s -w "\nHTTP Status: %{http_code}\n" -X GET "${BASE_URL}/doctors/dr-not-exist?locale=zh-CN"
echo ""
echo "----------------------------------------"
echo ""

# 测试 8: 获取主任医师的英文翻译
echo ">>> 测试 8: 验证王强医生的 title 字段英文翻译"
echo "URL: ${BASE_URL}/doctors/dr-wang-qiang?locale=en-US"
echo ""
curl -s -X GET "${BASE_URL}/doctors/dr-wang-qiang?locale=en-US" | jq '{name: .name, title: .title, department: .department, experience: .experience, specialties: .specialties}'
echo ""
echo "========================================"
echo "  测试完成"
echo "========================================"
