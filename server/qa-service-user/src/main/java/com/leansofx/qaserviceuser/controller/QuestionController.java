package com.leansofx.qaserviceuser.controller;

import com.leansofx.qaserviceuser.bean.Question;
import com.leansofx.qaserviceuser.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @GetMapping
    public Map<String, Object> getAllQuestions() {
        Map<String, Object> result = new HashMap<>();
        List<Question> questions = questionService.getAllQuestions();
        result.put("status", "success");
        result.put("data", questions);
        result.put("count", questions.size());
        return result;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getQuestionById(@PathVariable String id) {
        Map<String, Object> result = new HashMap<>();
        Question question = questionService.getQuestionById(id);
        if (question != null) {
            result.put("status", "success");
            result.put("data", question);
        } else {
            result.put("status", "error");
            result.put("message", "问题不存在");
        }
        return result;
    }

    @GetMapping("/patient/{patientId}")
    public Map<String, Object> getQuestionsByPatientId(@PathVariable String patientId) {
        Map<String, Object> result = new HashMap<>();
        List<Question> questions = questionService.getQuestionsByPatientId(patientId);
        result.put("status", "success");
        result.put("data", questions);
        result.put("count", questions.size());
        return result;
    }

    @GetMapping("/doctor/{doctorId}")
    public Map<String, Object> getQuestionsByDoctorId(@PathVariable String doctorId) {
        Map<String, Object> result = new HashMap<>();
        List<Question> questions = questionService.getQuestionsByDoctorId(doctorId);
        result.put("status", "success");
        result.put("data", questions);
        result.put("count", questions.size());
        return result;
    }

    @GetMapping("/status/{status}")
    public Map<String, Object> getQuestionsByStatus(@PathVariable String status) {
        Map<String, Object> result = new HashMap<>();
        List<Question> questions = questionService.getQuestionsByStatus(status);
        result.put("status", "success");
        result.put("data", questions);
        result.put("count", questions.size());
        return result;
    }

    @PostMapping
    public Map<String, Object> addQuestion(@RequestBody Question question) {
        Map<String, Object> result = new HashMap<>();
        boolean success = questionService.addQuestion(question);
        if (success) {
            result.put("status", "success");
            result.put("message", "问题添加成功");
        } else {
            result.put("status", "error");
            result.put("message", "问题添加失败");
        }
        return result;
    }

    @PutMapping("/{id}")
    public Map<String, Object> updateQuestion(@PathVariable String id, @RequestBody Question question) {
        Map<String, Object> result = new HashMap<>();
        question.setId(id);
        boolean success = questionService.updateQuestion(question);
        if (success) {
            result.put("status", "success");
            result.put("message", "问题更新成功");
        } else {
            result.put("status", "error");
            result.put("message", "问题更新失败");
        }
        return result;
    }

    @PostMapping("/{id}/answer")
    public Map<String, Object> answerQuestion(@PathVariable String id, @RequestBody Map<String, String> request) {
        Map<String, Object> result = new HashMap<>();
        String answer = request.get("answer");
        boolean success = questionService.answerQuestion(id, answer);
        if (success) {
            result.put("status", "success");
            result.put("message", "问题回答成功");
        } else {
            result.put("status", "error");
            result.put("message", "问题回答失败");
        }
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteQuestion(@PathVariable String id) {
        Map<String, Object> result = new HashMap<>();
        boolean success = questionService.deleteQuestion(id);
        if (success) {
            result.put("status", "success");
            result.put("message", "问题删除成功");
        } else {
            result.put("status", "error");
            result.put("message", "问题删除失败");
        }
        return result;
    }
}