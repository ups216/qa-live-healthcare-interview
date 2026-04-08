package com.leansofx.qaserviceuser.dto;

/**
 * 患者数据传输对象（API 响应使用，不含密码）
 */
public class PatientDTO {

    private String id;
    private String username;
    private String name;
    private String gender;
    private String birthday;
    private String phone;
    private boolean active;

    public PatientDTO() {
    }

    public PatientDTO(String id, String username, String name, String gender,
                      String birthday, String phone, boolean active) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.gender = gender;
        this.birthday = birthday;
        this.phone = phone;
        this.active = active;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getBirthday() { return birthday; }
    public void setBirthday(String birthday) { this.birthday = birthday; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
