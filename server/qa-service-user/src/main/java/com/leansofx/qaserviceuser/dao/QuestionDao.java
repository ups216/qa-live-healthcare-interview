package com.leansofx.qaserviceuser.dao;

import com.leansofx.qaserviceuser.bean.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class QuestionDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Question> questionRowMapper = new RowMapper<Question>() {
        @Override
        public Question mapRow(ResultSet rs, int rowNum) throws SQLException {
            Question question = new Question();
            question.setId(rs.getString("id"));
            question.setPatientId(rs.getString("patient_id"));
            question.setPatientName(rs.getString("patient_name"));
            question.setDoctorId(rs.getString("doctor_id"));
            question.setDoctorName(rs.getString("doctor_name"));
            question.setQuestion(rs.getString("question"));
            question.setSubmitTime(rs.getTimestamp("submit_time").toLocalDateTime());
            question.setStatus(rs.getString("status"));
            
            String answer = rs.getString("answer");
            question.setAnswer(answer);
            
            Timestamp answerTime = rs.getTimestamp("answer_time");
            if (answerTime != null) {
                question.setAnswerTime(answerTime.toLocalDateTime());
            }
            return question;
        }
    };

    public List<Question> findAll() {
        String sql = "SELECT * FROM question";
        return jdbcTemplate.query(sql, questionRowMapper);
    }

    public List<Question> findByPatientId(String patientId) {
        String sql = "SELECT * FROM question WHERE patient_id = ?";
        return jdbcTemplate.query(sql, questionRowMapper, patientId);
    }

    public List<Question> findByDoctorId(String doctorId) {
        String sql = "SELECT * FROM question WHERE doctor_id = ?";
        return jdbcTemplate.query(sql, questionRowMapper, doctorId);
    }

    public List<Question> findByStatus(String status) {
        String sql = "SELECT * FROM question WHERE status = ?";
        return jdbcTemplate.query(sql, questionRowMapper, status);
    }

    public Question findById(String id) {
        String sql = "SELECT * FROM question WHERE id = ?";
        List<Question> questions = jdbcTemplate.query(sql, questionRowMapper, id);
        return questions.isEmpty() ? null : questions.get(0);
    }

    public int save(Question question) {
        String sql = "INSERT INTO question (id, patient_id, patient_name, doctor_id, doctor_name, question, submit_time, status, answer, answer_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, question.getId(), question.getPatientId(), question.getPatientName(),
                question.getDoctorId(), question.getDoctorName(), question.getQuestion(), question.getSubmitTime(),
                question.getStatus(), question.getAnswer(), question.getAnswerTime());
    }

    public int update(Question question) {
        String sql = "UPDATE question SET patient_id = ?, patient_name = ?, doctor_id = ?, doctor_name = ?, question = ?, submit_time = ?, status = ?, answer = ?, answer_time = ? WHERE id = ?";
        return jdbcTemplate.update(sql, question.getPatientId(), question.getPatientName(), question.getDoctorId(),
                question.getDoctorName(), question.getQuestion(), question.getSubmitTime(), question.getStatus(),
                question.getAnswer(), question.getAnswerTime(), question.getId());
    }

    public int updateAnswer(String id, String answer, LocalDateTime answerTime) {
        String sql = "UPDATE question SET answer = ?, answer_time = ?, status = 'answered' WHERE id = ?";
        return jdbcTemplate.update(sql, answer, answerTime, id);
    }

    public int deleteById(String id) {
        String sql = "DELETE FROM question WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
