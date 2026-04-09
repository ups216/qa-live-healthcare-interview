package com.leansofx.qaserviceuser.controller;

import com.leansofx.qaserviceuser.common.ApiResponse;
import com.leansofx.qaserviceuser.entity.Doctor;
import com.leansofx.qaserviceuser.service.DoctorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 医生 REST API
 *
 * <pre>
 * GET  /api/doctors              查询医生列表（可选 isActive 过滤）
 * GET  /api/doctors/{id}         按 ID 查询医生详情
 * GET  /api/doctors/u/{username} 按 username 查询医生详情
 * </pre>
 */
@Slf4j
@RestController
@RequestMapping("/doctors")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowCredentials = "false")   // 开发阶段允许跨域；生产环境应配置具体域名
public class DoctorController {

    private final DoctorService doctorService;

    /**
     * 查询医生列表
     *
     * @param isActive 可选过滤参数：true=在线，false=离线，不传=全部
     */
    @GetMapping
    public ApiResponse<List<Doctor>> listDoctors(
            @RequestParam(value = "isActive", required = false) Boolean isActive) {
        log.info("GET /doctors, isActive={}", isActive);
        List<Doctor> doctors = doctorService.listDoctors(isActive);
        return ApiResponse.success(doctors);
    }

    /**
     * 按 ID 查询医生详情
     */
    @GetMapping("/{id}")
    public ApiResponse<Doctor> getDoctorById(@PathVariable String id) {
        log.info("GET /doctors/{}", id);
        return ApiResponse.success(doctorService.getDoctorById(id));
    }

    /**
     * 按 username 查询医生详情（前端路由 /consultation/:username 使用）
     */
    @GetMapping("/u/{username}")
    public ApiResponse<Doctor> getDoctorByUsername(@PathVariable String username) {
        log.info("GET /doctors/u/{}", username);
        return ApiResponse.success(doctorService.getDoctorByUsername(username));
    }
}
