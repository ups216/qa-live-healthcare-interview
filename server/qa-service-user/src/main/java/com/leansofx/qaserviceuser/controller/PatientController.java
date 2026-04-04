package com.leansofx.qaserviceuser.controller;

import com.leansofx.qaserviceuser.dto.*;
import com.leansofx.qaserviceuser.service.PatientService;
import com.leansofx.qaserviceuser.service.SmsCodeService;
import com.leansofx.qaserviceuser.util.JwtUtil;
import com.leansofx.qaserviceuser.util.ValidationUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "*")
public class PatientController {

    private final PatientService patientService;
    private final SmsCodeService smsCodeService;
    private final JwtUtil jwtUtil;

    public PatientController(PatientService patientService, SmsCodeService smsCodeService, JwtUtil jwtUtil) {
        this.patientService = patientService;
        this.smsCodeService = smsCodeService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/sms/send")
    public ResponseEntity<ApiResponse<Map<String, Object>>> sendSmsCode(@RequestBody SmsSendRequest request) {
        if (!ValidationUtil.isValidPhone(request.getPhone())) {
            return ResponseEntity.ok(ApiResponse.error(40001, "手机号格式错误"));
        }

        if (request.getPhone() != null && patientService.checkPhoneExists(request.getPhone())) {
            return ResponseEntity.ok(ApiResponse.error(40901, "该手机号已注册"));
        }

        SmsCodeService.SendResult result = smsCodeService.sendCode(request.getPhone(), request.getType());

        if (result.isSuccess()) {
            return ResponseEntity.ok(ApiResponse.success(result.getMessage(), Map.of("expiresIn", result.getExpiresIn())));
        } else {
            return ResponseEntity.ok(ApiResponse.error(result.getCode(), result.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<LoginResponse>> register(@RequestBody PatientRegisterRequest request) {
        if (!ValidationUtil.isValidPhone(request.getPhone())) {
            return ResponseEntity.ok(ApiResponse.error(40001, "请输入正确的手机号"));
        }

        if (!ValidationUtil.isValidPassword(request.getPassword())) {
            return ResponseEntity.ok(ApiResponse.error(40002, "密码需8-20位，包含字母和数字"));
        }

        if (!ValidationUtil.isValidName(request.getName())) {
            return ResponseEntity.ok(ApiResponse.error(40001, "姓名需2-20个字符"));
        }

        PatientService.RegisterResult result = patientService.register(request);

        if (result.isSuccess()) {
            return ResponseEntity.ok(ApiResponse.success(result.getMessage(), result.getData()));
        } else {
            return ResponseEntity.ok(ApiResponse.error(result.getCode(), result.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody PatientLoginRequest request) {
        if (!ValidationUtil.isValidPhone(request.getPhone())) {
            return ResponseEntity.ok(ApiResponse.error(40001, "请输入正确的手机号"));
        }

        PatientService.LoginResult result = patientService.login(request);

        if (result.isSuccess()) {
            return ResponseEntity.ok(ApiResponse.success(result.getMessage(), result.getData()));
        } else {
            return ResponseEntity.ok(ApiResponse.error(result.getCode(), result.getMessage()));
        }
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(@RequestBody TokenRefreshRequest request) {
        PatientService.TokenRefreshResult result = patientService.refreshToken(request.getRefreshToken());

        if (result.isSuccess()) {
            return ResponseEntity.ok(ApiResponse.success(result.getMessage(), result.getData()));
        } else {
            return ResponseEntity.ok(ApiResponse.error(result.getCode(), result.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtUtil.isAccessToken(token)) {
                String patientId = jwtUtil.getPatientIdFromToken(token);
                patientService.logout(patientId);
            }
        }
        return ResponseEntity.ok(ApiResponse.success("退出成功", null));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<PatientDTO>> getProfile(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.ok(ApiResponse.error(40101, "请先登录"));
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.isAccessToken(token)) {
            return ResponseEntity.ok(ApiResponse.error(40103, "Token无效"));
        }

        String patientId = jwtUtil.getPatientIdFromToken(token);
        PatientDTO patient = patientService.getPatientById(patientId);

        if (patient == null) {
            return ResponseEntity.ok(ApiResponse.error(40401, "用户不存在"));
        }

        return ResponseEntity.ok(ApiResponse.success(patient));
    }

    @GetMapping("/check-phone")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkPhone(@RequestParam String phone) {
        boolean exists = patientService.checkPhoneExists(phone);
        return ResponseEntity.ok(ApiResponse.success(Map.of("exists", exists)));
    }

    @PostMapping("/upgrade")
    public ResponseEntity<ApiResponse<Map<String, Object>>> upgrade(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody AccountUpgradeRequest request) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.ok(ApiResponse.error(40101, "请先登录"));
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.isAccessToken(token)) {
            return ResponseEntity.ok(ApiResponse.error(40103, "Token无效"));
        }

        String patientId = jwtUtil.getPatientIdFromToken(token);
        PatientService.UpgradeResult result = patientService.upgrade(patientId, request);

        if (result.isSuccess()) {
            return ResponseEntity.ok(ApiResponse.success(result.getMessage(), Map.of("linkedQuestions", result.getLinkedCount())));
        } else {
            return ResponseEntity.ok(ApiResponse.error(result.getCode(), result.getMessage()));
        }
    }
}
