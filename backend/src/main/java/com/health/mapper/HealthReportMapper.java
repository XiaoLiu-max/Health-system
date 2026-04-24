//package com.health.mapper;
//
//import com.baomidou.mybatisplus.core.mapper.BaseMapper;
//import com.health.entity.HealthReport;
//import org.apache.ibatis.annotations.Mapper;
//
//@Mapper
//public interface HealthReportMapper extends BaseMapper<HealthReport> {
//}

package com.health.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.health.entity.HealthReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface HealthReportMapper extends BaseMapper<HealthReport> {

    // ========== 对应周报查询：用户ID+报表类型，取最新一条 ==========
    HealthReport selectLatestByUserIdAndType(
            @Param("userId") Long userId,
            @Param("reportType") Integer reportType
    );
}