-- ============================================================
-- QA Live Healthcare - 数据库初始化 DDL
-- 由 docker-compose 挂载到 MySQL 容器的 /docker-entrypoint-initdb.d
-- 仅在数据卷为空的首次启动时执行
-- ============================================================

-- 【重要】必须显式声明客户端字符集。
-- MySQL 容器执行 initdb 脚本时，客户端默认字符集是 latin1，
-- 会把本文件里的 UTF-8 中文按 latin1 解读后再转成 utf8mb4 落库，
-- 导致数据"双重编码"（HEX 中会出现 C3A5C2BC 这类模式），
-- 表现为：直接用 mysql 客户端查询看着正常，但 JDBC 读出来是乱码。
SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS qa_live
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE qa_live;

-- ------------------------------------------------------------
-- 医生表（需求 2：将 web/qa-web/src/data/doctor-user-list.json 迁移到数据库）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS t_doctor;
CREATE TABLE t_doctor (
    id           VARCHAR(32)  NOT NULL COMMENT '业务主键，如 doc001',
    username     VARCHAR(64)  NOT NULL COMMENT '登录名；前端路由 /consultation/:doctorUsername 使用',
    password     VARCHAR(128) NOT NULL COMMENT '登录密码',
    name         VARCHAR(64)  NOT NULL COMMENT '医生姓名',
    title        VARCHAR(64)  NOT NULL COMMENT '职称，如主任医师',
    department   VARCHAR(64)  NOT NULL COMMENT '科室，如心内科',
    avatar       VARCHAR(512)          COMMENT '头像 URL',
    experience   VARCHAR(128)          COMMENT '从业年限描述，如15年临床经验',
    specialties  JSON                  COMMENT '擅长领域数组；由 JsonStringListTypeHandler 映射为 List<String>',
    is_active    TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否在线：1 在线 / 0 离线',
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_doctor_username (username)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='医生表';

-- ------------------------------------------------------------
-- 问诊用户（患者）表（需求 3：PATIENT-AUTH-001）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS t_patient;
CREATE TABLE t_patient (
    id          VARCHAR(32)  NOT NULL COMMENT '业务主键，应用层生成',
    username    VARCHAR(64)  NOT NULL COMMENT '登录用户名',
    password    VARCHAR(128) NOT NULL COMMENT 'BCrypt 密文，形如 $2a$10$...',
    name        VARCHAR(64)  NOT NULL COMMENT '姓名',
    birthday    VARCHAR(16)           COMMENT '生日 YYYY-MM-DD',
    phone       VARCHAR(32)           COMMENT '手机号',
    gender      VARCHAR(8)            COMMENT '性别',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_patient_username (username)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='问诊用户（患者）表';
