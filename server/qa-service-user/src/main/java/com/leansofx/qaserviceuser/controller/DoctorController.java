package com.leansofx.qaserviceuser.controller;

import com.leansofx.qaserviceuser.entity.Doctor;
import com.leansofx.qaserviceuser.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctors")
//@CrossOrigin(origins = "*")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllDoctors() {
        List<Doctor> doctors = doctorService.getAllDoctors();
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", doctors);
        return ResponseEntity.ok(response);
    }
}
