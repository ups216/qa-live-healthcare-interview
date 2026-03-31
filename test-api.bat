@echo off
chcp 65001 >nul
echo ============================================
echo 医生列表 API 测试脚本
echo ============================================
echo.

set BASE_URL=http://localhost:8080/api/doctors
set LOG_FILE=test-api.log

:: 清空日志文件
echo. > %LOG_FILE%

echo [测试1] 获取所有医生列表（中文）
echo URL: %BASE_URL%
echo. >> %LOG_FILE%
echo [测试1] 获取所有医生列表（中文） >> %LOG_FILE%
echo URL: %BASE_URL% >> %LOG_FILE%
curl -s -X GET "%BASE_URL%?lang=zh" >> %LOG_FILE%
type %LOG_FILE% | findstr "code\|name\|department"
echo.
echo --------------------------------------------
echo.

echo [测试2] 获取所有医生列表（英文）
echo URL: %BASE_URL%?lang=en
echo. >> %LOG_FILE%
echo [测试2] 获取所有医生列表（英文） >> %LOG_FILE%
echo URL: %BASE_URL%?lang=en >> %LOG_FILE%
curl -s -X GET "%BASE_URL%?lang=en" >> %LOG_FILE%
type %LOG_FILE% | findstr "code\|name\|department"
echo.
echo --------------------------------------------
echo.

echo [测试3] 获取在线医生列表（中文）
echo URL: %BASE_URL%/active
echo. >> %LOG_FILE%
echo [测试3] 获取在线医生列表（中文） >> %LOG_FILE%
echo URL: %BASE_URL%/active >> %LOG_FILE%
curl -s -X GET "%BASE_URL%/active?lang=zh" >> %LOG_FILE%
type %LOG_FILE% | findstr "code\|name\|isActive"
echo.
echo --------------------------------------------
echo.

echo [测试4] 获取在线医生列表（英文）
echo URL: %BASE_URL%/active?lang=en
echo. >> %LOG_FILE%
echo [测试4] 获取在线医生列表（英文） >> %LOG_FILE%
echo URL: %BASE_URL%/active?lang=en >> %LOG_FILE%
curl -s -X GET "%BASE_URL%/active?lang=en" >> %LOG_FILE%
type %LOG_FILE% | findstr "code\|name\|isActive"
echo.
echo --------------------------------------------
echo.

echo [测试5] 根据ID获取医生详情
echo URL: %BASE_URL%/doc001
echo. >> %LOG_FILE%
echo [测试5] 根据ID获取医生详情 >> %LOG_FILE%
echo URL: %BASE_URL%/doc001 >> %LOG_FILE%
curl -s -X GET "%BASE_URL%/doc001?lang=zh" >> %LOG_FILE%
type %LOG_FILE% | findstr "code\|id\|name"
echo.
echo --------------------------------------------
echo.

echo [测试6] 医生登录
echo URL: %BASE_URL%/login
echo. >> %LOG_FILE%
echo [测试6] 医生登录 >> %LOG_FILE%
echo URL: %BASE_URL%/login >> %LOG_FILE%
curl -s -X POST "%BASE_URL%/login?lang=zh" -H "Content-Type: application/json" -d "{\"username\":\"dr-zhang-wei\",\"password\":\"123456\"}" >> %LOG_FILE%
type %LOG_FILE% | findstr "code\|username\|name"
echo.
echo --------------------------------------------
echo.

echo [测试7] 医生登录（密码错误）
echo URL: %BASE_URL%/login
echo. >> %LOG_FILE%
echo [测试7] 医生登录（密码错误） >> %LOG_FILE%
echo URL: %BASE_URL%/login >> %LOG_FILE%
curl -s -X POST "%BASE_URL%/login?lang=zh" -H "Content-Type: application/json" -d "{\"username\":\"dr-zhang-wei\",\"password\":\"wrongpassword\"}" >> %LOG_FILE%
type %LOG_FILE% | findstr "code\|message"
echo.
echo ============================================
echo 测试完成
echo 详细日志请查看: %LOG_FILE%
pause
