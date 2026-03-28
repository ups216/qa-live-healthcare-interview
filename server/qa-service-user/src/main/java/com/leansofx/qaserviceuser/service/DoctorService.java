package com.leansofx.qaserviceuser.service;

import com.leansofx.qaserviceuser.entity.Doctor;
import com.leansofx.qaserviceuser.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    // 获取所有医生
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    // 根据ID获取医生
    public Doctor getDoctorById(String id) {
        return doctorRepository.findById(id).orElse(null);
    }

    // 多条件查询医生
    public List<Doctor> getDoctorsByConditions(String name, String department, String title, Boolean isActive) {
        return doctorRepository.findByConditions(name, department, title, isActive);
    }

    // 获取所有科室列表
    public List<String> getAllDepartments() {
        return doctorRepository.findAllDepartments();
    }

    // 获取所有职称列表
    public List<String> getAllTitles() {
        return doctorRepository.findAllTitles();
    }

    // 创建医生
    public Doctor createDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    // 更新医生
    public Doctor updateDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    // 删除医生
    public void deleteDoctor(String id) {
        doctorRepository.deleteById(id);
    }
}
