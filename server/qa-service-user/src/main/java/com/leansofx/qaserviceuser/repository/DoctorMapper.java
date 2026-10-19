package com.leansofx.qaserviceuser.repository;

import com.leansofx.qaserviceuser.domain.Doctor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface DoctorMapper {

    @Select("SELECT id, username, name, title, department, avatar, experience, specialties, is_active "
            + "FROM doctors")
    @Results(id = "doctorResult", value = {
            @Result(column = "is_active", property = "active"),
            @Result(column = "specialties", property = "specialties",
                    typeHandler = StringListTypeHandler.class)
    })
    List<Doctor> findAll();

    @Select("SELECT id, username, name, title, department, avatar, experience, specialties, is_active "
            + "FROM doctors WHERE is_active = 1")
    @ResultMap("doctorResult")
    List<Doctor> findActive();

    @Select("SELECT id, username, password, name FROM doctors WHERE username = #{username}")
    Doctor findByUsername(String username);
}
