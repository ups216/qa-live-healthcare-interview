package com.leansofx.qaserviceuser.dto;

public record PatientRegisterRequest(
        String username,
        String password,
        String name,
        String birthday,
        String phone,
        String gender) {
}
