package com.leansofx.qaserviceuser.service;

import com.leansofx.qaserviceuser.dto.DoctorDto;
import com.leansofx.qaserviceuser.repository.DoctorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public List<DoctorDto> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public List<DoctorDto> getActiveDoctors() {
        return doctorRepository.findActive();
    }

    public Optional<DoctorDto> getByUsername(String username) {
        return doctorRepository.findByUsername(username);
    }

    public Optional<DoctorDto> login(String username, String password) {
        return doctorRepository.findByCredentials(username, password);
    }
}
