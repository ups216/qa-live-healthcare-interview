-- 创建 QA 医疗健康平台数据库表结构

-- 启用外键约束
SET FOREIGN_KEY_CHECKS=0;

-- 如果表已存在则删除
DROP TABLE IF EXISTS doctor_specialties;
DROP TABLE IF EXISTS specialties;
DROP TABLE IF EXISTS doctors;
DROP TABLE IF EXISTS departments;
DROP TABLE IF EXISTS patients;

-- 启用外键约束
SET FOREIGN_KEY_CHECKS=1;

-- 创建科室表
CREATE TABLE departments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建专业领域表
CREATE TABLE specialties (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建医生表
CREATE TABLE doctors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    doctor_id VARCHAR(50) UNIQUE NOT NULL COMMENT '业务ID，如doc001',
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL COMMENT '医生姓名',
    title VARCHAR(50) NOT NULL COMMENT '职称',
    department_id INT NOT NULL,
    avatar VARCHAR(500) COMMENT '头像URL',
    experience VARCHAR(200) COMMENT '工作经验描述',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否在职',
    last_login_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_is_active (is_active),
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE RESTRICT
);

-- 创建医生专业领域关联表（多对多）
CREATE TABLE doctor_specialties (
    id INT AUTO_INCREMENT PRIMARY KEY,
    doctor_id INT NOT NULL,
    specialty_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_doctor_specialty (doctor_id, specialty_id),
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE,
    FOREIGN KEY (specialty_id) REFERENCES specialties(id) ON DELETE CASCADE
);

-- 创建患者表
CREATE TABLE patients (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    name VARCHAR(100) NOT NULL COMMENT '患者姓名',
    nickname VARCHAR(100) COMMENT '昵称',
    birthday VARCHAR(20) NOT NULL COMMENT '出生日期 (YYYY-MM-DD格式)',
    phone VARCHAR(20) UNIQUE COMMENT '手机号码',
    gender VARCHAR(10) COMMENT '性别 (male/female)',
    avatar VARCHAR(500) COMMENT '头像URL',
    email VARCHAR(100) COMMENT '电子邮箱',
    address TEXT COMMENT '联系地址',
    emergency_contact VARCHAR(100) COMMENT '紧急联系人',
    emergency_phone VARCHAR(20) COMMENT '紧急联系电话',
    medical_history TEXT COMMENT '病史',
    blood_type VARCHAR(10) COMMENT '血型',
    allergies TEXT COMMENT '过敏史',
    current_medications TEXT COMMENT '当前用药',
    is_active BOOLEAN DEFAULT TRUE COMMENT '账户是否激活',
    last_login_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_phone (phone),
    INDEX idx_is_active (is_active)
);