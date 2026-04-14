package com.leansofx.qaserviceuser.service;

import com.leansofx.qaserviceuser.dto.DoctorDTO;
import com.leansofx.qaserviceuser.dto.StatisticsDTO;
import com.leansofx.qaserviceuser.entity.Doctor;
import com.leansofx.qaserviceuser.repository.DoctorRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DoctorService {
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    /**
     * 获取所有医生列表
     */
    public List<DoctorDTO> getAllDoctors() {
        List<Object[]> results = doctorRepository.findDoctorsWithSpecialties(false);
        return convertToDTOList(results);
    }
    
    /**
     * 获取活跃医生列表
     */
    public List<DoctorDTO> getActiveDoctors() {
        List<Object[]> results = doctorRepository.findDoctorsWithSpecialties(true);
        return convertToDTOList(results);
    }
    
    /**
     * 根据ID获取医生
     */
    public DoctorDTO getDoctorById(String doctorId) {
        Doctor doctor = doctorRepository.findByDoctorId(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));
        
        return convertToDTO(doctor);
    }
    
    /**
     * 根据用户名获取医生
     */
    public DoctorDTO getDoctorByUsername(String username) {
        Doctor doctor = doctorRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Doctor not found with username: " + username));
        
        return convertToDTO(doctor);
    }
    
    /**
     * 根据科室获取医生
     */
    public List<DoctorDTO> getDoctorsByDepartment(String departmentCode) {
        List<Doctor> doctors = doctorRepository.findByDepartmentCode(departmentCode);
        return doctors.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取医生统计信息
     */
    public StatisticsDTO getDoctorStatistics() {
        long totalDoctors = doctorRepository.count();
        long activeDoctors = doctorRepository.countActiveDoctors();
        
        return new StatisticsDTO(
            (int) totalDoctors,
            (int) activeDoctors
        );
    }
    
    /**
     * 将Doctor实体转换为DTO
     */
    private DoctorDTO convertToDTO(Doctor doctor) {
        List<String> specialties = doctor.getSpecialties().stream()
                .map(specialty -> specialty.getName())
                .collect(Collectors.toList());
        
        return new DoctorDTO(
            doctor.getDoctorId(),
            doctor.getUsername(),
            doctor.getName(),
            doctor.getTitle(),
            doctor.getDepartment().getName(),
            doctor.getAvatar(),
            doctor.getExperience(),
            doctor.getIsActive(),
            specialties
        );
    }
    
    /**
     * 将原生查询结果转换为DTO列表
     */
    private List<DoctorDTO> convertToDTOList(List<Object[]> results) {
        List<DoctorDTO> doctorDTOs = new ArrayList<>();
        
        for (Object[] row : results) {
            DoctorDTO dto = new DoctorDTO();
            dto.setId((String) row[0]);
            dto.setUsername((String) row[1]);
            dto.setName((String) row[2]);
            dto.setTitle((String) row[3]);
            dto.setDepartment((String) row[4]);
            dto.setAvatar((String) row[5]);
            dto.setExperience((String) row[6]);
            dto.setIsActive(row[7] != null ? (Boolean) row[7] : false);
            
            // 处理专业领域字符串
            if (row[8] != null) {
                String specialtiesStr = (String) row[8];
                List<String> specialties = Arrays.asList(specialtiesStr.split(", "));
                dto.setSpecialties(specialties);
            } else {
                dto.setSpecialties(new ArrayList<>());
            }
            
            doctorDTOs.add(dto);
        }
        
        return doctorDTOs;
    }
}