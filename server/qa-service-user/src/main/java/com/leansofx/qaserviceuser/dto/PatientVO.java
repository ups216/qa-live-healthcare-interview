package com.leansofx.qaserviceuser.dto;

/**
 * 患者脱敏响应模型。
 *
 * <p><b>安全约束</b>：刻意不包含 password 字段。
 * 所有对外暴露患者信息的接口都必须返回本类，这是防止密码泄漏的结构性保障。
 */
public class PatientVO {

    private String id;
    private String username;
    private String name;
    private String birthday;
    private String phone;
    private String gender;

    public PatientVO() {
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
