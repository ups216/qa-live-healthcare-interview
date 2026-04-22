package com.leansofx.qaservicequestion.controller;

import com.leansofx.qaservicequestion.bean.Patient;
import com.leansofx.qaservicequestion.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @PostMapping(value = "/login", consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    public Map<String, Object> login(@RequestBody Map<String, String> loginRequest) {
        Map<String, Object> result = new HashMap<>();
        
        String name = loginRequest.get("name");
        String password = loginRequest.get("password");
        
        System.out.println("Received login request: name=" + name + ", password=" + password);
        
        if (name == null || password == null) {
            result.put("status", "error");
            result.put("message", "用户名和密码不能为空");
            return result;
        }
        
        // 查询数据库判断用户名与密码是否一致
        Patient patient = patientService.login(name, password);
        if (patient != null) {
            result.put("status", "success");
            result.put("message", "登录成功");
            result.put("name", patient.getName());
        } else {
            result.put("status", "error");
            result.put("message", "用户名或密码错误");
        }
        
        return result;
    }
}
