package com.leansofx.qaserviceuser.service;

import com.leansofx.qaserviceuser.dto.PatientDTO;
import com.leansofx.qaserviceuser.dto.PatientRegisterRequest;
import com.leansofx.qaserviceuser.entity.Patient;
import com.leansofx.qaserviceuser.repository.PatientRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public Optional<PatientDTO> login(String username, String password) {
        return patientRepository.findByUsername(username)
                .filter(patient -> patient.getPassword().equals(password))
                .map(this::toDto);
    }

    public PatientDTO register(PatientRegisterRequest request) {
        if (!StringUtils.hasText(request.username()) || !StringUtils.hasText(request.password())) {
            throw new IllegalArgumentException("用户名和密码不能为空");
        }
        if (!StringUtils.hasText(request.name())) {
            throw new IllegalArgumentException("姓名不能为空");
        }
        if (patientRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("用户名已存在");
        }

        Patient patient = new Patient();
        patient.setId("patient" + UUID.randomUUID().toString().replace("-", "").substring(0, 8));
        patient.setUsername(request.username());
        patient.setPassword(request.password());
        patient.setName(request.name());
        patient.setBirthday(StringUtils.hasText(request.birthday()) ? request.birthday() : "");
        patient.setPhone(StringUtils.hasText(request.phone()) ? request.phone() : "");
        patient.setGender(StringUtils.hasText(request.gender()) ? request.gender() : "");

        return toDto(patientRepository.save(patient));
    }

    private PatientDTO toDto(Patient patient) {
        return new PatientDTO(
                patient.getId(),
                patient.getUsername(),
                patient.getName(),
                patient.getBirthday(),
                patient.getPhone(),
                patient.getGender());
    }
}
