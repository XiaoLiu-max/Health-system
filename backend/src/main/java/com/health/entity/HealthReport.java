package com.health.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 健康报告（周报、月报）
 * 每周一、每月1号自动生成
 */
@Data
@TableName("health_report")
public class HealthReport {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    // 1=周报 2=月报
    private Integer reportType;

    // 统计周期开始
    private LocalDate startDate;

    // 统计周期结束
    private LocalDate endDate;

    // 智能分析文案
    private String analysisText;

    // 图表JSON数据
    private String chartData;

    private LocalDateTime createTime;
}