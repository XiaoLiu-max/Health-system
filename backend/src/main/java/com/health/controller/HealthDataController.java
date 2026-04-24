package com.health.controller;

import com.health.common.Result;
import com.health.entity.HealthData;
import com.health.entity.HealthReport;
import com.health.service.HealthDataService;
import com.health.service.HealthReportService;
import com.health.utils.JwtUtil;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;

/**
 * 健康数据管理接口
 * 严格遵循三层架构：Controller只调用Service，不直接操作数据库
 */
@RestController
@RequestMapping("/health")
public class HealthDataController {

    // ========== 注入Service，补全@Resource导入 ==========
    @Resource
    private HealthDataService healthDataService;

    @Resource
    private HealthReportService healthReportService;

    // ===================== 1. 获取昨日数据（一键复用） =====================
    @GetMapping("/yesterday")
    public Result getYesterday(HttpServletRequest request) {
        Long userId = JwtUtil.getUserId(request.getHeader("token"));
        return Result.success(healthDataService.getYesterdayData(userId));
    }

    // ===================== 2. 保存当天健康数据 =====================
    @PostMapping("/save")
    public Result save(HttpServletRequest request, @RequestBody HealthData data) {
        Long userId = JwtUtil.getUserId(request.getHeader("token"));
        healthDataService.saveDayData(data, userId, LocalDate.now());
        return Result.success("健康数据保存成功");
    }

    // ===================== 3. 查询我的所有历史数据 =====================
    @GetMapping("/list")
    public Result list(HttpServletRequest request) {
        Long userId = JwtUtil.getUserId(request.getHeader("token"));
        // 【修正】查询逻辑全部移入Service，Controller不再写lambdaQuery
        List<HealthData> dataList = healthDataService.listUserHealthData(userId);
        return Result.success(dataList);
    }

    // ===================== 4. 删除单条数据 =====================
    @PostMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        healthDataService.removeById(id);
        return Result.success("数据删除成功");
    }

    // ===================== 5. 前端折线图数据接口 =====================
//    @GetMapping("/chart")
//    public Result getChartData(HttpServletRequest request,
//                               @RequestParam LocalDate start,
//                               @RequestParam LocalDate end) {
//        Long userId = JwtUtil.getUserId(request.getHeader("token"));
//        // 【修正】查询逻辑移入Service
//        List<HealthData> chartData = healthDataService.getChartDataByDate(userId, start, end);
//        return Result.success(chartData);
//    }

    @GetMapping("/chart")
    public Result getChartData(HttpServletRequest request,
                               @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
                               @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        Long userId = JwtUtil.getUserId(request.getHeader("token"));
        List<HealthData> chartData = healthDataService.getChartDataByDate(userId, start, end);
        return Result.success(chartData);
    }

    // ===================== 6. 查询最新周报 =====================
    @GetMapping("/report/week")
    public Result getWeekReport(HttpServletRequest request) {
        Long userId = JwtUtil.getUserId(request.getHeader("token"));
        HealthReport weekReport = healthReportService.getLatestWeekReport(userId);
        return Result.success(weekReport);
    }

    // ===================== 7. 查询最新月报 =====================
    @GetMapping("/report/month")
    public Result getMonthReport(HttpServletRequest request) {
        Long userId = JwtUtil.getUserId(request.getHeader("token"));
        HealthReport monthReport = healthReportService.getLatestMonthReport(userId);
        return Result.success(monthReport);
    }
}