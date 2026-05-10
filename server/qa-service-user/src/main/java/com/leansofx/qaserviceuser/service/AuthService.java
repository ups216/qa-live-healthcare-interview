package com.leansofx.qaserviceuser.service;

import com.leansofx.qaserviceuser.dto.LoginRequest;
import com.leansofx.qaserviceuser.dto.LoginResponse;
import com.leansofx.qaserviceuser.dto.UserDTO;

public interface AuthService {
    
    LoginResponse login(LoginRequest loginRequest);
    
    UserDTO getCurrentUser(String token);
}
