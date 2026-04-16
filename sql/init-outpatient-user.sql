-- 创建门诊用户表
CREATE TABLE IF NOT EXISTS `outpatient_user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(20) NOT NULL UNIQUE COMMENT '用户名(4-20位字母、数字、下划线)',
    `password` VARCHAR(255) NOT NULL COMMENT '加密后的密码(BCrypt)',
    `name` VARCHAR(50) COMMENT '姓名',
    `phone` VARCHAR(20) COMMENT '手机号',
    `gender` VARCHAR(10) COMMENT '性别',
    `birthday` VARCHAR(20) COMMENT '生日',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `lock_until` DATETIME NULL COMMENT '锁定截止时间',
    `error_count` INT DEFAULT 0 COMMENT '连续错误次数',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='门诊用户表';

-- 初始化测试数据（密码均为: password123，使用BCrypt加密）
-- BCrypt哈希值: $2a$10$qUnIQkrGaxjYBc8Z7VaYi.mYNU8inXnYBJbbpLgNOFjSh56l31yDW (password123)
INSERT INTO `outpatient_user` (`username`, `password`, `name`, `phone`, `gender`, `birthday`, `status`) VALUES
('patient001', '$2a$10$qUnIQkrGaxjYBc8Z7VaYi.mYNU8inXnYBJbbpLgNOFjSh56l31yDW', '张三', '13800138001', '男', '1990-05-15', 1),
('patient002', '$2a$10$qUnIQkrGaxjYBc8Z7VaYi.mYNU8inXnYBJbbpLgNOFjSh56l31yDW', '李四', '13800138002', '女', '1988-08-20', 1),
('patient003', '$2a$10$qUnIQkrGaxjYBc8Z7VaYi.mYNU8inXnYBJbbpLgNOFjSh56l31yDW', '王五', '13800138003', '男', '1995-12-10', 1),
('patient004', '$2a$10$qUnIQkrGaxjYBc8Z7VaYi.mYNU8inXnYBJbbpLgNOFjSh56l31yDW', '赵六', '13800138004', '女', '1992-03-25', 1),
('patient005', '$2a$10$qUnIQkrGaxjYBc8Z7VaYi.mYNU8inXnYBJbbpLgNOFjSh56l31yDW', '孙七', '13800138005', '男', '1985-07-08', 0);
