package com.leansofx.qaservicequestion.controller;

import com.leansofx.qaservicequestion.dto.AnswerQuestionRequest;
import com.leansofx.qaservicequestion.dto.CreateQuestionRequest;
import com.leansofx.qaservicequestion.dto.QuestionResponse;
import com.leansofx.qaservicequestion.service.QuestionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping
    public List<QuestionResponse> listQuestions(
            @RequestParam(required = false) String doctorId,
            @RequestParam(required = false) String patientId,
            @RequestParam(required = false) String status
    ) {
        return questionService.listQuestions(doctorId, patientId, status);
    }

    @PostMapping
    public QuestionResponse createQuestion(@RequestBody CreateQuestionRequest request) {
        return questionService.createQuestion(request);
    }

    @PostMapping("/{questionId}/answer")
    public QuestionResponse answerQuestion(
            @PathVariable String questionId,
            @RequestBody AnswerQuestionRequest request
    ) {
        return questionService.answerQuestion(questionId, request);
    }

    @PostMapping("/{questionId}/mark-answered")
    public QuestionResponse markQuestionAsAnswered(@PathVariable String questionId) {
        return questionService.markQuestionAsAnswered(questionId);
    }

    @GetMapping("/summary")
    public Map<String, Object> summary() {
        return questionService.getSummary();
    }
}
