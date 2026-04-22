package com.leansofx.qaserviceuser.repository;

import com.leansofx.qaserviceuser.entity.DoctorSpecialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorSpecialtyRepository extends JpaRepository<DoctorSpecialty, Long> {

    List<DoctorSpecialty> findByDoctorId(String doctorId);

    void deleteByDoctorId(String doctorId);
}
