-- 创建医生用户表
CREATE TABLE IF NOT EXISTS doctor_user (
    id VARCHAR(20) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL,
    name VARCHAR(50) NOT NULL,
    title VARCHAR(50) NOT NULL,
    department VARCHAR(50) NOT NULL,
    avatar VARCHAR(255) NOT NULL,
    experience VARCHAR(100) NOT NULL,
    specialties VARCHAR(255) NOT NULL, -- 存储为JSON字符串
    is_active BOOLEAN NOT NULL
);
