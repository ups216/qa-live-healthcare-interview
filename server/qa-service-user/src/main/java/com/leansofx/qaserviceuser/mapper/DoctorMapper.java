package com.leansofx.qaserviceuser.mapper;

import com.leansofx.qaserviceuser.entity.Doctor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 医生数据访问接口，SQL 见 resources/mapper/DoctorMapper.xml。
 *
 * <p>所有参数通过 {@code #{}} 预编译传入，禁止使用 {@code ${}} 拼接用户输入，防止 SQL 注入。
 */
@Mapper
public interface DoctorMapper {

    /** 查询全部医生 */
    List<Doctor> selectAll();

    /** 查询在线医生（is_active = 1） */
    List<Doctor> selectActive();

    /** 按主键查询 */
    Doctor selectById(@Param("id") String id);

    /** 按登录名查询（前端路由 /consultation/:doctorUsername 使用） */
    Doctor selectByUsername(@Param("username") String username);
}
