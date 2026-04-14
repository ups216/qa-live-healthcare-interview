-- 创建存储过程

-- 获取所有医生列表（包括专业领域）
DELIMITER $$

CREATE PROCEDURE sp_get_all_doctors()
BEGIN
    SELECT 
        d.doctor_id AS id,
        d.username,
        d.name,
        d.title,
        dep.name AS department,
        d.avatar,
        d.experience,
        d.is_active,
        (SELECT GROUP_CONCAT(s.name SEPARATOR ', ') 
         FROM doctor_specialties ds 
         JOIN specialties s ON ds.specialty_id = s.id 
         WHERE ds.doctor_id = d.id) AS specialties
    FROM doctors d
    JOIN departments dep ON d.department_id = dep.id
    ORDER BY d.is_active DESC, d.created_at DESC;
END$$

-- 获取活跃医生列表
CREATE PROCEDURE sp_get_active_doctors()
BEGIN
    SELECT 
        d.doctor_id AS id,
        d.username,
        d.name,
        d.title,
        dep.name AS department,
        d.avatar,
        d.experience,
        (SELECT GROUP_CONCAT(s.name SEPARATOR ', ') 
         FROM doctor_specialties ds 
         JOIN specialties s ON ds.specialty_id = s.id 
         WHERE ds.doctor_id = d.id) AS specialties
    FROM doctors d
    JOIN departments dep ON d.department_id = dep.id
    WHERE d.is_active = TRUE
    ORDER BY d.created_at DESC;
END$$

-- 按科室获取医生
CREATE PROCEDURE sp_get_doctors_by_department(IN dept_code VARCHAR(50))
BEGIN
    SELECT 
        d.doctor_id AS id,
        d.username,
        d.name,
        d.title,
        dep.name AS department,
        d.avatar,
        d.experience,
        d.is_active,
        (SELECT GROUP_CONCAT(s.name SEPARATOR ', ') 
         FROM doctor_specialties ds 
         JOIN specialties s ON ds.specialty_id = s.id 
         WHERE ds.doctor_id = d.id) AS specialties
    FROM doctors d
    JOIN departments dep ON d.department_id = dep.id
    WHERE dep.code = dept_code
    ORDER BY d.is_active DESC, d.created_at DESC;
END$$

-- 获取医生统计信息
CREATE PROCEDURE sp_get_doctor_statistics()
BEGIN
    SELECT 
        COUNT(*) AS totalDoctors,
        SUM(CASE WHEN is_active = TRUE THEN 1 ELSE 0 END) AS activeDoctors
    FROM doctors;
END$$

DELIMITER ;