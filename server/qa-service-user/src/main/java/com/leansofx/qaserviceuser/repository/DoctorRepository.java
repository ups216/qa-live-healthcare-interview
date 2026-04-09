package com.leansofx.qaserviceuser.repository;

import com.leansofx.qaserviceuser.model.DoctorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoctorRepository extends JpaRepository<DoctorEntity, String> {
    Optional<DoctorEntity> findByUsername(String username);
}

