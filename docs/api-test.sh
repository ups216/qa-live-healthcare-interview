#!/bin/bash

# API 测试脚本

# 测试获取所有医生列表
echo "测试获取所有医生列表..."
curl -X GET "http://localhost:8080/api/doctors" -H "Content-Type: application/json"
echo "\n"

# 测试获取活跃的医生列表
echo "测试获取活跃的医生列表..."
curl -X GET "http://localhost:8080/api/doctors/active" -H "Content-Type: application/json"
echo "\n"

# 测试根据部门获取医生列表
echo "测试根据部门获取医生列表..."
curl -X GET "http://localhost:8080/api/doctors/department/心内科" -H "Content-Type: application/json"
echo "\n"

# 测试根据ID获取医生详情
echo "测试根据ID获取医生详情..."
curl -X GET "http://localhost:8080/api/doctors/doc001" -H "Content-Type: application/json"
echo "\n"

# 测试根据用户名获取医生详情
echo "测试根据用户名获取医生详情..."
curl -X GET "http://localhost:8080/api/doctors/username/dr-zhang-wei" -H "Content-Type: application/json"
echo "\n"
