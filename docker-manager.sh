#!/bin/bash

# Docker 环境管理脚本

set -e

echo "======================================"
echo "  QA Healthcare Docker 环境管理"
echo "======================================"
echo ""

case "$1" in
    start)
        echo "启动 Docker 服务..."
        docker-compose up -d
        echo ""
        echo "等待 MySQL 启动..."
        sleep 5
        echo ""
        echo "✅ 服务已启动！"
        echo ""
        echo "访问方式："
        echo "  - phpMyAdmin: http://localhost:8080"
        echo "  - MySQL: localhost:3306"
        echo ""
        echo "登录信息："
        echo "  - 用户名: qa_user"
        echo "  - 密码: qa_password123"
        echo "  - 数据库: qa_healthcare"
        ;;
    
    stop)
        echo "停止 Docker 服务..."
        docker-compose down
        echo "✅ 服务已停止"
        ;;
    
    restart)
        echo "重启 Docker 服务..."
        docker-compose restart
        echo "✅ 服务已重启"
        ;;
    
    status)
        echo "查看服务状态..."
        docker-compose ps
        ;;
    
    logs)
        echo "查看日志（Ctrl+C 退出）..."
        docker-compose logs -f ${2:-}
        ;;
    
    clean)
        echo "⚠️  警告：这将删除所有数据！"
        read -p "确认删除？(yes/no): " confirm
        if [ "$confirm" = "yes" ]; then
            docker-compose down -v
            echo "✅ 数据已清除"
        else
            echo "操作已取消"
        fi
        ;;
    
    mysql)
        echo "连接到 MySQL..."
        docker exec -it qa-mysql mysql -u root -proot123456 qa_healthcare
        ;;
    
    backup)
        echo "备份数据库..."
        BACKUP_FILE="mysql-backup-$(date +%Y%m%d_%H%M%S).sql"
        docker exec qa-mysql mysqldump -u root -proot123456 qa_healthcare > "$BACKUP_FILE"
        echo "✅ 备份完成: $BACKUP_FILE"
        ;;
    
    *)
        echo "用法: $0 {start|stop|restart|status|logs|clean|mysql|backup}"
        echo ""
        echo "命令说明："
        echo "  start   - 启动服务"
        echo "  stop    - 停止服务"
        echo "  restart - 重启服务"
        echo "  status  - 查看状态"
        echo "  logs    - 查看日志 (可指定服务名: logs mysql)"
        echo "  clean   - 清除所有数据"
        echo "  mysql   - 连接 MySQL"
        echo "  backup  - 备份数据库"
        exit 1
        ;;
esac

exit 0