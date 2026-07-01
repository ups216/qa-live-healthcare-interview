package com.leansofx.qaserviceuser.service;

import com.leansofx.qaserviceuser.dto.DoctorDTO;
import com.leansofx.qaserviceuser.entity.Doctor;
import com.leansofx.qaserviceuser.repository.DoctorRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public List<DoctorDTO> findAll() {
        return doctorRepository.findAll().stream().map(this::toDto).toList();
    }

    public List<DoctorDTO> findActiveDoctors() {
        return doctorRepository.findByIsActiveTrue().stream().map(this::toDto).toList();
    }

    public Optional<DoctorDTO> findByUsername(String username) {
        return doctorRepository.findByUsername(username).map(this::toDto);
    }

    public Optional<DoctorDTO> login(String username, String password) {
        return doctorRepository.findByUsername(username)
                .filter(doctor -> doctor.getPassword().equals(password))
                .map(this::toDto);
    }

    private DoctorDTO toDto(Doctor doctor) {
        return new DoctorDTO(
                doctor.getId(),
                doctor.getUsername(),
                doctor.getName(),
                doctor.getTitle(),
                doctor.getDepartment(),
                doctor.getAvatar(),
                doctor.getExperience(),
                doctor.getSpecialties(),
                doctor.isActive());
    }
}
