package com.leansofx.qaserviceuser.controller;

import com.leansofx.qaserviceuser.dto.ApiResponse;
import com.leansofx.qaserviceuser.dto.DoctorDTO;
import com.leansofx.qaserviceuser.entity.Doctor;
import com.leansofx.qaserviceuser.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/doctors")
@CrossOrigin(origins = "*")
public class DoctorController {
    
    @Autowired
    private DoctorService doctorService;
    
    // 获取所有医生列表
    @GetMapping
    public ResponseEntity<ApiResponse<List<DoctorDTO>>> getAllDoctors() {
        try {
            List<Doctor> doctors = doctorService.getAllDoctors();
            List<DoctorDTO> doctorDTOs = doctors.stream()
                    .map(DoctorDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("获取医生列表成功", doctorDTOs));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取医生列表失败: " + e.getMessage()));
        }
    }
    
    // 获取所有活跃的医生列表
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<DoctorDTO>>> getActiveDoctors() {
        try {
            List<Doctor> doctors = doctorService.getActiveDoctors();
            List<DoctorDTO> doctorDTOs = doctors.stream()
                    .map(DoctorDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("获取活跃医生列表成功", doctorDTOs));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取活跃医生列表失败: " + e.getMessage()));
        }
    }
    
    // 根据ID获取医生详情
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DoctorDTO>> getDoctorById(@PathVariable String id) {
        try {
            Optional<Doctor> doctor = doctorService.getDoctorById(id);
            if (doctor.isPresent()) {
                DoctorDTO doctorDTO = new DoctorDTO(doctor.get());
                return ResponseEntity.ok(ApiResponse.success("获取医生详情成功", doctorDTO));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("医生不存在"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取医生详情失败: " + e.getMessage()));
        }
    }
    
    // 根据科室获取医生列表
    @GetMapping("/department/{department}")
    public ResponseEntity<ApiResponse<List<DoctorDTO>>> getDoctorsByDepartment(@PathVariable String department) {
        try {
            List<Doctor> doctors = doctorService.getDoctorsByDepartment(department);
            List<DoctorDTO> doctorDTOs = doctors.stream()
                    .map(DoctorDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("获取科室医生列表成功", doctorDTOs));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取科室医生列表失败: " + e.getMessage()));
        }
    }
    
    // 根据专业领域获取医生列表
    @GetMapping("/specialty/{specialty}")
    public ResponseEntity<ApiResponse<List<DoctorDTO>>> getDoctorsBySpecialty(@PathVariable String specialty) {
        try {
            List<Doctor> doctors = doctorService.getDoctorsBySpecialty(specialty);
            List<DoctorDTO> doctorDTOs = doctors.stream()
                    .map(DoctorDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("获取专业领域医生列表成功", doctorDTOs));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取专业领域医生列表失败: " + e.getMessage()));
        }
    }
    
    // 搜索医生
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<DoctorDTO>>> searchDoctors(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String title) {
        try {
            List<Doctor> doctors = doctorService.searchDoctors(name, department, title);
            List<DoctorDTO> doctorDTOs = doctors.stream()
                    .map(DoctorDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("搜索医生成功", doctorDTOs));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("搜索医生失败: " + e.getMessage()));
        }
    }
    
    // 获取所有科室列表
    @GetMapping("/departments")
    public ResponseEntity<ApiResponse<List<String>>> getAllDepartments() {
        try {
            List<Doctor> doctors = doctorService.getActiveDoctors();
            List<String> departments = doctors.stream()
                    .map(Doctor::getDepartment)
                    .distinct()
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("获取科室列表成功", departments));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取科室列表失败: " + e.getMessage()));
        }
    }
    
    // 获取所有专业领域列表
    @GetMapping("/specialties")
    public ResponseEntity<ApiResponse<List<String>>> getAllSpecialties() {
        try {
            List<Doctor> doctors = doctorService.getActiveDoctors();
            List<String> specialties = doctors.stream()
                    .flatMap(doctor -> doctor.getSpecialtyNames().stream())
                    .distinct()
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("获取专业领域列表成功", specialties));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取专业领域列表失败: " + e.getMessage()));
        }
    }
}