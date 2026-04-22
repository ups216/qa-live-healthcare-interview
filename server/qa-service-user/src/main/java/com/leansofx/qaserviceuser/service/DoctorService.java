package com.leansofx.qaserviceuser.service;

import com.leansofx.qaserviceuser.entity.Doctor;
import com.leansofx.qaserviceuser.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public Optional<Doctor> getDoctorById(String id) {
        return doctorRepository.findById(id);
    }

    public Optional<Doctor> getDoctorByUsername(String username) {
        return doctorRepository.findByUsername(username);
    }

    public List<Doctor> getActiveDoctors() {
        return doctorRepository.findByIsActiveTrue();
    }

    public List<Doctor> getDoctorsByDepartment(String department) {
        return doctorRepository.findByDepartment(department);
    }

    public Doctor saveDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    public void deleteDoctor(String id) {
        doctorRepository.deleteById(id);
    }
}
