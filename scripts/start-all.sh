#!/usr/bin/env bash
# 按文档顺序启动全部服务：先基础设施，再后端（后台），再前端（后台）
# 依据：docs/运行与联调说明.md
# 使用方式：./scripts/start-all.sh 或 bash scripts/start-all.sh
# 日志：backend 与 frontend 分别输出到 scripts/logs/ 目录

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
LOGS_DIR="$SCRIPT_DIR/logs"
mkdir -p "$LOGS_DIR"

echo "=== 0. 清理旧容器与数据卷（避免 Access denied for qa_app）==="
cd "$PROJECT_ROOT"
docker-compose down -v 2>/dev/null || true

echo ""
echo "=== 1. 启动 MySQL + phpMyAdmin ==="
"$SCRIPT_DIR/start-infra.sh"

echo ""
echo "=== 2. 后台启动 qa-service-user ==="
cd "$PROJECT_ROOT/server/qa-service-user"
nohup ./mvnw spring-boot:run > "$LOGS_DIR/backend.log" 2>&1 &
BACKEND_PID=$!
echo "后端 PID: $BACKEND_PID，日志: $LOGS_DIR/backend.log"
echo "等待后端就绪（约 20 秒）..."
sleep 20

echo ""
echo "=== 3. 安装前端依赖并后台启动前端 ==="
cd "$PROJECT_ROOT/web/qa-web"
npm install
nohup npm run dev > "$LOGS_DIR/frontend.log" 2>&1 &
FRONTEND_PID=$!
echo "前端 PID: $FRONTEND_PID，日志: $LOGS_DIR/frontend.log"

echo ""
echo "=== 全部服务已启动 ==="
echo "  phpMyAdmin: http://localhost:8082"
echo "  后端 API:   http://localhost:8080"
echo "  前端:       http://localhost:5173"
echo "  后端日志:   $LOGS_DIR/backend.log"
echo "  前端日志:   $LOGS_DIR/frontend.log"
echo "停止后端: kill $BACKEND_PID"
echo "停止前端: kill $FRONTEND_PID"
