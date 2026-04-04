package com.leansofx.qaserviceuser.dto;

public class LoginResponse {

    private PatientDTO patient;
    private String accessToken;
    private String refreshToken;
    private long expiresIn;

    public LoginResponse() {
    }

    public LoginResponse(PatientDTO patient, String accessToken, String refreshToken, long expiresIn) {
        this.patient = patient;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
    }

    public PatientDTO getPatient() {
        return patient;
    }

    public void setPatient(PatientDTO patient) {
        this.patient = patient;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }
}
