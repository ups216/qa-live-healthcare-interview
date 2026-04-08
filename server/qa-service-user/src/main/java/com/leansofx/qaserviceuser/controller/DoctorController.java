package com.leansofx.qaserviceuser.controller;

import com.leansofx.qaserviceuser.dto.DoctorResponse;
import com.leansofx.qaserviceuser.service.DoctorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 医生相关 REST 接口。提供医生列表，供前端医生页与首页统计/开放诊室使用。
 */
@RestController
@RequestMapping("/api")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    /**
     * 获取医生列表。响应不包含 password，Content-Type 为 application/json;charset=UTF-8，避免前端中文乱码。
     */
    @GetMapping(value = "/doctors", produces = "application/json;charset=UTF-8")
    public ResponseEntity<List<DoctorResponse>> getDoctors() {
        List<DoctorResponse> list = doctorService.findAll();
        return ResponseEntity.ok(list);
    }
}
