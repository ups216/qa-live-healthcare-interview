package com.leansofx.qaserviceuser.controller;

import com.leansofx.qaserviceuser.dto.ApiResponse;
import com.leansofx.qaserviceuser.dto.LoginRequest;
import com.leansofx.qaserviceuser.dto.PatientDTO;
import com.leansofx.qaserviceuser.dto.RegisterRequest;
import com.leansofx.qaserviceuser.service.PatientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);

    @Autowired
    private PatientService patientService;

    /**
     * 用户注册
     * POST /api/patients/register
     */
    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<PatientDTO>> register(@Valid @RequestBody RegisterRequest request) {
        try {
            // 前端校验：两次密码一致性（后端不做 confirmPassword 存储，仅做逻辑校验）
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("VALIDATION_ERROR", "两次输入的密码不一致"));
            }

            PatientDTO result = patientService.register(request);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (RuntimeException e) {
            String errorMsg = e.getMessage();
            if ("USERNAME_EXISTS".equals(errorMsg)) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("USERNAME_EXISTS", "该用户名已被注册"));
            }
            logger.error("Registration failed", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("INTERNAL_ERROR", "注册失败，请稍后重试"));
        } catch (Exception e) {
            logger.error("Unexpected registration error", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("INTERNAL_ERROR", "注册失败，请稍后重试"));
        }
    }

    /**
     * 用户登录
     * POST /api/patients/login
     */
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<PatientDTO>> login(@Valid @RequestBody LoginRequest request) {
        try {
            PatientDTO result = patientService.login(request);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (RuntimeException e) {
            String errorMsg = e.getMessage();
            switch (errorMsg) {
                case "INVALID_CREDENTIALS":
                    return ResponseEntity.status(401)
                            .body(ApiResponse.error("INVALID_CREDENTIALS", "用户名或密码错误"));
                case "ACCOUNT_DISABLED":
                    return ResponseEntity.status(403)
                            .body(ApiResponse.error("ACCOUNT_DISABLED", "账号已被禁用，请联系客服"));
                default:
                    logger.error("Login failed: {}", errorMsg, e);
                    return ResponseEntity.internalServerError()
                            .body(ApiResponse.error("INTERNAL_ERROR", "登录失败，请稍后重试"));
            }
        } catch (Exception e) {
            logger.error("Unexpected login error", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("INTERNAL_ERROR", "登录失败，请稍后重试"));
        }
    }

    /**
     * 退出登录
     * POST /api/patients/logout
     */
    @PostMapping(value = "/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Void>> logout() {
        try {
            patientService.logout();
            ApiResponse<Void> response = new ApiResponse<>();
            response.setMessage("已退出登录");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Logout error", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("INTERNAL_ERROR", "退出登录失败"));
        }
    }

    /**
     * 检查用户名可用性
     * GET /api/patients/check-username?username=xxx
     */
    @GetMapping(value = "/check-username", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> checkUsername(@RequestParam String username) {
        boolean exists = patientService.checkUsernameExists(username);
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("available", !exists);
        dataMap.put("username", username);
        result.put("data", dataMap);
        result.put("error", null);
        return ResponseEntity.ok(result);
    }

    /**
     * 全局校验异常处理 - 捕获 @Valid 触发的 Bean Validation 异常
     * 将字段级错误信息统一返回为 400 + VALIDATION_ERROR
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "请求参数校验失败";
        logger.warn("Validation failed: {}", message);
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("VALIDATION_ERROR", message));
    }
}
