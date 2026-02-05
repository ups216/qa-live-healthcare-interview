-- 医生表：与前端 Doctor 模型及 doctor-user-list.json 字段对齐
-- 执行顺序：Spring Boot 先执行 schema.sql，再执行 data.sql

CREATE TABLE IF NOT EXISTS doctor (
    id VARCHAR(32) PRIMARY KEY,
    username VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(255),
    name VARCHAR(128) NOT NULL,
    title VARCHAR(64),
    department VARCHAR(64),
    avatar VARCHAR(512),
    experience VARCHAR(128),
    specialties JSON COMMENT '专长列表，JSON 数组',
    is_active TINYINT(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
