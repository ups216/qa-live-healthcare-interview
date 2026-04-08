package com.leansofx.qaserviceuser.repository;

import com.leansofx.qaserviceuser.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 医生数据访问层。使用 Spring Data JPA，所有查询均为参数化，无拼接 SQL，防止 SQL 注入。
 */
@Repository
public interface DoctorRepository extends JpaRepository<Doctor, String> {

    /**
     * 按是否在线筛选（可选扩展用）。当前列表接口返回全部，由前端展示在线/离线状态。
     */
    List<Doctor> findByIsActive(Boolean isActive);
}
