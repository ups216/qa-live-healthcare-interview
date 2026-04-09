package com.leansofx.qaserviceuser.controller;

import com.leansofx.qaserviceuser.dto.PatientDto;
import com.leansofx.qaserviceuser.dto.PatientLoginRequest;
import com.leansofx.qaserviceuser.dto.PatientLoginResponse;
import com.leansofx.qaserviceuser.dto.PatientRegisterRequest;
import com.leansofx.qaserviceuser.security.JwtService;
import com.leansofx.qaserviceuser.service.PatientAuthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/patients")
public class PatientAuthController {

    private final PatientAuthService patientAuthService;
    private final JwtService jwtService;

    public PatientAuthController(PatientAuthService patientAuthService, JwtService jwtService) {
        this.patientAuthService = patientAuthService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public PatientLoginResponse register(@Valid @RequestBody PatientRegisterRequest request) {
        return patientAuthService.register(request);
    }

    @PostMapping("/login")
    public PatientLoginResponse login(@Valid @RequestBody PatientLoginRequest request) {
        return patientAuthService.login(request);
    }

    @GetMapping("/me")
    public PatientDto me(@RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = extractBearerToken(authorization);
        if (token == null || token.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "AUTH_REQUIRED");
        }
        try {
            Claims claims = jwtService.parseAndValidate(token);
            return patientAuthService.getPatientById(claims.getSubject());
        } catch (JwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "AUTH_INVALID_TOKEN");
        }
    }

    private static String extractBearerToken(String authorization) {
        if (authorization == null || !authorization.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return null;
        }
        return authorization.substring(7).trim();
    }
}
