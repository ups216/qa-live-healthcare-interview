package com.leansofx.qaserviceuser.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "doctors")
public class Doctor {
    @Id
    @Column(name = "id", nullable = false, length = 50)
    private String id;

    @Column(name = "username", nullable = false, unique = true, length = 100)
    private String username;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "department", nullable = false, length = 100)
    private String department;

    @Column(name = "avatar", length = 500)
    private String avatar;

    @Column(name = "experience", length = 200)
    private String experience;

    @Column(name = "specialties", columnDefinition = "JSON")
    private String specialties;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    // 辅助方法，用于处理specialties的JSON转换
    public List<String> getSpecialtiesList() {
        if (specialties == null || specialties.isEmpty()) {
            return List.of();
        }
        // 简单的JSON数组解析，实际项目中建议使用JSON库
        return java.util.Arrays.stream(specialties.substring(1, specialties.length() - 1)
                .replaceAll("\"", "")
                .split(","))
                .map(String::trim)
                .toList();
    }

    public void setSpecialtiesList(List<String> specialtiesList) {
        if (specialtiesList == null || specialtiesList.isEmpty()) {
            this.specialties = "[]";
            return;
        }
        // 简单的JSON数组生成，实际项目中建议使用JSON库
        this.specialties = "[" + specialtiesList.stream()
                .map(s -> "\"" + s + "\"")
                .reduce((a, b) -> a + ", " + b)
                .orElse("") + "]";
    }
}
