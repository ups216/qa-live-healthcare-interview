USE qa_healthcare;

-- 删除现有数据
DELETE FROM doctors;

-- 插入与JSON文件完全一致的医生数据
INSERT INTO doctors (id, username, password, name, title, department, avatar, experience, specialties, is_active, created_at, updated_at) VALUES 
('doc001', 'dr-zhang-wei', '123456', '张伟医生', '主任医师', '心内科', 'https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg', '15年临床经验', '["高血压", "冠心病", "心律失常"]', true, NOW(), NOW()),
('doc002', 'dr-li-na', '123456', '李娜医生', '副主任医师', '儿科', 'https://images.pexels.com/photos/5327585/pexels-photo-5327585.jpeg', '10年临床经验', '["儿童感冒", "儿童发育", "疫苗接种"]', true, NOW(), NOW()),
('doc003', 'dr-wang-qiang', '123456', '王强医生', '主治医师', '骨科', 'https://images.pexels.com/photos/5452293/pexels-photo-5452293.jpeg', '8年临床经验', '["骨折", "关节炎", "运动损伤"]', true, NOW(), NOW()),
('doc004', 'dr-liu-min', '123456', '刘敏医生', '主任医师', '妇产科', 'https://images.pexels.com/photos/5452201/pexels-photo-5452201.jpeg', '18年临床经验', '["孕期保健", "妇科炎症", "产后恢复"]', false, NOW(), NOW()),
('doc005', 'dr-chen-jie', '123456', '陈杰医生', '副主任医师', '消化内科', 'https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg', '12年临床经验', '["胃炎", "肠道疾病", "肝病"]', true, NOW(), NOW());