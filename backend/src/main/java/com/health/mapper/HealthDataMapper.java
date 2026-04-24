//package com.health.mapper;
//
//import com.baomidou.mybatisplus.core.mapper.BaseMapper;
//import com.health.entity.HealthData;
//import org.apache.ibatis.annotations.Mapper;
//
//@Mapper
//public interface HealthDataMapper extends BaseMapper<HealthData> {
//}

package com.health.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.health.entity.HealthData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface HealthDataMapper extends BaseMapper<HealthData> {

    // 根据用户ID+日期查询单条数据（对应原来的昨日/当日数据查询）
    HealthData selectByUserIdAndDate(@Param("userId") Long userId, @Param("recordDate") LocalDate recordDate);

    // 查询用户全部历史数据
    List<HealthData> selectAllByUserId(@Param("userId") Long userId);

    // 时间区间折线图数据查询
    List<HealthData> selectByDateRange(
            @Param("userId") Long userId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );
}