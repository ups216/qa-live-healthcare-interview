package com.leansofx.qaserviceuser.service;

import com.leansofx.qaserviceuser.dto.PatientDto;
import com.leansofx.qaserviceuser.dto.PatientLoginRequest;
import com.leansofx.qaserviceuser.dto.PatientLoginResponse;
import com.leansofx.qaserviceuser.dto.PatientRegisterRequest;
import com.leansofx.qaserviceuser.model.PatientEntity;
import com.leansofx.qaserviceuser.repository.PatientRepository;
import com.leansofx.qaserviceuser.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Service
public class PatientAuthService {

    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public PatientAuthService(
            PatientRepository patientRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public PatientLoginResponse register(PatientRegisterRequest req) {
        String username = req.getUsername().trim();
        if (patientRepository.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "USERNAME_TAKEN");
        }
        PatientEntity e = new PatientEntity();
        // 与 patients.id VARCHAR(36) 一致：标准 UUID 含连字符共 36 字符
        e.setId(UUID.randomUUID().toString());
        e.setUsername(username);
        e.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        e.setName(req.getName().trim());
        e.setBirthday(blankToNull(req.getBirthday()));
        e.setPhone(blankToNull(req.getPhone()));
        e.setGender(blankToNull(req.getGender()));
        patientRepository.save(e);
        return buildLoginResponse(e);
    }

    public PatientLoginResponse login(PatientLoginRequest req) {
        Optional<PatientEntity> opt = patientRepository.findByUsername(req.getUsername().trim());
        if (opt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "AUTH_INVALID_CREDENTIALS");
        }
        PatientEntity e = opt.get();
        if (!passwordEncoder.matches(req.getPassword(), e.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "AUTH_INVALID_CREDENTIALS");
        }
        return buildLoginResponse(e);
    }

    public PatientDto getPatientById(String patientId) {
        return patientRepository.findById(patientId)
                .map(this::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NOT_FOUND"));
    }

    private PatientLoginResponse buildLoginResponse(PatientEntity e) {
        String token = jwtService.createToken(e.getId(), e.getUsername());
        return new PatientLoginResponse(token, jwtService.getExpirationSeconds(), toDto(e));
    }

    private PatientDto toDto(PatientEntity e) {
        return new PatientDto(
                e.getId(),
                e.getUsername(),
                e.getName(),
                e.getBirthday(),
                e.getPhone(),
                e.getGender()
        );
    }

    private static String blankToNull(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        return s.trim();
    }
}
