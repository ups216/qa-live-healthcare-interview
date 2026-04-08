package com.leansofx.qaserviceuser.controller;

import com.leansofx.qaserviceuser.entity.Doctor;
import com.leansofx.qaserviceuser.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/doctors")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    /**
     * 获取所有医生列表
     * GET /api/doctors?lang=en
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllDoctors(
            @RequestParam(name = "lang", defaultValue = "zh") String lang) {
        List<Doctor> doctors = doctorService.getAllDoctors();
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", convertToDTOList(doctors, lang));
        return ResponseEntity.ok(response);
    }

    /**
     * 获取在线医生列表
     * GET /api/doctors/active?lang=en
     */
    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getActiveDoctors(
            @RequestParam(name = "lang", defaultValue = "zh") String lang) {
        List<Doctor> doctors = doctorService.getActiveDoctors();
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", convertToDTOList(doctors, lang));
        return ResponseEntity.ok(response);
    }

    /**
     * 根据ID获取医生详情
     * GET /api/doctors/{id}?lang=en
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getDoctorById(
            @PathVariable String id,
            @RequestParam(name = "lang", defaultValue = "zh") String lang) {
        Optional<Doctor> doctor = doctorService.getDoctorById(id);
        Map<String, Object> response = new HashMap<>();
        
        if (doctor.isPresent()) {
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", convertToDTO(doctor.get(), lang));
            return ResponseEntity.ok(response);
        } else {
            response.put("code", 404);
            response.put("message", "Doctor not found");
            return ResponseEntity.status(404).body(response);
        }
    }

    /**
     * 根据用户名获取医生
     * GET /api/doctors/username/{username}?lang=en
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<Map<String, Object>> getDoctorByUsername(
            @PathVariable String username,
            @RequestParam(name = "lang", defaultValue = "zh") String lang) {
        Optional<Doctor> doctor = doctorService.getDoctorByUsername(username);
        Map<String, Object> response = new HashMap<>();
        
        if (doctor.isPresent()) {
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", convertToDTO(doctor.get(), lang));
            return ResponseEntity.ok(response);
        } else {
            response.put("code", 404);
            response.put("message", "Doctor not found");
            return ResponseEntity.status(404).body(response);
        }
    }

    /**
     * 医生登录
     * POST /api/doctors/login?lang=en
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody Map<String, String> loginRequest,
            @RequestParam(name = "lang", defaultValue = "zh") String lang) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        
        Optional<Doctor> doctor = doctorService.login(username, password);
        Map<String, Object> response = new HashMap<>();
        
        if (doctor.isPresent()) {
            response.put("code", 200);
            response.put("message", "Login successful");
            response.put("data", convertToDTO(doctor.get(), lang));
            return ResponseEntity.ok(response);
        } else {
            response.put("code", 401);
            response.put("message", "Invalid username or password");
            return ResponseEntity.status(401).body(response);
        }
    }

    /**
     * 将 Doctor 实体转换为 DTO（不包含密码等敏感信息）
     */
    private Map<String, Object> convertToDTO(Doctor doctor, String lang) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", doctor.getId());
        dto.put("username", doctor.getUsername());
        dto.put("name", doctor.getName(lang));
        dto.put("title", doctor.getTitle(lang));
        dto.put("department", doctor.getDepartment(lang));
        dto.put("avatar", doctor.getAvatar());
        dto.put("experience", doctor.getExperience(lang));
        
        // 将 specialties 字符串按逗号分割成数组
        String specialtiesStr = doctor.getSpecialties(lang);
        List<String> specialtiesList = specialtiesStr != null && !specialtiesStr.isEmpty() 
            ? Arrays.stream(specialtiesStr.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList())
            : Arrays.asList();
        dto.put("specialties", specialtiesList);
        
        dto.put("isActive", doctor.getIsActive());
        return dto;
    }

    /**
     * 将 Doctor 列表转换为 DTO 列表
     */
    private List<Map<String, Object>> convertToDTOList(List<Doctor> doctors, String lang) {
        return doctors.stream()
                .map(doctor -> convertToDTO(doctor, lang))
                .collect(Collectors.toList());
    }
}
