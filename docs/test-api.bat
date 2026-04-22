@echo off
chcp 65001 >nul
REM API接口测试脚本 (Windows版本)
REM 基于 docs/PRD_PATIENT_LOGIN.md 测试规范

set BASE_URL=http://localhost:8080

echo ========================================
echo   QA Live Healthcare API 测试脚本
echo ========================================
echo.

REM ========================================
REM 1. 获取医生列表接口测试
REM ========================================
echo ----------------------------------------
echo 【测试1】获取医生列表接口
echo ----------------------------------------
echo 请求: GET %BASE_URL%/api/doctors
echo.

curl -s -w "\nHTTP状态码: %%{http_code}\n" "%BASE_URL%/api/doctors"
echo.

REM ========================================
REM 2. 患者登录接口测试
REM ========================================
echo ========================================
echo 【测试2】患者登录接口测试
echo ========================================
echo.

REM TC-PL-01: 正确用户名密码登录
echo ----------------------------------------
echo 【TC-PL-01】正确用户名密码登录
echo ----------------------------------------
echo 请求: POST %BASE_URL%/api/patients/login
echo 参数: username=patient001, password=123456
echo.

curl -s -w "\nHTTP状态码: %%{http_code}\n" -X POST "%BASE_URL%/api/patients/login" ^
    -H "Content-Type: application/json" ^
    -d "{\"username\":\"patient001\",\"password\":\"123456\"}"
echo.

REM TC-PL-02: 错误密码登录
echo ----------------------------------------
echo 【TC-PL-02】错误密码登录
echo ----------------------------------------
echo 请求: POST %BASE_URL%/api/patients/login
echo 参数: username=patient001, password=wrongpassword
echo.

curl -s -w "\nHTTP状态码: %%{http_code}\n" -X POST "%BASE_URL%/api/patients/login" ^
    -H "Content-Type: application/json" ^
    -d "{\"username\":\"patient001\",\"password\":\"wrongpassword\"}"
echo.

REM TC-PL-03: 不存在用户登录
echo ----------------------------------------
echo 【TC-PL-03】不存在用户登录
echo ----------------------------------------
echo 请求: POST %BASE_URL%/api/patients/login
echo 参数: username=nonexist, password=123456
echo.

curl -s -w "\nHTTP状态码: %%{http_code}\n" -X POST "%BASE_URL%/api/patients/login" ^
    -H "Content-Type: application/json" ^
    -d "{\"username\":\"nonexist\",\"password\":\"123456\"}"
echo.

REM TC-PL-04: 空用户名登录
echo ----------------------------------------
echo 【TC-PL-04】空用户名登录
echo ----------------------------------------
echo 请求: POST %BASE_URL%/api/patients/login
echo 参数: username=(空), password=123456
echo.

curl -s -w "\nHTTP状态码: %%{http_code}\n" -X POST "%BASE_URL%/api/patients/login" ^
    -H "Content-Type: application/json" ^
    -d "{\"username\":\"\",\"password\":\"123456\"}"
echo.

REM TC-PL-05: 空密码登录
echo ----------------------------------------
echo 【TC-PL-05】空密码登录
echo ----------------------------------------
echo 请求: POST %BASE_URL%/api/patients/login
echo 参数: username=patient001, password=(空)
echo.

curl -s -w "\nHTTP状态码: %%{http_code}\n" -X POST "%BASE_URL%/api/patients/login" ^
    -H "Content-Type: application/json" ^
    -d "{\"username\":\"patient001\",\"password\":\"\"}"
echo.

REM 扩展测试
echo ----------------------------------------
echo 【扩展测试】测试其他有效账号
echo ----------------------------------------

for %%p in (patient002 patient003 patient004 patient005) do (
    echo 测试账号: %%p / 123456
    curl -s -w "HTTP状态码: %%{http_code}\n" -X POST "%BASE_URL%/api/patients/login" ^
        -H "Content-Type: application/json" ^
        -d "{\"username\":\"%%p\",\"password\":\"123456\"}"
    echo.
)

echo ========================================
echo   测试完成
echo ========================================
pause
