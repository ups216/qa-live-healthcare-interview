package com.leansofx.qaservicequestion.dao;

import com.leansofx.qaservicequestion.bean.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Repository
public class PatientDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Patient> patientRowMapper = new RowMapper<Patient>() {
        @Override
        public Patient mapRow(ResultSet rs, int rowNum) throws SQLException {
            Patient patient = new Patient();
            patient.setId(rs.getString("id"));
            patient.setName(rs.getString("name"));
            patient.setBirthday(rs.getDate("birthday").toLocalDate());
            patient.setPhone(rs.getString("phone"));
            patient.setGender(rs.getString("gender"));
            patient.setPassword(rs.getString("password"));
            return patient;
        }
    };

    public Patient findByNameAndPassword(String name, String password) {
        String sql = "SELECT * FROM patient_user WHERE name = ? AND password = ?";
        List<Patient> patients = jdbcTemplate.query(sql, patientRowMapper, name, password);
        return patients.isEmpty() ? null : patients.get(0);
    }

    public Patient findByName(String name) {
        String sql = "SELECT * FROM patient_user WHERE name = ?";
        List<Patient> patients = jdbcTemplate.query(sql, patientRowMapper, name);
        return patients.isEmpty() ? null : patients.get(0);
    }

    public List<Patient> findAll() {
        String sql = "SELECT * FROM patient_user";
        return jdbcTemplate.query(sql, patientRowMapper);
    }
}
