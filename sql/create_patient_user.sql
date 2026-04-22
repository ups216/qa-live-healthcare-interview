-- 创建患者用户表
CREATE TABLE IF NOT EXISTS patient_user (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    birthday DATE NOT NULL,
    phone VARCHAR(20) NOT NULL,
    gender VARCHAR(10) NOT NULL
);
