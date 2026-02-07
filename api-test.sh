#!/bin/bash

# API测试脚本 - 医生列表页面API测试

BASE_URL="http://localhost:8081/api/doctors"

echo "=== 医生列表API测试 ==="
echo "测试时间: $(date)"
echo "API基础地址: $BASE_URL"
echo ""

# 测试1: 获取所有医生
echo "1. 测试获取所有医生:"
echo "请求: GET $BASE_URL"
curl -s -X GET "$BASE_URL" -H "Accept: application/json"
echo ""
echo ""

# 测试2: 获取在线医生
echo "2. 测试获取在线医生:"
echo "请求: GET $BASE_URL/active"
curl -s -X GET "$BASE_URL/active" -H "Accept: application/json"
echo ""
echo ""

# 测试3: 根据ID获取医生
echo "3. 测试根据ID获取医生:"
echo "请求: GET $BASE_URL/doc001"
curl -s -X GET "$BASE_URL/doc001" -H "Accept: application/json"
echo ""
echo ""

# 测试4: 根据用户名获取医生
echo "4. 测试根据用户名获取医生:"
echo "请求: GET $BASE_URL/username/dr-zhang-wei"
curl -s -X GET "$BASE_URL/username/dr-zhang-wei" -H "Accept: application/json"
echo ""
echo ""

# 测试5: 初始化医生数据
echo "5. 测试初始化医生数据:"
echo "请求: POST $BASE_URL/init"
curl -s -X POST "$BASE_URL/init" -H "Content-Type: application/json" -H "Accept: application/json"
echo ""
echo ""

echo "=== 测试完成 ==="
