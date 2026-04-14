package com.leansofx.qaserviceuser.service;

import com.leansofx.qaserviceuser.dto.LoginResponse;
import com.leansofx.qaserviceuser.dto.PatientDTO;
import com.leansofx.qaserviceuser.dto.LoginRequest;

import java.util.List;

public interface PatientService {
    
    /**
     * 患者登录
     * @param loginRequest 登录请求
     * @return 登录响应
     */
    LoginResponse login(LoginRequest loginRequest);
    
    /**
     * 患者注册
     * @param patientDTO 患者信息
     * @return 注册的患者信息
     */
    PatientDTO register(PatientDTO patientDTO);
    
    /**
     * 根据用户名查找患者
     * @param username 用户名
     * @return 患者信息
     */
    PatientDTO getPatientByUsername(String username);
    
    /**
     * 获取所有活跃患者
     * @return 活跃患者列表
     */
    List<PatientDTO> getActivePatients();
    
    /**
     * 验证患者身份
     * @param name 姓名
     * @param birthday 生日
     * @return 验证结果
     */
    boolean verifyPatient(String name, String birthday);
    
    /**
     * 更新患者上次登录时间
     * @param username 用户名
     */
    void updateLastLoginTime(String username);
    
    /**
     * 生成JWT token（简化版）
     * @param username 用户名
     * @return JWT token
     */
    String generateToken(String username);
}