-- ============================================
-- QA Healthcare Database Schema
-- Database: qa_db
-- ============================================
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
USE qa_db;

-- -------------------------------------------
-- Table: doctor (医生信息表)
-- -------------------------------------------
CREATE TABLE IF NOT EXISTS doctor (
                                      id                    BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键 ID',
                                      username              VARCHAR(50)   NOT NULL UNIQUE COMMENT '医生登录用户名',
    password              VARCHAR(255)  NOT NULL DEFAULT '123456' COMMENT '登录密码',
    real_name             VARCHAR(100)  NOT NULL COMMENT '医生姓名',
    title                 VARCHAR(50)   NOT NULL COMMENT '职称（主任医师/副主任医师/主治医师）',
    department            VARCHAR(100)  NOT NULL COMMENT '科室',
    avatar                VARCHAR(500)  DEFAULT NULL COMMENT '头像 URL',
    years_of_experience   INT           DEFAULT NULL COMMENT '临床经验年数',
    specialty             JSON          DEFAULT NULL COMMENT '擅长领域（JSON 数组）',
    status                VARCHAR(20)   NOT NULL DEFAULT 'active' COMMENT '在线状态：active=在线, inactive=离线',
    created_at            DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at            DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_department (department),
    INDEX idx_status (status)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='医生信息表';

-- -------------------------------------------
-- Table: user (患者用户表)
-- -------------------------------------------
CREATE TABLE IF NOT EXISTS user (
                                    id           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键 ID',
                                    username     VARCHAR(50)   NOT NULL UNIQUE COMMENT '登录用户名',
    password     VARCHAR(255)  NOT NULL COMMENT 'BCrypt 加密后的密码',
    real_name    VARCHAR(100)  NOT NULL COMMENT '真实姓名',
    phone        VARCHAR(20)   DEFAULT NULL COMMENT '手机号',
    created_at   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='患者用户表';