package com.leansofx.qaserviceuser.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * 医生实体，与数据库表 doctor（小写）一一对应。
 * 表与库使用 utf8mb4，避免中文乱码；API 层通过 DTO 暴露时不包含 password。
 */
@Entity
@Table(name = "doctor")
public class Doctor {

    @Id
    private String id;

    @Column(nullable = false, unique = true, length = 64)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(length = 64)
    private String title;

    @Column(length = 64)
    private String department;

    @Column(length = 512)
    private String avatar;

    @Column(length = 128)
    private String experience;

    /**
     * 专长列表，以 JSON 数组形式存储，如 ["高血压","冠心病"]。
     * 数据库列名为 specialties，与《技术实现文档》表结构一致。
     */
    @Column(name = "specialties", length = 512)
    private String specialtiesJson;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

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

    /** 专长 JSON 字符串，由 Service 层解析为 List&lt;String&gt;，API 不返回 password。 */
    public String getSpecialtiesJson() {
        return specialtiesJson;
    }

    public void setSpecialtiesJson(String specialtiesJson) {
        this.specialtiesJson = specialtiesJson;
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
