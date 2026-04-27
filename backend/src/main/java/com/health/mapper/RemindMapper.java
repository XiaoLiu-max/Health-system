package com.health.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.health.entity.Remind;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;

@Mapper
public interface RemindMapper extends BaseMapper<Remind> {
    // 用 @Select 注解直接写 SQL，不用 XML 文件
    @Select("SELECT COUNT(*) > 0 " +
            "FROM remind " +
            "WHERE user_id = #{userId} " +
            "AND DATE(remind_time) = #{today} " +
            "AND content LIKE '【健康异常】%'")
    boolean existsTodayAbnormalRemind(
            @Param("userId") Long userId,
            @Param("today") LocalDate today
    );
}