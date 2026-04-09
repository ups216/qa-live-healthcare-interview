-- ============================================================
-- Database: qa_medical
-- Table:    doctors
-- ============================================================

CREATE DATABASE IF NOT EXISTS qa_medical
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE qa_medical;

-- ------------------------------------------------------------
-- Table structure
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `doctors` (
  `id`          VARCHAR(20)  NOT NULL COMMENT '医生唯一标识',
  `username`    VARCHAR(64)  NOT NULL COMMENT '登录用户名 / URL slug',
  `password`    VARCHAR(255) NOT NULL COMMENT '密码（生产环境应存储哈希值）',
  `name`        VARCHAR(64)  NOT NULL COMMENT '医生姓名',
  `title`       VARCHAR(64)  NOT NULL COMMENT '职称',
  `department`  VARCHAR(64)  NOT NULL COMMENT '科室',
  `avatar`      VARCHAR(512) DEFAULT NULL COMMENT '头像 URL',
  `experience`  VARCHAR(128) DEFAULT NULL COMMENT '从业经验描述',
  `specialties` JSON         DEFAULT NULL COMMENT '专长列表（JSON 数组）',
  `is_active`   TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否在线：1=在线，0=离线',
  `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
                             ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='医生信息表';

-- ------------------------------------------------------------
-- Seed data
-- ------------------------------------------------------------
INSERT INTO `doctors`
  (`id`, `username`, `password`, `name`, `title`, `department`,
   `avatar`, `experience`, `specialties`, `is_active`)
VALUES
(
  'doc001', 'dr-zhang-wei', '123456', '张伟医生', '主任医师', '心内科',
  'https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400',
  '15年临床经验',
  JSON_ARRAY('高血压', '冠心病', '心律失常'),
  1
),
(
  'doc002', 'dr-li-na', '123456', '李娜医生', '副主任医师', '儿科',
  'https://images.pexels.com/photos/5327585/pexels-photo-5327585.jpeg?auto=compress&cs=tinysrgb&w=400',
  '10年临床经验',
  JSON_ARRAY('儿童感冒', '儿童发育', '疫苗接种'),
  1
),
(
  'doc003', 'dr-wang-qiang', '123456', '王强医生', '主治医师', '骨科',
  'https://images.pexels.com/photos/5452293/pexels-photo-5452293.jpeg?auto=compress&cs=tinysrgb&w=400',
  '8年临床经验',
  JSON_ARRAY('骨折', '关节炎', '运动损伤'),
  1
),
(
  'doc004', 'dr-liu-min', '123456', '刘敏医生', '主任医师', '妇产科',
  'https://images.pexels.com/photos/5452201/pexels-photo-5452201.jpeg?auto=compress&cs=tinysrgb&w=400',
  '18年临床经验',
  JSON_ARRAY('孕期保健', '妇科炎症', '产后恢复'),
  0
),
(
  'doc005', 'dr-chen-jie', '123456', '陈杰医生', '副主任医师', '消化内科',
  'https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400',
  '12年临床经验',
  JSON_ARRAY('胃炎', '肠道疾病', '肝病'),
  1
);
