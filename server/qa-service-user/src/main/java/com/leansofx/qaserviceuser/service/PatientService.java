package com.leansofx.qaserviceuser.service;

import com.leansofx.qaserviceuser.dto.PatientDto;
import com.leansofx.qaserviceuser.repository.PatientRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public Optional<PatientDto> login(String username, String password) {
        Optional<String> encryptedPasswordOpt = patientRepository.findPasswordByUsername(username);
        if (encryptedPasswordOpt.isPresent()) {
            String storedHash = encryptedPasswordOpt.get();
            boolean matches = passwordEncoder.matches(password, storedHash);
            if (matches) {
                return patientRepository.findByUsername(username);
            }
        }
        return Optional.empty();
    }

    public Optional<PatientDto> verifyByNameAndBirthday(String name, String birthday) {
        return patientRepository.findByNameAndBirthday(name, birthday);
    }

    public Optional<PatientDto> findByUsername(String username) {
        return patientRepository.findByUsername(username);
    }

    public void register(PatientDto patient, String rawPassword) {
        String encryptedPassword = passwordEncoder.encode(rawPassword);
        patientRepository.save(patient, encryptedPassword);
    }
}
