package com.leansofx.qaserviceuser.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leansofx.qaserviceuser.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}