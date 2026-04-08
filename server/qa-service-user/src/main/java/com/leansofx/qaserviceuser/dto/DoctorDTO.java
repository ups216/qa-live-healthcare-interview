package com.leansofx.qaserviceuser.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDTO {

    private String id;
    private String username;
    private String password;
    private String name;
    private String title;
    private String department;
    private String avatar;
    private String experience;
    private List<String> specialties;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
