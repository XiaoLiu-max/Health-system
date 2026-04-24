package com.health.task;

import com.health.service.HealthReportService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class HealthReportTask {

    private final HealthReportService reportService;

    public HealthReportTask(HealthReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * 每周一 0点 生成上周周报
     */
    @Scheduled(cron = "0 0 0 ? * MON")
    public void weekReport() {
        reportService.createWeekReport();
        System.out.println("✅ 周报已生成");
    }

    /**
     * 每月1号 0点 生成上月月报
     */
    @Scheduled(cron = "0 0 0 1 * ?")
    public void monthReport() {
        reportService.createMonthReport();
        System.out.println("✅ 月报已生成");
    }
}