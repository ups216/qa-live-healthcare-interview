package com.leansofx.qaserviceuser.controller;

import com.leansofx.qaserviceuser.dto.ApiResponse;
import com.leansofx.qaserviceuser.dto.LoginRequest;
import com.leansofx.qaserviceuser.dto.LoginResponse;
import com.leansofx.qaserviceuser.dto.UserDTO;
import com.leansofx.qaserviceuser.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/patients")
public class PatientController {
    
    private final AuthService authService;
    
    public PatientController(AuthService authService) {
        this.authService = authService;
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(ApiResponse.success("登录成功", response));
        } catch (RuntimeException e) {
            String errorMessage = e.getMessage();
            if ("INVALID_CREDENTIALS".equals(errorMessage)) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error(401, "用户名或密码错误"));
            } else if ("ACCOUNT_LOCKED".equals(errorMessage)) {
                return ResponseEntity.status(423)
                        .body(ApiResponse.error(423, "账户已被锁定，请稍后重试"));
            } else if ("ACCOUNT_INACTIVE".equals(errorMessage)) {
                return ResponseEntity.status(403)
                        .body(ApiResponse.error(403, "账户未激活"));
            } else {
                return ResponseEntity.status(500)
                        .body(ApiResponse.error(500, "服务器内部错误"));
            }
        }
    }
    
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDTO>> getCurrentUser(@RequestHeader("Authorization") String authorization) {
        try {
            // 提取Bearer Token
            String token = authorization.replace("Bearer ", "");
            UserDTO user = authService.getCurrentUser(token);
            return ResponseEntity.ok(ApiResponse.success(user));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error(401, "无效的Token"));
        }
    }
}
