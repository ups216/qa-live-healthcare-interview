package com.leansofx.qaserviceuser.service;

import com.leansofx.qaserviceuser.model.Doctor;
import com.leansofx.qaserviceuser.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    // 获取所有医生列表
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    // 获取活跃的医生列表
    public List<Doctor> getActiveDoctors() {
        return doctorRepository.findByIsActive(true);
    }

    // 根据部门获取医生列表
    public List<Doctor> getDoctorsByDepartment(String department) {
        return doctorRepository.findByDepartment(department);
    }

    // 根据ID获取医生详情
    public Doctor getDoctorById(String id) {
        return doctorRepository.findById(id).orElse(null);
    }

    // 根据用户名获取医生详情
    public Doctor getDoctorByUsername(String username) {
        return doctorRepository.findByUsername(username);
    }

    // 保存医生信息
    public Doctor saveDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    // 删除医生
    public void deleteDoctor(String id) {
        doctorRepository.deleteById(id);
    }
}
