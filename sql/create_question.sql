-- 创建问题表
CREATE TABLE IF NOT EXISTS question (
    id VARCHAR(20) PRIMARY KEY,
    patient_id VARCHAR(20) NOT NULL,
    patient_name VARCHAR(50) NOT NULL,
    doctor_id VARCHAR(20) NOT NULL,
    doctor_name VARCHAR(50) NOT NULL,
    question TEXT NOT NULL,
    submit_time TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    answer TEXT,
    answer_time TIMESTAMP
);
