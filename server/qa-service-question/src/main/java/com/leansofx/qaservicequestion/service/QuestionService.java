package com.leansofx.qaservicequestion.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leansofx.qaservicequestion.dto.AnswerQuestionRequest;
import com.leansofx.qaservicequestion.dto.CreateQuestionRequest;
import com.leansofx.qaservicequestion.dto.QuestionResponse;
import com.leansofx.qaservicequestion.mapper.QuestionMapper;
import com.leansofx.qaservicequestion.model.Question;
import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class QuestionService {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final QuestionMapper questionMapper;
    private final ObjectMapper objectMapper;

    public QuestionService(QuestionMapper questionMapper, ObjectMapper objectMapper) {
        this.questionMapper = questionMapper;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    void init() throws IOException {
        if (questionMapper.selectCount(null) > 0) {
            return;
        }

        List<Map<String, Object>> questions = objectMapper.readValue(readProjectFile("question-list.json"), new TypeReference<>() {});
        for (Map<String, Object> item : questions) {
            Question question = new Question();
            question.setId(stringValue(item.get("id")));
            question.setPatientId(stringValue(item.get("patientId")));
            question.setPatientName(stringValue(item.get("patientName")));
            question.setDoctorId(stringValue(item.get("doctorId")));
            question.setDoctorName(stringValue(item.get("doctorName")));
            question.setQuestion(stringValue(item.get("question")));
            question.setSubmitTime(LocalDateTime.parse(stringValue(item.get("submitTime"))));
            question.setStatus(stringValue(item.get("status")));
            if (!stringValue(item.get("answer")).isBlank()) {
                question.setAnswer(stringValue(item.get("answer")));
            }
            if (!stringValue(item.get("answerTime")).isBlank()) {
                question.setAnswerTime(LocalDateTime.parse(stringValue(item.get("answerTime"))));
            }
            questionMapper.insert(question);
        }
    }

    public List<QuestionResponse> listQuestions(String doctorId, String patientId, String status) {
        LambdaQueryWrapper<Question> query = new LambdaQueryWrapper<>();
        query.eq(doctorId != null && !doctorId.isBlank(), Question::getDoctorId, doctorId);
        query.eq(patientId != null && !patientId.isBlank(), Question::getPatientId, patientId);
        query.eq(status != null && !status.isBlank(), Question::getStatus, status);

        return questionMapper.selectList(query).stream()
                .sorted(Comparator.comparing(Question::getSubmitTime).reversed())
                .map(this::toResponse)
                .toList();
    }

    public QuestionResponse createQuestion(CreateQuestionRequest request) {
        validateCreateRequest(request);
        Question question = new Question();
        question.setId("q" + System.currentTimeMillis());
        question.setPatientId(request.getPatientId().trim());
        question.setPatientName(request.getPatientName().trim());
        question.setDoctorId(request.getDoctorId().trim());
        question.setDoctorName(request.getDoctorName().trim());
        question.setQuestion(request.getQuestion().trim());
        question.setSubmitTime(LocalDateTime.now());
        question.setStatus("pending");
        questionMapper.insert(question);
        return toResponse(question);
    }

    public QuestionResponse answerQuestion(String questionId, AnswerQuestionRequest request) {
        if (request == null || request.getAnswer() == null || request.getAnswer().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Answer is required");
        }
        Question question = getQuestion(questionId);
        question.setStatus("answered");
        question.setAnswer(request.getAnswer().trim());
        question.setAnswerTime(LocalDateTime.now());
        questionMapper.updateById(question);
        return toResponse(question);
    }

    public QuestionResponse markQuestionAsAnswered(String questionId) {
        Question question = getQuestion(questionId);
        question.setStatus("answered");
        if (question.getAnswer() == null || question.getAnswer().isBlank()) {
            question.setAnswer("Answered offline by doctor.");
        }
        question.setAnswerTime(LocalDateTime.now());
        questionMapper.updateById(question);
        return toResponse(question);
    }

    public Map<String, Object> getSummary() {
        LambdaQueryWrapper<Question> pendingQuery = new LambdaQueryWrapper<>();
        pendingQuery.eq(Question::getStatus, "pending");
        long total = questionMapper.selectCount(null);
        long pending = questionMapper.selectCount(pendingQuery);
        return Map.of(
                "totalQuestions", total,
                "pendingQuestions", pending,
                "answeredQuestions", total - pending
        );
    }

    private void validateCreateRequest(CreateQuestionRequest request) {
        if (request == null
                || isBlank(request.getPatientId())
                || isBlank(request.getPatientName())
                || isBlank(request.getDoctorId())
                || isBlank(request.getDoctorName())
                || isBlank(request.getQuestion())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing required fields");
        }
    }

    private Question getQuestion(String questionId) {
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found");
        }
        return question;
    }

    private QuestionResponse toResponse(Question question) {
        QuestionResponse response = new QuestionResponse();
        response.setId(question.getId());
        response.setPatientId(question.getPatientId());
        response.setPatientName(question.getPatientName());
        response.setDoctorId(question.getDoctorId());
        response.setDoctorName(question.getDoctorName());
        response.setQuestion(question.getQuestion());
        response.setSubmitTime(question.getSubmitTime().format(TIME_FORMATTER));
        response.setStatus(question.getStatus());
        response.setAnswer(question.getAnswer());
        response.setAnswerTime(question.getAnswerTime() == null ? null : question.getAnswerTime().format(TIME_FORMATTER));
        return response;
    }

    private String readProjectFile(String fileName) throws IOException {
        Path dataFile = findDataFile(fileName);
        return Files.readString(dataFile);
    }

    private Path findDataFile(String fileName) throws IOException {
        Path current = Path.of(System.getProperty("user.dir")).toAbsolutePath().normalize();
        while (current != null) {
            Path candidate = current.resolve("web").resolve("qa-web").resolve("src").resolve("data").resolve(fileName);
            if (Files.exists(candidate)) {
                return candidate;
            }
            current = current.getParent();
        }
        throw new IOException("Unable to locate seed data file: " + fileName);
    }

    private String stringValue(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
