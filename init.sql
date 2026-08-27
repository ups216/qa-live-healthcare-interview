SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

USE qa_healthcare;

CREATE TABLE IF NOT EXISTS doctors (
    id VARCHAR(20) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(50) NOT NULL,
    title VARCHAR(50),
    department VARCHAR(50),
    avatar VARCHAR(500),
    experience VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS doctor_specialties (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    doctor_id VARCHAR(20) NOT NULL,
    specialty VARCHAR(100) NOT NULL,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO doctors (id, username, password, name, title, department, avatar, experience, is_active) VALUES
('doc001', 'dr-zhang-wei', '123456', '张伟医生', '主任医师', '心内科', 'https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400', '15年临床经验', true),
('doc002', 'dr-li-na', '123456', '李娜医生', '副主任医师', '儿科', 'https://images.pexels.com/photos/5327585/pexels-photo-5327585.jpeg?auto=compress&cs=tinysrgb&w=400', '10年临床经验', true),
('doc003', 'dr-wang-qiang', '123456', '王强医生', '主治医师', '骨科', 'https://images.pexels.com/photos/5452293/pexels-photo-5452293.jpeg?auto=compress&cs=tinysrgb&w=400', '8年临床经验', true),
('doc004', 'dr-liu-min', '123456', '刘敏医生', '主任医师', '妇产科', 'https://images.pexels.com/photos/5452201/pexels-photo-5452201.jpeg?auto=compress&cs=tinysrgb&w=400', '18年临床经验', false),
('doc005', 'dr-chen-jie', '123456', '陈杰医生', '副主任医师', '消化内科', 'https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400', '12年临床经验', true);

INSERT INTO doctor_specialties (doctor_id, specialty) VALUES
('doc001', '高血压'), ('doc001', '冠心病'), ('doc001', '心律失常'),
('doc002', '儿童感冒'), ('doc002', '儿童发育'), ('doc002', '疫苗接种'),
('doc003', '骨折'), ('doc003', '关节炎'), ('doc003', '运动损伤'),
('doc004', '孕期保健'), ('doc004', '妇科炎症'), ('doc004', '产后恢复'),
('doc005', '胃炎'), ('doc005', '肠道疾病'), ('doc005', '肝病');

CREATE TABLE IF NOT EXISTS patients (
    id VARCHAR(20) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    gender VARCHAR(10),
    birthday DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 密码使用 BCrypt 加密（原始密码: 123456）
INSERT INTO patients (id, username, password, name, phone, gender, birthday) VALUES
('patient001', 'zhao-ming', '$2a$10$Nj5CvhPTfPUBLyW1E4fxF.3gF6oCfoSB8iZW3j4wsvhnFdCzjuHda', '赵明', '138****1234', '男', '1985-03-15'),
('patient002', 'sun-li', '$2a$10$Nj5CvhPTfPUBLyW1E4fxF.3gF6oCfoSB8iZW3j4wsvhnFdCzjuHda', '孙丽', '139****5678', '女', '1990-07-22'),
('patient003', 'zhou-jie', '$2a$10$Nj5CvhPTfPUBLyW1E4fxF.3gF6oCfoSB8iZW3j4wsvhnFdCzjuHda', '周杰', '137****9012', '男', '1978-11-08'),
('patient004', 'wu-fang', '$2a$10$Nj5CvhPTfPUBLyW1E4fxF.3gF6oCfoSB8iZW3j4wsvhnFdCzjuHda', '吴芳', '136****3456', '女', '1995-05-20'),
('patient005', 'zheng-hao', '$2a$10$Nj5CvhPTfPUBLyW1E4fxF.3gF6oCfoSB8iZW3j4wsvhnFdCzjuHda', '郑浩', '135****7890', '男', '1988-09-12');
