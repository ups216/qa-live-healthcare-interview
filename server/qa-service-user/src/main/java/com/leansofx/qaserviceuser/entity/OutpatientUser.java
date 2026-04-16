package com.leansofx.qaserviceuser.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OutpatientUser {
    private Long id;
    private String username;
    private String password;
    private String name;
    private String phone;
    private String gender;
    private String birthday;
    private Integer status;
    private LocalDateTime lockUntil;
    private Integer errorCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
