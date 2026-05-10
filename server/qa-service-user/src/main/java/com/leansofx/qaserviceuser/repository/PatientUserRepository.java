package com.leansofx.qaserviceuser.repository;

import com.leansofx.qaserviceuser.entity.PatientUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientUserRepository extends JpaRepository<PatientUser, Long> {
    
    Optional<PatientUser> findByUsername(String username);
    
    Optional<PatientUser> findByEmail(String email);
}
