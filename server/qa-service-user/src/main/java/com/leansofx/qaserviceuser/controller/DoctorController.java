package com.leansofx.qaserviceuser.controller;

import com.leansofx.qaserviceuser.common.ApiResponse;
import com.leansofx.qaserviceuser.dto.DoctorVO;
import com.leansofx.qaserviceuser.exception.BizException;
import com.leansofx.qaserviceuser.exception.ErrorCode;
import com.leansofx.qaserviceuser.service.DoctorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 医生 REST 接口（需求2）。
 *
 * <p>数据来源：MySQL qa_live.t_doctor，替代前端原先的 data/doctor-user-list.json。
 * 全部接口位于 JWT 拦截器白名单中，可匿名访问。
 */
@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    /**
     * 医生全量列表。
     * 支撑前端 web/qa-web/src/views/Doctors.vue 与 Home.vue 的统计。
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<DoctorVO>>> list() {
        return ResponseEntity.ok(ApiResponse.ok(doctorService.findAll()));
    }

    /**
     * 仅在线医生。
     * 支撑 Consultation.vue 的医生下拉与 Home.vue 的开放诊室列表。
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<DoctorVO>>> listActive() {
        return ResponseEntity.ok(ApiResponse.ok(doctorService.findActive()));
    }

    /** 按主键查询，不存在返回 404 */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DoctorVO>> getById(@PathVariable("id") String id) {
        DoctorVO doctor = doctorService.findById(id);
        if (doctor == null) {
            throw new BizException(ErrorCode.DOCTOR_NOT_FOUND, "医生不存在，id=" + id);
        }
        return ResponseEntity.ok(ApiResponse.ok(doctor));
    }

    /**
     * 按登录名查询，不存在返回 404。
     * 前端 Consultation.vue 依据路由参数 /consultation/:doctorUsername 调用。
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<DoctorVO>> getByUsername(@PathVariable("username") String username) {
        DoctorVO doctor = doctorService.findByUsername(username);
        if (doctor == null) {
            throw new BizException(ErrorCode.DOCTOR_NOT_FOUND, "医生不存在，username=" + username);
        }
        return ResponseEntity.ok(ApiResponse.ok(doctor));
    }
}
