package com.leansofx.qaserviceuser.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "doctors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {

    @Id
    @Column(name = "id", length = 20, nullable = false)
    private String id;

    @Column(name = "username", length = 50, nullable = false, unique = true)
    private String username;

    @Column(name = "password", length = 100, nullable = false)
    private String password;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "title", length = 50, nullable = false)
    private String title;

    @Column(name = "department", length = 50, nullable = false)
    private String department;

    @Column(name = "avatar", length = 255, nullable = false)
    private String avatar;

    @Column(name = "experience", length = 100, nullable = false)
    private String experience;

    @Column(name = "specialties", columnDefinition = "TEXT", nullable = false)
    private String specialties;

    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean isActive;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    // 辅助方法：将字符串转换为字符串数组
    public String[] getSpecialtiesArray() {
        if (specialties == null || specialties.isEmpty()) {
            return new String[0];
        }
        // 移除首尾的 [] 并分割字符串
        String cleanSpecialties = specialties.replaceAll("^\\[|\\]$", "");
        // 移除引号并分割
        String[] parts = cleanSpecialties.split(",\s*");
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].replaceAll("^\"|\"$", "");
        }
        return parts;
    }

    // 辅助方法：将字符串数组转换为字符串
    public void setSpecialtiesArray(String[] specialtiesArray) {
        if (specialtiesArray == null || specialtiesArray.length == 0) {
            this.specialties = "[]";
        } else {
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < specialtiesArray.length; i++) {
                sb.append('"').append(specialtiesArray[i]).append('"');
                if (i < specialtiesArray.length - 1) {
                    sb.append(", ");
                }
            }
            sb.append("]");
            this.specialties = sb.toString();
        }
    }
}
