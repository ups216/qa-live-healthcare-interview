package com.leansofx.qaserviceuser.service;

import com.leansofx.qaserviceuser.entity.Doctor;
import com.leansofx.qaserviceuser.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    /**
     * 获取所有医生列表
     */
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    /**
     * 获取所有在线医生
     */
    public List<Doctor> getActiveDoctors() {
        return doctorRepository.findByIsActiveTrue();
    }

    /**
     * 根据ID获取医生
     */
    public Optional<Doctor> getDoctorById(String id) {
        return doctorRepository.findById(id);
    }

    /**
     * 根据用户名获取医生
     */
    public Optional<Doctor> getDoctorByUsername(String username) {
        return doctorRepository.findByUsername(username);
    }

    /**
     * 医生登录
     */
    public Optional<Doctor> login(String username, String password) {
        return doctorRepository.findByUsernameAndPassword(username, password);
    }

    /**
     * 根据科室查找医生
     */
    public List<Doctor> getDoctorsByDepartment(String department) {
        return doctorRepository.findByDepartmentZh(department);
    }
}
