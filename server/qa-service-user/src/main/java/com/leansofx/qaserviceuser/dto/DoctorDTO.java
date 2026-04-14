package com.leansofx.qaserviceuser.dto;

import java.util.List;

public class DoctorDTO {
    private String id;
    private String username;
    private String name;
    private String title;
    private String department;
    private String avatar;
    private String experience;
    private Boolean isActive;
    private List<String> specialties;
    
    // 构造函数
    public DoctorDTO() {
    }
    
    public DoctorDTO(String id, String username, String name, String title, String department, 
                     String avatar, String experience, Boolean isActive, List<String> specialties) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.title = title;
        this.department = department;
        this.avatar = avatar;
        this.experience = experience;
        this.isActive = isActive;
        this.specialties = specialties;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public String getAvatar() {
        return avatar;
    }
    
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    
    public String getExperience() {
        return experience;
    }
    
    public void setExperience(String experience) {
        this.experience = experience;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public List<String> getSpecialties() {
        return specialties;
    }
    
    public void setSpecialties(List<String> specialties) {
        this.specialties = specialties;
    }
}