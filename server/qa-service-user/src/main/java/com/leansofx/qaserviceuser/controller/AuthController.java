package com.leansofx.qaserviceuser.controller;

import com.leansofx.qaserviceuser.entity.LoginRequest;
import com.leansofx.qaserviceuser.entity.LoginResponse;
import com.leansofx.qaserviceuser.entity.OutpatientUser;
import com.leansofx.qaserviceuser.service.AuthService;
import com.leansofx.qaserviceuser.service.impl.AuthServiceImpl.AuthException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Map<String, Object> login(@Valid @RequestBody LoginRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            LoginResponse loginResponse = authService.login(request);
            response.put("code", 200);
            response.put("message", "登录成功");
            response.put("data", loginResponse);
        } catch (AuthException e) {
            response.put("code", 401);
            response.put("message", e.getMessage());
            Map<String, Object> data = new HashMap<>();
            data.put("remainingAttempts", e.getRemainingAttempts());
            data.put("errorCode", e.getErrorCode());
            response.put("data", data);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "系统内部错误");
            response.put("data", null);
        }
        return response;
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public Map<String, Object> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                authService.logout(token);
            }
            response.put("code", 200);
            response.put("message", "退出成功");
            response.put("data", null);
        } catch (AuthException e) {
            response.put("code", 401);
            response.put("message", e.getMessage());
            response.put("data", null);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "系统内部错误");
            response.put("data", null);
        }
        return response;
    }

    /**
     * 刷新Token
     */
    @PostMapping("/refresh")
    public Map<String, Object> refresh(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.put("code", 401);
                response.put("message", "Token格式错误");
                response.put("data", null);
                return response;
            }
            String token = authHeader.substring(7);
            LoginResponse loginResponse = authService.refreshToken(token);
            response.put("code", 200);
            response.put("message", "刷新成功");
            response.put("data", loginResponse);
        } catch (AuthException e) {
            response.put("code", 401);
            response.put("message", e.getMessage());
            response.put("data", null);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "系统内部错误");
            response.put("data", null);
        }
        return response;
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/userinfo")
    public Map<String, Object> getUserInfo(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.put("code", 401);
                response.put("message", "请先登录");
                response.put("data", null);
                return response;
            }
            String token = authHeader.substring(7);
            OutpatientUser user = authService.getUserByToken(token);
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", user);
        } catch (AuthException e) {
            response.put("code", 401);
            response.put("message", e.getMessage());
            response.put("data", null);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "系统内部错误");
            response.put("data", null);
        }
        return response;
    }
}
