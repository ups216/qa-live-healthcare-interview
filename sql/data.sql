-- 初始数据（医生密码统一使用 MD5 加密；明文 123456 的 MD5 = e10adc3949ba59abbe56e057f20f883e）
-- 患者无需初始数据：首次输入姓名 + 出生日期会自动建档
USE qa_healthcare;

INSERT INTO doctors (id, username, password, name, title, department, avatar, experience, specialties, is_active) VALUES
('doc001', 'dr-zhang-wei', 'e10adc3949ba59abbe56e057f20f883e', '张伟医生', '主任医师', '心内科',
 'https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400',
 '15年临床经验', '["高血压","冠心病","心律失常"]', 1),
('doc002', 'dr-li-na', 'e10adc3949ba59abbe56e057f20f883e', '李娜医生', '副主任医师', '儿科',
 'https://images.pexels.com/photos/5327585/pexels-photo-5327585.jpeg?auto=compress&cs=tinysrgb&w=400',
 '10年临床经验', '["儿童感冒","儿童发育","疫苗接种"]', 1),
('doc003', 'dr-wang-qiang', 'e10adc3949ba59abbe56e057f20f883e', '王强医生', '主治医师', '骨科',
 'https://images.pexels.com/photos/5452293/pexels-photo-5452293.jpeg?auto=compress&cs=tinysrgb&w=400',
 '8年临床经验', '["骨折","关节炎","运动损伤"]', 1),
('doc004', 'dr-liu-min', 'e10adc3949ba59abbe56e057f20f883e', '刘敏医生', '主任医师', '妇产科',
 'https://images.pexels.com/photos/5452201/pexels-photo-5452201.jpeg?auto=compress&cs=tinysrgb&w=400',
 '18年临床经验', '["孕期保健","妇科炎症","产后恢复"]', 0),
('doc005', 'dr-chen-jie', 'e10adc3949ba59abbe56e057f20f883e', '陈杰医生', '副主任医师', '消化内科',
 'https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400',
 '12年临床经验', '["胃炎","肠道疾病","肝病"]', 1);
