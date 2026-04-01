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
    
    // 获取所有医生
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }
    
    // 获取所有活跃的医生
    public List<Doctor> getActiveDoctors() {
        return doctorRepository.findByIsActiveTrue();
    }
    
    // 根据ID获取医生
    public Optional<Doctor> getDoctorById(String id) {
        return doctorRepository.findById(id);
    }
    
    // 根据用户名获取医生
    public Optional<Doctor> getDoctorByUsername(String username) {
        return doctorRepository.findByUsername(username);
    }
    
    // 根据科室获取医生
    public List<Doctor> getDoctorsByDepartment(String department) {
        return doctorRepository.findByDepartmentAndIsActiveTrue(department);
    }
    
    // 根据专业领域获取医生
    public List<Doctor> getDoctorsBySpecialty(String specialty) {
        // 简化版本，返回所有活跃医生
        return doctorRepository.findByIsActiveTrue();
    }
    
    // 根据多个条件搜索医生
    public List<Doctor> searchDoctors(String name, String department, String title) {
        // 简化搜索逻辑，先返回所有活跃医生
        return doctorRepository.findByIsActiveTrue();
    }
    
    // 保存医生信息
    public Doctor saveDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }
    
    // 删除医生
    public void deleteDoctor(String id) {
        doctorRepository.deleteById(id);
    }
    
    // 检查医生是否存在
    public boolean existsById(String id) {
        return doctorRepository.existsById(id);
    }
}