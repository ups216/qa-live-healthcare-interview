package com.leansofx.qaserviceuser.controller;

import com.leansofx.qaserviceuser.entity.Patient;
import com.leansofx.qaserviceuser.service.PatientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String password = request.get("password");
            String name = request.get("name");
            String phone = request.get("phone");

            Patient patient = patientService.register(username, password, name, phone);

            Map<String, Object> response = new HashMap<>();
            response.put("id", patient.getId());
            response.put("username", patient.getUsername());
            response.put("name", patient.getName());
            response.put("phone", patient.getPhone());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        Patient patient = patientService.login(username, password);
        if (patient != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("id", patient.getId());
            response.put("username", patient.getUsername());
            response.put("name", patient.getName());
            response.put("phone", patient.getPhone());
            response.put("gender", patient.getGender());
            response.put("birthday", patient.getBirthday());

            return ResponseEntity.ok(response);
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("message", "用户名或密码错误");
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPatient(@PathVariable String id) {
        Patient patient = patientService.findById(id);
        if (patient != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("id", patient.getId());
            response.put("username", patient.getUsername());
            response.put("name", patient.getName());
            response.put("phone", patient.getPhone());
            response.put("gender", patient.getGender());
            response.put("birthday", patient.getBirthday());

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
