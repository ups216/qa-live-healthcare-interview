-- 医生表：表名为小写 doctor，字符集 utf8mb4，与《技术实现文档》二、数据库设计一致。
-- 仅在首次建库时需执行；若使用 JPA ddl-auto=update 则由 Hibernate 建表，本文件可作参考。
-- 建表语句（MySQL 8 默认 utf8mb4）：
/*
CREATE TABLE IF NOT EXISTS doctor (
  id VARCHAR(32) PRIMARY KEY,
  username VARCHAR(64) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  name VARCHAR(128) NOT NULL,
  title VARCHAR(64),
  department VARCHAR(64),
  avatar VARCHAR(512),
  experience VARCHAR(128),
  specialties JSON,
  is_active TINYINT(1) DEFAULT 1,
  created_at DATETIME,
  updated_at DATETIME
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
*/
