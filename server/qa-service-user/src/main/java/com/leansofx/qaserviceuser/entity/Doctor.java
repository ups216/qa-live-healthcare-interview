package com.leansofx.qaserviceuser.entity;

import java.util.List;

/**
 * 医生实体，与 t_doctor 表一一对应。
 *
 * <p><b>注意</b>：本类含 password 字段，仅允许在 Service / Mapper 层流转，
 * 严禁作为 Controller 的返回值（需求2 要求对外响应剔除密码）。
 * 对外请统一使用 {@code DoctorVO}。
 */
public class Doctor {

    private String id;
    private String username;
    private String password;
    private String name;
    private String title;
    private String department;
    private String avatar;
    private String experience;

    /** 由 t_doctor.specialties(JSON) 经 JsonStringListTypeHandler 映射而来 */
    private List<String> specialties;

    /** 对应 t_doctor.is_active，由 map-underscore-to-camel-case 自动映射 */
    private Boolean isActive;

    public Doctor() {
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

    public List<String> getSpecialties() {
        return specialties;
    }

    public void setSpecialties(List<String> specialties) {
        this.specialties = specialties;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
