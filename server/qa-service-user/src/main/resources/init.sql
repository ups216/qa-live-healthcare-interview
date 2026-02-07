-- 医生信息表建表脚本
USE qa_live_healthcare;

CREATE TABLE IF NOT EXISTS doctors (
    id VARCHAR(50) NOT NULL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    title VARCHAR(100) NOT NULL,
    department VARCHAR(100) NOT NULL,
    avatar VARCHAR(500),
    experience VARCHAR(200),
    specialties JSON,
    is_active BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 数据初始化脚本
INSERT INTO doctors (id, username, password, name, title, department, avatar, experience, specialties, is_active)
VALUES
    ('doc001', 'dr-zhang-wei', '123456', '张伟医生', '主任医师', '心内科', 'https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400', '15年临床经验', '["高血压", "冠心病", "心律失常"]', TRUE),
    ('doc002', 'dr-li-na', '123456', '李娜医生', '副主任医师', '儿科', 'https://images.pexels.com/photos/5327585/pexels-photo-5327585.jpeg?auto=compress&cs=tinysrgb&w=400', '10年临床经验', '["儿童感冒", "儿童发育", "疫苗接种"]', TRUE),
    ('doc003', 'dr-wang-qiang', '123456', '王强医生', '主治医师', '骨科', 'https://images.pexels.com/photos/5452293/pexels-photo-5452293.jpeg?auto=compress&cs=tinysrgb&w=400', '8年临床经验', '["骨折", "关节炎", "运动损伤"]', TRUE),
    ('doc004', 'dr-liu-min', '123456', '刘敏医生', '主任医师', '妇产科', 'https://images.pexels.com/photos/5452201/pexels-photo-5452201.jpeg?auto=compress&cs=tinysrgb&w=400', '18年临床经验', '["孕期保健", "妇科炎症", "产后恢复"]', FALSE),
    ('doc005', 'dr-chen-jie', '123456', '陈杰医生', '副主任医师', '消化内科', 'https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400', '12年临床经验', '["胃炎", "肠道疾病", "肝病"]', TRUE)
ON DUPLICATE KEY UPDATE
    username = VALUES(username),
    password = VALUES(password),
    name = VALUES(name),
    title = VALUES(title),
    department = VALUES(department),
    avatar = VALUES(avatar),
    experience = VALUES(experience),
    specialties = VALUES(specialties),
    is_active = VALUES(is_active),
    updated_at = CURRENT_TIMESTAMP;
