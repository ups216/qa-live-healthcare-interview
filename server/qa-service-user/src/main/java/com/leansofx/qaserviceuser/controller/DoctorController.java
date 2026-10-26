package com.leansofx.qaserviceuser.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.leansofx.qaserviceuser.dto.ApiResponse;
import com.leansofx.qaserviceuser.entity.Doctor;
import com.leansofx.qaserviceuser.mapper.DoctorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorMapper doctorMapper;

    @GetMapping
    public ApiResponse<List<Doctor>> list(@RequestParam(required = false) String status) {
        LambdaQueryWrapper<Doctor> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isBlank()) {
            wrapper.eq(Doctor::getStatus, status);
        }
        return ApiResponse.success(doctorMapper.selectList(wrapper));
    }

    @GetMapping("/{username}")
    public ApiResponse<Doctor> getByUsername(@PathVariable String username) {
        LambdaQueryWrapper<Doctor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Doctor::getUsername, username);
        Doctor doctor = doctorMapper.selectOne(wrapper);
        if (doctor == null) {
            return ApiResponse.error(404, "医生不存在");
        }
        return ApiResponse.success(doctor);
    }
}