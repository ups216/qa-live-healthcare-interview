package com.leansofx.qaserviceuser.dto;

public class LoginResponse {
    
    private String token;
    private long expiresIn;
    private UserDTO user;
    
    // Constructors
    public LoginResponse() {
    }
    
    public LoginResponse(String token, long expiresIn, UserDTO user) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.user = user;
    }
    
    // Getters and Setters
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
    
    public UserDTO getUser() {
        return user;
    }
    
    public void setUser(UserDTO user) {
        this.user = user;
    }
}
