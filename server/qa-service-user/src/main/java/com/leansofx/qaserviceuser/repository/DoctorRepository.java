package com.leansofx.qaserviceuser.repository;

import com.leansofx.qaserviceuser.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, String> {

    // 根据是否活跃查询医生列表
    List<Doctor> findByIsActive(boolean isActive);

    // 根据部门查询医生列表
    List<Doctor> findByDepartment(String department);

    // 根据用户名查询医生
    Doctor findByUsername(String username);
}
