package com.leansofx.qaserviceuser.controller;

import com.leansofx.qaserviceuser.dto.*;
import com.leansofx.qaserviceuser.entity.Patient;
import com.leansofx.qaserviceuser.repository.PatientRepository;
import com.leansofx.qaserviceuser.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patient")
public class PatientController {

    private final UserService userService;
    private final PatientRepository patientRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public PatientController(UserService userService, PatientRepository patientRepository) {
        this.userService = userService;
        this.patientRepository = patientRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<PatientDTO>> login(@RequestBody LoginRequest request) {
        try {
            PatientDTO patient = userService.loginPatient(request.getUsername(), request.getPassword());
            return ResponseEntity.ok(ApiResponse.success("登录成功", patient));
        } catch (RuntimeException e) {
            if ("AUTH_FAILED".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "用户名或密码错误"));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "服务器内部错误"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<PatientDTO>> register(@Valid @RequestBody RegisterRequest request) {
        try {
            PatientDTO patient = userService.registerPatient(
                    request.getUsername(),
                    request.getPassword(),
                    request.getName(),
                    request.getBirthday(),
                    request.getPhone(),
                    request.getGender()
            );
            return ResponseEntity.ok(ApiResponse.success("注册成功", patient));
        } catch (RuntimeException e) {
            if ("USERNAME_EXISTS".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(400, "该用户名已被注册"));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "服务器内部错误"));
        }
    }

    /**
     * 临时接口：重置所有患者密码为 123456（开发环境使用，上线前请删除）
     */
    @GetMapping("/init-passwords")
    public ResponseEntity<ApiResponse<String>> initPasswords() {
        String encodedPwd = passwordEncoder.encode("123456");
        List<Patient> patients = patientRepository.findAll();
        for (Patient p : patients) {
            p.setPassword(encodedPwd);
        }
        patientRepository.saveAll(patients);
        return ResponseEntity.ok(ApiResponse.success("已重置 " + patients.size() + " 个患者密码为 123456"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PatientDTO>> getPatient(@PathVariable String id) {
        try {
            PatientDTO patient = userService.getPatientById(id);
            return ResponseEntity.ok(ApiResponse.success(patient));
        } catch (RuntimeException e) {
            if ("NOT_FOUND".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, "患者不存在"));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "服务器内部错误"));
        }
    }
}
