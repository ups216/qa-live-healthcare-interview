#!/bin/bash

# ===============================================
# API测试脚本 - qa-service-user 医生相关API测试
# 使用curl构建测试用例
# ===============================================

# 检测操作系统并设置相应的curl命令
if [[ "$OSTYPE" == "msys" ]] || [[ "$OSTYPE" == "win32" ]]; then
    CURL_CMD="curl.exe"
    BASE_URL="http://localhost:8081/api/doctors"
else
    CURL_CMD="curl"
    BASE_URL="http://localhost:8081/api/doctors"
fi

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 打印分隔线
print_separator() {
    echo "=================================================="
}

# 打印测试标题
print_test_title() {
    echo -e "${BLUE}测试: $1${NC}"
    echo "URL: $2"
    print_separator
}

# 检查响应状态
check_response_status() {
    local response="$1"
    local expected_status="$2"
    
    if echo "$response" | grep -q "\"status\":\"$expected_status\""; then
        echo -e "${GREEN}✓ 测试通过${NC}"
        return 0
    else
        echo -e "${RED}✗ 测试失败${NC}"
        return 1
    fi
}

# 检查HTTP状态码
check_http_status() {
    local url="$1"
    local expected_code="$2"
    
    http_code=$($CURL_CMD -s -o /dev/null -w "%{http_code}" "$url")
    if [ "$http_code" -eq "$expected_code" ]; then
        echo -e "${GREEN}✓ HTTP状态码正确 ($http_code)${NC}"
        return 0
    else
        echo -e "${RED}✗ HTTP状态码错误 (期望: $expected_code, 实际: $http_code)${NC}"
        return 1
    fi
}

# 测试1: 获取所有医生列表
test_get_all_doctors() {
    print_test_title "获取所有医生列表" "$BASE_URL"
    
    echo "执行curl命令:"
    echo "$CURL_CMD -X GET \"$BASE_URL\""
    
    response=$($CURL_CMD -s -X GET "$BASE_URL")
    echo "响应内容:"
    echo "$response" | python -m json.tool 2>/dev/null || echo "$response"
    
    check_response_status "$response" "success"
    check_http_status "$BASE_URL" 200
}

# 测试2: 获取活跃医生列表
test_get_active_doctors() {
    url="$BASE_URL/active"
    print_test_title "获取活跃医生列表" "$url"
    
    echo "执行curl命令:"
    echo "$CURL_CMD -X GET \"$url\""
    
    response=$($CURL_CMD -s -X GET "$url")
    echo "响应内容:"
    echo "$response" | python -m json.tool 2>/dev/null || echo "$response"
    
    check_response_status "$response" "success"
    check_http_status "$url" 200
}

# 测试3: 根据ID获取医生详情
test_get_doctor_by_id() {
    url="$BASE_URL/doc001"
    print_test_title "根据ID获取医生详情" "$url"
    
    echo "执行curl命令:"
    echo "$CURL_CMD -X GET \"$url\""
    
    response=$($CURL_CMD -s -X GET "$url")
    echo "响应内容:"
    echo "$response" | python -m json.tool 2>/dev/null || echo "$response"
    
    check_response_status "$response" "success"
    check_http_status "$url" 200
}

# 测试4: 根据科室获取医生列表
test_get_doctors_by_department() {
    url="$BASE_URL/department/心内科"
    print_test_title "根据科室获取医生列表" "$url"
    
    echo "执行curl命令:"
    echo "$CURL_CMD -X GET \"$url\""
    
    response=$($CURL_CMD -s -X GET "$url")
    echo "响应内容:"
    echo "$response" | python -m json.tool 2>/dev/null || echo "$response"
    
    check_response_status "$response" "success"
    check_http_status "$url" 200
}

# 测试5: 根据专业领域获取医生列表
test_get_doctors_by_specialty() {
    url="$BASE_URL/specialty/心脏病"
    print_test_title "根据专业领域获取医生列表" "$url"
    
    echo "执行curl命令:"
    echo "curl -X GET \"$url\""
    
    response=$(curl -s -X GET "$url")
    echo "响应内容:"
    echo "$response" | python -m json.tool 2>/dev/null || echo "$response"
    
    check_response_status "$response" "success"
    check_http_status "$url" 200
}

# 测试6: 搜索医生
test_search_doctors() {
    url="$BASE_URL/search?name=张伟&department=心内科"
    print_test_title "搜索医生" "$url"
    
    echo "执行curl命令:"
    echo "curl -X GET \"$url\""
    
    response=$(curl -s -X GET "$url")
    echo "响应内容:"
    echo "$response" | python -m json.tool 2>/dev/null || echo "$response"
    
    check_response_status "$response" "success"
    check_http_status "$url" 200
}

# 测试7: 获取所有科室列表
test_get_all_departments() {
    url="$BASE_URL/departments"
    print_test_title "获取所有科室列表" "$url"
    
    echo "执行curl命令:"
    echo "curl -X GET \"$url\""
    
    response=$(curl -s -X GET "$url")
    echo "响应内容:"
    echo "$response" | python -m json.tool 2>/dev/null || echo "$response"
    
    check_response_status "$response" "success"
    check_http_status "$url" 200
}

# 测试8: 获取所有专业领域列表
test_get_all_specialties() {
    url="$BASE_URL/specialties"
    print_test_title "获取所有专业领域列表" "$url"
    
    echo "执行curl命令:"
    echo "curl -X GET \"$url\""
    
    response=$(curl -s -X GET "$url")
    echo "响应内容:"
    echo "$response" | python -m json.tool 2>/dev/null || echo "$response"
    
    check_response_status "$response" "success"
    check_http_status "$url" 200
}

# 测试9: 测试不存在的医生ID
test_get_nonexistent_doctor() {
    url="$BASE_URL/nonexistent"
    print_test_title "测试不存在的医生ID" "$url"
    
    echo "执行curl命令:"
    echo "curl -X GET \"$url\""
    
    response=$(curl -s -X GET "$url")
    echo "响应内容:"
    echo "$response" | python -m json.tool 2>/dev/null || echo "$response"
    
    # 期望返回错误状态
    if echo "$response" | grep -q "\"status\":\"error\""; then
        echo -e "${GREEN}✓ 测试通过 (正确返回错误信息)${NC}"
        check_http_status "$url" 404
        return 0
    else
        echo -e "${YELLOW}⚠ 测试结果不符合预期${NC}"
        return 1
    fi
}

# 测试10: 测试错误请求
test_invalid_request() {
    url="$BASE_URL/invalid"
    print_test_title "测试错误请求" "$url"
    
    echo "执行curl命令:"
    echo "curl -X GET \"$url\""
    
    response=$(curl -s -X GET "$url")
    echo "响应内容:"
    echo "$response" | python -m json.tool 2>/dev/null || echo "$response"
    
    check_http_status "$url" 404
}

# 主测试函数
main() {
    echo -e "${YELLOW}开始执行 qa-service-user API 测试${NC}"
    echo "基础URL: $BASE_URL"
    echo "测试时间: $(date)"
    print_separator
    
    # 检查后端服务是否可用
    echo "检查后端服务连通性..."
    if $CURL_CMD -s -o /dev/null -w "%{http_code}" "$BASE_URL" | grep -q "200"; then
        echo -e "${GREEN}✓ 后端服务连接正常${NC}"
    else
        echo -e "${RED}✗ 后端服务连接失败，请确保服务正在运行${NC}"
        exit 1
    fi
    
    print_separator
    
    # 执行所有测试
    tests=(
        "test_get_all_doctors"
        "test_get_active_doctors" 
        "test_get_doctor_by_id"
        "test_get_doctors_by_department"
        "test_get_doctors_by_specialty"
        "test_search_doctors"
        "test_get_all_departments"
        "test_get_all_specialties"
        "test_get_nonexistent_doctor"
        "test_invalid_request"
    )
    
    passed=0
    failed=0
    
    for test in "${tests[@]}"; do
        if $test; then
            ((passed++))
        else
            ((failed++))
        fi
        echo
    done
    
    print_separator
    echo -e "${YELLOW}测试结果汇总:${NC}"
    echo -e "${GREEN}通过: $passed${NC}"
    echo -e "${RED}失败: $failed${NC}"
    echo -e "${BLUE}总计: ${#tests[@]}${NC}"
    
    if [ $failed -eq 0 ]; then
        echo -e "${GREEN}🎉 所有测试通过！${NC}"
    else
        echo -e "${RED}❌ 有测试失败，请检查服务状态和API实现${NC}"
    fi
    
    print_separator
    echo "测试完成时间: $(date)"
}

# 执行主函数
main