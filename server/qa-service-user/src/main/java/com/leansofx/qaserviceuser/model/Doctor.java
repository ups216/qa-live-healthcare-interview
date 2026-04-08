package com.leansofx.qaserviceuser.model;

public class Doctor {
    private Long id;
    private String name;
    private String title;
    private String department;
    private String hospital;
    private String avatar;
    private String specialty;

    public Doctor() {
    }

    public Doctor(Long id, String name, String title, String department, String hospital, String avatar, String specialty) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.department = department;
        this.hospital = hospital;
        this.avatar = avatar;
        this.specialty = specialty;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }
}