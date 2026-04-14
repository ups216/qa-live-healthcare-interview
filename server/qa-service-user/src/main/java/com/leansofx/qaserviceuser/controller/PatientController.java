package com.leansofx.qaserviceuser.controller;

import com.leansofx.qaserviceuser.dto.ApiResponse;
import com.leansofx.qaserviceuser.dto.LoginRequest;
import com.leansofx.qaserviceuser.dto.LoginResponse;
import com.leansofx.qaserviceuser.dto.PatientDTO;
import com.leansofx.qaserviceuser.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PatientController {
    
    @Autowired
    private PatientService patientService;
    
    /**
     * 患者登录
     * POST /api/patients/login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse loginResponse = patientService.login(loginRequest);
            
            if (loginResponse != null) {
                return ResponseEntity.ok(ApiResponse.success("登录成功", loginResponse));
            } else {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("用户名或密码错误"));
            }
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("登录失败: " + e.getMessage()));
        }
    }
    
    /**
     * 患者注册
     * POST /api/patients/register
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<PatientDTO>> register(@RequestBody PatientDTO patientDTO) {
        try {
            PatientDTO registeredPatient = patientService.register(patientDTO);
            return ResponseEntity.ok(ApiResponse.success("注册成功", registeredPatient));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("注册失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取所有活跃患者列表
     * GET /api/patients
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PatientDTO>>> getAllPatients() {
        try {
            List<PatientDTO> patients = patientService.getActivePatients();
            return ResponseEntity.ok(ApiResponse.success(patients));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("获取患者列表失败: " + e.getMessage()));
        }
    }
    
    /**
     * 根据用户名获取患者信息
     * GET /api/patients/username/{username}
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<PatientDTO>> getPatientByUsername(@PathVariable String username) {
        try {
            PatientDTO patient = patientService.getPatientByUsername(username);
            
            if (patient != null) {
                return ResponseEntity.ok(ApiResponse.success(patient));
            } else {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("患者不存在"));
            }
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("获取患者信息失败: " + e.getMessage()));
        }
    }
    
    /**
     * 验证患者身份（兼容旧版接口）
     * GET /api/patients/verify
     */
    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<Boolean>> verifyPatient(
            @RequestParam String name,
            @RequestParam String birthday) {
        try {
            boolean isValid = patientService.verifyPatient(name, birthday);
            return ResponseEntity.ok(ApiResponse.success(isValid));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("验证失败: " + e.getMessage()));
        }
    }
    
    /**
     * 健康检查端点
     * GET /api/patients/health
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("Patient service is healthy"));
    }
}