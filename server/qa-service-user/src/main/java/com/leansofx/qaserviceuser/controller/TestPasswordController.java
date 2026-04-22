package com.leansofx.qaserviceuser.controller;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test-password")
public class TestPasswordController {

    @PostMapping("/reset-password")
    public Map<String, Object> resetPassword(@RequestBody Map<String, String> resetRequest) {
        Map<String, Object> result = new HashMap<>();
        String name = resetRequest.get("name");
        String birthdayStr = resetRequest.get("birthday");
        String newPassword = resetRequest.get("newPassword");
        
        if (name == null || birthdayStr == null || newPassword == null) {
            result.put("status", "error");
            result.put("message", "姓名、生日和新密码不能为空");
            return result;
        }
        
        result.put("status", "success");
        result.put("message", "密码重置成功");
        result.put("name", name);
        result.put("birthday", birthdayStr);
        return result;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginRequest) {
        Map<String, Object> result = new HashMap<>();
        String name = loginRequest.get("name");
        String password = loginRequest.get("password");
        
        if (name == null || password == null) {
            result.put("status", "error");
            result.put("message", "用户名和密码不能为空");
            return result;
        }
        
        result.put("status", "success");
        result.put("message", "登录成功");
        result.put("name", name);
        return result;
    }
}