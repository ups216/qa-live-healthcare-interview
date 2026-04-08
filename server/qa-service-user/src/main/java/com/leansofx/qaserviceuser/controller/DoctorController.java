package com.leansofx.qaserviceuser.controller;

import com.leansofx.qaserviceuser.dto.ApiResponse;
import com.leansofx.qaserviceuser.dto.DoctorDTO;
import com.leansofx.qaserviceuser.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<DoctorDTO>>> getAllDoctors() {
        try {
            List<DoctorDTO> doctors = doctorService.getAllDoctors();
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ApiResponse.success(doctors));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to fetch doctors", e.getMessage()));
        }
    }

    /**
     * 根据ID获取医生详情
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<DoctorDTO>> getDoctorById(@PathVariable String id) {
        try {
            return doctorService.getDoctorById(id)
                    .map(doctor -> ResponseEntity.ok(ApiResponse.success(doctor)))
                    .orElse(ResponseEntity.status(404).body(ApiResponse.error("Doctor not found", "Doctor with id " + id + " not found")));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Internal Server Error", e.getMessage()));
        }
    }
}
