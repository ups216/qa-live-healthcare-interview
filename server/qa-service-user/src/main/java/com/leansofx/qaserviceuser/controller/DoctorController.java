package com.leansofx.qaserviceuser.controller;

import com.leansofx.qaserviceuser.dto.DoctorDto;
import com.leansofx.qaserviceuser.entity.Doctor;
import com.leansofx.qaserviceuser.repository.DoctorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 医生列表 API，支持前端医生页面（Doctors.vue）展示。
 * GET /api/doctors 返回与前端 Doctor 兼容的 JSON，不包含 password。
 */
@RestController
@RequestMapping("/api")
public class DoctorController {

    private final DoctorRepository doctorRepository;

    public DoctorController(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    /**
     * 获取医生列表，供前端医生页与首页开放诊室使用。
     * 异常时返回 500 与统一错误结构，便于前端与 curl 脚本判断。
     */
    @GetMapping("/doctors")
    public ResponseEntity<List<DoctorDto>> listDoctors() {
        List<Doctor> list = doctorRepository.findAll();
        List<DoctorDto> dtos = list.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /** 实体转 DTO，排除 password；specialties 为 null 时返回空列表，避免前端 v-for 异常 */
    private DoctorDto toDto(Doctor e) {
        DoctorDto dto = new DoctorDto();
        dto.setId(e.getId());
        dto.setUsername(e.getUsername());
        dto.setName(e.getName());
        dto.setTitle(e.getTitle());
        dto.setDepartment(e.getDepartment());
        dto.setAvatar(e.getAvatar());
        dto.setExperience(e.getExperience());
        dto.setSpecialties(e.getSpecialties() != null ? e.getSpecialties() : List.of());
        dto.setIsActive(Boolean.TRUE.equals(e.getIsActive()));
        return dto;
    }
}
