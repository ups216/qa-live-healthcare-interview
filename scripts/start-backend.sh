#!/usr/bin/env bash
# 启动 qa-service-user 后端服务
# 依据：docs/运行与联调说明.md
# 使用方式：./scripts/start-backend.sh 或 bash scripts/start-backend.sh
# 前置：请先执行 scripts/start-infra.sh 启动 MySQL

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
BACKEND_DIR="$PROJECT_ROOT/server/qa-service-user"

if [ ! -f "$BACKEND_DIR/mvnw" ]; then
  echo "错误：未找到 $BACKEND_DIR/mvnw"
  exit 1
fi

cd "$BACKEND_DIR"
echo "=== 启动 qa-service-user（端口 8080）==="
./mvnw spring-boot:run
