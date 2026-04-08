package com.leansofx.qaserviceuser.util;

import com.leansofx.qaserviceuser.entity.Doctor;
import com.leansofx.qaserviceuser.dto.DoctorDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;

public class DoctorMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 将 Doctor 实体转换为 DoctorDTO
    public static DoctorDTO toDTO(Doctor doctor) {
        DoctorDTO dto = new DoctorDTO();
        dto.setId(doctor.getId());
        dto.setUsername(doctor.getUsername());
        dto.setPassword(doctor.getPassword());
        dto.setName(doctor.getName());
        dto.setTitle(doctor.getTitle());
        dto.setDepartment(doctor.getDepartment());
        dto.setAvatar(doctor.getAvatar());
        dto.setExperience(doctor.getExperience());
        dto.setActive(doctor.isActive());
        dto.setCreatedAt(doctor.getCreatedAt());
        dto.setUpdatedAt(doctor.getUpdatedAt());
        
        // 处理 specialties 字段
        try {
            if (doctor.getSpecialties() != null && !doctor.getSpecialties().isEmpty()) {
                List<String> specialtiesList = Arrays.asList(doctor.getSpecialtiesArray());
                dto.setSpecialties(specialtiesList);
            }
        } catch (Exception e) {
            dto.setSpecialties(List.of());
        }
        
        return dto;
    }

    // 将 DoctorDTO 转换为 Doctor 实体
    public static Doctor toEntity(DoctorDTO dto) {
        Doctor doctor = new Doctor();
        doctor.setId(dto.getId());
        doctor.setUsername(dto.getUsername());
        doctor.setPassword(dto.getPassword());
        doctor.setName(dto.getName());
        doctor.setTitle(dto.getTitle());
        doctor.setDepartment(dto.getDepartment());
        doctor.setAvatar(dto.getAvatar());
        doctor.setExperience(dto.getExperience());
        doctor.setActive(dto.isActive());
        doctor.setCreatedAt(dto.getCreatedAt());
        doctor.setUpdatedAt(dto.getUpdatedAt());
        
        // 处理 specialties 字段
        try {
            if (dto.getSpecialties() != null && !dto.getSpecialties().isEmpty()) {
                String specialtiesJson = objectMapper.writeValueAsString(dto.getSpecialties());
                doctor.setSpecialties(specialtiesJson);
            } else {
                doctor.setSpecialties("[]");
            }
        } catch (JsonProcessingException e) {
            doctor.setSpecialties("[]");
        }
        
        return doctor;
    }

    // 批量转换
    public static List<DoctorDTO> toDTOList(List<Doctor> doctors) {
        return doctors.stream()
                .map(DoctorMapper::toDTO)
                .toList();
    }
}
