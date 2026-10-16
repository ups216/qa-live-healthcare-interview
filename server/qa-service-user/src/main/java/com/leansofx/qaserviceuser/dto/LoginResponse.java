package com.leansofx.qaserviceuser.dto;

/**
 * 登录成功响应。
 */
public class LoginResponse {

    /** JWT 令牌 */
    private String token;

    /** 令牌有效期（毫秒） */
    private Long expiresIn;

    /** 登录用户的脱敏信息 */
    private PatientVO patient;

    public LoginResponse() {
    }

    public LoginResponse(String token, Long expiresIn, PatientVO patient) {
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

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public PatientVO getPatient() {
        return patient;
    }

    public void setPatient(PatientVO patient) {
        this.patient = patient;
    }
}
