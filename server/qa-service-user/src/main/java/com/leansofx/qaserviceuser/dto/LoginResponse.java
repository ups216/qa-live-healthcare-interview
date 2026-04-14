package com.leansofx.qaserviceuser.dto;

public class LoginResponse {
    private String token;
    private PatientDTO patient;
    private String message;
    
    // 构造函数
    public LoginResponse() {
    }
    
    public LoginResponse(String token, PatientDTO patient) {
        this.token = token;
        this.patient = patient;
        this.message = "Login successful";
    }
    
    public LoginResponse(String token, PatientDTO patient, String message) {
        this.token = token;
        this.patient = patient;
        this.message = message;
    }
    
    // Getters and Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public PatientDTO getPatient() {
        return patient;
    }
    
    public void setPatient(PatientDTO patient) {
        this.patient = patient;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}