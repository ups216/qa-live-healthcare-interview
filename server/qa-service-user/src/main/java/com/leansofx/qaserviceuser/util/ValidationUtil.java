package com.leansofx.qaserviceuser.util;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class ValidationUtil {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,20}$");

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    public static boolean isValidSmsCode(String code) {
        return code != null && code.matches("^\\d{6}$");
    }

    public static boolean isValidName(String name) {
        return name != null && name.length() >= 2 && name.length() <= 20;
    }

    public static boolean isValidBirthday(String birthday) {
        if (birthday == null || birthday.isEmpty()) {
            return true;
        }
        return birthday.matches("^\\d{4}-\\d{2}-\\d{2}$");
    }
}
