package com.leansofx.qaserviceuser.service;

import com.leansofx.qaserviceuser.entity.Doctor;
import java.util.List;

public interface DoctorService {

    /**
     * 获取所有医生列表
     */
    List<Doctor> getDoctorList();

    /**
     * 根据ID获取医生详情
     */
    Doctor getDoctorById(String id);

    /**
     * 根据科室获取医生列表
     */
    List<Doctor> getDoctorListByDepartment(String department);

    /**
     * 获取激活状态的医生列表（首页用）
     */
    List<Doctor> getActiveDoctorList();

    /**
     * 分页获取医生列表（医生页面用）
     */
    List<Doctor> getDoctorListByPage(int page, int pageSize);
}
