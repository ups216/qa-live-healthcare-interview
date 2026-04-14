@echo off
echo ========================================
echo   QA-Service-User 后端服务启动
echo ========================================
echo.

REM 检查是否在项目根目录
if not exist "server\qa-service-user\pom.xml" (
    echo ❌ 错误：请确保在项目根目录运行此脚本
    pause
    exit /b 1
)

echo 正在启动后端服务...
echo 使用环境:
echo - Java版本: 17 (使用set-java17.bat设置环境)
echo - Spring Boot: 3.5.7
echo - 数据库: H2内存数据库
echo.

cd server\qa-service-user
echo 正在编译和运行Spring Boot应用...
echo 这可能需要几分钟时间...
echo.

.\mvnw.cmd spring-boot:run

echo.
echo 如果看到错误，请检查：
echo 1. Java是否已正确安装 (java -version)
echo 2. Maven Wrapper是否正常工作
echo 3. 8080端口是否被占用
echo.