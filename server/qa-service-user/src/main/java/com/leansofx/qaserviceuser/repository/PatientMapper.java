package com.leansofx.qaserviceuser.repository;

import com.leansofx.qaserviceuser.domain.Patient;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PatientMapper {

    @Select("SELECT id, name, birthday, phone, gender "
            + "FROM patients WHERE name = #{name} AND birthday = #{birthday}")
    Patient findByUnique(@Param("name") String name, @Param("birthday") String birthday);

    @Insert("INSERT INTO patients (id, name, birthday, phone, gender) "
            + "VALUES (#{id}, #{name}, #{birthday}, #{phone}, #{gender})")
    void insert(Patient patient);
}
