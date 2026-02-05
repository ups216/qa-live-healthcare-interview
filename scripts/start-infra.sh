#!/usr/bin/env bash
# 启动 MySQL + phpMyAdmin（docker-compose）
# 依据：docs/运行与联调说明.md
# 使用方式：在项目根目录执行 ./scripts/start-infra.sh 或 bash scripts/start-infra.sh

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

cd "$PROJECT_ROOT"
echo "=== 启动 MySQL + phpMyAdmin（项目根目录）==="
docker-compose up -d
echo "等待约 10 秒使 MySQL 就绪..."
sleep 10
# 确保 qa_app 可从宿主机连接（解决 Access denied for user 'qa_app'@'192.168.65.1'）
docker exec qa-mysql mysql -uroot -proot123 -e "CREATE USER IF NOT EXISTS 'qa_app'@'%' IDENTIFIED WITH mysql_native_password BY 'qa_app_pass'; GRANT ALL PRIVILEGES ON qa_user.* TO 'qa_app'@'%'; FLUSH PRIVILEGES;" 2>/dev/null || true
echo "就绪：phpMyAdmin → http://localhost:8082 ，MySQL → localhost:3306"
