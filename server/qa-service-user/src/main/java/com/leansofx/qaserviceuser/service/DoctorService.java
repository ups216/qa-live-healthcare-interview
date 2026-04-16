package com.leansofx.qaserviceuser.service;

import com.leansofx.qaserviceuser.dto.DoctorAuthRequest;
import com.leansofx.qaserviceuser.dto.DoctorAuthResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.leansofx.qaserviceuser.dto.DoctorResponse;
import com.leansofx.qaserviceuser.entity.Doctor;
import com.leansofx.qaserviceuser.mapper.DoctorMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

@Service
public class DoctorService {

    private final DoctorMapper doctorMapper;

    public DoctorService(DoctorMapper doctorMapper) {
        this.doctorMapper = doctorMapper;
    }

    public List<DoctorResponse> getAllDoctors() {
        return doctorMapper.selectList(null).stream().map(this::toResponse).toList();
    }

    public List<DoctorResponse> getActiveDoctors() {
        LambdaQueryWrapper<Doctor> query = new LambdaQueryWrapper<>();
        query.eq(Doctor::getActive, true);
        return doctorMapper.selectList(query).stream().map(this::toResponse).toList();
    }

    public DoctorAuthResponse login(DoctorAuthRequest request) {
        if (request.username() == null || request.username().isBlank() || request.password() == null || request.password().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "用户名和密码不能为空");
        }

        LambdaQueryWrapper<Doctor> query = new LambdaQueryWrapper<>();
        query.eq(Doctor::getUsername, request.username().trim());
        Doctor doctor = doctorMapper.selectOne(query);
        if (doctor == null || !doctor.getPassword().equals(request.password().trim())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户名或密码错误");
        }

        return new DoctorAuthResponse(
                doctor.getId(),
                doctor.getUsername(),
                doctor.getName(),
                doctor.getTitle(),
                doctor.getDepartment(),
                doctor.getAvatar(),
                doctor.getExperience(),
                Arrays.stream(doctor.getSpecialties().split(",")).map(String::trim).toList(),
                Boolean.TRUE.equals(doctor.getActive()),
                "登录成功"
        );
    }

    private DoctorResponse toResponse(Doctor doctor) {
        return new DoctorResponse(
                doctor.getId(),
                doctor.getUsername(),
                doctor.getName(),
                doctor.getTitle(),
                doctor.getDepartment(),
                doctor.getAvatar(),
                doctor.getExperience(),
                Arrays.stream(doctor.getSpecialties().split(",")).map(String::trim).toList(),
                Boolean.TRUE.equals(doctor.getActive())
        );
    }
}
