-- 创建医生表
CREATE TABLE IF NOT EXISTS doctors (
    id VARCHAR(20) NOT NULL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(50) NOT NULL,
    title VARCHAR(50) NOT NULL,
    department VARCHAR(50) NOT NULL,
    avatar VARCHAR(255) NOT NULL,
    experience VARCHAR(100) NOT NULL,
    specialties TEXT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建索引
CREATE INDEX idx_doctors_username ON doctors(username);
CREATE INDEX idx_doctors_department ON doctors(department);
CREATE INDEX idx_doctors_is_active ON doctors(is_active);
