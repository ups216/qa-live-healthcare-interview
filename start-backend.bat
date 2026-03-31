@echo off
chcp 65001 >nul
echo ============================================
echo Starting Backend Spring Boot Service
echo ============================================
echo.

cd /d %~dp0\server\qa-service-user

:: 创建日志目录
if not exist "logs" mkdir logs

echo Cleaning and compiling...
echo [日志将写入 logs/startup.log]
echo.

:: 执行 mvn clean spring-boot:run，输出重定向到日志文件
call mvn clean spring-boot:run > logs/startup.log 2>&1

echo.
echo ============================================
echo 服务已停止，请查看 logs/startup.log 了解详情
pause
