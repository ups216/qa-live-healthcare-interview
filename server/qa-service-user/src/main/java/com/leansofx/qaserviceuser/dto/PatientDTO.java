package com.leansofx.qaserviceuser.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PatientDTO {

    private String id;
    private String phone;
    private String name;
    private Integer gender;
    private LocalDate birthday;
    private String avatar;
    private Integer status;
    private Integer questionCount;
    private LocalDateTime createdAt;

    public PatientDTO() {
    }

    public PatientDTO(String id, String phone, String name, Integer gender, LocalDate birthday, String avatar, Integer status, LocalDateTime createdAt) {
        this.id = id;
        this.phone = phone;
        this.name = name;
        this.gender = gender;
        this.birthday = birthday;
        this.avatar = avatar;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(Integer questionCount) {
        this.questionCount = questionCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
