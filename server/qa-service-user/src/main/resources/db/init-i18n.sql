-- 设置字符集
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 创建带多语言支持的医生表
CREATE TABLE IF NOT EXISTS doctors (
    id VARCHAR(20) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    -- 中文字段
    name_zh VARCHAR(100) NOT NULL,
    title_zh VARCHAR(50) NOT NULL,
    department_zh VARCHAR(50) NOT NULL,
    experience_zh VARCHAR(100),
    specialties_zh VARCHAR(500),
    -- 英文字段
    name_en VARCHAR(100) NOT NULL,
    title_en VARCHAR(50) NOT NULL,
    department_en VARCHAR(50) NOT NULL,
    experience_en VARCHAR(100),
    specialties_en VARCHAR(500),
    -- 通用字段
    avatar VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 插入医生数据（中英文）
INSERT INTO doctors (id, username, password, 
    name_zh, title_zh, department_zh, experience_zh, specialties_zh,
    name_en, title_en, department_en, experience_en, specialties_en, 
    avatar, is_active) VALUES
('doc001', 'dr-zhang-wei', '123456', 
 '张伟医生', '主任医师', '心内科', '15年临床经验', '高血压,冠心病,心律失常',
 'Dr. Zhang Wei', 'Chief Physician', 'Cardiology', '15 years clinical experience', 'Hypertension,Coronary Heart Disease,Arrhythmia',
 'https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400', TRUE),

('doc002', 'dr-li-na', '123456', 
 '李娜医生', '副主任医师', '儿科', '10年临床经验', '儿童感冒,儿童发育,疫苗接种',
 'Dr. Li Na', 'Associate Chief Physician', 'Pediatrics', '10 years clinical experience', 'Children\'s Cold,Child Development,Vaccination',
 'https://images.pexels.com/photos/5327585/pexels-photo-5327585.jpeg?auto=compress&cs=tinysrgb&w=400', TRUE),

('doc003', 'dr-wang-qiang', '123456', 
 '王强医生', '主治医师', '骨科', '8年临床经验', '骨折,关节炎,运动损伤',
 'Dr. Wang Qiang', 'Attending Physician', 'Orthopedics', '8 years clinical experience', 'Fracture,Arthritis,Sports Injury',
 'https://images.pexels.com/photos/5452293/pexels-photo-5452293.jpeg?auto=compress&cs=tinysrgb&w=400', TRUE),

('doc004', 'dr-liu-min', '123456', 
 '刘敏医生', '主任医师', '妇产科', '18年临床经验', '孕期保健,妇科炎症,产后恢复',
 'Dr. Liu Min', 'Chief Physician', 'Obstetrics & Gynecology', '18 years clinical experience', 'Prenatal Care,Gynecological Inflammation,Postpartum Recovery',
 'https://images.pexels.com/photos/5452201/pexels-photo-5452201.jpeg?auto=compress&cs=tinysrgb&w=400', FALSE),

('doc005', 'dr-chen-jie', '123456', 
 '陈杰医生', '副主任医师', '消化内科', '12年临床经验', '胃炎,肠道疾病,肝病',
 'Dr. Chen Jie', 'Associate Chief Physician', 'Gastroenterology', '12 years clinical experience', 'Gastritis,Intestinal Diseases,Liver Disease',
 'https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400', TRUE);
