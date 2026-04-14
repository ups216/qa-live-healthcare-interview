package com.leansofx.qaserviceuser.repository;

import com.leansofx.qaserviceuser.entity.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpecialtyRepository extends JpaRepository<Specialty, Integer> {
    
    Optional<Specialty> findByName(String name);
    
    Optional<Specialty> findByCode(String code);
}