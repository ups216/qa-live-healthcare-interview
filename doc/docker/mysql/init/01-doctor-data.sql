SET NAMES utf8mb4;
CREATE DATABASE IF NOT EXISTS qa_live_healthcare
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE qa_live_healthcare;

DROP TABLE IF EXISTS doctor_specialties;
DROP TABLE IF EXISTS doctors;

CREATE TABLE doctors (
  id VARCHAR(32) NOT NULL PRIMARY KEY COMMENT '医生ID，如 doc001',
  username VARCHAR(64) NOT NULL COMMENT '登录用户名',
  password VARCHAR(255) NOT NULL COMMENT '登录密码，当前沿用前端示例数据明文，后续建议改为哈希值',
  name VARCHAR(64) NOT NULL COMMENT '医生姓名',
  title VARCHAR(64) NOT NULL COMMENT '职称',
  department VARCHAR(64) NOT NULL COMMENT '科室',
  avatar VARCHAR(512) DEFAULT NULL COMMENT '头像URL',
  experience VARCHAR(128) DEFAULT NULL COMMENT '从业经验描述',
  is_active TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否在线',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_doctors_username (username)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='医生主表';

CREATE TABLE doctor_specialties (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  doctor_id VARCHAR(32) NOT NULL COMMENT '医生ID',
  specialty VARCHAR(128) NOT NULL COMMENT '擅长领域',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_doctor_specialties_doctor
    FOREIGN KEY (doctor_id) REFERENCES doctors(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  UNIQUE KEY uk_doctor_specialty (doctor_id, specialty)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='医生擅长项表';

INSERT INTO doctors (id, username, password, name, title, department, avatar, experience, is_active) VALUES
  ('doc001', 'dr-zhang-wei', '123456', '张伟医生', '主任医师', '心内科', 'https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400', '15年临床经验', 1),
  ('doc002', 'dr-li-na', '123456', '李娜医生', '副主任医师', '儿科', 'https://images.pexels.com/photos/5327585/pexels-photo-5327585.jpeg?auto=compress&cs=tinysrgb&w=400', '10年临床经验', 1),
  ('doc003', 'dr-wang-qiang', '123456', '王强医生', '主治医师', '骨科', 'https://images.pexels.com/photos/5452293/pexels-photo-5452293.jpeg?auto=compress&cs=tinysrgb&w=400', '8年临床经验', 1),
  ('doc004', 'dr-liu-min', '123456', '刘敏医生', '主任医师', '妇产科', 'https://images.pexels.com/photos/5452201/pexels-photo-5452201.jpeg?auto=compress&cs=tinysrgb&w=400', '18年临床经验', 0),
  ('doc005', 'dr-chen-jie', '123456', '陈杰医生', '副主任医师', '消化内科', 'https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400', '12年临床经验', 1);

INSERT INTO doctor_specialties (doctor_id, specialty) VALUES
  ('doc001', '高血压'),
  ('doc001', '冠心病'),
  ('doc001', '心律失常'),
  ('doc002', '儿童感冒'),
  ('doc002', '儿童发育'),
  ('doc002', '疫苗接种'),
  ('doc003', '骨折'),
  ('doc003', '关节炎'),
  ('doc003', '运动损伤'),
  ('doc004', '孕期保健'),
  ('doc004', '妇科炎症'),
  ('doc004', '产后恢复'),
  ('doc005', '胃炎'),
  ('doc005', '肠道疾病'),
  ('doc005', '肝病');

