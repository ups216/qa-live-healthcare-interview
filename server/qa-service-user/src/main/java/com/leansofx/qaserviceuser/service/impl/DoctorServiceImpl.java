package com.leansofx.qaserviceuser.service.impl;

import com.leansofx.qaserviceuser.entity.Doctor;
import com.leansofx.qaserviceuser.mapper.DoctorMapper;
import com.leansofx.qaserviceuser.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private DoctorMapper doctorMapper;

    @Override
    public List<Doctor> getDoctorList() {
        return doctorMapper.selectDoctorList();
    }

    @Override
    public Doctor getDoctorById(String id) {
        return doctorMapper.selectDoctorById(id);
    }

    @Override
    public List<Doctor> getDoctorListByDepartment(String department) {
        return doctorMapper.selectDoctorListByDepartment(department);
    }

    @Override
    public List<Doctor> getActiveDoctorList() {
        return doctorMapper.selectActiveDoctorList();
    }

    @Override
    public List<Doctor> getDoctorListByPage(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return doctorMapper.selectDoctorListByPage(offset, pageSize);
    }
}
