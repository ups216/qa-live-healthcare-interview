package com.leansofx.qaserviceuser.entity;

/**
 * 问诊用户（患者）实体，与 t_patient 表一一对应。
 *
 * <p><b>注意</b>：含 password 字段，仅允许在 Service / Mapper 层流转。
 * 严禁作为 Controller 的返回值，对外请一律使用 {@code PatientVO}。
 */
public class Patient {

    private String id;
    private String username;

    /** BCrypt 密文，形如 $2a$10$...，绝不对外输出 */
    private String password;

    private String name;
    private String birthday;
    private String phone;
    private String gender;

    public Patient() {
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
