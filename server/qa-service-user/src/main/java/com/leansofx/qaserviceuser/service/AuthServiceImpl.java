package com.leansofx.qaserviceuser.service;

import com.leansofx.qaserviceuser.config.JwtUtil;
import com.leansofx.qaserviceuser.dto.LoginRequest;
import com.leansofx.qaserviceuser.dto.LoginResponse;
import com.leansofx.qaserviceuser.dto.UserDTO;
import com.leansofx.qaserviceuser.entity.PatientUser;
import com.leansofx.qaserviceuser.repository.PatientUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {
    
    private final PatientUserRepository patientUserRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;
    
    @Value("${app.auth.jwt.expiration:7200000}")
    private long tokenExpiration;
    
    public AuthServiceImpl(PatientUserRepository patientUserRepository, 
                          JwtUtil jwtUtil) {
        this.patientUserRepository = patientUserRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }
    
    @Override
    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        // 1. 查询用户
        PatientUser user = patientUserRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("INVALID_CREDENTIALS"));
        
        // 2. 检查账户是否激活
        if (!user.getIsActive()) {
            throw new RuntimeException("ACCOUNT_INACTIVE");
        }
        
        // 3. 检查账户是否被锁定
        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("ACCOUNT_LOCKED");
        }
        
        // 4. 验证密码
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            // 增加失败计数
            handleFailedLogin(user);
            throw new RuntimeException("INVALID_CREDENTIALS");
        }
        
        // 5. 登录成功，重置失败计数
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        patientUserRepository.save(user);
        
        // 6. 生成JWT Token
        String token = jwtUtil.generateToken(user.getUsername(), user.getId());
        
        // 7. 构建响应
        UserDTO userDTO = new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone()
        );
        
        return new LoginResponse(token, tokenExpiration / 1000, userDTO);
    }
    
    @Override
    public UserDTO getCurrentUser(String token) {
        String username = jwtUtil.extractUsername(token);
        PatientUser user = patientUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));
        
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone()
        );
    }
    
    private void handleFailedLogin(PatientUser user) {
        int failedAttempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(failedAttempts);
        
        // 连续5次失败，锁定30分钟
        if (failedAttempts >= 5) {
            user.setLockedUntil(LocalDateTime.now().plusMinutes(30));
        }
        
        patientUserRepository.save(user);
    }
}
