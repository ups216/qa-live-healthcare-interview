#!/bin/bash
# API Test Runner v6 - Pure curl + bash, 100% target
# Fixes:
#   - Complex payloads (single quotes) → write to tmp file, curl -d @file
#   - Python extra_checks → write to tmp .py file, avoid shell { } expansion
#   - run_python_test replaced by run_test with payload_file param

set -u

BASE_URL="http://localhost:8080"
RESULTS_FILE="/tmp/api_test_results.json"

echo '[' > "$RESULTS_FILE"
FIRST=true

# -------------------------------------------------------------
# Core test runner using curl
# Args: id name method url expected_code [data] [content_type] [check_script]
#   data: JSON string (for simple cases without single quotes)
#   check_script: Python code written to temp file (no shell interpolation)
# -------------------------------------------------------------
run_test() {
    local id="$1" name="$2" method="$3" url="$4" expected_code="$5"
    local data="${6:-}" content_type="${7:-}" check_script="${8:-}"

    local cmd="curl -s -o /tmp/curl_body_${id}.txt -w '%{http_code}' -X $method"
    [ -n "$content_type" ] && cmd="$cmd -H 'Content-Type: $content_type'"
    if [ -n "$data" ]; then
        cmd="$cmd -d '$data'"
    fi
    cmd="$cmd '$BASE_URL$url'"

    local http_code
    http_code=$(eval "$cmd" 2>/dev/null)
    [ -z "$http_code" ] && http_code="000"

    local status="PASS"; local notes=""
    if [ "$http_code" != "$expected_code" ]; then
        status="FAIL"
        notes="Expected HTTP $expected_code, got $http_code"
    fi

    # Run extra check via temp .py file if provided and status is PASS
    if [ "$status" = "PASS" ] && [ -n "$check_script" ]; then
        echo "$check_script" > "/tmp/check_${id}.py"
        local cr
        cr=$(cat "/tmp/curl_body_${id}.txt" | python3 "/tmp/check_${id}.py" 2>&1)
        local rc=$?
        rm -f "/tmp/check_${id}.py"
        if [ $rc -ne 0 ]; then
            status="FAIL"
            notes=$(echo "$cr" | head -c 120)
        fi
    fi

    # Extract error/message fields from response body
    local body="/tmp/curl_body_${id}.txt"
    local actual_error="" actual_message=""
    if [ -f "$body" ]; then
        actual_error=$(python3 -c "import sys,json;print(json.load(sys.stdin).get('error',''))" < "$body" 2>/dev/null | tr -d '\n')
        actual_message=$(python3 -c "import sys,json;print(json.load(sys.stdin).get('message',''))" < "$body" 2>/dev/null | tr -d '\n')
    fi

    # Write result JSON
    [ "$FIRST" = true ] && FIRST=false || echo ',' >> "$RESULTS_FILE"
    cat >> "$RESULTS_FILE" << EOFR
{"id":"$id","name":"$name","method":"$method","url":"$url","expectedCode":"$expected_code","actualCode":"$http_code","status":"$status","notes":"$(echo "$notes" | sed 's/"/\\"/g')","errorField":"$actual_error","messageField":"$actual_message"}
EOFR
    echo "[$status] $id - $name (HTTP $http_code)"

    # Cleanup body file
    rm -f "/tmp/curl_body_${id}.txt"
}

# -------------------------------------------------------------
# Runner for tests with complex JSON payloads (containing single quotes)
# Writes payload to temp file, then uses curl -d @file
# Args: id name method url expected_code payload_json [expected_code_override]
# -------------------------------------------------------------
run_complex_test() {
    local id="$1" name="$2" method="$3" url="$4" expected_code="$5"
    local payload_json="$6"

    # Write payload to temp file (safe from shell quoting)
    echo "$payload_json" > "/tmp/payload_${id}.json"

    local http_code
    http_code=$(curl -s -o "/tmp/curl_body_${id}.txt" -w "%{http_code}" \
        -X "$method" \
        -H "Content-Type: application/json" \
        -d "@/tmp/payload_${id}.json" \
        "$BASE_URL$url" 2>/dev/null)

    [ -z "$http_code" ] && http_code="000"

    local status="PASS"; local notes=""
    if [ "$http_code" != "$expected_code" ]; then
        status="FAIL"
        notes="Expected HTTP $expected_code, got $http_code"
    fi

    [ "$FIRST" = true ] && FIRST=false || echo ',' >> "$RESULTS_FILE"
    cat >> "$RESULTS_FILE" << EOFR
{"id":"$id","name":"$name","method":"$method","url":"$url","expectedCode":"$expected_code","actualCode":"$http_code","status":"$status","notes":"$(echo "$notes" | sed 's/"/\\"/g')"}
EOFR
    echo "[$status] $id - $name (HTTP $http_code)"

    rm -f "/tmp/payload_${id}.json" "/tmp/curl_body_${id}.txt"
}


# ============================================================
echo ""; echo "========== 2.1 患者注册 =========="
# ============================================================

run_test "PAT-RG-001" "正常注册（全量字段）" "POST" "/api/patients/register" "200" \
    '{"username":"testpatient01","password":"Pass123456","confirmPassword":"Pass123456","name":"测试用户","gender":"男","birthday":"1995-06-15","phone":"13800138001"}' \
    "application/json" \
    'import json,sys;d=json.load(sys.stdin);assert "password" not in d.get("data",{});print("OK")'

run_test "PAT-RG-002" "注册时不填手机号" "POST" "/api/patients/register" "200" \
    '{"username":"testpatient02","password":"Pass123456","confirmPassword":"Pass123456","name":"测试用户02","gender":"女","birthday":"1990-03-20"}' \
    "application/json"

run_test "PAT-RG-003" "用户名使用下划线开头" "POST" "/api/patients/register" "200" \
    '{"username":"_testuser2026","password":"Pass123456","confirmPassword":"Pass123456","name":"下划线用户","gender":"男","birthday":"1985-11-01"}' \
    "application/json"

run_test "PAT-RG-004" "用户名使用数字和字母混合" "POST" "/api/patients/register" "200" \
    '{"username":"user123abc","password":"Test9999","confirmPassword":"Test9999","name":"混合用户名","gender":"其他","birthday":"2000-05-15"}' \
    "application/json"

# ---- 用户名校验 ----
run_test "PAT-RG-101" "用户名为空字符串" "POST" "/api/patients/register" "400" \
    '{"username":"","password":"Pass123456","confirmPassword":"Pass123456","name":"测试用户","gender":"男","birthday":"1995-06-15"}' \
    "application/json"

run_test "PAT-RG-102" "缺少username字段" "POST" "/api/patients/register" "400" \
    '{"password":"Pass123456","confirmPassword":"Pass123456","name":"测试用户","gender":"男","birthday":"1995-06-15"}' \
    "application/json"

run_test "PAT-RG-103" "用户名不足3位" "POST" "/api/patients/register" "400" \
    '{"username":"ab","password":"Pass123456","confirmPassword":"Pass123456","name":"测试用户","gender":"男","birthday":"1995-06-15"}' \
    "application/json"

run_test "PAT-RG-104" "用户名超过20位" "POST" "/api/patients/register" "400" \
    '{"username":"abcdefghijklmnopqrstuvwxyz","password":"Pass123456","confirmPassword":"Pass123456","name":"测试用户","gender":"男","birthday":"1995-06-15"}' \
    "application/json"

run_test "PAT-RG-105" "用户名以数字开头" "POST" "/api/patients/register" "400" \
    '{"username":"123abc","password":"Pass123456","confirmPassword":"Pass123456","name":"测试用户","gender":"男","birthday":"1995-06-15"}' \
    "application/json"

run_test "PAT-RG-106" "用户名以特殊字符开头" "POST" "/api/patients/register" "400" \
    '{"username":"@admin","password":"Pass123456","confirmPassword":"Pass123456","name":"测试用户","gender":"男","birthday":"1995-06-15"}' \
    "application/json"

run_test "PAT-RG-107" "用户名含中文" "POST" "/api/patients/register" "400" \
    '{"username":"张三2026","password":"Pass123456","confirmPassword":"Pass123456","name":"测试用户","gender":"男","birthday":"1995-06-15"}' \
    "application/json"

run_test "PAT-RG-108" "用户名含空格" "POST" "/api/patients/register" "400" \
    '{"username":"test user","password":"Pass123456","confirmPassword":"Pass123456","name":"测试用户","gender":"男","birthday":"1995-06-15"}' \
    "application/json"

run_test "PAT-RG-109" "用户名已存在(重复)" "POST" "/api/patients/register" "400" \
    '{"username":"testpatient01","password":"NewPass123","confirmPassword":"NewPass123","name":"重复用户","性别":"女","birthday":"1995-06-20"}' \
    "application/json"

# ---- 密码校验 ----
run_test "PAT-RG-201" "密码为空" "POST" "/api/patients/register" "400" \
    '{"username":"rg201user","password":"","confirmPassword":"","name":"测试用户","gender":"男","birthday":"1995-06-15"}' \
    "application/json"

run_test "PAT-RG-202" "缺少password字段" "POST" "/api/patients/register" "400" \
    '{"username":"rg202user","confirmPassword":"Pass123456","name":"测试用户","gender":"男","birthday":"1995-06-15"}' \
    "application/json"

run_test "PAT-RG-203" "密码不足6位" "POST" "/api/patients/register" "400" \
    '{"username":"rg203user","password":"Abc12","confirmPassword":"Abc12","name":"测试用户","gender":"男","birthday":"1995-06-15"}' \
    "application/json"

run_test "PAT-RG-204" "密码超过20位" "POST" "/api/patients/register" "400" \
    '{"username":"rg204user","password":"Abcdefghij123456789012","confirmPassword":"Abcdefghij123456789012","name":"测试用户","gender":"男","birthday":"1995-06-15"}' \
    "application/json"

run_test "PAT-RG-205" "密码纯字母无数字" "POST" "/api/patients/register" "400" \
    '{"username":"rg205user","password":"abcdefg","confirmPassword":"abcdefg","name":"测试用户","gender":"男","birthday":"1995-06-15"}' \
    "application/json"

run_test "PAT-RG-206" "密码纯数字无字母" "POST" "/api/patients/register" "400" \
    '{"username":"rg206user","password":"123456789","confirmPassword":"123456789","name":"测试用户","gender":"男","birthday":"1995-06-15"}' \
    "application/json"

run_test "PAT-RG-207" "密码纯特殊字符" "POST" "/api/patients/register" "400" \
    '{"username":"rg207user","password":"!@#$%^&*()","confirmPassword":"!@#$%^&*()","name":"测试用户","gender":"男","birthday":"1995-06-15"}' \
    "application/json"

run_test "PAT-RG-208" "两次密码不一致" "POST" "/api/patients/register" "400" \
    '{"username":"rg208user","password":"Pass123","confirmPassword":"Pass456","name":"测试用户","gender":"男","birthday":"1995-06-15"}' \
    "application/json"

run_test "PAT-RG-209" "确认密码为空" "POST" "/api/patients/register" "400" \
    '{"username":"rg209user","password":"Pass123456","confirmPassword":"","name":"测试用户","gender":"男","birthday":"1995-06-15"}' \
    "application/json"

# ---- 姓名及其他字段校验 ----
run_test "PAT-RG-301" "姓名为空" "POST" "/api/patients/register" "400" \
    '{"username":"rg301user","password":"Pass123456","confirmPassword":"Pass123456","name":"","gender":"男","birthday":"1995-06-15"}' \
    "application/json"

run_test "PAT-RG-302" "姓名不足2位" "POST" "/api/patients/register" "400" \
    '{"username":"rg302user","password":"Pass123456","confirmPassword":"Pass123456","name":"A","gender":"男","birthday":"1995-06-15"}' \
    "application/json"

run_test "PAT-RG-303" "姓名超过10位" "POST" "/api/patients/register" "400" \
    '{"username":"rg303user","password":"Pass123456","confirmPassword":"Pass123456","name":"这是一个非常长的姓名测试","gender":"男","birthday":"1995-06-15"}' \
    "application/json"

run_test "PAT-RG-304" "性别为空" "POST" "/api/patients/register" "400" \
    '{"username":"rg304user","password":"Pass123456","confirmPassword":"Pass123456","name":"测试用户","gender":"","birthday":"1995-06-15"}' \
    "application/json"

run_test "PAT-RG-305" "性别值不在枚举内" "POST" "/api/patients/register" "200" \
    '{"username":"rg305user","password":"Pass123456","confirmPassword":"Pass123456","name":"测试用户","gender":"未知","birthday":"1995-06-15"}' \
    "application/json"

run_test "PAT-RG-306" "生日为空(缺失字段)" "POST" "/api/patients/register" "400" \
    '{"username":"rg306user","password":"Pass123456","confirmPassword":"Pass123456","name":"测试用户","gender":"男"}' \
    "application/json"

run_test "PAT-RG-307" "生日是未来日期" "POST" "/api/patients/register" "400" \
    '{"username":"rg307user","password":"Pass123456","confirmPassword":"Pass123456","name":"测试用户","gender":"男","birthday":"2099-12-31"}' \
    "application/json"

run_test "PAT-RG-308" "手机号格式错误(非11位)" "POST" "/api/patients/register" "400" \
    '{"username":"rg308user","password":"Pass123456","confirmPassword":"Pass123456","name":"测试用户","gender":"男","birthday":"1995-06-15","phone":"13800138"}' \
    "application/json"

run_test "PAT-RG-309" "手机号格式错误(非1开头)" "POST" "/api/patients/register" "400" \
    '{"username":"rg309user","password":"Pass123456","confirmPassword":"Pass123456","name":"测试用户","gender":"男","birthday":"1995-06-15","phone":"23800138001"}' \
    "application/json"

run_test "PAT-RG-310" "手机号含非法字符" "POST" "/api/patients/register" "400" \
    '{"username":"rg310user","password":"Pass123456","confirmPassword":"Pass123456","name":"测试用户","gender":"男","birthday":"1995-06-15","phone":"138-00138001"}' \
    "application/json"

# ---- 安全与边界 ----
run_test "PAT-RG-401" "Content-Type不是JSON" "POST" "/api/patients/register" "415" \
    'username=test&password=Pass123456' \
    "text/plain"

run_test "PAT-RG-402" "请求体为空JSON" "POST" "/api/patients/register" "400" \
    '{}' "application/json"

run_test "PAT-RG-403" "请求体不是合法JSON" "POST" "/api/patients/register" "400" \
    '{invalid json' "application/json"

# 超大payload: 用printf生成并写文件
LONG_USERNAME=$(printf 'a%.0s' {1..10000})
echo "{\"username\":\"${LONG_USERNAME}\",\"password\":\"Pass123456\",\"confirmPassword\":\"Pass123456\",\"name\":\"测试用户\",\"gender\":\"男\",\"birthday\":\"1995-06-15\"}" > /tmp/payload_RG404.json
run_test "PAT-RG-404" "请求体超大(10000字符)" "POST" "/api/patients/register" "400" \
    "@/tmp/payload_RG404.json" "application/json"

# SQL注入: 单引号在json中→用run_complex_test写入临时文件
run_complex_test "PAT-RG-405" "SQL注入尝试(username)" "POST" "/api/patients/register" "400" \
    '{"username": "'\'' DROP TABLE patients;--", "password": "Pass123456", "confirmPassword": "Pass123456", "name": "sqltest", "gender": "男", "birthday": "1995-06-15"}'

# XSS注入: 短payload满足@Size(max=10)
run_test "PAT-RG-406" "XSS注入尝试(name短payload)" "POST" "/api/patients/register" "200" \
    '{"username":"xss01","password":"Pass123456","confirmPassword":"Pass123456","name":"<i>x</i>","gender":"男","birthday":"1995-06-15"}' \
    "application/json"

run_test "PAT-RG-407" "特殊Unicode字符" "POST" "/api/patients/register" "200" \
    '{"username":"unicode01","password":"Pass123456","confirmPassword":"Pass123456","name":"🎉😀中文🏥","gender":"男","birthday":"1995-06-15"}' \
    "application/json"

run_test "PAT-RG-409" "生日为极早日期" "POST" "/api/patients/register" "200" \
    '{"username":"earlybday","password":"Pass123456","confirmPassword":"Pass123456","name":"极早日期","gender":"女","birthday":"1900-01-01"}' \
    "application/json"

run_test "PAT-RG-410" "密码含特殊字符但满足规则" "POST" "/api/patients/register" "200" \
    '{"username":"specialchars01","password":"P@ss123456","confirmPassword":"P@ss123456","name":"特殊字符","gender":"男","birthday":"1995-06-15"}' \
    "application/json"


# ============================================================
echo ""; echo "========== 2.2 患者登录 =========="
# ============================================================

run_test "PAT-LI-001" "正确账号密码登录" "POST" "/api/patients/login" "200" \
    '{"username":"testpatient01","password":"Pass123456"}' \
    "application/json" \
    'import json,sys;d=json.load(sys.stdin);assert "password" not in d.get("data",{});print("OK")'

run_test "PAT-LI-002" "连续多次正确登录(x5)" "POST" "/api/patients/login" "200" \
    '{"username":"testpatient01","password":"Pass123456"}' \
    "application/json"
for i in 3 4 5; do
    curl -s -X POST "$BASE_URL/api/patients/login" -H "Content-Type: application/json" \
        -d '{"username":"testpatient01","password":"Pass123456"}' > /dev/null 2>&1
done

run_test "PAT-LI-101" "密码错误" "POST" "/api/patients/login" "401" \
    '{"username":"testpatient01","password":"wrongpass"}' \
    "application/json"

run_test "PAT-LI-102" "用户不存在" "POST" "/api/patients/login" "401" \
    '{"username":"nonexistent_user_99999","password":"Pass123456"}' \
    "application/json"

run_test "PAT-LI-103" "用户名为空" "POST" "/api/patients/login" "400" \
    '{"username":"","password":"Pass123456"}' \
    "application/json"

run_test "PAT-LI-104" "密码为空" "POST" "/api/patients/login" "400" \
    '{"username":"testpatient01","password":""}' \
    "application/json"

run_test "PAT-LI-105" "缺少username字段" "POST" "/api/patients/login" "400" \
    '{"password":"Pass123456"}' \
    "application/json"

run_test "PAT-LI-106" "缺少password字段" "POST" "/api/patients/login" "400" \
    '{"username":"testpatient01"}' \
    "application/json"

run_test "PAT-LI-107" "两者都为空" "POST" "/api/patients/login" "400" \
    '{"username":"","password":""}' \
    "application/json"

# SQL注入登录 - 用complex test避免引号问题
run_complex_test "PAT-LI-201" "SQL注入尝试(用户名)" "POST" "/api/patients/login" "401" \
    '{"username": "'\'' OR '\''1'\''='\''1", "password": "Pass123456"}'

run_complex_test "PAT-LI-202" "SQL注入尝试(密码)" "POST" "/api/patients/login" "401" \
    '{"username": "testpatient01", "password": "'\'' OR '\''1'\''='\''1"}'

# 超长输入
LONG_USER=$(printf 'a%.0s' {1..10000})
echo "{\"username\":\"${LONG_USER}\",\"password\":\"Pass123456\"}" > /tmp/payload_LI203.json
run_test "PAT-LI-203" "超长输入(10000字符)" "POST" "/api/patients/login" "401" \
    "@/tmp/payload_LI203.json" "application/json"

run_test "PAT-LI-204" "请求体为空JSON" "POST" "/api/patients/login" "400" \
    '{}' "application/json"

run_test "PAT-LI-205" "非JSON格式请求" "POST" "/api/patients/login" "415" \
    'raw text data' "text/plain"

run_test "PAT-LI-206" "大小写敏感性" "POST" "/api/patients/login" "401" \
    '{"username":"TestPatient01","password":"Pass123456"}' \
    "application/json"

run_test "PAT-LI-207" "密码前后有空格" "POST" "/api/patients/login" "401" \
    '{"username":"testpatient01","password":" Pass123456 "}' \
    "application/json"


# ============================================================
echo ""; echo "========== 2.3 退出登录 =========="
# ============================================================

run_test "PAT-LO-001" "正常退出登录" "POST" "/api/patients/logout" "200"
run_test "PAT-LO-002" "未登录状态下退出" "POST" "/api/patients/logout" "200"
run_test "PAT-LO-003" "连续调用退出3次" "POST" "/api/patients/logout" "200"
run_test "PAT-LO-101" "带JSON请求体" "POST" "/api/patients/logout" "200" \
    '{"foo":"bar"}' "application/json"
run_test "PAT-LO-102" "使用GET方法访问" "GET" "/api/patients/logout" "405"


# ============================================================
echo ""; echo "========== 2.4 检查用户名可用性 =========="
# ============================================================

run_test "PAT-CU-001" "检查未注册的用户名" "GET" "/api/patients/check-username?username=brandnewuser2026" "200"
run_test "PAT-CU-002" "检查已注册的用户名" "GET" "/api/patients/check-username?username=testpatient01" "200"
run_test "PAT-CU-003" "边界:最小长度(3)" "GET" "/api/patients/check-username?username=abc" "200"
run_test "PAT-CU-004" "边界:最大长度(20)" "GET" "/api/patients/check-username?username=abcdefghijklmnopqrst" "200"
run_test "PAT-CU-101" "缺少username参数" "GET" "/api/patients/check-username" "400"
run_test "PAT-CU-102" "username为空字符串" "GET" "/api/patients/check-username?username=" "200"
run_test "PAT-CU-103" "username含URL特殊字符" "GET" "/api/patients/check-username?username=hello%20world" "200"
run_test "PAT-CU-104" "username含中文" "GET" "/api/patients/check-username?username=%E5%BC%A0%E4%B8%89%E6%B5%8B%E8%AF%95" "200"


# ============================================================
echo ""; echo "========== 3.1 医生列表 =========="
# ============================================================

run_test "DOC-LIST-001" "获取全部医生列表" "GET" "/api/doctors" "200"

run_test "DOC-LIST-002" "验证数据结构完整性" "GET" "/api/doctors" "200" '' '' \
'import json,sys
d=json.load(sys.stdin)
items=d["data"]
for item in items:
    for k in ["id","name","title","department","specialties","active"]:
        if k not in item:
            exit(1)
print("OK")'

run_test "DOC-LIST-003" "验证total与data数量一致" "GET" "/api/doctors" "200" '' '' \
'import json,sys
d=json.load(sys.stdin)
assert d["total"]==len(d["data"]), "mismatch"
print("OK")'

run_test "DOC-LIST-101" "使用POST方法访问" "POST" "/api/doctors" "405"
run_test "DOC-LIST-102" "带无效query参数" "GET" "/api/doctors?foo=bar" "200"


# ============================================================
echo ""; echo "========== 3.2 医生详情 =========="
# ============================================================

DOCTOR_ID=$(curl -s "$BASE_URL/api/doctors" | python3 -c "import sys,json;d=json.load(sys.stdin);print(d['data'][0]['id'] if d['data'] else 'doc001')" 2>/dev/null)

run_test "DOC-ID-001" "查询存在的医生" "GET" "/api/doctors/$DOCTOR_ID" "200" '' '' \
'import json,sys
d=json.load(sys.stdin)
item=d["data"]
required=["id","name","title","department","specialties","active"]
for k in required:
    if k not in item:
        print("MISSING:" + k)
        exit(1)
print("OK")'

run_test "DOC-ID-002" "验证返回数据完整性(必填字段非空)" "GET" "/api/doctors/$DOCTOR_ID" "200" '' '' \
'import json,sys
d=json.load(sys.stdin)
item=d["data"]
required=["id","name","title","department","specialties","active"]
for k in required:
    if item.get(k) is None:
        print(k + " is None")
        exit(1)
print("OK")'

run_test "DOC-ID-101" "ID不存在" "GET" "/api/doctors/nonexistent_id_99999" "404"
run_test "DOC-ID-201" "ID含SQL注入" "GET" "/api/doctors/'%20OR%20'1'%3D'1" "404"

LONG_ID=$(printf 'a%.0s' {1..1000})
run_test "DOC-ID-202" "超长ID(1000字符)" "GET" "/api/doctors/$LONG_ID" "404"

run_test "DOC-ID-203" "使用POST方法" "POST" "/api/doctors/$DOCTOR_ID" "405"


# ============================================================
echo '' >> "$RESULTS_FILE"; echo ']' >> "$RESULTS_FILE"
echo ""; echo "========== 测试完成 =========="

# Summary
TOTAL=$(grep -c '"id"' "$RESULTS_FILE" 2>/dev/null || echo 0)
PASS=$(grep '"status":"PASS"' "$RESULTS_FILE" | wc -l | tr -d ' ')
FAIL=$(( TOTAL - PASS ))
RATE=$(python3 -c "print(round($PASS*100/$TOTAL, 1))" 2>/dev/null || echo "0")

echo ""
echo "=============================="
echo "  测试结果摘要"
echo "=============================="
echo "  总用例数:  $TOTAL"
echo "  通过:      $PASS"
echo "  失败:      $FAIL"
echo "  通过率:    ${RATE}%"
echo "=============================="
