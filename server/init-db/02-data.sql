-- ============================================
-- QA Healthcare Interview - 初始数据
-- 数据来源: 前端 JSON 模拟数据
-- ============================================

USE qa_healthcare;

-- --------------------------------------------
-- 医生数据 (5条)
-- --------------------------------------------
INSERT INTO `doctor` (`id`, `username`, `password`, `name`, `title`, `department`, `avatar`, `experience`, `is_active`) VALUES
('doc001', 'dr-zhang-wei', '123456', '张伟医生', '主任医师', '心内科', 'https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400', '15年临床经验', 1),
('doc002', 'dr-li-na', '123456', '李娜医生', '副主任医师', '儿科', 'https://images.pexels.com/photos/5327585/pexels-photo-5327585.jpeg?auto=compress&cs=tinysrgb&w=400', '10年临床经验', 1),
('doc003', 'dr-wang-qiang', '123456', '王强医生', '主治医师', '骨科', 'https://images.pexels.com/photos/5452293/pexels-photo-5452293.jpeg?auto=compress&cs=tinysrgb&w=400', '8年临床经验', 1),
('doc004', 'dr-liu-min', '123456', '刘敏医生', '主任医师', '妇产科', 'https://images.pexels.com/photos/5452201/pexels-photo-5452201.jpeg?auto=compress&cs=tinysrgb&w=400', '18年临床经验', 0),
('doc005', 'dr-chen-jie', '123456', '陈杰医生', '副主任医师', '消化内科', 'https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400', '12年临床经验', 1);

-- --------------------------------------------
-- 医生专长数据
-- --------------------------------------------
INSERT INTO `doctor_specialty` (`doctor_id`, `specialty`) VALUES
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

-- --------------------------------------------
-- 患者数据 (5条)
-- 密码均为 123456，BCrypt加密存储
-- --------------------------------------------
INSERT INTO `patient` (`id`, `username`, `password`, `name`, `birthday`, `phone`, `gender`) VALUES
('patient001', 'patient-zhao-ming', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '赵明', '1985-03-15', '138****1234', '男'),
('patient002', 'patient-sun-li', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '孙丽', '1990-07-22', '139****5678', '女'),
('patient003', 'patient-zhou-jie', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '周杰', '1978-11-08', '137****9012', '男'),
('patient004', 'patient-wu-fang', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '吴芳', '1995-05-20', '136****3456', '女'),
('patient005', 'patient-zheng-hao', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '郑浩', '1988-09-12', '135****7890', '男');

-- --------------------------------------------
-- 问诊问题数据 (7条)
-- --------------------------------------------
INSERT INTO `question` (`id`, `patient_id`, `patient_name`, `doctor_id`, `doctor_name`, `question`, `submit_time`, `status`, `answer`, `answer_time`) VALUES
('q001', 'patient001', '赵明', 'doc001', '张伟医生', '最近总是感觉胸闷气短,特别是爬楼梯的时候,这是什么原因?', '2025-11-02 09:30:00', 'answered', '根据您的描述,可能是心脏功能问题。建议您做个心电图和心脏彩超检查,同时注意休息,避免剧烈运动。', '2025-11-02 09:45:00'),
('q002', 'patient002', '孙丽', 'doc002', '李娜医生', '孩子5岁,最近总是咳嗽,晚上更严重,需要吃什么药?', '2025-11-02 10:15:00', 'pending', NULL, NULL),
('q003', 'patient003', '周杰', 'doc003', '王强医生', '打篮球时扭伤了脚踝,肿了很大一块,该怎么处理?', '2025-11-02 11:00:00', 'answered', '立即冰敷,抬高患肢,24小时内不要热敷。建议拍个X光片排除骨折。', '2025-11-02 11:20:00'),
('q004', 'patient001', '赵明', 'doc001', '张伟医生', '血压最近有点高,早上测量是145/95,需要吃降压药吗?', '2025-11-02 14:20:00', 'pending', NULL, NULL),
('q005', 'patient004', '吴芳', 'doc005', '陈杰医生', '经常胃痛,吃完饭后更明显,是不是胃炎?', '2025-11-02 15:30:00', 'pending', NULL, NULL),
('q006', 'patient005', '郑浩', 'doc001', '张伟医生', '体检发现心律不齐,平时没什么感觉,严重吗?', '2025-11-02 16:00:00', 'pending', NULL, NULL),
('q007', 'patient002', '孙丽', 'doc002', '李娜医生', '宝宝6个月,可以开始添加辅食了吗?应该吃什么?', '2025-11-02 16:45:00', 'answered', '6个月可以开始添加辅食了。建议从米粉开始,然后逐步添加蔬菜泥、水果泥。注意一次只添加一种新食物,观察3-5天。', '2025-11-02 17:00:00');
