#!/bin/bash

# 医生API测试脚本
BASE_URL="http://localhost:8080/api/doctors"

echo "=== 医生API测试脚本 ==="
echo "测试环境: $BASE_URL"
echo ""

# 测试1: 获取所有医生
echo "测试1: 获取所有医生"
echo "请求: GET $BASE_URL"
echo "响应:"
curl -s "$BASE_URL"
echo ""
echo "----------------------------------------"

# 测试2: 按姓名查询
echo "测试2: 按姓名查询 (张伟)"
echo "请求: GET $BASE_URL/conditions?name=张伟"
echo "响应:"
curl -s "$BASE_URL/conditions?name=张伟"
echo ""
echo "----------------------------------------"

# 测试3: 按科室查询
echo "测试3: 按科室查询 (心内科)"
echo "请求: GET $BASE_URL/conditions?department=心内科"
echo "响应:"
curl -s "$BASE_URL/conditions?department=心内科"
echo ""
echo "----------------------------------------"

# 测试4: 按职称查询
echo "测试4: 按职称查询 (主任医师)"
echo "请求: GET $BASE_URL/conditions?title=主任医师"
echo "响应:"
curl -s "$BASE_URL/conditions?title=主任医师"
echo ""
echo "----------------------------------------"

# 测试5: 按在线状态查询
echo "测试5: 按在线状态查询 (在线)"
echo "请求: GET $BASE_URL/conditions?isActive=true"
echo "响应:"
curl -s "$BASE_URL/conditions?isActive=true"
echo ""
echo "----------------------------------------"

# 测试6: 多条件组合查询
echo "测试6: 多条件组合查询 (心内科 + 主任医师)"
echo "请求: GET $BASE_URL/conditions?department=心内科&title=主任医师"
echo "响应:"
curl -s "$BASE_URL/conditions?department=心内科&title=主任医师"
echo ""
echo "----------------------------------------"

# 测试7: 获取所有科室
echo "测试7: 获取所有科室"
echo "请求: GET $BASE_URL/departments"
echo "响应:"
curl -s "$BASE_URL/departments"
echo ""
echo "----------------------------------------"

# 测试8: 获取所有职称
echo "测试8: 获取所有职称"
echo "请求: GET $BASE_URL/titles"
echo "响应:"
curl -s "$BASE_URL/titles"
echo ""
echo "----------------------------------------"

# 测试9: 根据ID获取医生
echo "测试9: 根据ID获取医生 (doc001)"
echo "请求: GET $BASE_URL/doc001"
echo "响应:"
curl -s "$BASE_URL/doc001"
echo ""
echo "----------------------------------------"

# 测试10: 边界条件测试 - 不存在的医生
echo "测试10: 边界条件测试 - 不存在的医生"
echo "请求: GET $BASE_URL/invalid-id"
echo "响应:"
curl -s -w "\nHTTP状态码: %{http_code}\n" "$BASE_URL/invalid-id"
echo ""
echo "----------------------------------------"

echo "=== 测试完成 ==="
