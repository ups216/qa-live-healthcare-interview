package com.leansofx.qaserviceuser.dto;

public class StatisticsDTO {
    private Integer totalDoctors;
    private Integer activeDoctors;
    private Integer totalDepartments;
    private Integer totalSpecialties;
    
    // 构造函数
    public StatisticsDTO() {
    }
    
    public StatisticsDTO(Integer totalDoctors, Integer activeDoctors) {
        this.totalDoctors = totalDoctors;
        this.activeDoctors = activeDoctors;
    }
    
    public StatisticsDTO(Integer totalDoctors, Integer activeDoctors, Integer totalDepartments, Integer totalSpecialties) {
        this.totalDoctors = totalDoctors;
        this.activeDoctors = activeDoctors;
        this.totalDepartments = totalDepartments;
        this.totalSpecialties = totalSpecialties;
    }
    
    // Getters and Setters
    public Integer getTotalDoctors() {
        return totalDoctors;
    }
    
    public void setTotalDoctors(Integer totalDoctors) {
        this.totalDoctors = totalDoctors;
    }
    
    public Integer getActiveDoctors() {
        return activeDoctors;
    }
    
    public void setActiveDoctors(Integer activeDoctors) {
        this.activeDoctors = activeDoctors;
    }
    
    public Integer getTotalDepartments() {
        return totalDepartments;
    }
    
    public void setTotalDepartments(Integer totalDepartments) {
        this.totalDepartments = totalDepartments;
    }
    
    public Integer getTotalSpecialties() {
        return totalSpecialties;
    }
    
    public void setTotalSpecialties(Integer totalSpecialties) {
        this.totalSpecialties = totalSpecialties;
    }
}