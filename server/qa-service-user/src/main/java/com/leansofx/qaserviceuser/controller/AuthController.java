package com.leansofx.qaserviceuser.controller;

import com.leansofx.qaserviceuser.domain.Doctor;
import com.leansofx.qaserviceuser.domain.Patient;
import com.leansofx.qaserviceuser.dto.LoginRequest;
import com.leansofx.qaserviceuser.repository.DoctorMapper;
import com.leansofx.qaserviceuser.repository.PatientMapper;
import com.leansofx.qaserviceuser.util.JwtUtil;
import com.leansofx.qaserviceuser.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwt.expiration:7200000}")
    private long expirationMillis;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        if ("patient".equalsIgnoreCase(request.getType())) {
            return patientLogin(request.getName(), request.getBirthday());
        }
        // 默认走医生登录
        return doctorLogin(request.getUsername(), request.getPassword());
    }

    private ResponseEntity<?> doctorLogin(String username, String password) {
        if (username == null || password == null) {
            return badRequest("用户名或密码不能为空");
        }
        String passwordMd5 = MD5Util.md5(password);
        Doctor doctor = doctorMapper.findByUsername(username);
        if (doctor != null && passwordMd5.equals(doctor.getPassword())) {
            return buildSuccess("DOCTOR", doctor.getId(), doctor.getUsername(), doctor.getName());
        }
        return unauthorized("用户名或密码错误");
    }

    private ResponseEntity<?> patientLogin(String name, String birthday) {
        if (name == null || birthday == null) {
            return badRequest("姓名和出生日期不能为空");
        }
        Patient patient = patientMapper.findByUnique(name, birthday);
        if (patient == null) {
            // 首次输入：自动创建账户
            patient = new Patient();
            patient.setId(UUID.randomUUID().toString().replace("-", ""));
            patient.setName(name);
            patient.setBirthday(birthday);
            patientMapper.insert(patient);
        }
        // 再次输入相同信息即可登录
        return buildSuccess("PATIENT", patient.getId(), patient.getName(), patient.getName());
    }

    private ResponseEntity<?> buildSuccess(String role, String id, String username, String name) {
        String token = jwtUtil.generateToken(username, role);

        Map<String, Object> user = new LinkedHashMap<>();
        user.put("id", id);
        user.put("username", username);
        user.put("name", name);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("token", token);
        result.put("expiresIn", expirationMillis / 1000); // 返回秒
        result.put("role", role);
        result.put("user", user);
        return ResponseEntity.ok(result);
    }

    private ResponseEntity<?> unauthorized(String message) {
        Map<String, String> error = new LinkedHashMap<>();
        error.put("message", message);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    private ResponseEntity<?> badRequest(String message) {
        Map<String, String> error = new LinkedHashMap<>();
        error.put("message", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
