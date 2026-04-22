package com.leansofx.qaservicequestion.service;

import com.leansofx.qaservicequestion.bean.Patient;
import com.leansofx.qaservicequestion.dao.PatientDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {

    @Autowired
    private PatientDao patientDao;

    public Patient login(String name, String password) {
        System.out.println("Login attempt for user: " + name);
        
        // 首先尝试通过名称和密码查询
        Patient patient = patientDao.findByNameAndPassword(name, password);
        if (patient != null) {
            return patient;
        }
        
        // 如果失败，尝试获取所有患者，然后逐个比较
        List<Patient> allPatients = patientDao.findAll();
        for (Patient p : allPatients) {
            if (p.getName() != null && p.getPassword() != null) {
                if (p.getName().equals(name) && p.getPassword().equals(password)) {
                    return p;
                }
            }
        }
        
        return null;
    }

    public List<Patient> getAllPatients() {
        return patientDao.findAll();
    }
}
