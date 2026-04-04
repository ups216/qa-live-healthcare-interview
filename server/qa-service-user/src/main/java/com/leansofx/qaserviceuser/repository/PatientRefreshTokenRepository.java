package com.leansofx.qaserviceuser.repository;

import com.leansofx.qaserviceuser.entity.PatientRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PatientRefreshTokenRepository extends JpaRepository<PatientRefreshToken, Long> {

    Optional<PatientRefreshToken> findByToken(String token);

    Optional<PatientRefreshToken> findByTokenAndExpiredAtAfter(String token, LocalDateTime now);

    @Modifying
    @Query("DELETE FROM PatientRefreshToken t WHERE t.patientId = :patientId")
    void deleteByPatientId(@Param("patientId") String patientId);

    @Modifying
    @Query("DELETE FROM PatientRefreshToken t WHERE t.token = :token")
    void deleteByToken(@Param("token") String token);

    @Modifying
    @Query("DELETE FROM PatientRefreshToken t WHERE t.expiredAt < :now")
    void deleteByExpiredAtBefore(@Param("now") LocalDateTime now);
}
