-- ============================================================
-- QA Live Healthcare - 医生种子数据
--
-- 数据来源：web/qa-web/src/data/doctor-user-list.json（必须与之一一对应）
-- 对应风险：若 username 不一致，Consultation.vue:215 的 getDoctorByUsername 会失效；
--          若 is_active 不一致，Home.vue 的在线诊室数与 Doctors.vue 的"暂未开放"态会失真。
-- ============================================================

-- 与 01-schema.sql 同理：必须声明客户端字符集，否则中文会被双重编码。
-- 详见 01-schema.sql 顶部注释。
SET NAMES utf8mb4;

USE qa_live;

INSERT INTO t_doctor
    (id, username, password, name, title, department, avatar, experience, specialties, is_active)
VALUES
    ('doc001', 'dr-zhang-wei', '123456', '张伟医生', '主任医师', '心内科',
     'https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400',
     '15年临床经验', '["高血压", "冠心病", "心律失常"]', 1),

    ('doc002', 'dr-li-na', '123456', '李娜医生', '副主任医师', '儿科',
     'https://images.pexels.com/photos/5327585/pexels-photo-5327585.jpeg?auto=compress&cs=tinysrgb&w=400',
     '10年临床经验', '["儿童感冒", "儿童发育", "疫苗接种"]', 1),

    ('doc003', 'dr-wang-qiang', '123456', '王强医生', '主治医师', '骨科',
     'https://images.pexels.com/photos/5452293/pexels-photo-5452293.jpeg?auto=compress&cs=tinysrgb&w=400',
     '8年临床经验', '["骨折", "关节炎", "运动损伤"]', 1),

    ('doc004', 'dr-liu-min', '123456', '刘敏医生', '主任医师', '妇产科',
     'https://images.pexels.com/photos/5452201/pexels-photo-5452201.jpeg?auto=compress&cs=tinysrgb&w=400',
     '18年临床经验', '["孕期保健", "妇科炎症", "产后恢复"]', 0),

    ('doc005', 'dr-chen-jie', '123456', '陈杰医生', '副主任医师', '消化内科',
     'https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400',
     '12年临床经验', '["胃炎", "肠道疾病", "肝病"]', 1);
