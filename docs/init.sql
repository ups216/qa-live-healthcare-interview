-- QA Healthcare Database Initialization Script

USE qa_healthcare;

-- Create doctors table
CREATE TABLE IF NOT EXISTS doctors (
    id VARCHAR(50) PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    title VARCHAR(50),
    department VARCHAR(50),
    avatar VARCHAR(500),
    experience VARCHAR(100),
    is_active BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create specialties table
CREATE TABLE IF NOT EXISTS specialties (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    doctor_id VARCHAR(50),
    specialty VARCHAR(100),
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE
);

-- Create patients table
CREATE TABLE IF NOT EXISTS patients (
    id VARCHAR(50) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    birthday DATE,
    phone VARCHAR(20),
    gender VARCHAR(10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insert sample doctor data
INSERT INTO doctors (id, username, password, name, title, department, avatar, experience, is_active) VALUES
('doc001', 'dr-zhang-wei', '123456', '张伟医生', '主任医师', '心内科', 'https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400', '15年临床经验', TRUE),
('doc002', 'dr-li-na', '123456', '李娜医生', '副主任医师', '儿科', 'https://images.pexels.com/photos/5327585/pexels-photo-5327585.jpeg?auto=compress&cs=tinysrgb&w=400', '10年临床经验', TRUE),
('doc003', 'dr-wang-qiang', '123456', '王强医生', '主治医师', '骨科', 'https://images.pexels.com/photos/5452293/pexels-photo-5452293.jpeg?auto=compress&cs=tinysrgb&w=400', '8年临床经验', TRUE),
('doc004', 'dr-liu-min', '123456', '刘敏医生', '主任医师', '妇产科', 'https://images.pexels.com/photos/5452201/pexels-photo-5452201.jpeg?auto=compress&cs=tinysrgb&w=400', '18年临床经验', FALSE),
('doc005', 'dr-chen-jie', '123456', '陈杰医生', '副主任医师', '消化内科', 'https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400', '12年临床经验', TRUE);

-- Insert specialties for each doctor
INSERT INTO specialties (doctor_id, specialty) VALUES
('doc001', '高血压'), ('doc001', '冠心病'), ('doc001', '心律失常'),
('doc002', '儿童感冒'), ('doc002', '儿童发育'), ('doc002', '疫苗接种'),
('doc003', '骨折'), ('doc003', '关节炎'), ('doc003', '运动损伤'),
('doc004', '孕期保健'), ('doc004', '妇科炎症'), ('doc004', '产后恢复'),
('doc005', '胃炎'), ('doc005', '肠道疾病'), ('doc005', '肝病');

-- Insert sample patient data (password is MD5 of '123456')
INSERT INTO patients (id, username, password, name, birthday, phone, gender) VALUES
('patient001', 'patient001', 'e10adc3949ba59abbe56e057f20f883e', '赵明', '1985-03-15', '138****1234', '男'),
('patient002', 'patient002', 'e10adc3949ba59abbe56e057f20f883e', '孙丽', '1990-07-22', '139****5678', '女'),
('patient003', 'patient003', 'e10adc3949ba59abbe56e057f20f883e', '周杰', '1978-11-08', '137****9012', '男'),
('patient004', 'patient004', 'e10adc3949ba59abbe56e057f20f883e', '吴芳', '1995-05-20', '136****3456', '女'),
('patient005', 'patient005', 'e10adc3949ba59abbe56e057f20f883e', '郑浩', '1988-09-12', '135****7890', '男');
