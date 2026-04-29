package com.health.controller;

import com.health.entity.HealthReport;
import com.health.service.HealthReportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/report")
public class ReportController {

    @Resource
    private HealthReportService healthReportService;

    // 这就是你一直打不开的：http://localhost:8081/report/2
    @GetMapping("/{userId}")
    public Map<String, Object> getReport(@PathVariable Long userId) {
        Map<String, Object> map = new HashMap<>();

        HealthReport week = healthReportService.getLatestWeekReport(userId);
        HealthReport month = healthReportService.getLatestMonthReport(userId);

        map.put("code", 200);
        map.put("weekReport", week);
        map.put("monthReport", month);
        return map;
    }
}