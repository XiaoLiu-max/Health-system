package com.health.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 健康数据实体
 * 每天一条记录，允许空值（未测项目为NULL）
 */
@Data
@TableName("health_data")
public class HealthData {

    // 主键自增
    @TableId(type = IdType.AUTO)
    private Long id;

    // 用户ID（关联登录用户）
    private Long userId;

    // 记录日期：yyyy-MM-dd
    private LocalDate recordDate;

    // 身高（米）
    private BigDecimal height;

    // 体重（公斤）
    private BigDecimal weight;

    // BMI（系统自动计算）
    private BigDecimal bmi;

    // 收缩压 / 高压
    private Integer sbp;

    // 舒张压 / 低压
    private Integer dbp;

    // 体温
    private BigDecimal bodyTemp;

    // 空腹血糖
    private BigDecimal bloodSugar;

    // 睡眠时间（小时）
    private BigDecimal sleepHour;

    // 创建时间
    private LocalDateTime createTime;

    // 更新时间
    private LocalDateTime updateTime;
}