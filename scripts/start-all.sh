#!/usr/bin/env bash
# 一键启动全部服务：MySQL -> phpMyAdmin -> qa-service-user -> 前端
# 使用方式：在项目根目录执行 ./scripts/start-all.sh 或 bash scripts/start-all.sh
# 启动前会先关闭已有相关服务，再按顺序启动并做健康检查。

set -e
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
cd "$PROJECT_ROOT"

# 端口与超时
MYSQL_PORT=3306
PMA_PORT=8081
BACKEND_PORT=8080
FRONTEND_PORT=5173
HEALTH_WAIT_MAX=90
HEALTH_INTERVAL=3

# 颜色输出
red='\033[0;31m'
green='\033[0;32m'
yellow='\033[1;33m'
nc='\033[0m'
info() { echo -e "${green}[INFO]${nc} $*"; }
warn() { echo -e "${yellow}[WARN]${nc} $*"; }
err() { echo -e "${red}[ERR]${nc} $*"; }

# 关闭占用端口的进程（若存在）
kill_port() {
  local port=$1
  local name=$2
  if command -v lsof &>/dev/null; then
    local pids
    pids=$(lsof -ti ":$port" 2>/dev/null || true)
    if [ -n "$pids" ]; then
      warn "关闭占用 $port 的进程: $name (PIDs: $pids)"
      echo "$pids" | xargs kill -9 2>/dev/null || true
      sleep 2
    fi
  fi
}

# 停止所有相关服务
stop_all() {
  info "步骤 0：确认并关闭已有服务..."
  # 1) 关闭前端（Vite）
  kill_port "$FRONTEND_PORT" "前端"
  # 2) 关闭后端（Spring Boot）
  kill_port "$BACKEND_PORT" "后端"
  # 3) 关闭 Docker Compose 中的服务
  if docker compose version &>/dev/null; then
    docker compose -f "$PROJECT_ROOT/docker-compose.yml" down --remove-orphans 2>/dev/null || true
  else
    docker-compose -f "$PROJECT_ROOT/docker-compose.yml" down --remove-orphans 2>/dev/null || true
  fi
  info "已有服务已关闭。"
}

# 健康检查：MySQL（通过 docker 健康状态）
wait_mysql_healthy() {
  info "等待 MySQL 就绪..."
  local elapsed=0
  while [ $elapsed -lt $HEALTH_WAIT_MAX ]; do
    if docker inspect qa-healthcare-mysql --format '{{.State.Health.Status}}' 2>/dev/null | grep -q healthy; then
      info "MySQL 已就绪。"
      return 0
    fi
    sleep $HEALTH_INTERVAL
    elapsed=$((elapsed + HEALTH_INTERVAL))
  done
  err "等待 MySQL 超时（${HEALTH_WAIT_MAX}s）"
  return 1
}

# 健康检查：后端 Actuator
wait_backend_healthy() {
  info "等待后端 qa-service-user 就绪..."
  local elapsed=0
  while [ $elapsed -lt $HEALTH_WAIT_MAX ]; do
    if curl -sf "http://localhost:$BACKEND_PORT/actuator/health" &>/dev/null; then
      info "后端已就绪。"
      return 0
    fi
    sleep $HEALTH_INTERVAL
    elapsed=$((elapsed + HEALTH_INTERVAL))
  done
  err "等待后端健康检查超时（${HEALTH_WAIT_MAX}s）"
  return 1
}

# 健康检查：前端（可选，仅检查端口可连）
wait_frontend_ready() {
  info "等待前端就绪..."
  local elapsed=0
  while [ $elapsed -lt 60 ]; do
    if curl -sf "http://localhost:$FRONTEND_PORT" &>/dev/null; then
      info "前端已就绪。"
      return 0
    fi
    sleep 2
    elapsed=$((elapsed + 2))
  done
  warn "前端可能仍在启动，请稍后访问 http://localhost:$FRONTEND_PORT"
  return 0
}

# 启动 Docker Compose
start_docker() {
  info "步骤 1：启动 MySQL 与 phpMyAdmin (Docker)..."
  if docker compose version &>/dev/null; then
    docker compose -f "$PROJECT_ROOT/docker-compose.yml" up -d
  else
    docker-compose -f "$PROJECT_ROOT/docker-compose.yml" up -d
  fi
  wait_mysql_healthy
}

# 启动后端
start_backend() {
  info "步骤 2：启动后端 qa-service-user..."
  local backend_dir="$PROJECT_ROOT/server/qa-service-user"
  if [ ! -f "$backend_dir/mvnw" ]; then
    err "未找到 $backend_dir/mvnw，请确认项目结构。"
    exit 1
  fi
  cd "$backend_dir"
  nohup ./mvnw -q spring-boot:run &>"$PROJECT_ROOT/logs/backend.log" &
  echo $! > "$PROJECT_ROOT/logs/backend.pid"
  cd "$PROJECT_ROOT"
  wait_backend_healthy
}

# 启动前端
start_frontend() {
  info "步骤 3：启动前端 (Vite)..."
  local frontend_dir="$PROJECT_ROOT/web/qa-web"
  if [ ! -f "$frontend_dir/package.json" ]; then
    err "未找到 $frontend_dir/package.json，请确认项目结构。"
    exit 1
  fi
  if [ ! -d "$frontend_dir/node_modules" ]; then
    warn "未检测到 node_modules，正在执行 npm install..."
    (cd "$frontend_dir" && npm install)
  fi
  cd "$frontend_dir"
  nohup npm run dev &>"$PROJECT_ROOT/logs/frontend.log" &
  echo $! > "$PROJECT_ROOT/logs/frontend.pid"
  cd "$PROJECT_ROOT"
  wait_frontend_ready
}

# 主流程
main() {
  mkdir -p "$PROJECT_ROOT/logs"
  stop_all
  start_docker
  start_backend
  start_frontend
  echo ""
  info "全部服务已启动。"
  echo "  - MySQL:        localhost:$MYSQL_PORT (用户 qa_user / qa_pass，库 qa_healthcare)"
  echo "  - phpMyAdmin:   http://localhost:$PMA_PORT"
  echo "  - 后端 API:     http://localhost:$BACKEND_PORT (健康: /actuator/health)"
  echo "  - 前端:         http://localhost:$FRONTEND_PORT"
  echo "  - 日志:         $PROJECT_ROOT/logs/backend.log, logs/frontend.log"
  echo "  - 停止后端/前端: kill \$(cat logs/backend.pid) \$(cat logs/frontend.pid); 停止容器: docker compose down"
}

main "$@"
