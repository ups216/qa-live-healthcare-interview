package com.leansofx.qaserviceuser.service;

import com.leansofx.qaserviceuser.dto.DoctorDTO;
import com.leansofx.qaserviceuser.entity.Doctor;
import com.leansofx.qaserviceuser.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class DoctorService {

    private static final Logger logger = Logger.getLogger(DoctorService.class.getName());

    @Autowired
    private DoctorRepository doctorRepository;

    /**
     * 获取所有医生列表
     */
    public List<DoctorDTO> getAllDoctors() {
        List<Doctor> doctors = doctorRepository.findAll();
        List<DoctorDTO> doctorDTOs = new ArrayList<>();

        for (Doctor doctor : doctors) {
            doctorDTOs.add(convertToDTO(doctor));
        }

        return doctorDTOs;
    }

    /**
     * 根据ID获取医生详情
     */
    public Optional<DoctorDTO> getDoctorById(String id) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(id);
        return doctorOpt.map(this::convertToDTO);
    }

    /**
     * 将Entity转换为DTO
     */
    private DoctorDTO convertToDTO(Doctor doctor) {
        DoctorDTO dto = new DoctorDTO();
        dto.setId(doctor.getId());
        dto.setUsername(doctor.getUsername());
        dto.setName(doctor.getName());
        dto.setTitle(doctor.getTitle());
        dto.setDepartment(doctor.getDepartment());
        dto.setAvatar(doctor.getAvatar());
        dto.setExperience(doctor.getExperience());
        dto.setSpecialties(parseSpecialties(doctor.getSpecialties()));
        dto.setActive(doctor.getIsActive() != null ? doctor.getIsActive() : false);
        return dto;
    }

    /**
     * 解析specialties JSON字符串为List
     */
    private List<String> parseSpecialties(String specialtiesJson) {
        if (specialtiesJson == null || specialtiesJson.isEmpty()) {
            return List.of();
        }
        try {
            String trimmed = specialtiesJson.trim();
            if (trimmed.startsWith("[")) {
                String content = trimmed.substring(1, trimmed.length() - 1);
                return List.of(content.split(",")).stream()
                        .map(s -> s.trim().replace("\"", ""))
                        .toList();
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to parse specialties: " + e.getMessage(), e);
        }
        return List.of();
    }
}
