package com.leansofx.qaservicequestion.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leansofx.qaservicequestion.model.Question;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface QuestionMapper extends BaseMapper<Question> {
}
