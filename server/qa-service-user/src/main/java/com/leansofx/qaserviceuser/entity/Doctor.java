package com.leansofx.qaserviceuser.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Doctor {
    private String id;
    private String username;
    private String password;
    private String name;
    private String title;
    private String department;
    private String avatar;
    private String experience;
    private String specialties;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
