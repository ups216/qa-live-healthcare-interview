package com.leansofx.qaserviceuser.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName(value = "doctor", autoResultMap = true)
public class Doctor {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;
    private String password;
    private String realName;
    private String title;
    private String department;
    private String avatar;
    private Integer yearsOfExperience;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> specialty;

    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}