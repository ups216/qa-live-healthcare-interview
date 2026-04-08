-- 初始医生数据：与原有 doctor-user-list.json 等效，至少 5 条。
-- 使用 INSERT IGNORE 避免重复启动时主键冲突。
INSERT IGNORE INTO doctor (id, username, password, name, title, department, avatar, experience, specialties, is_active, created_at, updated_at) VALUES
('doc001', 'dr-zhang-wei', '123456', '张伟医生', '主任医师', '心内科', 'https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400', '15年临床经验', '["高血压","冠心病","心律失常"]', 1, NOW(), NOW()),
('doc002', 'dr-li-na', '123456', '李娜医生', '副主任医师', '儿科', 'https://images.pexels.com/photos/5327585/pexels-photo-5327585.jpeg?auto=compress&cs=tinysrgb&w=400', '10年临床经验', '["儿童感冒","儿童发育","疫苗接种"]', 1, NOW(), NOW()),
('doc003', 'dr-wang-qiang', '123456', '王强医生', '主治医师', '骨科', 'https://images.pexels.com/photos/5452293/pexels-photo-5452293.jpeg?auto=compress&cs=tinysrgb&w=400', '8年临床经验', '["骨折","关节炎","运动损伤"]', 1, NOW(), NOW()),
('doc004', 'dr-liu-min', '123456', '刘敏医生', '主任医师', '妇产科', 'https://images.pexels.com/photos/5452201/pexels-photo-5452201.jpeg?auto=compress&cs=tinysrgb&w=400', '18年临床经验', '["孕期保健","妇科炎症","产后恢复"]', 0, NOW(), NOW()),
('doc005', 'dr-chen-jie', '123456', '陈杰医生', '副主任医师', '消化内科', 'https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400', '12年临床经验', '["胃炎","肠道疾病","肝病"]', 1, NOW(), NOW());
