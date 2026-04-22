package com.leansofx.qaserviceuser.controller;

import com.leansofx.qaserviceuser.bean.Doctor;
import com.leansofx.qaserviceuser.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @GetMapping
    public Map<String, Object> getAllDoctors() {
        Map<String, Object> result = new HashMap<>();
        List<Doctor> doctors = doctorService.getAllDoctors();
        result.put("status", "success");
        result.put("data", doctors);
        result.put("count", doctors.size());
        return result;
    }

    @GetMapping("/active")
    public Map<String, Object> getActiveDoctors() {
        Map<String, Object> result = new HashMap<>();
        List<Doctor> doctors = doctorService.getActiveDoctors();
        result.put("status", "success");
        result.put("data", doctors);
        result.put("count", doctors.size());
        return result;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getDoctorById(@PathVariable String id) {
        Map<String, Object> result = new HashMap<>();
        Doctor doctor = doctorService.getDoctorById(id);
        if (doctor != null) {
            result.put("status", "success");
            result.put("data", doctor);
        } else {
            result.put("status", "error");
            result.put("message", "医生不存在");
        }
        return result;
    }

    @GetMapping("/username/{username}")
    public Map<String, Object> getDoctorByUsername(@PathVariable String username) {
        Map<String, Object> result = new HashMap<>();
        Doctor doctor = doctorService.getDoctorByUsername(username);
        if (doctor != null) {
            result.put("status", "success");
            result.put("data", doctor);
        } else {
            result.put("status", "error");
            result.put("message", "医生不存在");
        }
        return result;
    }

    @PostMapping
    public Map<String, Object> addDoctor(@RequestBody Doctor doctor) {
        Map<String, Object> result = new HashMap<>();
        boolean success = doctorService.addDoctor(doctor);
        if (success) {
            result.put("status", "success");
            result.put("message", "医生添加成功");
        } else {
            result.put("status", "error");
            result.put("message", "医生添加失败");
        }
        return result;
    }

    @PutMapping("/{id}")
    public Map<String, Object> updateDoctor(@PathVariable String id, @RequestBody Doctor doctor) {
        Map<String, Object> result = new HashMap<>();
        doctor.setId(id);
        boolean success = doctorService.updateDoctor(doctor);
        if (success) {
            result.put("status", "success");
            result.put("message", "医生更新成功");
        } else {
            result.put("status", "error");
            result.put("message", "医生更新失败");
        }
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteDoctor(@PathVariable String id) {
        Map<String, Object> result = new HashMap<>();
        boolean success = doctorService.deleteDoctor(id);
        if (success) {
            result.put("status", "success");
            result.put("message", "医生删除成功");
        } else {
            result.put("status", "error");
            result.put("message", "医生删除失败");
        }
        return result;
    }
}
