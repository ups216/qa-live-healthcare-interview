#!/bin/bash

# QA Service User API 测试脚本
# 测试医生列表相关的 API

BASE_URL="http://localhost:8081/api/doctors"

echo "=== QA Service User API 测试 ==="
echo "基础 URL: $BASE_URL"
echo ""

# 测试 1: 获取所有医生列表
echo "测试 1: 获取所有医生列表"
echo "请求: GET $BASE_URL"
response=$(curl -s -w "%{http_code}" -X GET "$BASE_URL" -H "Content-Type: application/json")
http_code="${response: -3}"
body="${response%???}"

echo "状态码: $http_code"
echo "响应内容:"
echo "$body" | jq '.' 2>/dev/null || echo "$body"
echo ""

# 测试 2: 根据科室获取医生
echo "测试 2: 根据科室获取医生（心血管内科）"
echo "请求: GET $BASE_URL/department/心血管内科"
response=$(curl -s -w "%{http_code}" -X GET "$BASE_URL/department/心血管内科" -H "Content-Type: application/json")
http_code="${response: -3}"
body="${response%???}"

echo "状态码: $http_code"
echo "响应内容:"
echo "$body" | jq '.' 2>/dev/null || echo "$body"
echo ""

# 测试 3: 按姓名搜索医生
echo "测试 3: 按姓名搜索医生（关键字：张）"
echo "请求: GET $BASE_URL/search?name=张"
response=$(curl -s -w "%{http_code}" -X GET "$BASE_URL/search?name=张" -H "Content-Type: application/json")
http_code="${response: -3}"
body="${response%???}"

echo "状态码: $http_code"
echo "响应内容:"
echo "$body" | jq '.' 2>/dev/null || echo "$body"
echo ""

# 测试 4: 获取特定医生详情
echo "测试 4: 获取特定医生详情（用户名：dr_zhang）"
echo "请求: GET $BASE_URL/dr_zhang"
response=$(curl -s -w "%{http_code}" -X GET "$BASE_URL/dr_zhang" -H "Content-Type: application/json")
http_code="${response: -3}"
body="${response%???}"

echo "状态码: $http_code"
echo "响应内容:"
echo "$body" | jq '.' 2>/dev/null || echo "$body"
echo ""

echo "=== 测试完成 ==="