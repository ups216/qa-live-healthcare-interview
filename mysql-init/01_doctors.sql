-- 初始化 healthcare 库的 doctors 表与数据
CREATE TABLE IF NOT EXISTS doctors (
  id VARCHAR(20) NOT NULL PRIMARY KEY COMMENT '医生ID，如 doc001',
  username VARCHAR(64) NOT NULL UNIQUE COMMENT '登录用户名',
  password VARCHAR(255) NOT NULL COMMENT '登录密码（应加密存储）',
  name VARCHAR(50) NOT NULL COMMENT '医生姓名',
  title VARCHAR(50) DEFAULT NULL COMMENT '职称',
  department VARCHAR(50) DEFAULT NULL COMMENT '科室',
  avatar VARCHAR(512) DEFAULT NULL COMMENT '头像URL',
  experience VARCHAR(100) DEFAULT NULL COMMENT '临床经验描述',
  specialties JSON DEFAULT NULL COMMENT '擅长领域列表（JSON数组）',
  is_active TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否在职',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

INSERT INTO doctors (
  id, username, password, name, title, department, avatar, experience, specialties, is_active
) VALUES
  (
    'doc001',
    'qijingwei',
    '123456',
    '齐京位医生0207',
    '主任医师',
    '心内科',
    'https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400',
    '15年临床经验',
    JSON_ARRAY('高血压', '冠心病', '心律失常'),
    1
  ),
  (
    'doc002',
    'dr-li-na',
    '123456',
    '李娜医生',
    '副主任医师',
    '儿科',
    'https://images.pexels.com/photos/5327585/pexels-photo-5327585.jpeg?auto=compress&cs=tinysrgb&w=400',
    '10年临床经验',
    JSON_ARRAY('儿童感冒', '儿童发育', '疫苗接种'),
    1
  ),
  (
    'doc003',
    'dr-wang-qiang',
    '123456',
    '王强医生',
    '主治医师',
    '骨科',
    'https://images.pexels.com/photos/5452293/pexels-photo-5452293.jpeg?auto=compress&cs=tinysrgb&w=400',
    '8年临床经验',
    JSON_ARRAY('骨折', '关节炎', '运动损伤'),
    1
  ),
  (
    'doc004',
    'dr-liu-min',
    '123456',
    '刘敏医生',
    '主任医师',
    '妇产科',
    'https://images.pexels.com/photos/5452201/pexels-photo-5452201.jpeg?auto=compress&cs=tinysrgb&w=400',
    '18年临床经验',
    JSON_ARRAY('孕期保健', '妇科炎症', '产后恢复'),
    0
  ),
  (
    'doc005',
    'dr-chen-jie',
    '123456',
    '陈杰医生',
    '副主任医师',
    '消化内科',
    'https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400',
    '12年临床经验',
    JSON_ARRAY('胃炎', '肠道疾病', '肝病'),
    1
  )
ON DUPLICATE KEY UPDATE
  username = VALUES(username);

