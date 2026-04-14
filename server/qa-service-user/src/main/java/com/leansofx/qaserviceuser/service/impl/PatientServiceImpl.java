package com.leansofx.qaserviceuser.service.impl;

import com.leansofx.qaserviceuser.dto.LoginRequest;
import com.leansofx.qaserviceuser.dto.LoginResponse;
import com.leansofx.qaserviceuser.dto.PatientDTO;
import com.leansofx.qaserviceuser.entity.Patient;
import com.leansofx.qaserviceuser.repository.PatientRepository;
import com.leansofx.qaserviceuser.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PatientServiceImpl implements PatientService {
    
    @Autowired
    private PatientRepository patientRepository;
    
    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        
        // 查找患者
        Optional<Patient> patientOpt = patientRepository.findByUsernameAndPassword(username, password);
        
        if (patientOpt.isPresent()) {
            Patient patient = patientOpt.get();
            
            // 检查账户是否活跃
            if (patient.getIsActive() != null && !patient.getIsActive()) {
                return null; // 账户被禁用
            }
            
            // 更新上次登录时间
            patient.setLastLoginAt(LocalDateTime.now());
            patientRepository.save(patient);
            
            // 生成token（简化版）
            String token = generateToken(username);
            
            // 转换为DTO
            PatientDTO patientDTO = convertToDTO(patient);
            
            return new LoginResponse(token, patientDTO, "登录成功");
        }
        
        return null; // 登录失败
    }
    
    @Override
    public PatientDTO register(PatientDTO patientDTO) {
        // 检查用户名是否已存在
        if (patientRepository.existsByUsername(patientDTO.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查手机号是否已存在
        if (patientDTO.getPhone() != null && !patientDTO.getPhone().isEmpty()) {
            if (patientRepository.existsByPhone(patientDTO.getPhone())) {
                throw new RuntimeException("手机号已存在");
            }
        }
        
        // 创建患者实体
        Patient patient = new Patient();
        patient.setUsername(patientDTO.getUsername());
        patient.setPassword(patientDTO.getPassword()); // 使用前端传来的密码
        patient.setName(patientDTO.getName());
        patient.setNickname(patientDTO.getNickname());
        patient.setBirthday(patientDTO.getBirthday());
        patient.setPhone(patientDTO.getPhone());
        patient.setGender(patientDTO.getGender());
        patient.setAvatar(patientDTO.getAvatar());
        patient.setEmail(patientDTO.getEmail());
        patient.setAddress(patientDTO.getAddress());
        patient.setIsActive(true);
        
        // 保存到数据库
        Patient savedPatient = patientRepository.save(patient);
        
        return convertToDTO(savedPatient);
    }
    
    @Override
    public PatientDTO getPatientByUsername(String username) {
        Optional<Patient> patientOpt = patientRepository.findByUsername(username);
        return patientOpt.map(this::convertToDTO).orElse(null);
    }
    
    @Override
    public List<PatientDTO> getActivePatients() {
        List<Patient> patients = patientRepository.findByIsActiveTrue();
        return patients.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean verifyPatient(String name, String birthday) {
        Optional<Patient> patientOpt = patientRepository.findByNameAndBirthday(name, birthday);
        return patientOpt.isPresent();
    }
    
    @Override
    public void updateLastLoginTime(String username) {
        Optional<Patient> patientOpt = patientRepository.findByUsername(username);
        if (patientOpt.isPresent()) {
            Patient patient = patientOpt.get();
            patient.setLastLoginAt(LocalDateTime.now());
            patientRepository.save(patient);
        }
    }
    
    @Override
    public String generateToken(String username) {
        // 简化版token生成，实际项目中应该使用JWT
        // 这里使用base64编码的用户名+时间戳作为token
        String timestamp = String.valueOf(System.currentTimeMillis());
        String tokenString = username + ":" + timestamp;
        // 实际项目中应该使用安全的加密方式
        return java.util.Base64.getEncoder().encodeToString(tokenString.getBytes());
    }
    
    /**
     * 将Patient实体转换为PatientDTO
     * @param patient 患者实体
     * @return 患者DTO
     */
    private PatientDTO convertToDTO(Patient patient) {
        if (patient == null) {
            return null;
        }
        
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setId(patient.getId());
        patientDTO.setUsername(patient.getUsername());
        patientDTO.setName(patient.getName());
        patientDTO.setNickname(patient.getNickname());
        patientDTO.setBirthday(patient.getBirthday());
        patientDTO.setPhone(patient.getPhone());
        patientDTO.setGender(patient.getGender());
        patientDTO.setAvatar(patient.getAvatar());
        patientDTO.setEmail(patient.getEmail());
        patientDTO.setAddress(patient.getAddress());
        patientDTO.setIsActive(patient.getIsActive());
        
        return patientDTO;
    }
    
    /**
     * 将PatientDTO转换为Patient实体
     * @param patientDTO 患者DTO
     * @return 患者实体
     */
    private Patient convertToEntity(PatientDTO patientDTO) {
        if (patientDTO == null) {
            return null;
        }
        
        Patient patient = new Patient();
        patient.setId(patientDTO.getId());
        patient.setUsername(patientDTO.getUsername());
        patient.setName(patientDTO.getName());
        patient.setNickname(patientDTO.getNickname());
        patient.setBirthday(patientDTO.getBirthday());
        patient.setPhone(patientDTO.getPhone());
        patient.setGender(patientDTO.getGender());
        patient.setAvatar(patientDTO.getAvatar());
        patient.setEmail(patientDTO.getEmail());
        patient.setAddress(patientDTO.getAddress());
        patient.setIsActive(patientDTO.getIsActive());
        
        return patient;
    }
}