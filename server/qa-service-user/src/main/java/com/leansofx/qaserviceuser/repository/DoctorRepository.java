package com.leansofx.qaserviceuser.repository;

import com.leansofx.qaserviceuser.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, String> {
    // 根据用户名查找医生
    Doctor findByUsername(String username);

    // 查找所有在线的医生
    java.util.List<Doctor> findByIsActiveTrue();
}
