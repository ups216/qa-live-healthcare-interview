#!/bin/bash
echo "=== 医疗健康咨询平台API测试 ==="
echo "测试时间: $(date)"
echo "================================"
echo

# 测试医生API
echo "1. 测试医生相关API..."
echo "-------------------"

# 测试获取活跃医生列表
echo "测试: GET /api/doctors/active"
curl -X GET "http://localhost:8080/api/doctors/active" \
  -H "Content-Type: application/json" \
  -w " | HTTP状态码: %{http_code}\n" \
  --silent
echo

# 测试获取所有医生
echo "测试: GET /api/doctors"
curl -X GET "http://localhost:8080/api/doctors" \
  -H "Content-Type: application/json" \
  -w " | HTTP状态码: %{http_code}\n" \
  --silent
echo

# 测试获取医生统计信息
echo "测试: GET /api/doctors/statistics"
curl -X GET "http://localhost:8080/api/doctors/statistics" \
  -H "Content-Type: application/json" \
  -w " | HTTP状态码: %{http_code}\n" \
  --silent
echo

# 测试患者API
echo "2. 测试患者相关API..."
echo "-------------------"

# 测试患者登录（正确凭据）
echo "测试: POST /api/patients/login (正确凭据)"
curl -X POST "http://localhost:8080/api/patients/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"patient001","password":"123456"}' \
  -w " | HTTP状态码: %{http_code}\n" \
  --silent
echo

# 测试患者登录（错误凭据）
echo "测试: POST /api/patients/login (错误凭据)"
curl -X POST "http://localhost:8080/api/patients/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"patient001","password":"wrongpassword"}' \
  -w " | HTTP状态码: %{http_code}\n" \
  --silent
echo

# 测试健康检查
echo "3. 测试健康检查..."
echo "----------------"

# 测试Spring Boot Actuator健康检查
echo "测试: GET /actuator/health"
curl -X GET "http://localhost:8080/actuator/health" \
  -w " | HTTP状态码: %{http_code}\n" \
  --silent
echo

echo "================================"
echo "测试完成!"
echo "注意: 如果API服务未启动，所有测试将失败"
echo "需要先启动Spring Boot应用:"
echo "cd server/qa-service-user && ./mvnw spring-boot:run"
echo "================================"