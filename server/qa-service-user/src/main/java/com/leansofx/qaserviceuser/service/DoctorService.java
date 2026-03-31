package com.leansofx.qaserviceuser.service;

import com.leansofx.qaserviceuser.dto.DoctorDTO;
import com.leansofx.qaserviceuser.entity.Doctor;
import com.leansofx.qaserviceuser.entity.DoctorSpecialty;
import com.leansofx.qaserviceuser.repository.DoctorRepository;
import com.leansofx.qaserviceuser.repository.DoctorSpecialtyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorSpecialtyRepository specialtyRepository;
    private final TranslationService translationService;

    public DoctorService(DoctorRepository doctorRepository, 
                        DoctorSpecialtyRepository specialtyRepository,
                        TranslationService translationService) {
        this.doctorRepository = doctorRepository;
        this.specialtyRepository = specialtyRepository;
        this.translationService = translationService;
    }

    public List<DoctorDTO> getAllDoctors(String locale) {
        List<Doctor> doctors = doctorRepository.findAll();
        return doctors.stream()
                .map(d -> convertToDTO(d, locale))
                .collect(Collectors.toList());
    }

    public List<DoctorDTO> getActiveDoctors(String locale) {
        List<Doctor> doctors = doctorRepository.findByIsActiveTrue();
        return doctors.stream()
                .map(d -> convertToDTO(d, locale))
                .collect(Collectors.toList());
    }

    public DoctorDTO getDoctorByUsername(String username, String locale) {
        return doctorRepository.findByUsername(username)
                .map(d -> convertToDTO(d, locale))
                .orElse(null);
    }

    public DoctorDTO getDoctorById(String id, String locale) {
        return doctorRepository.findById(id)
                .map(d -> convertToDTO(d, locale))
                .orElse(null);
    }

    private DoctorDTO convertToDTO(Doctor doctor, String locale) {
        List<String> specialties = specialtyRepository.findByDoctorId(doctor.getId())
                .stream()
                .map(DoctorSpecialty::getSpecialty)
                .collect(Collectors.toList());

        String translatedName = translationService.translate(doctor.getName(), locale);
        String title = translationService.translate(doctor.getTitle(), locale);
        String department = translationService.translate(doctor.getDepartment(), locale);
        String experience = translationService.translateExperience(doctor.getExperience(), locale);
        List<String> translatedSpecialties = translationService.translateList(specialties, locale);

        return new DoctorDTO(
                doctor.getId(),
                doctor.getUsername(),
                translatedName,
                title,
                department,
                doctor.getAvatar(),
                experience,
                translatedSpecialties,
                doctor.getIsActive()
        );
    }
}
