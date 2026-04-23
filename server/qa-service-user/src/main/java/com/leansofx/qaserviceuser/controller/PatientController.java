package com.leansofx.qaserviceuser.controller;

import com.leansofx.qaserviceuser.dto.PatientDto;
import com.leansofx.qaserviceuser.service.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping("/login")
    public PatientDto login(@RequestBody LoginRequest request) {
        return patientService.login(request.getUsername(), request.getPassword())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
    }

    @PostMapping("/verify")
    public PatientDto verify(@RequestBody VerifyRequest request) {
        return patientService.verifyByNameAndBirthday(request.getName(), request.getBirthday())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Name or birthday mismatch"));
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody RegisterRequest request) {
        // 简单校验：检查用户名是否已存在
        if (patientService.findByUsername(request.getPhone()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Phone number already registered");
        }

        PatientDto patient = new PatientDto();
        patient.setId(UUID.randomUUID().toString().replace("-", "").substring(0, 16)); // 简化 ID 生成
        patient.setUsername(request.getPhone());
        patient.setName(request.getName());
        patient.setPhone(request.getPhone());
        patient.setGender(request.getGender());
        
        // 默认密码为手机号后四位
        String defaultPassword = request.getPhone().length() >= 4 ? 
                request.getPhone().substring(request.getPhone().length() - 4) : "0000";
        
        patientService.register(patient, defaultPassword);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Registration successful");
        response.put("patientId", patient.getId());
        return response;
    }

    // DTOs for requests
    public static class LoginRequest {
        private String username;
        private String password;
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class VerifyRequest {
        private String name;
        private String birthday;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getBirthday() { return birthday; }
        public void setBirthday(String birthday) { this.birthday = birthday; }
    }

    public static class RegisterRequest {
        private String name;
        private String phone;
        private String gender;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
    }
}
