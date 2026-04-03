USE qa_healthcare;

-- 插入医生测试数据
INSERT INTO doctors (id, username, password, name, title, department, avatar, experience, specialties, is_active, created_at, updated_at) VALUES 
('1', 'zhangwei', 'password123', '张伟医生', '主任医师', '内科', 'https://example.com/avatar1.jpg', '10年临床经验', '["内科", "消化科"]', true, NOW(), NOW()),
('2', 'lihua', 'password123', '李华医生', '副主任医师', '外科', 'https://example.com/avatar2.jpg', '8年临床经验', '["外科", "骨科"]', true, NOW(), NOW()),
('3', 'wangfang', 'password123', '王芳医生', '主治医师', '儿科', 'https://example.com/avatar3.jpg', '6年临床经验', '["儿科", "儿童保健"]', true, NOW(), NOW()),
('4', 'chenming', 'password123', '陈明医生', '主任医师', '妇产科', 'https://example.com/avatar4.jpg', '12年临床经验', '["妇产科", "妇科"]', true, NOW(), NOW()),
('5', 'zhaohong', 'password123', '赵红医生', '副主任医师', '眼科', 'https://example.com/avatar5.jpg', '9年临床经验', '["眼科", "白内障"]', false, NOW(), NOW());