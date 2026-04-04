package com.leansofx.qaserviceuser.service;

import com.leansofx.qaserviceuser.dto.*;
import com.leansofx.qaserviceuser.entity.Patient;
import com.leansofx.qaserviceuser.entity.PatientRefreshToken;
import com.leansofx.qaserviceuser.repository.PatientRefreshTokenRepository;
import com.leansofx.qaserviceuser.repository.PatientRepository;
import com.leansofx.qaserviceuser.util.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class PatientService {

    private static final int ACCESS_TOKEN_EXPIRE_HOURS = 2;
    private static final int REFRESH_TOKEN_EXPIRE_DAYS = 7;

    private final PatientRepository patientRepository;
    private final PatientRefreshTokenRepository refreshTokenRepository;
    private final SmsCodeService smsCodeService;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    public PatientService(PatientRepository patientRepository,
                         PatientRefreshTokenRepository refreshTokenRepository,
                         SmsCodeService smsCodeService,
                         JwtUtil jwtUtil) {
        this.patientRepository = patientRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.smsCodeService = smsCodeService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public boolean checkPhoneExists(String phone) {
        return patientRepository.existsByPhone(phone);
    }

    @Transactional
    public RegisterResult register(PatientRegisterRequest request) {
        if (patientRepository.existsByPhone(request.getPhone())) {
            return RegisterResult.error(40901, "该手机号已注册");
        }

        SmsCodeService.VerifyResult verifyResult = smsCodeService.verifyCode(
                request.getPhone(), request.getCode(), "register");
        if (!verifyResult.isSuccess()) {
            return RegisterResult.error(verifyResult.getCode(), verifyResult.getMessage());
        }

        Patient patient = new Patient();
        patient.setId(UUID.randomUUID().toString());
        patient.setPhone(request.getPhone());
        patient.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        patient.setName(request.getName());
        if (request.getGender() != null) {
            patient.setGender(request.getGender());
        }
        if (request.getBirthday() != null && !request.getBirthday().isEmpty()) {
            patient.setBirthday(LocalDate.parse(request.getBirthday()));
        }
        patient.setStatus(1);
        patient.setFailedAttempts(0);

        patient = patientRepository.save(patient);

        LoginResponse loginResponse = generateLoginResponse(patient);
        return RegisterResult.success(loginResponse);
    }

    @Transactional
    public LoginResult login(PatientLoginRequest request) {
        Patient patient = patientRepository.findByPhone(request.getPhone()).orElse(null);

        if (patient == null) {
            return LoginResult.error(40401, "该手机号未注册");
        }

        if (patient.isLocked()) {
            return LoginResult.error(40102, "账户已锁定，请15分钟后重试");
        }

        if (!passwordEncoder.matches(request.getPassword(), patient.getPasswordHash())) {
            patient.incrementFailedAttempts();
            patientRepository.save(patient);
            return LoginResult.error(40101, "手机号或密码错误");
        }

        patient.resetFailedAttempts();
        patientRepository.save(patient);

        LoginResponse loginResponse = generateLoginResponse(patient);
        return LoginResult.success(loginResponse);
    }

    @Transactional
    public TokenRefreshResult refreshToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken) || !jwtUtil.isRefreshToken(refreshToken)) {
            return TokenRefreshResult.error(40103, "Refresh Token 无效或已过期");
        }

        PatientRefreshToken storedToken = refreshTokenRepository
                .findByTokenAndExpiredAtAfter(refreshToken, java.time.LocalDateTime.now())
                .orElse(null);

        if (storedToken == null) {
            return TokenRefreshResult.error(40103, "Refresh Token 无效或已过期");
        }

        Patient patient = patientRepository.findById(storedToken.getPatientId()).orElse(null);
        if (patient == null) {
            return TokenRefreshResult.error(40103, "用户不存在");
        }

        storedToken.setToken(jwtUtil.generateRefreshToken(patient.getId()));
        refreshTokenRepository.save(storedToken);

        String accessToken = jwtUtil.generateAccessToken(patient.getId(), patient.getPhone(), patient.getName());
        LoginResponse response = new LoginResponse(
                convertToDTO(patient),
                accessToken,
                storedToken.getToken(),
                ACCESS_TOKEN_EXPIRE_HOURS * 3600L
        );

        return TokenRefreshResult.success(response);
    }

    @Transactional
    public void logout(String patientId) {
        refreshTokenRepository.deleteByPatientId(patientId);
    }

    public PatientDTO getPatientById(String id) {
        return patientRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    @Transactional
    public UpgradeResult upgrade(String currentPatientId, AccountUpgradeRequest request) {
        if (patientRepository.existsByPhone(request.getPhone())) {
            return UpgradeResult.error(40901, "该手机号已被注册");
        }

        Patient currentPatient = patientRepository.findById(currentPatientId).orElse(null);
        if (currentPatient == null) {
            return UpgradeResult.error(40401, "用户不存在");
        }

        SmsCodeService.VerifyResult verifyResult = smsCodeService.verifyCode(
                request.getPhone(), request.getCode(), "register");
        if (!verifyResult.isSuccess()) {
            return UpgradeResult.error(verifyResult.getCode(), verifyResult.getMessage());
        }

        currentPatient.setPhone(request.getPhone());
        currentPatient.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        patientRepository.save(currentPatient);

        int linkedCount = linkHistoryQuestions(currentPatient.getName(), request.getPhone());

        return UpgradeResult.success(linkedCount);
    }

    private int linkHistoryQuestions(String patientName, String phone) {
        return 0;
    }

    private LoginResponse generateLoginResponse(Patient patient) {
        String accessToken = jwtUtil.generateAccessToken(patient.getId(), patient.getPhone(), patient.getName());
        String refreshToken = jwtUtil.generateRefreshToken(patient.getId());

        PatientRefreshToken tokenEntity = new PatientRefreshToken(
                patient.getId(), refreshToken, REFRESH_TOKEN_EXPIRE_DAYS);
        refreshTokenRepository.save(tokenEntity);

        return new LoginResponse(
                convertToDTO(patient),
                accessToken,
                refreshToken,
                ACCESS_TOKEN_EXPIRE_HOURS * 3600L
        );
    }

    private PatientDTO convertToDTO(Patient patient) {
        PatientDTO dto = new PatientDTO();
        dto.setId(patient.getId());
        dto.setPhone(patient.getMaskedPhone());
        dto.setName(patient.getName());
        dto.setGender(patient.getGender());
        dto.setBirthday(patient.getBirthday());
        dto.setAvatar(patient.getAvatar());
        dto.setStatus(patient.getStatus());
        dto.setCreatedAt(patient.getCreatedAt());
        return dto;
    }

    public static class RegisterResult {
        private final boolean success;
        private final int code;
        private final String message;
        private final LoginResponse data;

        private RegisterResult(boolean success, int code, String message, LoginResponse data) {
            this.success = success;
            this.code = code;
            this.message = message;
            this.data = data;
        }

        public static RegisterResult success(LoginResponse data) {
            return new RegisterResult(true, 0, "注册成功", data);
        }

        public static RegisterResult error(int code, String message) {
            return new RegisterResult(false, code, message, null);
        }

        public boolean isSuccess() { return success; }
        public int getCode() { return code; }
        public String getMessage() { return message; }
        public LoginResponse getData() { return data; }
    }

    public static class LoginResult {
        private final boolean success;
        private final int code;
        private final String message;
        private final LoginResponse data;

        private LoginResult(boolean success, int code, String message, LoginResponse data) {
            this.success = success;
            this.code = code;
            this.message = message;
            this.data = data;
        }

        public static LoginResult success(LoginResponse data) {
            return new LoginResult(true, 0, "登录成功", data);
        }

        public static LoginResult error(int code, String message) {
            return new LoginResult(false, code, message, null);
        }

        public boolean isSuccess() { return success; }
        public int getCode() { return code; }
        public String getMessage() { return message; }
        public LoginResponse getData() { return data; }
    }

    public static class TokenRefreshResult {
        private final boolean success;
        private final int code;
        private final String message;
        private final LoginResponse data;

        private TokenRefreshResult(boolean success, int code, String message, LoginResponse data) {
            this.success = success;
            this.code = code;
            this.message = message;
            this.data = data;
        }

        public static TokenRefreshResult success(LoginResponse data) {
            return new TokenRefreshResult(true, 0, "刷新成功", data);
        }

        public static TokenRefreshResult error(int code, String message) {
            return new TokenRefreshResult(false, code, message, null);
        }

        public boolean isSuccess() { return success; }
        public int getCode() { return code; }
        public String getMessage() { return message; }
        public LoginResponse getData() { return data; }
    }

    public static class UpgradeResult {
        private final boolean success;
        private final int code;
        private final String message;
        private final int linkedCount;

        private UpgradeResult(boolean success, int code, String message, int linkedCount) {
            this.success = success;
            this.code = code;
            this.message = message;
            this.linkedCount = linkedCount;
        }

        public static UpgradeResult success(int linkedCount) {
            return new UpgradeResult(true, 0, "升级成功", linkedCount);
        }

        public static UpgradeResult error(int code, String message) {
            return new UpgradeResult(false, code, message, 0);
        }

        public boolean isSuccess() { return success; }
        public int getCode() { return code; }
        public String getMessage() { return message; }
        public int getLinkedCount() { return linkedCount; }
    }
}
