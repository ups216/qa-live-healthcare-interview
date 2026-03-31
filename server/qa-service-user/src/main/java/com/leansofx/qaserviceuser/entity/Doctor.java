package com.leansofx.qaserviceuser.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "doctors")
public class Doctor {

    @Id
    @Column(name = "id", length = 20)
    private String id;

    @Column(name = "username", length = 50, nullable = false, unique = true)
    private String username;

    @Column(name = "password", length = 100, nullable = false)
    private String password;

    // 中文字段
    @Column(name = "name_zh", length = 100, nullable = false)
    private String nameZh;

    @Column(name = "title_zh", length = 50, nullable = false)
    private String titleZh;

    @Column(name = "department_zh", length = 50, nullable = false)
    private String departmentZh;

    @Column(name = "experience_zh", length = 100)
    private String experienceZh;

    @Column(name = "specialties_zh", length = 500)
    private String specialtiesZh;

    // 英文字段
    @Column(name = "name_en", length = 100, nullable = false)
    private String nameEn;

    @Column(name = "title_en", length = 50, nullable = false)
    private String titleEn;

    @Column(name = "department_en", length = 50, nullable = false)
    private String departmentEn;

    @Column(name = "experience_en", length = 100)
    private String experienceEn;

    @Column(name = "specialties_en", length = 500)
    private String specialtiesEn;

    // 通用字段
    @Column(name = "avatar", length = 500)
    private String avatar;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 构造函数
    public Doctor() {
    }

    // 根据语言获取对应字段值
    public String getName(String lang) {
        return "en".equals(lang) ? nameEn : nameZh;
    }

    public String getTitle(String lang) {
        return "en".equals(lang) ? titleEn : titleZh;
    }

    public String getDepartment(String lang) {
        return "en".equals(lang) ? departmentEn : departmentZh;
    }

    public String getExperience(String lang) {
        return "en".equals(lang) ? experienceEn : experienceZh;
    }

    public String getSpecialties(String lang) {
        return "en".equals(lang) ? specialtiesEn : specialtiesZh;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNameZh() {
        return nameZh;
    }

    public void setNameZh(String nameZh) {
        this.nameZh = nameZh;
    }

    public String getTitleZh() {
        return titleZh;
    }

    public void setTitleZh(String titleZh) {
        this.titleZh = titleZh;
    }

    public String getDepartmentZh() {
        return departmentZh;
    }

    public void setDepartmentZh(String departmentZh) {
        this.departmentZh = departmentZh;
    }

    public String getExperienceZh() {
        return experienceZh;
    }

    public void setExperienceZh(String experienceZh) {
        this.experienceZh = experienceZh;
    }

    public String getSpecialtiesZh() {
        return specialtiesZh;
    }

    public void setSpecialtiesZh(String specialtiesZh) {
        this.specialtiesZh = specialtiesZh;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

    public String getDepartmentEn() {
        return departmentEn;
    }

    public void setDepartmentEn(String departmentEn) {
        this.departmentEn = departmentEn;
    }

    public String getExperienceEn() {
        return experienceEn;
    }

    public void setExperienceEn(String experienceEn) {
        this.experienceEn = experienceEn;
    }

    public String getSpecialtiesEn() {
        return specialtiesEn;
    }

    public void setSpecialtiesEn(String specialtiesEn) {
        this.specialtiesEn = specialtiesEn;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
