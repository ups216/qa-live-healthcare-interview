package com.leansofx.qaserviceuser.dto;

public class PatientDto {
    private String id;
    private String username;
    private String name;
    private String birthday;
    private String phone;
    private String gender;

    public PatientDto() {
    }

    public PatientDto(String id, String username, String name, String birthday, String phone, String gender) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.birthday = birthday;
        this.phone = phone;
        this.gender = gender;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
