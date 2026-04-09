package com.leansofx.qaserviceuser.dto;

public class PatientLoginResponse {
    private String token;
    private long expiresIn;
    private PatientDto patient;

    public PatientLoginResponse() {
    }

    public PatientLoginResponse(String token, long expiresIn, PatientDto patient) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.patient = patient;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public PatientDto getPatient() {
        return patient;
    }

    public void setPatient(PatientDto patient) {
        this.patient = patient;
    }
}
