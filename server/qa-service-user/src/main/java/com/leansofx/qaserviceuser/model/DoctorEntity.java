package com.leansofx.qaserviceuser.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "doctors")
public class DoctorEntity {

    @Id
    @Column(name = "id", length = 32, nullable = false)
    private String id;

    @Column(name = "username", length = 64, nullable = false, unique = true)
    private String username;

    @Column(name = "password", length = 128, nullable = false)
    private String password;

    @Column(name = "name", length = 64, nullable = false)
    private String name;

    @Column(name = "title", length = 64, nullable = false)
    private String title;

    @Column(name = "department", length = 64, nullable = false)
    private String department;

    @Column(name = "avatar", length = 512, nullable = false)
    private String avatar;

    @Column(name = "experience", length = 128, nullable = false)
    private String experience;

    @Column(name = "specialties_json", columnDefinition = "json", nullable = false)
    private String specialtiesJson;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

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

    public String getSpecialtiesJson() {
        return specialtiesJson;
    }

    public void setSpecialtiesJson(String specialtiesJson) {
        this.specialtiesJson = specialtiesJson;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}

