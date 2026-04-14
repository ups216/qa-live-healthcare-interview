package com.leansofx.qaserviceuser.repository;

import com.leansofx.qaserviceuser.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    
    Optional<Department> findByName(String name);
    
    Optional<Department> findByCode(String code);
}