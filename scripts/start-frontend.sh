#!/usr/bin/env bash
# 启动前端开发服务（web/qa-web）
# 依据：docs/运行与联调说明.md
# 使用方式：./scripts/start-frontend.sh 或 bash scripts/start-frontend.sh
# 前置：建议先启动 MySQL 与 qa-service-user，以便联调

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
WEB_DIR="$PROJECT_ROOT/web/qa-web"

if [ ! -f "$WEB_DIR/package.json" ]; then
  echo "错误：未找到 $WEB_DIR/package.json"
  exit 1
fi

cd "$WEB_DIR"
echo "=== 安装依赖（如需要）==="
npm install
echo "=== 启动前端开发服务（默认 http://localhost:5173）==="
npm run dev
