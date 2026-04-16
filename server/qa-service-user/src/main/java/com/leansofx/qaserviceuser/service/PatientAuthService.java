package com.leansofx.qaserviceuser.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.leansofx.qaserviceuser.dto.PatientAuthRequest;
import com.leansofx.qaserviceuser.dto.PatientAuthResponse;
import com.leansofx.qaserviceuser.entity.PatientUser;
import com.leansofx.qaserviceuser.mapper.PatientUserMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PatientAuthService {

    private final PatientUserMapper patientUserMapper;

    public PatientAuthService(PatientUserMapper patientUserMapper) {
        this.patientUserMapper = patientUserMapper;
    }

    public PatientAuthResponse register(PatientAuthRequest request) {
        if (request.username() == null || request.username().isBlank() || request.password() == null || request.password().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "鐢ㄦ埛鍚嶅拰瀵嗙爜涓嶈兘涓虹┖");
        }

        LambdaQueryWrapper<PatientUser> query = new LambdaQueryWrapper<>();
        query.eq(PatientUser::getUsername, request.username().trim());
        if (patientUserMapper.selectCount(query) > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "鐢ㄦ埛鍚嶅凡瀛樺湪");
        }

        PatientUser user = new PatientUser();
        user.setId("patient" + System.currentTimeMillis());
        user.setUsername(request.username().trim());
        user.setPassword(request.password().trim());
        user.setDisplayName((request.displayName() == null || request.displayName().isBlank()) ? request.username().trim() : request.displayName().trim());
        patientUserMapper.insert(user);
        return new PatientAuthResponse(user.getId(), user.getUsername(), user.getDisplayName(), "娉ㄥ唽鎴愬姛");
    }

    public PatientAuthResponse login(PatientAuthRequest request) {
        if (request.username() == null || request.username().isBlank() || request.password() == null || request.password().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "鐢ㄦ埛鍚嶅拰瀵嗙爜涓嶈兘涓虹┖");
        }

        LambdaQueryWrapper<PatientUser> query = new LambdaQueryWrapper<>();
        query.eq(PatientUser::getUsername, request.username().trim());
        PatientUser user = patientUserMapper.selectOne(query);
        if (user == null || !user.getPassword().equals(request.password().trim())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "鐢ㄦ埛鍚嶆垨瀵嗙爜閿欒");
        }

        return new PatientAuthResponse(user.getId(), user.getUsername(), user.getDisplayName(), "鐧诲綍鎴愬姛");
    }
}
