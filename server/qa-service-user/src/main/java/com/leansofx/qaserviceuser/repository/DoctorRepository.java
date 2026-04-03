package com.leansofx.qaserviceuser.repository;

import com.leansofx.qaserviceuser.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, String> {
    
    // 根据用户名查找医生
    Optional<Doctor> findByUsername(String username);
    
    // 根据用户名和密码查找医生（用于登录验证）
    Optional<Doctor> findByUsernameAndPassword(String username, String password);
    
    // 根据科室查找医生
    List<Doctor> findByDepartment(String department);
    
    // 根据职称查找医生
    List<Doctor> findByTitle(String title);
    
    // 查找所有活跃的医生
    List<Doctor> findByIsActiveTrue();
    
    // 根据科室查找活跃的医生
    List<Doctor> findByDepartmentAndIsActiveTrue(String department);
    
    // 根据职称查找活跃的医生
    List<Doctor> findByTitleAndIsActiveTrue(String title);
    
    // 使用自定义查询根据专业特长查找医生
    // 注意：由于Hibernate对JSON字段的支持有限，这里使用LIKE查询
    @Query("SELECT d FROM Doctor d WHERE d.specialties LIKE %:specialty%")
    List<Doctor> findBySpecialty(@Param("specialty") String specialty);
    
    // 检查用户名是否存在
    boolean existsByUsername(String username);
    
    // 检查邮箱是否存在（如果需要的话）
    // boolean existsByEmail(String email);
}