package com.leansofx.qaserviceuser.mapper;

import com.leansofx.qaserviceuser.entity.Patient;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 患者数据访问接口，SQL 见 resources/mapper/PatientMapper.xml。
 *
 * <p>所有参数均通过 #{}
 * 预编译传入，杜绝 ${} 拼接用户输入导致的 SQL 注入。
 */
@Mapper
public interface PatientMapper {

    /** 新增患者。主键由应用层生成，故不使用自增 */
    int insert(Patient patient);

    /** 按登录名查询 */
    Patient selectByUsername(@Param("username") String username);

    /** 按主键查询 */
    Patient selectById(@Param("id") String id);
}
