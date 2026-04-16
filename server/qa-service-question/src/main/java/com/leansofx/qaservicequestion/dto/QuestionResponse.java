package com.leansofx.qaservicequestion.dto;

public class QuestionResponse {

    private String id;
    private String patientId;
    private String patientName;
    private String doctorId;
    private String doctorName;
    private String question;
    private String submitTime;
    private String status;
    private String answer;
    private String answerTime;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public String getSubmitTime() { return submitTime; }
    public void setSubmitTime(String submitTime) { this.submitTime = submitTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
    public String getAnswerTime() { return answerTime; }
    public void setAnswerTime(String answerTime) { this.answerTime = answerTime; }
}
