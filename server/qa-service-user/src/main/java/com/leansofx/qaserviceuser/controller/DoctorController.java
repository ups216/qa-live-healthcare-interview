package com.leansofx.qaserviceuser.controller;

import com.leansofx.qaserviceuser.dto.DoctorDTO;
import com.leansofx.qaserviceuser.service.DoctorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@CrossOrigin(origins = "*")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping
    public ResponseEntity<List<DoctorDTO>> getAllDoctors(
            @RequestParam(required = false, defaultValue = "zh-CN") String locale) {
        List<DoctorDTO> doctors = doctorService.getAllDoctors(locale);
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/active")
    public ResponseEntity<List<DoctorDTO>> getActiveDoctors(
            @RequestParam(required = false, defaultValue = "zh-CN") String locale) {
        List<DoctorDTO> doctors = doctorService.getActiveDoctors(locale);
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/{username}")
    public ResponseEntity<DoctorDTO> getDoctorByUsername(
            @PathVariable String username,
            @RequestParam(required = false, defaultValue = "zh-CN") String locale) {
        DoctorDTO doctor = doctorService.getDoctorByUsername(username, locale);
        if (doctor == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(doctor);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<DoctorDTO> getDoctorById(
            @PathVariable String id,
            @RequestParam(required = false, defaultValue = "zh-CN") String locale) {
        DoctorDTO doctor = doctorService.getDoctorById(id, locale);
        if (doctor == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(doctor);
    }
}
