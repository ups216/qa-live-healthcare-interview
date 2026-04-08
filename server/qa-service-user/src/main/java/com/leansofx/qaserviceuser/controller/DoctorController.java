package com.leansofx.qaserviceuser.controller;

import com.leansofx.qaserviceuser.dto.DoctorDTO;
import com.leansofx.qaserviceuser.entity.Doctor;
import com.leansofx.qaserviceuser.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(originPatterns = "*", allowCredentials = "false")
public class DoctorController {

    @Autowired
    private DoctorRepository doctorRepository;

    @GetMapping("/doctors")
    public ResponseEntity<List<DoctorDTO>> getAllDoctors() {
        List<Doctor> doctors = doctorRepository.findAll();
        List<DoctorDTO> doctorDTOs = doctors.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(doctorDTOs);
    }

    @GetMapping("/doctors/active")
    public ResponseEntity<List<DoctorDTO>> getActiveDoctors() {
        List<Doctor> doctors = doctorRepository.findByIsActiveTrue();
        List<DoctorDTO> doctorDTOs = doctors.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(doctorDTOs);
    }

    private DoctorDTO convertToDTO(Doctor doctor) {
        DoctorDTO dto = new DoctorDTO();
        dto.setId(doctor.getId());
        dto.setUsername(doctor.getUsername());
        dto.setName(doctor.getName());
        dto.setTitle(doctor.getTitle());
        dto.setDepartment(doctor.getDepartment());
        dto.setAvatar(doctor.getAvatar());
        dto.setExperience(doctor.getExperience());
        dto.setSpecialties(doctor.getSpecialties());
        dto.setIsActive(doctor.getIsActive());
        return dto;
    }
}
