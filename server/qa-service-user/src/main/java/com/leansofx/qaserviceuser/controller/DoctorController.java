package com.leansofx.qaserviceuser.controller;

import com.leansofx.qaserviceuser.dto.ApiResponse;
import com.leansofx.qaserviceuser.dto.DoctorDTO;
import com.leansofx.qaserviceuser.dto.StatisticsDTO;
import com.leansofx.qaserviceuser.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class DoctorController {
    
    @Autowired
    private DoctorService doctorService;
    
    /**
     * 获取所有医生列表
     * GET /api/doctors
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<DoctorDTO>>> getAllDoctors() {
        List<DoctorDTO> doctors = doctorService.getAllDoctors();
        return ResponseEntity.ok(ApiResponse.success(doctors));
    }
    
    /**
     * 获取活跃医生列表
     * GET /api/doctors/active
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<DoctorDTO>>> getActiveDoctors() {
        List<DoctorDTO> activeDoctors = doctorService.getActiveDoctors();
        return ResponseEntity.ok(ApiResponse.success(activeDoctors));
    }
    
    /**
     * 根据ID获取医生信息
     * GET /api/doctors/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DoctorDTO>> getDoctorById(@PathVariable String id) {
        DoctorDTO doctor = doctorService.getDoctorById(id);
        return ResponseEntity.ok(ApiResponse.success(doctor));
    }
    
    /**
     * 根据用户名获取医生信息
     * GET /api/doctors/username/{username}
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<DoctorDTO>> getDoctorByUsername(@PathVariable String username) {
        DoctorDTO doctor = doctorService.getDoctorByUsername(username);
        return ResponseEntity.ok(ApiResponse.success(doctor));
    }
    
    /**
     * 根据科室获取医生列表
     * GET /api/doctors/department/{departmentCode}
     */
    @GetMapping("/department/{departmentCode}")
    public ResponseEntity<ApiResponse<List<DoctorDTO>>> getDoctorsByDepartment(@PathVariable String departmentCode) {
        List<DoctorDTO> doctors = doctorService.getDoctorsByDepartment(departmentCode);
        return ResponseEntity.ok(ApiResponse.success(doctors));
    }
    
    /**
     * 获取医生统计信息
     * GET /api/doctors/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<StatisticsDTO>> getDoctorStatistics() {
        StatisticsDTO statistics = doctorService.getDoctorStatistics();
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }
    
    /**
     * 获取与JSON文件相同格式的活跃医生列表
     * GET /api/doctors/json/active
     * 返回格式与原来的doctor-user-list.json相同
     */
    @GetMapping("/json/active")
    public ResponseEntity<ApiResponse<List<DoctorDTO>>> getActiveDoctorsJsonFormat() {
        List<DoctorDTO> activeDoctors = doctorService.getActiveDoctors();
        return ResponseEntity.ok(ApiResponse.success(activeDoctors));
    }
}