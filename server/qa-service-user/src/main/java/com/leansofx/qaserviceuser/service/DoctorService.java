package com.leansofx.qaserviceuser.service;

import com.leansofx.qaserviceuser.entity.Doctor;

import java.util.List;

/**
 * 医生业务服务接口
 */
public interface DoctorService {

    /**
     * 获取医生列表
     *
     * @param isActive null = 全部；true = 在线；false = 离线
     */
    List<Doctor> listDoctors(Boolean isActive);

    /**
     * 按 ID 获取医生详情
     */
    Doctor getDoctorById(String id);

    /**
     * 按 username 获取医生详情
     */
    Doctor getDoctorByUsername(String username);
}
