package com.leansofx.qaserviceuser.repository;

import com.leansofx.qaserviceuser.dto.DoctorDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Repository
public class DoctorRepository {

    private final JdbcTemplate jdbcTemplate;

    public DoctorRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<DoctorDto> findAll() {
        String sql = "SELECT d.id, d.username, d.name, d.title, d.department, d.avatar, d.experience, d.is_active, ds.specialty FROM doctors d LEFT JOIN doctor_specialties ds ON ds.doctor_id = d.id ORDER BY d.id, ds.id";
        return queryDoctors(sql, new Object[]{});
    }

    public List<DoctorDto> findActive() {
        String sql = "SELECT d.id, d.username, d.name, d.title, d.department, d.avatar, d.experience, d.is_active, ds.specialty FROM doctors d LEFT JOIN doctor_specialties ds ON ds.doctor_id = d.id WHERE d.is_active = 1 ORDER BY d.id, ds.id";
        return queryDoctors(sql, new Object[]{});
    }

    public Optional<DoctorDto> findByUsername(String username) {
        String sql = "SELECT d.id, d.username, d.name, d.title, d.department, d.avatar, d.experience, d.is_active, ds.specialty FROM doctors d LEFT JOIN doctor_specialties ds ON ds.doctor_id = d.id WHERE d.username = ? ORDER BY d.id, ds.id";
        List<DoctorDto> doctors = queryDoctors(sql, new Object[]{username});
        return doctors.isEmpty() ? Optional.empty() : Optional.of(doctors.get(0));
    }

    public Optional<DoctorDto> findByCredentials(String username, String password) {
        Objects.requireNonNull(username, "username must not be null");
        Objects.requireNonNull(password, "password must not be null");
        String sql = "SELECT d.id, d.username, d.name, d.title, d.department, d.avatar, d.experience, d.is_active, ds.specialty FROM doctors d LEFT JOIN doctor_specialties ds ON ds.doctor_id = d.id WHERE d.username = ? AND d.password = ? ORDER BY d.id, ds.id";
        List<DoctorDto> doctors = queryDoctors(sql, new Object[]{username, password});
        return doctors.isEmpty() ? Optional.empty() : Optional.of(doctors.get(0));
    }

    private List<DoctorDto> queryDoctors(String sql, Object[] args) {
        return jdbcTemplate.query(sql, args, rs -> {
            Map<String, DoctorDto> doctors = new LinkedHashMap<>();
            while (rs.next()) {
                String doctorId = rs.getString("id");
                DoctorDto doctor = doctors.computeIfAbsent(doctorId, key -> mapDoctor(rs));

                String specialty = rs.getString("specialty");
                if (specialty != null && !specialty.isBlank()) {
                    doctor.getSpecialties().add(specialty);
                }
            }
            return new ArrayList<>(doctors.values());
        });
    }

    private DoctorDto mapDoctor(ResultSet rs) {
        try {
            DoctorDto doctor = new DoctorDto();
            doctor.setId(rs.getString("id"));
            doctor.setUsername(rs.getString("username"));
            doctor.setName(rs.getString("name"));
            doctor.setTitle(rs.getString("title"));
            doctor.setDepartment(rs.getString("department"));
            doctor.setAvatar(rs.getString("avatar"));
            doctor.setExperience(rs.getString("experience"));
            doctor.setActive(rs.getBoolean("is_active"));
            doctor.setSpecialties(new ArrayList<>());
            return doctor;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to map doctor record", e);
        }
    }
}
