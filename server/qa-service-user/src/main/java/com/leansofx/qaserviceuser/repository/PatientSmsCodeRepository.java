package com.leansofx.qaserviceuser.repository;

import com.leansofx.qaserviceuser.entity.PatientSmsCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PatientSmsCodeRepository extends JpaRepository<PatientSmsCode, Long> {

    @Query("SELECT s FROM PatientSmsCode s WHERE s.phone = :phone AND s.type = :type AND s.used = 0 ORDER BY s.createdAt DESC")
    List<PatientSmsCode> findValidByPhoneAndType(@Param("phone") String phone, @Param("type") String type);

    Optional<PatientSmsCode> findTopByPhoneAndTypeOrderByCreatedAtDesc(String phone, String type);

    @Query("SELECT s FROM PatientSmsCode s WHERE s.phone = :phone AND s.type = :type AND s.used = 0 AND s.expireAt > :now ORDER BY s.createdAt DESC")
    Optional<PatientSmsCode> findLatestValidByPhoneAndType(@Param("phone") String phone, @Param("type") String type, @Param("now") LocalDateTime now);

    void deleteByPhoneAndType(String phone, String type);

    void deleteByExpireAtBefore(LocalDateTime dateTime);
}
