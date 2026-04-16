package com.leansofx.qaserviceuser.service;

import com.leansofx.qaserviceuser.entity.LoginRequest;
import com.leansofx.qaserviceuser.entity.LoginResponse;
import com.leansofx.qaserviceuser.entity.OutpatientUser;

public interface AuthService {

    /**
     * 用户登录
     * @param request 登录请求
     * @return 登录响应
     */
    LoginResponse login(LoginRequest request);

    /**
     * 退出登录
     * @param token JWT Token
     */
    void logout(String token);

    /**
     * 刷新Token
     * @param token JWT Token
     * @return 新的登录响应
     */
    LoginResponse refreshToken(String token);

    /**
     * 根据Token获取用户信息
     * @param token JWT Token
     * @return 用户信息
     */
    OutpatientUser getUserByToken(String token);
}
