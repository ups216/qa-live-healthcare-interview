CREATE TABLE IF NOT EXISTS questions (
    id VARCHAR(64) PRIMARY KEY,
    patient_id VARCHAR(64) NOT NULL,
    patient_name VARCHAR(128) NOT NULL,
    doctor_id VARCHAR(64) NOT NULL,
    doctor_name VARCHAR(128) NOT NULL,
    question TEXT NOT NULL,
    submit_time DATETIME NOT NULL,
    status VARCHAR(32) NOT NULL,
    answer TEXT NULL,
    answer_time DATETIME NULL
);
