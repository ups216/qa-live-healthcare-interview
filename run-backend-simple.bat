@echo off
echo ========================================
echo   医疗健康咨询平台 - 后端服务启动
echo ========================================
echo.

REM 切换到后端目录
cd server\qa-service-user

echo 1. 下载Maven依赖...
call mvnw.cmd dependency:resolve

echo.
echo 2. 编译项目...
call mvnw.cmd clean compile

echo.
echo 3. 运行Spring Boot应用...
echo   服务将启动在 http://localhost:8080
echo.
call mvnw.cmd spring-boot:run

pause