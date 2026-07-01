package com.leansofx.qaserviceuser.repository;

import com.leansofx.qaserviceuser.entity.Patient;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, String> {

    Optional<Patient> findByUsername(String username);

    boolean existsByUsername(String username);
}
