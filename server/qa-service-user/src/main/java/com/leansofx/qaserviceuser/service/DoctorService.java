package com.leansofx.qaserviceuser.service;

import com.leansofx.qaserviceuser.bean.Doctor;
import com.leansofx.qaserviceuser.dao.DoctorDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorService {

    @Autowired
    private DoctorDao doctorDao;

    public List<Doctor> getAllDoctors() {
        return doctorDao.findAll();
    }

    public List<Doctor> getActiveDoctors() {
        return doctorDao.findActiveDoctors();
    }

    public Doctor getDoctorById(String id) {
        return doctorDao.findById(id);
    }

    public Doctor getDoctorByUsername(String username) {
        return doctorDao.findByUsername(username);
    }

    public boolean addDoctor(Doctor doctor) {
        return doctorDao.save(doctor) > 0;
    }

    public boolean updateDoctor(Doctor doctor) {
        return doctorDao.update(doctor) > 0;
    }

    public boolean deleteDoctor(String id) {
        return doctorDao.deleteById(id) > 0;
    }
}
