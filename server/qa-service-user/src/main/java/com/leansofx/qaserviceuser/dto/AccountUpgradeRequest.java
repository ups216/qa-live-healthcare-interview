package com.leansofx.qaserviceuser.dto;

public class AccountUpgradeRequest {

    private String phone;
    private String code;
    private String password;
    private String historyName;
    private String historyBirthday;

    public AccountUpgradeRequest() {
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHistoryName() {
        return historyName;
    }

    public void setHistoryName(String historyName) {
        this.historyName = historyName;
    }

    public String getHistoryBirthday() {
        return historyBirthday;
    }

    public void setHistoryBirthday(String historyBirthday) {
        this.historyBirthday = historyBirthday;
    }
}
