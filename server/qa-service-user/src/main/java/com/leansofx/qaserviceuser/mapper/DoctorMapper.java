package com.leansofx.qaserviceuser.mapper;

import com.leansofx.qaserviceuser.entity.Doctor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface DoctorMapper {

    /**
     * 查询所有医生列表
     */
    List<Doctor> selectDoctorList();

    /**
     * 根据ID查询医生
     */
    Doctor selectDoctorById(@Param("id") String id);

    /**
     * 根据科室查询医生列表
     */
    List<Doctor> selectDoctorListByDepartment(@Param("department") String department);

    /**
     * 查询激活状态的医生列表
     */
    List<Doctor> selectActiveDoctorList();

    /**
     * 分页查询医生列表
     */
    List<Doctor> selectDoctorListByPage(@Param("offset") int offset, @Param("limit") int limit);
}
