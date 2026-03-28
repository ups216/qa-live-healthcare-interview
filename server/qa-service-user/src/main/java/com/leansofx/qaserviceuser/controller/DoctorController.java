package com.leansofx.qaserviceuser.controller;

import com.leansofx.qaserviceuser.entity.Doctor;
import com.leansofx.qaserviceuser.dto.DoctorDTO;
import com.leansofx.qaserviceuser.service.DoctorService;
import com.leansofx.qaserviceuser.util.DoctorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    // 获取所有医生
    @GetMapping
    public ResponseEntity<List<DoctorDTO>> getAllDoctors() {
        try {
            List<Doctor> doctors = doctorService.getAllDoctors();
            List<DoctorDTO> doctorDTOs = DoctorMapper.toDTOList(doctors);
            return new ResponseEntity<>(doctorDTOs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 根据ID获取医生
    @GetMapping("/{id}")
    public ResponseEntity<DoctorDTO> getDoctorById(@PathVariable String id) {
        try {
            Doctor doctor = doctorService.getDoctorById(id);
            if (doctor == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            DoctorDTO doctorDTO = DoctorMapper.toDTO(doctor);
            return new ResponseEntity<>(doctorDTO, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 多条件查询医生
    @GetMapping("/conditions")
    public ResponseEntity<List<DoctorDTO>> getDoctorsByConditions(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Boolean isActive) {
        try {
            List<Doctor> doctors = doctorService.getDoctorsByConditions(name, department, title, isActive);
            List<DoctorDTO> doctorDTOs = DoctorMapper.toDTOList(doctors);
            return new ResponseEntity<>(doctorDTOs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 获取所有科室列表
    @GetMapping("/departments")
    public ResponseEntity<List<String>> getAllDepartments() {
        try {
            List<String> departments = doctorService.getAllDepartments();
            return new ResponseEntity<>(departments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 获取所有职称列表
    @GetMapping("/titles")
    public ResponseEntity<List<String>> getAllTitles() {
        try {
            List<String> titles = doctorService.getAllTitles();
            return new ResponseEntity<>(titles, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 创建医生
    @PostMapping
    public ResponseEntity<DoctorDTO> createDoctor(@RequestBody DoctorDTO doctorDTO) {
        try {
            Doctor doctor = DoctorMapper.toEntity(doctorDTO);
            Doctor createdDoctor = doctorService.createDoctor(doctor);
            DoctorDTO createdDoctorDTO = DoctorMapper.toDTO(createdDoctor);
            return new ResponseEntity<>(createdDoctorDTO, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 更新医生
    @PutMapping("/{id}")
    public ResponseEntity<DoctorDTO> updateDoctor(@PathVariable String id, @RequestBody DoctorDTO doctorDTO) {
        try {
            Doctor existingDoctor = doctorService.getDoctorById(id);
            if (existingDoctor == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            Doctor doctor = DoctorMapper.toEntity(doctorDTO);
            doctor.setId(id);
            Doctor updatedDoctor = doctorService.updateDoctor(doctor);
            DoctorDTO updatedDoctorDTO = DoctorMapper.toDTO(updatedDoctor);
            return new ResponseEntity<>(updatedDoctorDTO, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 删除医生
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable String id) {
        try {
            Doctor existingDoctor = doctorService.getDoctorById(id);
            if (existingDoctor == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            doctorService.deleteDoctor(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
