@echo off
echo ========================================
echo   医疗健康咨询平台 - 开发环境启动脚本
echo ========================================
echo.

REM 检查是否在前端目录
if not exist "web\qa-web\package.json" (
    echo ❌ 错误：请确保在项目根目录运行此脚本
    pause
    exit /b 1
)

echo 1. 启动前端开发服务器...
cd web\qa-web
start cmd /k "npm run dev"
cd ..\..

echo.
echo 2. 前端服务将在 http://localhost:5173 启动
echo.

echo 3. 启动后端服务 (可选，需要Java 17)
echo    如果需要API功能，请手动启动后端：
echo    cd server\qa-service-user
echo    .\mvnw spring-boot:run
echo.

echo 4. API测试页面：file:///%CD%\api-test.html
echo.

echo 5. 开发指南：file:///%CD%\DEVELOPMENT_GUIDE.md
echo.

echo ✅ 启动完成！
echo.
echo 按任意键打开前端页面...
pause >nul

REM 打开前端页面
start http://localhost:5173