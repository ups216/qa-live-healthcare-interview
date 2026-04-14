package com.leansofx.qaserviceuser.repository;

import com.leansofx.qaserviceuser.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Integer> {
    
    // 根据业务ID查找医生
    Optional<Doctor> findByDoctorId(String doctorId);
    
    // 根据用户名查找医生
    Optional<Doctor> findByUsername(String username);
    
    // 查找所有活跃的医生
    List<Doctor> findByIsActiveTrue();
    
    // 根据科室ID查找医生
    List<Doctor> findByDepartmentId(Integer departmentId);
    
    // 根据科室代码查找医生
    @Query("SELECT d FROM Doctor d JOIN d.department dept WHERE dept.code = :deptCode")
    List<Doctor> findByDepartmentCode(@Param("deptCode") String deptCode);
    
    // 查找包含特定专业的医生
    @Query("SELECT DISTINCT d FROM Doctor d JOIN d.specialties s WHERE s.code = :specialtyCode")
    List<Doctor> findBySpecialtyCode(@Param("specialtyCode") String specialtyCode);
    
    // 统计活跃医生数量
    @Query("SELECT COUNT(d) FROM Doctor d WHERE d.isActive = true")
    Long countActiveDoctors();
    
    // 获取医生列表（包括专业领域） - 使用原生查询以简化
    @Query(value = "SELECT " +
           "d.doctor_id AS id, " +
           "d.username, " +
           "d.name, " +
           "d.title, " +
           "dep.name AS department, " +
           "d.avatar, " +
           "d.experience, " +
           "d.is_active, " +
           "GROUP_CONCAT(s.name SEPARATOR ', ') AS specialties " +
           "FROM doctors d " +
           "JOIN departments dep ON d.department_id = dep.id " +
           "LEFT JOIN doctor_specialties ds ON d.id = ds.doctor_id " +
           "LEFT JOIN specialties s ON ds.specialty_id = s.id " +
           "WHERE d.is_active = :active " +
           "GROUP BY d.id, d.doctor_id, d.username, d.name, d.title, dep.name, " +
           "d.avatar, d.experience, d.is_active " +
           "ORDER BY d.created_at DESC", nativeQuery = true)
    List<Object[]> findDoctorsWithSpecialties(@Param("active") boolean active);
}