package com.leansofx.qaserviceuser.service;

import com.leansofx.qaserviceuser.dao.DoctorMapper;
import com.leansofx.qaserviceuser.entity.Doctor;
import com.leansofx.qaserviceuser.service.DoctorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 医生业务服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorMapper doctorMapper;

    @Override
    public List<Doctor> listDoctors(Boolean isActive) {
        log.debug("listDoctors called, isActive={}", isActive);
        return doctorMapper.findAll(isActive);
    }

    @Override
    public Doctor getDoctorById(String id) {
        log.debug("getDoctorById called, id={}", id);
        Doctor doctor = doctorMapper.findById(id);
        if (doctor == null) {
            throw new IllegalArgumentException("医生不存在，id=" + id);
        }
        return doctor;
    }

    @Override
    public Doctor getDoctorByUsername(String username) {
        log.debug("getDoctorByUsername called, username={}", username);
        Doctor doctor = doctorMapper.findByUsername(username);
        if (doctor == null) {
            throw new IllegalArgumentException("医生不存在，username=" + username);
        }
        return doctor;
    }
}
