package com.leansofx.qaserviceuser.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

/**
 * 医生实体，与前端 Doctor 及 doctor 表对齐。
 * 列表 API 返回时转为 DTO 并排除 password。
 */
@Entity
@Table(name = "doctor")
public class Doctor {

    @Id
    @Column(length = 32)
    private String id;

    @Column(nullable = false, unique = true, length = 64)
    private String username;

    @Column(length = 255)
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

    /** 专长列表，MySQL JSON 存储 */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<String> specialties;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

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

    public List<String> getSpecialties() {
        return specialties;
    }

    public void setSpecialties(List<String> specialties) {
        this.specialties = specialties;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }
}
