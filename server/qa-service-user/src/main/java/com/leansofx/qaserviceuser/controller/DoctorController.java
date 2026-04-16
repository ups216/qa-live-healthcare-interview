package com.leansofx.qaserviceuser.controller;

import com.leansofx.qaserviceuser.dto.DoctorAuthRequest;
import com.leansofx.qaserviceuser.dto.DoctorAuthResponse;
import com.leansofx.qaserviceuser.dto.DoctorResponse;
import com.leansofx.qaserviceuser.service.DoctorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping
    public List<DoctorResponse> listDoctors(@RequestParam(defaultValue = "false") boolean activeOnly) {
        return activeOnly ? doctorService.getActiveDoctors() : doctorService.getAllDoctors();
    }

    @PostMapping("/login")
    public DoctorAuthResponse login(@RequestBody DoctorAuthRequest request) {
        return doctorService.login(request);
    }
}
