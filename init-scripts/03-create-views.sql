-- 创建视图以便于查询

-- 医生详细信息视图
CREATE VIEW doctor_details AS
SELECT 
    d.id,
    d.doctor_id,
    d.username,
    d.name,
    d.title,
    dep.name AS department,
    dep.code AS department_code,
    d.avatar,
    d.experience,
    d.is_active,
    d.last_login_at,
    d.created_at,
    d.updated_at,
    GROUP_CONCAT(s.name ORDER BY s.name SEPARATOR ', ') AS specialties
FROM doctors d
JOIN departments dep ON d.department_id = dep.id
LEFT JOIN doctor_specialties ds ON d.id = ds.doctor_id
LEFT JOIN specialties s ON ds.specialty_id = s.id
GROUP BY d.id, d.doctor_id, d.username, d.name, d.title, dep.name, dep.code, 
         d.avatar, d.experience, d.is_active, d.last_login_at, d.created_at, d.updated_at;

-- 活跃医生视图
CREATE VIEW active_doctors AS
SELECT * FROM doctor_details WHERE is_active = TRUE;

-- 医生统计视图
CREATE VIEW doctor_statistics AS
SELECT 
    COUNT(*) AS total_doctors,
    SUM(CASE WHEN is_active = TRUE THEN 1 ELSE 0 END) AS active_doctors,
    COUNT(DISTINCT department_id) AS total_departments,
    (SELECT COUNT(DISTINCT specialty_id) FROM doctor_specialties) AS total_specialties
FROM doctors;