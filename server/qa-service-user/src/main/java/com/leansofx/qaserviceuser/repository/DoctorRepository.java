package com.leansofx.qaserviceuser.repository;

import com.leansofx.qaserviceuser.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 医生表访问，供医生列表 API 使用。
 */
public interface DoctorRepository extends JpaRepository<Doctor, String> {

    @Override
    List<Doctor> findAll();
}
