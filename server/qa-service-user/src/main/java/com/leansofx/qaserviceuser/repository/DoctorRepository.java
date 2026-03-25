package com.leansofx.qaserviceuser.repository;

import com.leansofx.qaserviceuser.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    
    List<Doctor> findByDepartment(String department);
    
    List<Doctor> findByNameContaining(String name);
    
    Doctor findByUsername(String username);
}
