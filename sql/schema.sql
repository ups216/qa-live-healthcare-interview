-- 医生表结构
-- 数据库名需与 docker-compose.yml 中 MYSQL_DATABASE 一致（qa_healthcare）
CREATE DATABASE IF NOT EXISTS qa_healthcare DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE qa_healthcare;

CREATE TABLE IF NOT EXISTS doctors (
  id          VARCHAR(32)   NOT NULL PRIMARY KEY,
  username    VARCHAR(64)   NOT NULL UNIQUE,
  password    VARCHAR(64)   NOT NULL,
  name        VARCHAR(64)   NOT NULL,
  title       VARCHAR(64),
  department  VARCHAR(64),
  avatar      VARCHAR(255),
  experience  VARCHAR(64),
  specialties TEXT,
  is_active   TINYINT(1)    NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 患者表结构（以姓名 + 出生日期标识身份，首次登录自动建档，无需密码）
CREATE TABLE IF NOT EXISTS patients (
  id          VARCHAR(32)   NOT NULL PRIMARY KEY,
  name        VARCHAR(64)   NOT NULL,
  birthday    VARCHAR(16),
  phone       VARCHAR(32),
  gender      VARCHAR(8),
  create_time DATETIME      DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_name_birthday (name, birthday)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
