package com.leansofx.qaserviceuser.controller;

import com.leansofx.qaserviceuser.common.ApiResponse;
import com.leansofx.qaserviceuser.dto.LoginRequest;
import com.leansofx.qaserviceuser.dto.LoginResponse;
import com.leansofx.qaserviceuser.dto.PatientVO;
import com.leansofx.qaserviceuser.dto.RegisterRequest;
import com.leansofx.qaserviceuser.exception.BizException;
import com.leansofx.qaserviceuser.exception.ErrorCode;
import com.leansofx.qaserviceuser.service.PatientService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 问诊用户 REST 接口（需求3：PATIENT-AUTH-001）。
 *
 * <p>register / login 位于 JWT 白名单中可匿名访问；
 * me 需要 Bearer token，由 JwtInterceptor 完成校验。
 */
@RestController
@RequestMapping("/api/patient")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    /**
     * 患者注册。
     *
     * <p>成功返回 201 Created；用户名重复返回 400 + code 1001；
     * 参数缺失返回 400 + code 1002（由 GlobalExceptionHandler 统一处理校验异常）。
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<PatientVO>> register(@Valid @RequestBody RegisterRequest request) {
        PatientVO patient = patientService.register(request);
        // 注册成功，资源已创建
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(patient, "注册成功"));
    }

    /**
     * 患者登录，返回 JWT。
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = patientService.login(request);
        return ResponseEntity.ok(ApiResponse.ok(response, "登录成功"));
    }

    /**
     * 获取当前登录患者信息。
     *
     * <p>patientId 由 JwtInterceptor 写入 request attribute，此处无需重复解析 token。
     * 走到这里说明 token 已经通过校验；查不到用户说明账号已被删除，按 401 处理。
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<PatientVO>> me(HttpServletRequest request) {
        String patientId = (String) request.getAttribute("patientId");
        PatientVO patient = patientId == null ? null : patientService.findById(patientId);
        if (patient == null) {
            throw new BizException(ErrorCode.TOKEN_INVALID, "token 无效或已过期");
        }
        return ResponseEntity.ok(ApiResponse.ok(patient));
    }
}
