package com.leansofx.qaserviceuser.repository;

import com.leansofx.qaserviceuser.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, String> {
    
    // 根据用户名查找医生
    Optional<Doctor> findByUsername(String username);
    
    // 查找所有活跃的医生
    List<Doctor> findByIsActiveTrue();
    
    // 根据科室查找医生
    List<Doctor> findByDepartment(String department);
    
    // 根据科室和活跃状态查找医生
    List<Doctor> findByDepartmentAndIsActiveTrue(String department);
}