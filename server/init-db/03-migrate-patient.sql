-- ============================================
-- 数据库迁移脚本：患者表增加 username/password 字段
-- 执行前提：已有 qa_healthcare 数据库和 patient 表
-- 执行方式：mysql -u root -p qa_healthcare < 03-migrate-patient.sql
-- ============================================

USE qa_healthcare;

-- 1. 增加 username 字段（允许为空，后续填充数据后再设为 NOT NULL）
ALTER TABLE `patient` ADD COLUMN `username` VARCHAR(50) NULL COMMENT '登录用户名' AFTER `id`;

-- 2. 增加 password 字段（允许为空，后续填充数据后再设为 NOT NULL）
ALTER TABLE `patient` ADD COLUMN `password` VARCHAR(100) NULL COMMENT '登录密码(BCrypt加密)' AFTER `username`;

-- 3. 为已有患者设置 username 和 password
-- 密码 123456 的 BCrypt 加密值
SET @bcrypt_pwd = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy';

UPDATE `patient` SET `username` = 'patient-zhao-ming', `password` = @bcrypt_pwd WHERE `id` = 'patient001';
UPDATE `patient` SET `username` = 'patient-sun-li', `password` = @bcrypt_pwd WHERE `id` = 'patient002';
UPDATE `patient` SET `username` = 'patient-zhou-jie', `password` = @bcrypt_pwd WHERE `id` = 'patient003';
UPDATE `patient` SET `username` = 'patient-wu-fang', `password` = @bcrypt_pwd WHERE `id` = 'patient004';
UPDATE `patient` SET `username` = 'patient-zheng-hao', `password` = @bcrypt_pwd WHERE `id` = 'patient005';

-- 4. 将字段改为 NOT NULL（确保所有数据已填充）
ALTER TABLE `patient` MODIFY `username` VARCHAR(50) NOT NULL COMMENT '登录用户名';
ALTER TABLE `patient` MODIFY `password` VARCHAR(100) NOT NULL COMMENT '登录密码(BCrypt加密)';

-- 5. 添加唯一索引
ALTER TABLE `patient` ADD UNIQUE KEY `uk_username` (`username`);

-- 6. 验证
SELECT `id`, `username`, `name`, `birthday` FROM `patient`;
