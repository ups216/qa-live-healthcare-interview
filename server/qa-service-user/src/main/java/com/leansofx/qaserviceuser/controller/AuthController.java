package com.leansofx.qaserviceuser.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.leansofx.qaserviceuser.config.JwtUtil;
import com.leansofx.qaserviceuser.dto.ApiResponse;
import com.leansofx.qaserviceuser.dto.LoginRequest;
import com.leansofx.qaserviceuser.dto.LoginResponse;
import com.leansofx.qaserviceuser.entity.User;
import com.leansofx.qaserviceuser.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ApiResponse<?> login(@RequestBody LoginRequest request) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername());
        User user = userMapper.selectOne(wrapper);

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("登录失败: 用户名={}", request.getUsername());
            return ApiResponse.error(401, "用户名或密码错误");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getId());
        log.info("登录成功: 用户名={}", user.getUsername());

        LoginResponse response = LoginResponse.builder()
                .token(token)
                .user(LoginResponse.UserInfo.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .realName(user.getRealName())
                        .phone(user.getPhone())
                        .build())
                .build();

        return ApiResponse.success("登录成功", response);
    }

    @PostMapping("/fix-password")
    public ApiResponse<?> fixPassword(@RequestParam(defaultValue = "123456") String password) {
        String hash = passwordEncoder.encode(password);
        log.info("生成 BCrypt hash: {}", hash);

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, "admin");
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            return ApiResponse.error(404, "用户 admin 不存在");
        }

        user.setPassword(hash);
        userMapper.updateById(user);
        log.info("已更新 admin 密码，hash={}", hash);

        return ApiResponse.success("密码已更新", hash);
    }

}