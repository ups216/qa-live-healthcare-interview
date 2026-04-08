package com.leansofx.qaserviceuser.service;

import com.leansofx.qaserviceuser.dto.PatientDTO;
import com.leansofx.qaserviceuser.dto.LoginRequest;
import com.leansofx.qaserviceuser.dto.RegisterRequest;
import com.leansofx.qaserviceuser.entity.Patient;
import com.leansofx.qaserviceuser.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PatientService {

    private static final Logger logger = LoggerFactory.getLogger(PatientService.class);

    @Autowired
    private PatientRepository patientRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    /**
     * 用户注册
     */
    public PatientDTO register(RegisterRequest request) {
        // 检查用户名是否已存在
        if (patientRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("USERNAME_EXISTS");
        }

        Patient patient = new Patient();
        patient.setId(UUID.randomUUID().toString());
        patient.setUsername(request.getUsername());
        patient.setPassword(passwordEncoder.encode(request.getPassword()));
        patient.setName(request.getName());
        patient.setGender(request.getGender());
        patient.setBirthday(request.getBirthday());
        patient.setPhone(request.getPhone());
        patient.setIsActive(true);

        patientRepository.save(patient);  // JPA save

        logger.info("Patient registered: username={}", request.getUsername());
        return convertToDTO(patient);
    }

    /**
     * 用户登录
     */
    public PatientDTO login(LoginRequest request) {
        String inputUsername = request.getUsername();
        Patient patient = patientRepository.findByUsername(inputUsername)
                .orElse(null);

        // 统一提示：不泄露用户是否存在
        if (patient == null || !passwordEncoder.matches(request.getPassword(), patient.getPassword())) {
            throw new RuntimeException("INVALID_CREDENTIALS");
        }

        // 大小写精确匹配（防止 DB collation 不区分大小写导致的安全绕过）
        if (!inputUsername.equals(patient.getUsername())) {
            throw new RuntimeException("INVALID_CREDENTIALS");
        }

        // 检查账号是否被禁用
        if (!Boolean.TRUE.equals(patient.getIsActive())) {
            throw new RuntimeException("ACCOUNT_DISABLED");
        }

        logger.info("Patient login success: username={}", request.getUsername());
        return convertToDTO(patient);
    }

    /**
     * 退出登录（服务端清除逻辑，如有 session/token 可在此扩展）
     */
    public void logout() {
        // 当前版本无状态认证，退出仅由前端处理
        // 预留服务端清理入口，便于后续接入 session / JWT
        logger.info("Patient logout");
    }

    /**
     * 检查用户名是否可用
     */
    public boolean checkUsernameExists(String username) {
        return patientRepository.existsByUsername(username);
    }

    /**
     * 将 Entity 转换为 DTO（不含密码字段）
     */
    private PatientDTO convertToDTO(Patient patient) {
        return new PatientDTO(
                patient.getId(),
                patient.getUsername(),
                patient.getName(),
                patient.getGender(),
                patient.getBirthday() != null ? patient.getBirthday().toString() : null,
                patient.getPhone(),
                Boolean.TRUE.equals(patient.getIsActive())
        );
    }
}
