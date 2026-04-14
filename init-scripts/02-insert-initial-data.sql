-- 插入初始数据

-- 插入科室数据
INSERT INTO departments (name, code, description) VALUES
('心内科', 'cardiology', '心血管内科，专注于心脏和血管疾病的诊断与治疗'),
('儿科', 'pediatrics', '儿童医疗服务，涵盖新生儿到青少年阶段的健康问题'),
('骨科', 'orthopedics', '骨骼、关节、肌肉和韧带相关疾病的治疗'),
('妇产科', 'obstetrics_gynecology', '女性生殖系统疾病和孕产期保健'),
('消化内科', 'gastroenterology', '消化系统疾病的诊断与治疗');

-- 插入专业领域数据
INSERT INTO specialties (name, code, description) VALUES
('高血压', 'hypertension', '高血压及相关并发症的诊治'),
('冠心病', 'coronary_heart_disease', '冠状动脉疾病的诊断与治疗'),
('心律失常', 'arrhythmia', '心脏节律异常的诊断与治疗'),
('儿童感冒', 'pediatric_cold', '儿童呼吸道感染的治疗'),
('儿童发育', 'child_development', '儿童生长发育监测与指导'),
('疫苗接种', 'vaccination', '儿童疫苗接种计划与管理'),
('骨折', 'fracture', '骨骼断裂的诊断与治疗'),
('关节炎', 'arthritis', '关节炎症性疾病的治疗'),
('运动损伤', 'sports_injury', '运动相关损伤的康复治疗'),
('孕期保健', 'prenatal_care', '孕产妇健康管理与指导'),
('妇科炎症', 'gynecological_inflammation', '女性生殖系统炎症治疗'),
('产后恢复', 'postpartum_recovery', '产后身体恢复与健康管理'),
('胃炎', 'gastritis', '胃部炎症性疾病的治疗'),
('肠道疾病', 'intestinal_disease', '肠道疾病的诊断与治疗'),
('肝病', 'liver_disease', '肝脏相关疾病的治疗');

-- 插入医生数据
INSERT INTO doctors (doctor_id, username, password, name, title, department_id, avatar, experience, is_active) VALUES
('doc001', 'dr-zhang-wei', '$2a$10$YourEncryptedPasswordHere', '张伟医生', '主任医师', 
 (SELECT id FROM departments WHERE code = 'cardiology'),
 'https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400',
 '15年临床经验', TRUE),
('doc002', 'dr-li-na', '$2a$10$YourEncryptedPasswordHere', '李娜医生', '副主任医师', 
 (SELECT id FROM departments WHERE code = 'pediatrics'),
 'https://images.pexels.com/photos/5327585/pexels-photo-5327585.jpeg?auto=compress&cs=tinysrgb&w=400',
 '10年临床经验', TRUE),
('doc003', 'dr-wang-qiang', '$2a$10$YourEncryptedPasswordHere', '王强医生', '主治医师', 
 (SELECT id FROM departments WHERE code = 'orthopedics'),
 'https://images.pexels.com/photos/5452293/pexels-photo-5452293.jpeg?auto=compress&cs=tinysrgb&w=400',
 '8年临床经验', TRUE),
('doc004', 'dr-liu-min', '$2a$10$YourEncryptedPasswordHere', '刘敏医生', '主任医师', 
 (SELECT id FROM departments WHERE code = 'obstetrics_gynecology'),
 'https://images.pexels.com/photos/5452201/pexels-photo-5452201.jpeg?auto=compress&cs=tinysrgb&w=400',
 '18年临床经验', FALSE),
('doc005', 'dr-chen-jie', '$2a$10$YourEncryptedPasswordHere', '陈杰医生', '副主任医师', 
 (SELECT id FROM departments WHERE code = 'gastroenterology'),
 'https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400',
 '12年临床经验', TRUE);

-- 为张伟医生（心内科）插入专业领域
INSERT INTO doctor_specialties (doctor_id, specialty_id)
SELECT d.id, s.id
FROM doctors d, specialties s
WHERE d.username = 'dr-zhang-wei'
AND s.code IN ('hypertension', 'coronary_heart_disease', 'arrhythmia');

-- 为李娜医生（儿科）插入专业领域
INSERT INTO doctor_specialties (doctor_id, specialty_id)
SELECT d.id, s.id
FROM doctors d, specialties s
WHERE d.username = 'dr-li-na'
AND s.code IN ('pediatric_cold', 'child_development', 'vaccination');

-- 为王强医生（骨科）插入专业领域
INSERT INTO doctor_specialties (doctor_id, specialty_id)
SELECT d.id, s.id
FROM doctors d, specialties s
WHERE d.username = 'dr-wang-qiang'
AND s.code IN ('fracture', 'arthritis', 'sports_injury');

-- 为刘敏医生（妇产科）插入专业领域
INSERT INTO doctor_specialties (doctor_id, specialty_id)
SELECT d.id, s.id
FROM doctors d, specialties s
WHERE d.username = 'dr-liu-min'
AND s.code IN ('prenatal_care', 'gynecological_inflammation', 'postpartum_recovery');

-- 为陈杰医生（消化内科）插入专业领域
INSERT INTO doctor_specialties (doctor_id, specialty_id)
SELECT d.id, s.id
FROM doctors d, specialties s
WHERE d.username = 'dr-chen-jie'
AND s.code IN ('gastritis', 'intestinal_disease', 'liver_disease');

-- 插入患者数据
INSERT INTO patients (username, password, name, nickname, birthday, phone, gender, avatar, email, address, emergency_contact, emergency_phone, medical_history, blood_type, allergies, current_medications, is_active) VALUES
('patient001', '123456', '张三', '老张', '1980-05-15', '13800138001', 'male', 'https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg?auto=compress&cs=tinysrgb&w=400', 'zhangsan@example.com', '北京市朝阳区', '李四', '13800138002', '高血压病史5年', 'A型', '青霉素过敏', '降压药每日一次', TRUE),
('patient002', '123456', '李四', '小李', '1992-08-22', '13800138003', 'female', 'https://images.pexels.com/photos/415829/pexels-photo-415829.jpeg?auto=compress&cs=tinysrgb&w=400', 'lisi@example.com', '上海市浦东新区', '王五', '13800138004', '无', 'B型', '无', '无', TRUE),
('patient003', '123456', '王五', '小王', '1975-03-10', '13800138005', 'male', 'https://images.pexels.com/photos/2379004/pexels-photo-2379004.jpeg?auto=compress&cs=tinysrgb&w=400', 'wangwu@example.com', '广州市天河区', '赵六', '13800138006', '糖尿病病史8年', 'O型', '无', '胰岛素每日两次', TRUE),
('patient004', '123456', '赵六', '小赵', '1988-11-30', '13800138007', 'female', 'https://images.pexels.com/photos/372042/pexels-photo-372042.jpeg?auto=compress&cs=tinysrgb&w=400', 'zhaoliu@example.com', '深圳市南山区', '孙七', '13800138008', '哮喘病史', 'AB型', '海鲜过敏', '哮喘喷雾剂备用', TRUE),
('patient005', '123456', '孙七', '老孙', '1965-07-18', '13800138009', 'male', 'https://images.pexels.com/photos/2182970/pexels-photo-2182970.jpeg?auto=compress&cs=tinysrgb&w=400', 'sunqi@example.com', '杭州市西湖区', '周八', '13800138010', '冠心病病史', 'A型', '无', '冠心病药物每日一次', TRUE);

-- 插入更多测试患者
INSERT INTO patients (username, password, name, birthday, phone, gender, is_active) VALUES
('testuser1', '123456', '测试用户1', '1990-01-01', '13900139001', 'male', TRUE),
('testuser2', '123456', '测试用户2', '1991-02-02', '13900139002', 'female', TRUE),
('testuser3', '123456', '测试用户3', '1992-03-03', '13900139003', 'male', TRUE),
('testuser4', '123456', '测试用户4', '1993-04-04', '13900139004', 'female', TRUE),
('testuser5', '123456', '测试用户5', '1994-05-05', '13900139005', 'male', FALSE);