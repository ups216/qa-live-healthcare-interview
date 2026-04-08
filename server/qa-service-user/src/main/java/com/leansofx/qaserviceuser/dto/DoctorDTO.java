package com.leansofx.qaserviceuser.dto;

import lombok.Data;

import java.util.List;

@Data
public class DoctorDTO {
    private String id;
    private String username;
    private String name;
    private String title;
    private String department;
    private String avatar;
    private String experience;
    private List<String> specialties;
    private boolean isActive;

    // 从实体类创建DTO的静态方法
    public static DoctorDTO fromEntity(com.leansofx.qaserviceuser.entity.Doctor doctor) {
        DoctorDTO dto = new DoctorDTO();
        dto.setId(doctor.getId());
        dto.setUsername(doctor.getUsername());
        dto.setName(doctor.getName());
        dto.setTitle(doctor.getTitle());
        dto.setDepartment(doctor.getDepartment());
        dto.setAvatar(doctor.getAvatar());
        dto.setExperience(doctor.getExperience());
        dto.setSpecialties(doctor.getSpecialtiesList());
        dto.setActive(doctor.isActive());
        return dto;
    }

    // 从DTO创建实体类的静态方法
    public static com.leansofx.qaserviceuser.entity.Doctor toEntity(DoctorDTO dto) {
        com.leansofx.qaserviceuser.entity.Doctor doctor = new com.leansofx.qaserviceuser.entity.Doctor();
        doctor.setId(dto.getId());
        doctor.setUsername(dto.getUsername());
        doctor.setPassword("123456"); // 默认密码，实际项目中应该通过加密处理
        doctor.setName(dto.getName());
        doctor.setTitle(dto.getTitle());
        doctor.setDepartment(dto.getDepartment());
        doctor.setAvatar(dto.getAvatar());
        doctor.setExperience(dto.getExperience());
        doctor.setSpecialtiesList(dto.getSpecialties());
        doctor.setActive(dto.isActive());
        return doctor;
    }
}
