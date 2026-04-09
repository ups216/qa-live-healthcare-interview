package com.leansofx.qaserviceuser.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 医生实体，对应 doctors 表
 */
@Data
public class Doctor {

    /** 医生唯一标识，如 doc001 */
    private String id;

    /** 登录用户名 / URL slug */
    private String username;

    /** 密码（接口响应时不返回） */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    /** 医生姓名 */
    private String name;

    /** 职称 */
    private String title;

    /** 科室 */
    private String department;

    /** 头像 URL */
    private String avatar;

    /** 从业经验描述 */
    private String experience;

    /**
     * 专长列表。
     * MySQL JSON 列由 MyBatis 的 ListTypeHandler 处理，映射为 Java List<String>。
     */
    private List<String> specialties;

    /** 是否在线 */
    @JsonProperty("isActive")
    private Boolean isActive;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
