package com.leansofx.qaserviceuser.controller;

import com.leansofx.qaserviceuser.entity.Doctor;
import com.leansofx.qaserviceuser.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {
    
    @Autowired
    private DoctorService doctorService;
    
    // 获取所有医生列表
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> getAllDoctors() {
        List<Doctor> doctors = doctorService.getAllDoctors();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", doctors);
        response.put("total", doctors.size());
        return ResponseEntity.ok(response);
    }
    
    // 获取所有活跃的医生列表
    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getActiveDoctors() {
        List<Doctor> doctors = doctorService.getActiveDoctors();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", doctors);
        response.put("total", doctors.size());
        return ResponseEntity.ok(response);
    }
    
    // 根据ID获取医生详情
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getDoctorById(@PathVariable String id) {
        Optional<Doctor> doctor = doctorService.getDoctorById(id);
        Map<String, Object> response = new HashMap<>();
        
        if (doctor.isPresent()) {
            response.put("success", true);
            response.put("data", doctor.get());
        } else {
            response.put("success", false);
            response.put("message", "医生不存在");
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(response);
    }
    
    // 根据用户名获取医生详情
    @GetMapping("/username/{username}")
    public ResponseEntity<Map<String, Object>> getDoctorByUsername(@PathVariable String username) {
        Optional<Doctor> doctor = doctorService.getDoctorByUsername(username);
        Map<String, Object> response = new HashMap<>();
        
        if (doctor.isPresent()) {
            response.put("success", true);
            response.put("data", doctor.get());
        } else {
            response.put("success", false);
            response.put("message", "医生不存在");
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(response);
    }
    
    // 根据科室获取医生列表
    @GetMapping("/department/{department}")
    public ResponseEntity<Map<String, Object>> getDoctorsByDepartment(@PathVariable String department) {
        List<Doctor> doctors = doctorService.getDoctorsByDepartment(department);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", doctors);
        response.put("total", doctors.size());
        return ResponseEntity.ok(response);
    }
    
    // 根据科室获取活跃的医生列表
    @GetMapping("/department/{department}/active")
    public ResponseEntity<Map<String, Object>> getActiveDoctorsByDepartment(@PathVariable String department) {
        List<Doctor> doctors = doctorService.getActiveDoctorsByDepartment(department);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", doctors);
        response.put("total", doctors.size());
        return ResponseEntity.ok(response);
    }
    
    // 医生登录
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        
        Optional<Doctor> doctor = doctorService.login(username, password);
        Map<String, Object> response = new HashMap<>();
        
        if (doctor.isPresent()) {
            response.put("success", true);
            response.put("message", "登录成功");
            response.put("data", doctor.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "用户名或密码错误");
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // 创建新医生
    @PostMapping("/")
    public ResponseEntity<Map<String, Object>> createDoctor(@RequestBody Doctor doctor) {
        Map<String, Object> response = new HashMap<>();
        
        // 检查用户名是否已存在
        if (doctorService.usernameExists(doctor.getUsername())) {
            response.put("success", false);
            response.put("message", "用户名已存在");
            return ResponseEntity.badRequest().body(response);
        }
        
        Doctor savedDoctor = doctorService.saveDoctor(doctor);
        response.put("success", true);
        response.put("message", "医生创建成功");
        response.put("data", savedDoctor);
        
        return ResponseEntity.ok(response);
    }
    
    // 更新医生信息
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateDoctor(@PathVariable String id, @RequestBody Doctor doctor) {
        Map<String, Object> response = new HashMap<>();
        
        Optional<Doctor> existingDoctor = doctorService.getDoctorById(id);
        if (existingDoctor.isEmpty()) {
            response.put("success", false);
            response.put("message", "医生不存在");
            return ResponseEntity.notFound().build();
        }
        
        doctor.setId(id);
        Doctor updatedDoctor = doctorService.saveDoctor(doctor);
        response.put("success", true);
        response.put("message", "医生信息更新成功");
        response.put("data", updatedDoctor);
        
        return ResponseEntity.ok(response);
    }
    
    // 删除医生
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteDoctor(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        
        Optional<Doctor> doctor = doctorService.getDoctorById(id);
        if (doctor.isEmpty()) {
            response.put("success", false);
            response.put("message", "医生不存在");
            return ResponseEntity.notFound().build();
        }
        
        doctorService.deleteDoctor(id);
        response.put("success", true);
        response.put("message", "医生删除成功");
        
        return ResponseEntity.ok(response);
    }
    
    // 更新医生状态
    @PatchMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateDoctorStatus(@PathVariable String id, @RequestBody Map<String, Boolean> statusRequest) {
        Boolean isActive = statusRequest.get("isActive");
        Map<String, Object> response = new HashMap<>();
        
        Doctor updatedDoctor = doctorService.updateDoctorStatus(id, isActive);
        if (updatedDoctor == null) {
            response.put("success", false);
            response.put("message", "医生不存在");
            return ResponseEntity.notFound().build();
        }
        
        response.put("success", true);
        response.put("message", "医生状态更新成功");
        response.put("data", updatedDoctor);
        
        return ResponseEntity.ok(response);
    }
}