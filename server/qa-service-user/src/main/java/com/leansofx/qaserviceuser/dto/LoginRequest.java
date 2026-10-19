package com.leansofx.qaserviceuser.dto;

public class LoginRequest {

    /** 登录类型：doctor（默认） / patient */
    private String type;

    // 医生登录凭证
    private String username;
    private String password;

    // 患者登录凭证（姓名 + 出生日期）
    private String name;
    private String birthday;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
}
