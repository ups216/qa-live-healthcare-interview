CREATE DATABASE IF NOT EXISTS qa_live_healthcare;
USE qa_live_healthcare;

CREATE TABLE IF NOT EXISTS doctors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    title VARCHAR(100) NOT NULL,
    department VARCHAR(100) NOT NULL,
    experience TEXT,
    specialties JSON,
    avatar VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insert sample data
INSERT INTO doctors (username, name, title, department, experience, specialties, avatar) VALUES
('dr_zhang', '张医生', '主任医师', '心血管内科', '从事心血管疾病诊疗工作15年，擅长高血压、冠心病等疾病的诊治', '["高血压", "冠心病", "心律失常"]', 'https://images.pexels.com/photos/3771090/pexels-photo-3771090.jpeg?auto=compress&cs=tinysrgb&w=600'),
('dr_li', '李医生', '副主任医师', '呼吸内科', '呼吸系统疾病专家，专注于哮喘、慢阻肺等慢性呼吸道疾病的长期管理', '["哮喘", "慢阻肺", "肺炎"]', 'https://images.pexels.com/photos/3763182/pexels-photo-3763182.jpeg?auto=compress&cs=tinysrgb&w=600'),
('dr_wang', '王医生', '主治医师', '消化内科', '消化系统疾病诊疗经验丰富，擅长胃炎、胃溃疡等常见病的诊治', '["胃炎", "胃溃疡", "肠炎"]', 'https://images.pexels.com/photos/3771089/pexels-photo-3771089.jpeg?auto=compress&cs=tinysrgb&w=600'),
('dr_chen', '陈医生', '主任医师', '内分泌科', '糖尿病及甲状腺疾病专家，从事内分泌代谢疾病临床工作20年', '["糖尿病", "甲状腺疾病", "肥胖症"]', 'https://images.pexels.com/photos/3771091/pexels-photo-3771091.jpeg?auto=compress&cs=tinysrgb&w=600'),
('dr_liu', '刘医生', '副主任医师', '神经内科', '神经系统疾病专家，专注于脑血管疾病、头痛等疾病的诊断和治疗', '["脑血管疾病", "头痛", "癫痫"]', 'https://images.pexels.com/photos/3771092/pexels-photo-3771092.jpeg?auto=compress&cs=tinysrgb&w=600');