@echo off
echo ========================================
echo   QA-Service-User 后端服务启动 (Java 17)
echo ========================================
echo.

REM 第一步：设置Java 17环境
echo 1. 设置Java 17环境变量...
call set-java17.bat
if %ERRORLEVEL% neq 0 (
    echo ❌ Java 17环境设置失败
    pause
    exit /b 1
)

echo.
echo 2. 验证Java版本...
java -version

echo.
echo 3. 启动后端服务...
cd server\qa-service-user
echo 正在编译和运行Spring Boot应用...
echo 这可能需要几分钟时间...
echo.

.\mvnw.cmd spring-boot:run

echo.
echo 如果看到错误，请检查：
echo 1. Java 17是否正确安装
echo 2. Maven Wrapper是否正常工作  
echo 3. 8080端口是否被占用
echo.
pause