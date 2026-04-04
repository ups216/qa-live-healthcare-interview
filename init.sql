-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS qa_healthcare CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE qa_healthcare;

-- 设置字符集
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 创建医生表
CREATE TABLE IF NOT EXISTS doctors (
    id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(50) NOT NULL,
    title VARCHAR(50),
    department VARCHAR(50),
    avatar VARCHAR(500),
    experience VARCHAR(100),
    is_active BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建医生专长表
CREATE TABLE IF NOT EXISTS doctor_specialties (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    doctor_id VARCHAR(36) NOT NULL,
    specialty VARCHAR(50) NOT NULL,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 插入医生数据
INSERT INTO doctors (id, username, password, name, title, department, avatar, experience, is_active) VALUES
('doc001', 'dr-zhang-wei', '123456', '张伟医生', '主任医师', '心内科', 'https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400', '15年临床经验', TRUE),
('doc002', 'dr-li-na', '123456', '李娜医生', '副主任医师', '儿科', 'https://images.pexels.com/photos/5327585/pexels-photo-5327585.jpeg?auto=compress&cs=tinysrgb&w=400', '10年临床经验', TRUE),
('doc003', 'dr-wang-qiang', '123456', '王强医生', '主治医师', '骨科', 'https://images.pexels.com/photos/5452293/pexels-photo-5452293.jpeg?auto=compress&cs=tinysrgb&w=400', '8年临床经验', TRUE),
('doc004', 'dr-liu-min', '123456', '刘敏医生', '主任医师', '妇产科', 'https://images.pexels.com/photos/5452201/pexels-photo-5452201.jpeg?auto=compress&cs=tinysrgb&w=400', '18年临床经验', FALSE),
('doc005', 'dr-chen-jie', '123456', '陈杰医生', '副主任医师', '消化内科', 'https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400', '12年临床经验', TRUE);

-- 插入专长数据
INSERT INTO doctor_specialties (doctor_id, specialty) VALUES
('doc001', '高血压'),
('doc001', '冠心病'),
('doc001', '心律失常'),
('doc002', '儿童感冒'),
('doc002', '儿童发育'),
('doc002', '疫苗接种'),
('doc003', '骨折'),
('doc003', '关节炎'),
('doc003', '运动损伤'),
('doc004', '孕期保健'),
('doc004', '妇科炎症'),
('doc004', '产后恢复'),
('doc005', '胃炎'),
('doc005', '肠道疾病'),
('doc005', '肝病');

-- =====================================================
-- 患者相关表
-- =====================================================

-- 创建患者表
CREATE TABLE IF NOT EXISTS patients (
    id VARCHAR(36) PRIMARY KEY,
    phone VARCHAR(11) NOT NULL UNIQUE COMMENT '手机号',
    password_hash VARCHAR(255) NOT NULL COMMENT '密码哈希(BCrypt)',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    gender TINYINT DEFAULT 0 COMMENT '性别: 0-未知, 1-男, 2-女',
    birthday DATE COMMENT '生日',
    avatar VARCHAR(500) COMMENT '头像URL',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    failed_attempts INT DEFAULT 0 COMMENT '连续登录失败次数',
    locked_until DATETIME COMMENT '账户锁定截止时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_phone (phone),
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='患者表';

-- 创建患者短信验证码表
CREATE TABLE IF NOT EXISTS patient_sms_codes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    phone VARCHAR(11) NOT NULL COMMENT '手机号',
    code VARCHAR(6) NOT NULL COMMENT '验证码',
    type VARCHAR(20) NOT NULL COMMENT '验证码类型: register',
    used TINYINT DEFAULT 0 COMMENT '是否已使用',
    error_count INT DEFAULT 0 COMMENT '错误次数',
    expire_at DATETIME NOT NULL COMMENT '过期时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_phone_type (phone, type),
    INDEX idx_expire_at (expire_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='患者短信验证码表';

-- 创建患者刷新令牌表
CREATE TABLE IF NOT EXISTS patient_refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id VARCHAR(36) NOT NULL COMMENT '患者ID',
    token VARCHAR(255) NOT NULL UNIQUE COMMENT '令牌',
    device_info VARCHAR(255) COMMENT '设备信息',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    expired_at DATETIME NOT NULL COMMENT '过期时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_token (token),
    INDEX idx_patient_id (patient_id),
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='患者刷新令牌表';

-- 创建问诊记录表
CREATE TABLE IF NOT EXISTS questions (
    id VARCHAR(36) PRIMARY KEY,
    patient_id VARCHAR(36) COMMENT '患者ID',
    patient_name VARCHAR(50) COMMENT '患者姓名',
    patient_phone VARCHAR(11) COMMENT '患者手机号',
    doctor_id VARCHAR(36) COMMENT '医生ID',
    doctor_name VARCHAR(50) COMMENT '医生姓名',
    question TEXT COMMENT '问题内容',
    answer TEXT COMMENT '回答内容',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态: pending-待回复, answered-已回复',
    submit_time DATETIME COMMENT '提交时间',
    answer_time DATETIME COMMENT '回答时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_patient_id (patient_id),
    INDEX idx_patient_phone (patient_phone),
    INDEX idx_doctor_id (doctor_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='问诊记录表';

-- 插入测试患者数据（密码 Test1234 的 BCrypt 加密结果）
INSERT INTO patients (id, phone, password_hash, name, birthday, status) VALUES
('p001', '13800138000', '$2a$10$N9qo8uLOickgx2ZMRZoMye0fBi5PqXR3e5L1T6V0x5q5x5q5x5q5x', '张三', '1990-01-01', 1),
('p002', '13900139000', '$2a$10$N9qo8uLOickgx2ZMRZoMye0fBi5PqXR3e5L1T6V0x5q5x5q5x5q5x', '李四', '1985-05-15', 1);

-- 插入测试问诊数据
INSERT INTO questions (id, patient_id, patient_name, patient_phone, doctor_id, doctor_name, question, status, submit_time) VALUES
('q001', 'p001', '张三', '13800138000', 'doc001', '张伟医生', '最近总是感觉胸闷气短,特别是爬楼梯的时候,这是什么原因?', 'answered', '2025-11-02 09:30:00'),
('q002', 'p002', '李四', '13900139000', 'doc002', '李娜医生', '孩子5岁,最近总是咳嗽,晚上更严重,需要吃什么药?', 'pending', '2025-11-02 10:15:00');
