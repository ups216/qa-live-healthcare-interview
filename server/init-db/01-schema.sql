-- ============================================
-- QA Healthcare Interview - 数据库初始化脚本
-- 数据库名: qa_healthcare
-- 字符集: utf8mb4
-- ============================================

CREATE DATABASE IF NOT EXISTS qa_healthcare
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE qa_healthcare;

-- --------------------------------------------
-- 1. 医生表
-- --------------------------------------------
DROP TABLE IF EXISTS `doctor`;
CREATE TABLE `doctor` (
  `id` VARCHAR(20) NOT NULL COMMENT '医生ID',
  `username` VARCHAR(50) NOT NULL COMMENT '登录用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '登录密码',
  `name` VARCHAR(50) NOT NULL COMMENT '医生姓名',
  `title` VARCHAR(50) NOT NULL COMMENT '职称',
  `department` VARCHAR(50) NOT NULL COMMENT '科室',
  `avatar` VARCHAR(500) NOT NULL COMMENT '头像URL',
  `experience` VARCHAR(100) NOT NULL COMMENT '经验描述',
  `is_active` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否在线',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='医生表';

-- --------------------------------------------
-- 2. 医生专长表
-- --------------------------------------------
DROP TABLE IF EXISTS `doctor_specialty`;
CREATE TABLE `doctor_specialty` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `doctor_id` VARCHAR(20) NOT NULL COMMENT '医生ID',
  `specialty` VARCHAR(100) NOT NULL COMMENT '专长',
  PRIMARY KEY (`id`),
  KEY `idx_doctor_id` (`doctor_id`),
  CONSTRAINT `fk_specialty_doctor` FOREIGN KEY (`doctor_id`) REFERENCES `doctor`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='医生专长表';

-- --------------------------------------------
-- 3. 患者表
-- --------------------------------------------
DROP TABLE IF EXISTS `patient`;
CREATE TABLE `patient` (
  `id` VARCHAR(20) NOT NULL COMMENT '患者ID',
  `username` VARCHAR(50) NOT NULL COMMENT '登录用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '登录密码(BCrypt加密)',
  `name` VARCHAR(50) NOT NULL COMMENT '患者姓名',
  `birthday` VARCHAR(20) NOT NULL COMMENT '出生日期',
  `phone` VARCHAR(20) DEFAULT '' COMMENT '手机号',
  `gender` VARCHAR(10) DEFAULT '' COMMENT '性别',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='患者表';

-- --------------------------------------------
-- 4. 问诊问题表
-- --------------------------------------------
DROP TABLE IF EXISTS `question`;
CREATE TABLE `question` (
  `id` VARCHAR(20) NOT NULL COMMENT '问题ID',
  `patient_id` VARCHAR(20) NOT NULL COMMENT '患者ID',
  `patient_name` VARCHAR(50) NOT NULL COMMENT '患者姓名',
  `doctor_id` VARCHAR(20) NOT NULL COMMENT '医生ID',
  `doctor_name` VARCHAR(50) NOT NULL COMMENT '医生姓名',
  `question` TEXT NOT NULL COMMENT '问题内容',
  `submit_time` DATETIME NOT NULL COMMENT '提交时间',
  `status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态: pending/answered',
  `answer` TEXT NULL COMMENT '回答内容',
  `answer_time` DATETIME NULL COMMENT '回答时间',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_patient_id` (`patient_id`),
  KEY `idx_doctor_id` (`doctor_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='问诊问题表';
