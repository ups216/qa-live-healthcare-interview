@echo off
echo ========================================
echo   检查Maven依赖配置
echo ========================================
echo.

echo 当前Maven配置:
echo 1. 镜像仓库: 阿里云 (https://maven.aliyun.com/repository/central)
echo 2. SSL验证: 已跳过
echo 3. 本地仓库: %USERPROFILE%\.m2\repository
echo.

echo 测试下载依赖...
cd server\qa-service-user
mvnw.cmd dependency:resolve -DskipTests

echo.
echo 如果看到下载进度，说明配置正常工作
echo 如果遇到问题，请检查:
echo 1. 网络连接
echo 2. 阿里云镜像是否可用
echo 3. .mvn/settings.xml 文件配置
echo.
pause