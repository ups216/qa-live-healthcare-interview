-- Init schema & seed data for qa_healthcare
-- This script runs automatically on first container startup (empty volume).
-- Each *.sql file runs in a NEW mysql session; set charset before any Chinese literals.
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET collation_connection = utf8mb4_0900_ai_ci;

ALTER DATABASE qa_healthcare CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS doctors (
  id VARCHAR(32) NOT NULL,
  username VARCHAR(64) NOT NULL,
  password VARCHAR(128) NOT NULL,
  name VARCHAR(64) NOT NULL,
  title VARCHAR(64) NOT NULL,
  department VARCHAR(64) NOT NULL,
  avatar VARCHAR(512) NOT NULL,
  experience VARCHAR(128) NOT NULL,
  specialties_json JSON NOT NULL,
  is_active TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (id),
  UNIQUE KEY uk_doctors_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO doctors (id, username, password, name, title, department, avatar, experience, specialties_json, is_active)
VALUES
  ('doc001', 'dr-zhang-wei', '123456', '张伟医生', '主任医师', '心内科',
   'https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400',
   '15年临床经验', JSON_ARRAY('高血压','冠心病','心律失常'), 1),
  ('doc002', 'dr-li-na', '123456', '李娜医生', '副主任医师', '儿科',
   'https://images.pexels.com/photos/5327585/pexels-photo-5327585.jpeg?auto=compress&cs=tinysrgb&w=400',
   '10年临床经验', JSON_ARRAY('儿童感冒','儿童发育','疫苗接种'), 1),
  ('doc003', 'dr-wang-qiang', '123456', '王强医生', '主治医师', '骨科',
   'https://images.pexels.com/photos/5452293/pexels-photo-5452293.jpeg?auto=compress&cs=tinysrgb&w=400',
   '8年临床经验', JSON_ARRAY('骨折','关节炎','运动损伤'), 1),
  ('doc004', 'dr-liu-min', '123456', '刘敏医生', '主任医师', '妇产科',
   'https://images.pexels.com/photos/5452201/pexels-photo-5452201.jpeg?auto=compress&cs=tinysrgb&w=400',
   '18年临床经验', JSON_ARRAY('孕期保健','妇科炎症','产后恢复'), 0),
  ('doc005', 'dr-chen-jie', '123456', '陈杰医生', '副主任医师', '消化内科',
   'https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400',
   '12年临床经验', JSON_ARRAY('胃炎','肠道疾病','肝病'), 1)
ON DUPLICATE KEY UPDATE
  username=VALUES(username),
  password=VALUES(password),
  name=VALUES(name),
  title=VALUES(title),
  department=VALUES(department),
  avatar=VALUES(avatar),
  experience=VALUES(experience),
  specialties_json=VALUES(specialties_json),
  is_active=VALUES(is_active);

