package com.leansofx.qaserviceuser.repository;

import com.leansofx.qaserviceuser.dto.PatientDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class PatientRepository {

    private final JdbcTemplate jdbcTemplate;

    public PatientRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<PatientDto> ROW_MAPPER = (rs, rowNum) -> {
        PatientDto patient = new PatientDto();
        patient.setId(rs.getString("id"));
        patient.setUsername(rs.getString("username"));
        patient.setName(rs.getString("name"));
        patient.setPhone(rs.getString("phone"));
        patient.setBirthday(rs.getString("birthday"));
        patient.setGender(rs.getString("gender"));
        return patient;
    };

    public Optional<PatientDto> findByUsername(String username) {
        String sql = "SELECT id, username, name, phone, birthday, gender FROM patients WHERE username = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, ROW_MAPPER, username));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<String> findPasswordByUsername(String username) {
        String sql = "SELECT password FROM patients WHERE username = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, String.class, username));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<PatientDto> findByNameAndBirthday(String name, String birthday) {
        String sql = "SELECT * FROM patients WHERE name = ? AND birthday = ? LIMIT 1";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, ROW_MAPPER, name, birthday));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public void save(PatientDto patient, String encryptedPassword) {
        String sql = "INSERT INTO patients (id, username, password, name, phone, birthday, gender) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, patient.getId(), patient.getUsername(), encryptedPassword, 
                patient.getName(), patient.getPhone(), patient.getBirthday(), patient.getGender());
    }
}
