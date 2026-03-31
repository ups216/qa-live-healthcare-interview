package com.leansofx.qaserviceuser.repository;

import com.leansofx.qaserviceuser.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, String> {

    /**
     * 根据用户名查找医生
     */
    Optional<Doctor> findByUsername(String username);

    /**
     * 查找所有在线的医生
     */
    List<Doctor> findByIsActiveTrue();

    /**
     * 根据中文科室查找医生
     */
    List<Doctor> findByDepartmentZh(String departmentZh);

    /**
     * 根据用户名和密码查找医生（用于登录）
     */
    Optional<Doctor> findByUsernameAndPassword(String username, String password);
}
