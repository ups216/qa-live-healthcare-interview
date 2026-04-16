package com.leansofx.qaserviceuser.mapper;

import com.leansofx.qaserviceuser.entity.OutpatientUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OutpatientUserMapper {

    /**
     * 根据用户名查询用户
     */
    OutpatientUser selectByUsername(@Param("username") String username);

    /**
     * 更新错误次数和锁定时间
     */
    int updateErrorCount(@Param("id") Long id, @Param("errorCount") Integer errorCount, @Param("lockUntil") String lockUntil);

    /**
     * 重置错误次数
     */
    int resetErrorCount(@Param("id") Long id);

    /**
     * 根据ID查询用户
     */
    OutpatientUser selectById(@Param("id") Long id);
}
