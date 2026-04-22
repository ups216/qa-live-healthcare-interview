package com.leansofx.qaserviceuser.service;

import com.leansofx.qaserviceuser.bean.Question;
import com.leansofx.qaserviceuser.dao.QuestionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionDao questionDao;

    public List<Question> getAllQuestions() {
        return questionDao.findAll();
    }

    public List<Question> getQuestionsByPatientId(String patientId) {
        return questionDao.findByPatientId(patientId);
    }

    public List<Question> getQuestionsByDoctorId(String doctorId) {
        return questionDao.findByDoctorId(doctorId);
    }

    public List<Question> getQuestionsByStatus(String status) {
        return questionDao.findByStatus(status);
    }

    public Question getQuestionById(String id) {
        return questionDao.findById(id);
    }

    public boolean addQuestion(Question question) {
        return questionDao.save(question) > 0;
    }

    public boolean updateQuestion(Question question) {
        return questionDao.update(question) > 0;
    }

    public boolean answerQuestion(String id, String answer) {
        return questionDao.updateAnswer(id, answer, LocalDateTime.now()) > 0;
    }

    public boolean deleteQuestion(String id) {
        return questionDao.deleteById(id) > 0;
    }
}
