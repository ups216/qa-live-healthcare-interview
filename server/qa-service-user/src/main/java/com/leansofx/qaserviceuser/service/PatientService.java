package com.leansofx.qaserviceuser.service;

import com.leansofx.qaserviceuser.entity.Patient;
import com.leansofx.qaserviceuser.repository.PatientRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public Patient register(String username, String password, String name, String phone) {
        if (patientRepository.existsByUsername(username)) {
            throw new RuntimeException("用户名已存在");
        }

        Patient patient = new Patient();
        patient.setId("patient" + System.currentTimeMillis());
        patient.setUsername(username);
        patient.setPassword(passwordEncoder.encode(password));
        patient.setName(name);
        patient.setPhone(phone);

        return patientRepository.save(patient);
    }

    public Patient login(String username, String password) {
        Optional<Patient> optionalPatient = patientRepository.findByUsername(username);
        if (optionalPatient.isPresent()) {
            Patient patient = optionalPatient.get();
            if (passwordEncoder.matches(password, patient.getPassword())) {
                return patient;
            }
        }
        return null;
    }

    public Patient findById(String id) {
        return patientRepository.findById(id).orElse(null);
    }

    public Patient findByUsername(String username) {
        return patientRepository.findByUsername(username).orElse(null);
    }
}
