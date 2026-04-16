package com.leansofx.qaserviceuser.service.impl;

import com.leansofx.qaserviceuser.entity.LoginRequest;
import com.leansofx.qaserviceuser.entity.LoginResponse;
import com.leansofx.qaserviceuser.entity.OutpatientUser;
import com.leansofx.qaserviceuser.mapper.OutpatientUserMapper;
import com.leansofx.qaserviceuser.service.AuthService;
import com.leansofx.qaserviceuser.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private OutpatientUserMapper userMapper;

    @Autowired
    private JwtUtils jwtUtils;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 连续错误次数限制
    private static final int MAX_ERROR_COUNT = 5;
    // 锁定时间（分钟）
    private static final int LOCK_MINUTES = 10;

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();

        // 查询用户
        OutpatientUser user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new AuthException("用户名或密码错误", "AUTH_101", MAX_ERROR_COUNT);
        }

        // 检查账号状态
        if (user.getStatus() == 0) {
            throw new AuthException("账号已被禁用，请联系管理员", "AUTH_103", 0);
        }

        // 检查是否被锁定
        if (user.getLockUntil() != null && user.getLockUntil().isAfter(LocalDateTime.now())) {
            long remainingMinutes = java.time.Duration.between(LocalDateTime.now(), user.getLockUntil()).toMinutes();
            throw new AuthException("账号已锁定，请" + remainingMinutes + "分钟后再试", "AUTH_102", 0);
        }

        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            // 密码错误，增加错误次数
            int newErrorCount = user.getErrorCount() + 1;
            if (newErrorCount >= MAX_ERROR_COUNT) {
                // 锁定账号
                LocalDateTime lockUntil = LocalDateTime.now().plusMinutes(LOCK_MINUTES);
                userMapper.updateErrorCount(user.getId(), newErrorCount, 
                    lockUntil.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                throw new AuthException("账号已锁定，请" + LOCK_MINUTES + "分钟后再试", "AUTH_102", 0);
            } else {
                userMapper.updateErrorCount(user.getId(), newErrorCount, null);
                throw new AuthException("用户名或密码错误", "AUTH_101", MAX_ERROR_COUNT - newErrorCount);
            }
        }

        // 密码正确，重置错误次数
        userMapper.resetErrorCount(user.getId());

        // 生成Token
        String token = jwtUtils.generateToken(user.getId(), user.getUsername());

        return LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(jwtUtils.getExpiration())
                .userInfo(LoginResponse.UserInfo.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .name(user.getName())
                        .status(user.getStatus())
                        .build())
                .build();
    }

    @Override
    public void logout(String token) {
        // JWT无状态，无需特殊处理
        // 如需黑名单机制，可在此处添加
    }

    @Override
    public LoginResponse refreshToken(String token) {
        // 验证Token
        if (!jwtUtils.validateToken(token)) {
            throw new AuthException("Token无效或已过期", "AUTH_104", 0);
        }

        // 获取用户信息
        Long userId = jwtUtils.getUserIdFromToken(token);
        OutpatientUser user = userMapper.selectById(userId);

        if (user == null) {
            throw new AuthException("用户不存在", "AUTH_101", 0);
        }

        if (user.getStatus() == 0) {
            throw new AuthException("账号已被禁用，请联系管理员", "AUTH_103", 0);
        }

        // 生成新Token
        String newToken = jwtUtils.generateToken(user.getId(), user.getUsername());

        return LoginResponse.builder()
                .token(newToken)
                .tokenType("Bearer")
                .expiresIn(jwtUtils.getExpiration())
                .userInfo(LoginResponse.UserInfo.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .name(user.getName())
                        .status(user.getStatus())
                        .build())
                .build();
    }

    @Override
    public OutpatientUser getUserByToken(String token) {
        if (!jwtUtils.validateToken(token)) {
            throw new AuthException("Token无效或已过期", "AUTH_104", 0);
        }

        Long userId = jwtUtils.getUserIdFromToken(token);
        OutpatientUser user = userMapper.selectById(userId);

        if (user == null) {
            throw new AuthException("用户不存在", "AUTH_101", 0);
        }

        return user;
    }

    /**
     * 认证异常
     */
    public static class AuthException extends RuntimeException {
        private final String errorCode;
        private final int remainingAttempts;

        public AuthException(String message, String errorCode, int remainingAttempts) {
            super(message);
            this.errorCode = errorCode;
            this.remainingAttempts = remainingAttempts;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public int getRemainingAttempts() {
            return remainingAttempts;
        }
    }
}
