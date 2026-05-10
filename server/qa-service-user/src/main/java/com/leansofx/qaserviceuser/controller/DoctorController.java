package com.leansofx.qaserviceuser.controller;

import com.leansofx.qaserviceuser.dto.DoctorDTO;
import com.leansofx.qaserviceuser.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {
    
    @Autowired
    private DoctorService doctorService;
    
    /**
     * 获取所有医生列表
     */
    @GetMapping
    public ResponseEntity<List<DoctorDTO>> getAllDoctors() {
        List<DoctorDTO> doctors = doctorService.getAllDoctors();
        return ResponseEntity.ok(doctors);
    }
    
    /**
     * 获取在线医生列表
     */
    @GetMapping("/active")
    public ResponseEntity<List<DoctorDTO>> getActiveDoctors() {
        List<DoctorDTO> doctors = doctorService.getActiveDoctors();
        return ResponseEntity.ok(doctors);
    }
    
    /**
     * 根据ID获取医生信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<DoctorDTO> getDoctorById(@PathVariable String id) {
        DoctorDTO doctor = doctorService.getDoctorById(id);
        if (doctor != null) {
            return ResponseEntity.ok(doctor);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 根据用户名获取医生信息
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<DoctorDTO> getDoctorByUsername(@PathVariable String username) {
        DoctorDTO doctor = doctorService.getDoctorByUsername(username);
        if (doctor != null) {
            return ResponseEntity.ok(doctor);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
