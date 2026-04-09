package com.leansofx.qaserviceuser.repository;

import com.leansofx.qaserviceuser.model.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<PatientEntity, String> {

    Optional<PatientEntity> findByUsername(String username);

    boolean existsByUsername(String username);
}
