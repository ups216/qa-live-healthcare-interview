package com.leansofx.qaserviceuser.dto;

import java.util.List;

/**
 * 医生对外响应模型。
 *
 * <p><b>安全约束</b>：字段与前端 {@code store/index.ts} 的 Doctor 接口对齐，
 * 且<b>刻意不包含 password 字段</b>。这是防止密码泄漏的结构性保障 ——
 * Controller 层不会接触到含密码的 Doctor 实体。
 */
public class DoctorVO {

    private String id;
    private String username;
    private String name;
    private String title;
    private String department;
    private String avatar;
    private String experience;

    /** 必须是真数组，前端 v-for 依赖其为 string[] */
    private List<String> specialties;

    /** 必须是真布尔，前端 Doctors.vue 的 :disabled 与 v-if 依赖其真假判断 */
    private Boolean isActive;

    public DoctorVO() {
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
