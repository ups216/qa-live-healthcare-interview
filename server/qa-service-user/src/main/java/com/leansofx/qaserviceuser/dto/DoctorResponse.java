package com.leansofx.qaserviceuser.dto;

import java.util.List;

public record DoctorResponse(
        String id,
        String username,
        String name,
        String title,
        String department,
        String avatar,
        String experience,
        List<String> specialties,
        boolean isActive
) {}
