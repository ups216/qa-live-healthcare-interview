@echo off
echo ========================================
echo   设置Java 17环境变量
echo ========================================
echo.

REM 查找Java 17安装路径
echo 正在查找Java 17安装路径...

REM 检查常见的Java 17安装位置
set FOUND=0

if exist "C:\Program Files\Java\jdk-17" (
    set "JAVA_HOME=C:\Program Files\Java\jdk-17"
    set FOUND=1
) else if exist "C:\Program Files\Java\jdk-17.0.*" (
    for /d %%i in ("C:\Program Files\Java\jdk-17.0.*") do set "JAVA_HOME=%%i"
    set FOUND=1
) else if exist "C:\Program Files\Java\jdk17" (
    set "JAVA_HOME=C:\Program Files\Java\jdk17"
    set FOUND=1
) else if exist "D:\java\jdk-17" (
    set "JAVA_HOME=D:\java\jdk-17"
    set FOUND=1
) else if exist "D:\java\jdk-17.0.*" (
    for /d %%i in ("D:\java\jdk-17.0.*") do set "JAVA_HOME=%%i"
    set FOUND=1
) else if exist "%USERPROFILE%\java\jdk-17" (
    set "JAVA_HOME=%USERPROFILE%\java\jdk-17"
    set FOUND=1
) else if exist "%USERPROFILE%\AppData\Local\Programs\Java\jdk-17" (
    set "JAVA_HOME=%USERPROFILE%\AppData\Local\Programs\Java\jdk-17"
    set FOUND=1
)

if %FOUND%==0 (
    echo ❌ 未找到Java 17安装
    echo.
    echo 请手动设置JAVA_HOME环境变量:
    echo 1. 找到你的Java 17安装路径
    echo 2. 运行: set JAVA_HOME=你的Java17路径
    echo 3. 运行: set PATH=%%JAVA_HOME%%\bin;%%PATH%%
    echo.
    echo 或者你可以安装Java 17:
    echo 下载地址: https://adoptium.net/temurin/releases/?version=17
    echo.
    set /p JAVA_HOME=请输入Java 17安装路径:
    if "%JAVA_HOME%"=="" (
        echo ❌ 未输入Java路径
        pause
        exit /b 1
    )
    if not exist "%JAVA_HOME%\bin\java.exe" (
        echo ❌ 找不到Java可执行文件
        echo 请确认路径正确
        pause
        exit /b 1
    )
    set FOUND=1
)

if %FOUND%==1 (
    echo 找到Java 17在: %JAVA_HOME%
)

REM 设置环境变量
echo.
echo 设置环境变量...
set "PATH=%JAVA_HOME%\bin;%PATH%"

REM 验证Java版本
echo.
echo 验证Java版本:
java -version

echo.
echo ✅ Java 17环境设置完成!
echo 现在可以启动后端项目:
echo cd server\qa-service-user
echo .\mvnw.cmd spring-boot:run
echo.
pause