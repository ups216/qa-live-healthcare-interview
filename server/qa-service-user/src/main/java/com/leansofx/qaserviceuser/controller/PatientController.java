package com.leansofx.qaserviceuser.controller;

import com.leansofx.qaserviceuser.entity.Patient;
import com.leansofx.qaserviceuser.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/patients")
//@CrossOrigin(origins = "*")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        
        Map<String, Object> response = new HashMap<>();
        
        if (username == null || username.trim().isEmpty()) {
            response.put("code", 400);
            response.put("message", "用户名不能为空");
            response.put("data", null);
            return ResponseEntity.badRequest().body(response);
        }
        
        if (password == null || password.trim().isEmpty()) {
            response.put("code", 400);
            response.put("message", "密码不能为空");
            response.put("data", null);
            return ResponseEntity.badRequest().body(response);
        }
        
        Optional<Patient> patient = patientService.login(username, password);
        
        if (patient.isPresent()) {
            Map<String, Object> data = new HashMap<>();
            data.put("token", "token_" + System.currentTimeMillis());
            
            Patient p = patient.get();
            Map<String, Object> patientInfo = new HashMap<>();
            patientInfo.put("id", p.getId());
            patientInfo.put("username", p.getUsername());
            patientInfo.put("name", p.getName());
            patientInfo.put("phone", p.getPhone());
            patientInfo.put("gender", p.getGender());
            data.put("patient", patientInfo);
            
            response.put("code", 200);
            response.put("message", "登录成功");
            response.put("data", data);
            return ResponseEntity.ok(response);
        } else {
            response.put("code", 401);
            response.put("message", "用户名或密码错误");
            response.put("data", null);
            return ResponseEntity.status(401).body(response);
        }
    }
}
