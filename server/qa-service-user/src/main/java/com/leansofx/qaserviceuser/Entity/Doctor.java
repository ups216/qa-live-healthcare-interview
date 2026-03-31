package com.leansofx.qaserviceuser.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("doctor")
public class Doctor {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String title;

    private String hospital;

    private String department;

    private String avatar;

    private String intro;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
