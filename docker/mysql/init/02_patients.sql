SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS patients (
  id VARCHAR(36) NOT NULL,
  username VARCHAR(64) NOT NULL,
  password_hash VARCHAR(120) NOT NULL,
  name VARCHAR(64) NOT NULL,
  birthday VARCHAR(32) NULL,
  phone VARCHAR(32) NULL,
  gender VARCHAR(16) NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_patients_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
