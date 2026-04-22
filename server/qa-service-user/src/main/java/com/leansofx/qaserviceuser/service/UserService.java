package com.leansofx.qaserviceuser.service;

import com.leansofx.qaserviceuser.dto.DoctorDTO;
import com.leansofx.qaserviceuser.dto.PatientDTO;
import com.leansofx.qaserviceuser.entity.Doctor;
import com.leansofx.qaserviceuser.entity.DoctorSpecialty;
import com.leansofx.qaserviceuser.entity.Patient;
import com.leansofx.qaserviceuser.repository.DoctorRepository;
import com.leansofx.qaserviceuser.repository.DoctorSpecialtyRepository;
import com.leansofx.qaserviceuser.repository.PatientRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final DoctorSpecialtyRepository doctorSpecialtyRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(PatientRepository patientRepository,
                       DoctorRepository doctorRepository,
                       DoctorSpecialtyRepository doctorSpecialtyRepository) {
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.doctorSpecialtyRepository = doctorSpecialtyRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // ========== 患者相关 ==========

    public PatientDTO loginPatient(String username, String rawPassword) {
        Patient patient = patientRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("AUTH_FAILED"));

        if (!passwordEncoder.matches(rawPassword, patient.getPassword())) {
            throw new RuntimeException("AUTH_FAILED");
        }

        return toPatientDTO(patient);
    }

    public PatientDTO registerPatient(String username, String rawPassword, String name,
                                       String birthday, String phone, String gender) {
        if (patientRepository.existsByUsername(username)) {
            throw new RuntimeException("USERNAME_EXISTS");
        }

        Patient patient = new Patient();
        patient.setId("patient" + System.currentTimeMillis());
        patient.setUsername(username);
        patient.setPassword(passwordEncoder.encode(rawPassword));
        patient.setName(name);
        patient.setBirthday(birthday);
        patient.setPhone(phone != null ? phone : "");
        patient.setGender(gender != null ? gender : "");

        patient = patientRepository.save(patient);
        return toPatientDTO(patient);
    }

    public PatientDTO getPatientById(String id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("NOT_FOUND"));
        return toPatientDTO(patient);
    }

    private PatientDTO toPatientDTO(Patient patient) {
        PatientDTO dto = new PatientDTO();
        dto.setId(patient.getId());
        dto.setUsername(patient.getUsername());
        dto.setName(patient.getName());
        dto.setBirthday(patient.getBirthday());
        dto.setPhone(patient.getPhone());
        dto.setGender(patient.getGender());
        return dto;
    }

    // ========== 医生相关 ==========

    public DoctorDTO loginDoctor(String username, String rawPassword) {
        Doctor doctor = doctorRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("AUTH_FAILED"));

        if (!rawPassword.equals(doctor.getPassword())) {
            throw new RuntimeException("AUTH_FAILED");
        }

        return toDoctorDTO(doctor);
    }

    public List<DoctorDTO> getAllDoctors() {
        List<Doctor> doctors = doctorRepository.findAll();
        return doctors.stream().map(this::toDoctorDTO).collect(Collectors.toList());
    }

    public List<DoctorDTO> getActiveDoctors() {
        List<Doctor> doctors = doctorRepository.findByIsActiveTrue();
        return doctors.stream().map(this::toDoctorDTO).collect(Collectors.toList());
    }

    public DoctorDTO getDoctorByUsername(String username) {
        Doctor doctor = doctorRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("NOT_FOUND"));
        return toDoctorDTO(doctor);
    }

    public DoctorDTO getDoctorById(String id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("NOT_FOUND"));
        return toDoctorDTO(doctor);
    }

    private DoctorDTO toDoctorDTO(Doctor doctor) {
        DoctorDTO dto = new DoctorDTO();
        dto.setId(doctor.getId());
        dto.setUsername(doctor.getUsername());
        dto.setName(doctor.getName());
        dto.setTitle(doctor.getTitle());
        dto.setDepartment(doctor.getDepartment());
        dto.setAvatar(doctor.getAvatar());
        dto.setExperience(doctor.getExperience());
        dto.setIsActive(doctor.getIsActive());

        List<String> specialties = doctorSpecialtyRepository.findByDoctorId(doctor.getId())
                .stream().map(DoctorSpecialty::getSpecialty).collect(Collectors.toList());
        dto.setSpecialties(specialties);

        return dto;
    }
}
