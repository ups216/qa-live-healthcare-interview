-- 初始化脚本示例
-- 该目录下的所有 .sql 文件会在 MySQL 容器首次启动时自动执行

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO users (username, email) VALUES 
('admin', 'admin@example.com'),
('doctor1', 'doctor1@example.com'),
('patient1', 'patient1@example.com');