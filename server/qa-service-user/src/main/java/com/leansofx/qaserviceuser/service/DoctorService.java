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
        return doctorRepository.findByDepartment(department);
    }
    
    // 根据科室获取活跃的医生
    public List<Doctor> getActiveDoctorsByDepartment(String department) {
        return doctorRepository.findByDepartmentAndIsActiveTrue(department);
    }
    
    // 根据职称获取医生
    public List<Doctor> getDoctorsByTitle(String title) {
        return doctorRepository.findByTitle(title);
    }
    
    // 根据职称获取活跃的医生
    public List<Doctor> getActiveDoctorsByTitle(String title) {
        return doctorRepository.findByTitleAndIsActiveTrue(title);
    }
    
    // 医生登录验证
    public Optional<Doctor> login(String username, String password) {
        return doctorRepository.findByUsernameAndPassword(username, password);
    }
    
    // 检查用户名是否存在
    public boolean usernameExists(String username) {
        return doctorRepository.existsByUsername(username);
    }
    
    // 保存医生信息
    public Doctor saveDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }
    
    // 删除医生
    public void deleteDoctor(String id) {
        doctorRepository.deleteById(id);
    }
    
    // 更新医生状态
    public Doctor updateDoctorStatus(String id, boolean isActive) {
        Optional<Doctor> doctorOptional = doctorRepository.findById(id);
        if (doctorOptional.isPresent()) {
            Doctor doctor = doctorOptional.get();
            doctor.setIsActive(isActive);
            return doctorRepository.save(doctor);
        }
        return null;
    }
}