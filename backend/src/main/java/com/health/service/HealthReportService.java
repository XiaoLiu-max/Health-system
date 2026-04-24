package com.health.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.health.entity.HealthReport;

public interface HealthReportService extends IService<HealthReport> {

    // 自动生成周报
    void createWeekReport();

    // 自动生成月报
    void createMonthReport();

    // ========== 新增：查询用户最新周报 ==========
    HealthReport getLatestWeekReport(Long userId);

    // ========== 新增：查询用户最新月报 ==========
    HealthReport getLatestMonthReport(Long userId);

}