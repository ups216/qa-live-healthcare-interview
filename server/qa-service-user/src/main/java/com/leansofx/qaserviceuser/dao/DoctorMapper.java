package com.leansofx.qaserviceuser.dao;

import com.leansofx.qaserviceuser.entity.Doctor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 医生数据访问层
 */
@Mapper
public interface DoctorMapper {

    /**
     * 查询所有医生（支持按在线状态过滤）
     *
     * @param isActive null = 全部；true = 在线；false = 离线
     */
    List<Doctor> findAll(@Param("isActive") Boolean isActive);

    /**
     * 按主键查询
     */
    Doctor findById(@Param("id") String id);

    /**
     * 按 username 查询（用于进入诊室跳转）
     */
    Doctor findByUsername(@Param("username") String username);
}
