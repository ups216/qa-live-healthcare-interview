package com.leansofx.qaserviceuser.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * 用户注册请求体
 */
public class RegisterRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度需在3-20个字符之间")
    @Pattern(regexp = "^[a-zA-Z_][a-zA-Z0-9_]*$", message = "用户名只能包含字母、数字、下划线，且必须以字母或下划线开头")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度需在6-20个字符之间")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).+$", message = "密码必须同时包含至少一个字母和一个数字")
    private String password;

    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

    @NotBlank(message = "姓名不能为空")
    @Size(min = 2, max = 10, message = "姓名长度需在2-10个字符之间")
    private String name;

    @NotBlank(message = "性别不能为空")
    private String gender;

    @NotNull(message = "生日不能为空")
    @Past(message = "生日不能晚于当前日期")
    private LocalDate birthday;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "请输入正确的手机号格式")
    private String phone;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public LocalDate getBirthday() { return birthday; }
    public void setBirthday(LocalDate birthday) { this.birthday = birthday; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
