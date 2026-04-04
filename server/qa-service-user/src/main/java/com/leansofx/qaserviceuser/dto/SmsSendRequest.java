package com.leansofx.qaserviceuser.dto;

public class SmsSendRequest {

    private String phone;
    private String type;

    public SmsSendRequest() {
    }

    public SmsSendRequest(String phone, String type) {
        this.phone = phone;
        this.type = type;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
