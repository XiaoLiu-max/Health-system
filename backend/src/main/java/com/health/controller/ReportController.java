package com.health.controller;

import com.health.entity.HealthReport;
import com.health.service.HealthReportService;
import com.health.utils.JwtUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/health/report")
public class ReportController {

    @Resource
    private HealthReportService healthReportService;

    // 🔥 前端请求：/health/report/friend?userId=xxx&type=1&date=xxx
    @GetMapping("/friend")
    public Map<String, Object> friendReport(
            @RequestParam Long userId,
            @RequestParam Integer type,
            @RequestParam String date,
            HttpServletRequest request
    ) {
        Map<String, Object> res = new HashMap<>();

        // 登录校验
        JwtUtil.getUserId(request.getHeader("token"));

        LocalDate localDate = LocalDate.parse(date);
        HealthReport report = healthReportService.getReportByDate(userId, type, localDate);

        res.put("code", 200);
        res.put("data", report);
        return res;
    }
}