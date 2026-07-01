package com.leansofx.qaserviceuser.dto;

public record PatientDTO(
        String id,
        String username,
        String name,
        String birthday,
        String phone,
        String gender) {
}
