package com.leansofx.qaserviceuser.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leansofx.qaserviceuser.dto.DoctorResponse;
import com.leansofx.qaserviceuser.entity.Doctor;
import com.leansofx.qaserviceuser.repository.DoctorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 医生业务逻辑层。负责查询医生列表并转换为 API 响应 DTO（不暴露 password）。
 */
@Service
public class DoctorService {

    private static final Logger log = LoggerFactory.getLogger(DoctorService.class);

    private final DoctorRepository doctorRepository;
    private final ObjectMapper objectMapper;

    public DoctorService(DoctorRepository doctorRepository, ObjectMapper objectMapper) {
        this.doctorRepository = doctorRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * 查询全部医生，转换为前端所需的 DTO 列表（不含密码）。
     */
    @Transactional(readOnly = true)
    public List<DoctorResponse> findAll() {
        List<Doctor> doctors = doctorRepository.findAll();
        return doctors.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 实体转 DTO，解析 specialties JSON 字符串为 List；不包含 password。
     */
    private DoctorResponse toResponse(Doctor entity) {
        DoctorResponse dto = new DoctorResponse();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setName(entity.getName());
        dto.setTitle(entity.getTitle());
        dto.setDepartment(entity.getDepartment());
        dto.setAvatar(entity.getAvatar());
        dto.setExperience(entity.getExperience());
        dto.setIsActive(entity.getIsActive() != null ? entity.getIsActive() : true);
        dto.setSpecialties(parseSpecialties(entity.getSpecialtiesJson()));
        return dto;
    }

    /** 将 JSON 数组字符串解析为 List，解析失败时返回空列表 */
    private List<String> parseSpecialties(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            log.warn("医生专长 JSON 解析失败，doctor 数据可能异常: json={}, error={}", json, e.getMessage());
            return Collections.emptyList();
        }
    }
}
