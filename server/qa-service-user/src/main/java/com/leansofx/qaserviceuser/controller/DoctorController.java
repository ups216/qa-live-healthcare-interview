package com.leansofx.qaserviceuser.controller;

import com.leansofx.qaserviceuser.dto.ApiResponse;
import com.leansofx.qaserviceuser.dto.DoctorDTO;
import com.leansofx.qaserviceuser.dto.LoginRequest;
import com.leansofx.qaserviceuser.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctor")
public class DoctorController {

    private final UserService userService;

    public DoctorController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<DoctorDTO>> login(@RequestBody LoginRequest request) {
        try {
            DoctorDTO doctor = userService.loginDoctor(request.getUsername(), request.getPassword());
            return ResponseEntity.ok(ApiResponse.success("登录成功", doctor));
        } catch (RuntimeException e) {
            if ("AUTH_FAILED".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "用户名或密码错误"));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "服务器内部错误"));
        }
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<DoctorDTO>>> listDoctors() {
        List<DoctorDTO> doctors = userService.getAllDoctors();
        return ResponseEntity.ok(ApiResponse.success(doctors));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<DoctorDTO>>> listActiveDoctors() {
        List<DoctorDTO> doctors = userService.getActiveDoctors();
        return ResponseEntity.ok(ApiResponse.success(doctors));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DoctorDTO>> getDoctor(@PathVariable String id) {
        try {
            DoctorDTO doctor = userService.getDoctorById(id);
            return ResponseEntity.ok(ApiResponse.success(doctor));
        } catch (RuntimeException e) {
            if ("NOT_FOUND".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, "医生不存在"));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "服务器内部错误"));
        }
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<DoctorDTO>> getDoctorByUsername(@PathVariable String username) {
        try {
            DoctorDTO doctor = userService.getDoctorByUsername(username);
            return ResponseEntity.ok(ApiResponse.success(doctor));
        } catch (RuntimeException e) {
            if ("NOT_FOUND".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, "医生不存在"));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "服务器内部错误"));
        }
    }
}
