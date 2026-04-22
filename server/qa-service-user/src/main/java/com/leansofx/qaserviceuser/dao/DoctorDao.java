package com.leansofx.qaserviceuser.dao;

import com.leansofx.qaserviceuser.bean.Doctor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class DoctorDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Doctor> doctorRowMapper = new RowMapper<Doctor>() {
        @Override
        public Doctor mapRow(ResultSet rs, int rowNum) throws SQLException {
            Doctor doctor = new Doctor();
            doctor.setId(rs.getString("id"));
            doctor.setUsername(rs.getString("username"));
            doctor.setPassword(rs.getString("password"));
            doctor.setName(rs.getString("name"));
            doctor.setTitle(rs.getString("title"));
            doctor.setDepartment(rs.getString("department"));
            doctor.setAvatar(rs.getString("avatar"));
            doctor.setExperience(rs.getString("experience"));
            doctor.setSpecialties(rs.getString("specialties"));
            doctor.setActive(rs.getBoolean("is_active"));
            return doctor;
        }
    };

    public List<Doctor> findAll() {
        String sql = "SELECT * FROM doctor_user";
        return jdbcTemplate.query(sql, doctorRowMapper);
    }

    public List<Doctor> findActiveDoctors() {
        String sql = "SELECT * FROM doctor_user WHERE is_active = true";
        return jdbcTemplate.query(sql, doctorRowMapper);
    }

    public Doctor findById(String id) {
        String sql = "SELECT * FROM doctor_user WHERE id = ?";
        List<Doctor> doctors = jdbcTemplate.query(sql, doctorRowMapper, id);
        return doctors.isEmpty() ? null : doctors.get(0);
    }

    public Doctor findByUsername(String username) {
        String sql = "SELECT * FROM doctor_user WHERE username = ?";
        List<Doctor> doctors = jdbcTemplate.query(sql, doctorRowMapper, username);
        return doctors.isEmpty() ? null : doctors.get(0);
    }

    public int save(Doctor doctor) {
        String sql = "INSERT INTO doctor_user (id, username, password, name, title, department, avatar, experience, specialties, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, doctor.getId(), doctor.getUsername(), doctor.getPassword(), 
                doctor.getName(), doctor.getTitle(), doctor.getDepartment(), doctor.getAvatar(), 
                doctor.getExperience(), doctor.getSpecialties(), doctor.isActive());
    }

    public int update(Doctor doctor) {
        String sql = "UPDATE doctor_user SET username = ?, password = ?, name = ?, title = ?, department = ?, avatar = ?, experience = ?, specialties = ?, is_active = ? WHERE id = ?";
        return jdbcTemplate.update(sql, doctor.getUsername(), doctor.getPassword(), doctor.getName(), 
                doctor.getTitle(), doctor.getDepartment(), doctor.getAvatar(), doctor.getExperience(), 
                doctor.getSpecialties(), doctor.isActive(), doctor.getId());
    }

    public int deleteById(String id) {
        String sql = "DELETE FROM doctor_user WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
