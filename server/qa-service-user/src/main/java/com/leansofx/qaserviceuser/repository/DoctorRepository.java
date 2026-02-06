package com.leansofx.qaserviceuser.repository;

import com.leansofx.qaserviceuser.domain.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, String> {
}

