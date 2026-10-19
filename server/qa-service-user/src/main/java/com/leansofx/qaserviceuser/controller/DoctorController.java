package com.leansofx.qaserviceuser.controller;

import com.leansofx.qaserviceuser.domain.Doctor;
import com.leansofx.qaserviceuser.repository.DoctorMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private static final Logger log = LoggerFactory.getLogger(DoctorController.class);

    private final DoctorMapper doctorMapper;

    public DoctorController(DoctorMapper doctorMapper) {
        this.doctorMapper = doctorMapper;
    }

    @GetMapping
    public List<Doctor> getAllDoctors() {
        log.info("[GET /api/doctors] 收到获取全部医生列表请求");
        List<Doctor> doctors = doctorMapper.findAll();
        log.info("[GET /api/doctors] 返回医生数量: {}", doctors.size());
        return doctors;
    }

    @GetMapping("/active")
    public List<Doctor> getActiveDoctors() {
        log.info("[GET /api/doctors/active] 收到获取在线医生列表请求");
        List<Doctor> doctors = doctorMapper.findActive();
        log.info("[GET /api/doctors/active] 返回医生数量: {}", doctors.size());
        return doctors;
    }
}
