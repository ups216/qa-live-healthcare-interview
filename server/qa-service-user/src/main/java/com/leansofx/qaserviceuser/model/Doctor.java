package com.leansofx.qaserviceuser.model;

import javax.persistence.*;

@Entity
@Table(name = "doctors")
public class Doctor {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "title")
    private String title;

    @Column(name = "department")
    private String department;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "experience")
    private String experience;

    @Column(name = "specialties")
    private String specialties;

    @Column(name = "is_active")
    private boolean isActive;

    // Getters and Setters
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String[] getSpecialties() {
        if (specialties == null || specialties.isEmpty()) {
            return new String[0];
        }
        return specialties.split(",");
    }

    public void setSpecialties(String specialties) {
        this.specialties = specialties;
    }

    // 用于 JPA 存储的 setter
    public void setSpecialtiesArray(String[] specialties) {
        if (specialties == null || specialties.length == 0) {
            this.specialties = "";
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < specialties.length; i++) {
                sb.append(specialties[i]);
                if (i < specialties.length - 1) {
                    sb.append(",");
                }
            }
            this.specialties = sb.toString();
        }
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
