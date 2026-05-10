package com.leansofx.qaserviceuser.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leansofx.qaserviceuser.dto.DoctorDTO;
import com.leansofx.qaserviceuser.entity.Doctor;
import com.leansofx.qaserviceuser.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DoctorService {
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 获取所有医生列表
     */
    public List<DoctorDTO> getAllDoctors() {
        List<Doctor> doctors = doctorRepository.findAll();
        return doctors.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取在线医生列表
     */
    public List<DoctorDTO> getActiveDoctors() {
        List<Doctor> doctors = doctorRepository.findByIsActive(true);
        return doctors.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据ID获取医生信息
     */
    public DoctorDTO getDoctorById(String id) {
        return doctorRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }
    
    /**
     * 根据用户名获取医生信息
     */
    public DoctorDTO getDoctorByUsername(String username) {
        return doctorRepository.findByUsername(username)
                .map(this::convertToDTO)
                .orElse(null);
    }
    
    /**
     * 将Doctor实体转换为DoctorDTO
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
        dto.setIsActive(doctor.getIsActive());
        
        // 将specialties JSON字符串转换为List
        if (doctor.getSpecialties() != null && !doctor.getSpecialties().isEmpty()) {
            try {
                List<String> specialtiesList = objectMapper.readValue(
                    doctor.getSpecialties(), 
                    new TypeReference<List<String>>() {}
                );
                dto.setSpecialties(specialtiesList);
            } catch (Exception e) {
                // 如果解析失败，返回空列表
                dto.setSpecialties(new ArrayList<>());
            }
        } else {
            dto.setSpecialties(new ArrayList<>());
        }
        
        return dto;
    }
}
