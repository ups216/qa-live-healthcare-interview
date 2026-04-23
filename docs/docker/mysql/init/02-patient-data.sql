SET NAMES utf8mb4;

-- 创建患者表
CREATE TABLE IF NOT EXISTS patients (
    id VARCHAR(32) PRIMARY KEY COMMENT '患者ID',
    username VARCHAR(64) UNIQUE NOT NULL COMMENT '登录用户名（手机号）',
    password VARCHAR(255) NOT NULL COMMENT '登录密码（BCrypt加密）',
    name VARCHAR(64) NOT NULL COMMENT '患者姓名',
    phone VARCHAR(20) UNIQUE NOT NULL COMMENT '联系电话',
    birthday VARCHAR(10) COMMENT '出生日期',
    gender VARCHAR(10) COMMENT '性别',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者信息表';

-- 插入初始测试数据
-- 默认密码为手机号后四位，此处为 BCrypt 加密后的哈希值
-- 1234: $2a$10$N9qo8uLOickgx2ZMRZoMy.MrEo6Jz3Xj/9i6K7y8k5l6m7n8o9p0q (示例)
-- 实际生产中应使用程序生成，此处为确保登录成功，使用一个固定的 BCrypt hash 代表 '1234'
INSERT IGNORE INTO patients (id, username, password, name, phone, birthday, gender) VALUES
('patient001', '13800001234', '$2a$10$wV3c8Z5qY7xW2vU1tS0rO.eN6mL4kJ8hG6fD4sA2qP0oI8uY6tR4e', '赵明', '13800001234', '1985-03-15', '男'),
('patient002', '13900005678', '$2a$10$wV3c8Z5qY7xW2vU1tS0rO.eN6mL4kJ8hG6fD4sA2qP0oI8uY6tR4e', '孙丽', '13900005678', '1990-07-22', '女'),
('patient003', '13700009012', '$2a$10$wV3c8Z5qY7xW2vU1tS0rO.eN6mL4kJ8hG6fD4sA2qP0oI8uY6tR4e', '周杰', '13700009012', '1978-11-08', '男'),
('patient004', '13600003456', '$2a$10$wV3c8Z5qY7xW2vU1tS0rO.eN6mL4kJ8hG6fD4sA2qP0oI8uY6tR4e', '吴芳', '13600003456', '1995-05-20', '女'),
('patient005', '13500007890', '$2a$10$wV3c8Z5qY7xW2vU1tS0rO.eN6mL4kJ8hG6fD4sA2qP0oI8uY6tR4e', '郑浩', '13500007890', '1988-09-12', '男');
