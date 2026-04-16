package com.leansofx.qaserviceuser.controller;

import com.leansofx.qaserviceuser.dto.PatientAuthRequest;
import com.leansofx.qaserviceuser.dto.PatientAuthResponse;
import com.leansofx.qaserviceuser.service.PatientAuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/patients")
public class PatientAuthController {

    private final PatientAuthService patientAuthService;

    public PatientAuthController(PatientAuthService patientAuthService) {
        this.patientAuthService = patientAuthService;
    }

    @PostMapping("/register")
    public PatientAuthResponse register(@RequestBody PatientAuthRequest request) {
        return patientAuthService.register(request);
    }

    @PostMapping("/login")
    public PatientAuthResponse login(@RequestBody PatientAuthRequest request) {
        return patientAuthService.login(request);
    }
}
