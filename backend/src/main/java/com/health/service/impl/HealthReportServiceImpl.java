package com.health.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.health.entity.HealthData;
import com.health.entity.HealthReport;
import com.health.entity.User;
import com.health.mapper.HealthReportMapper;
import com.health.service.HealthDataService;
import com.health.service.HealthReportService;
import com.health.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HealthReportServiceImpl extends ServiceImpl<HealthReportMapper, HealthReport>
        implements HealthReportService {

    @Resource
    private HealthDataService dataService;

    @Resource
    private UserService userService;

    @Resource
    private HealthReportMapper healthReportMapper;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public void createWeekReport() {
        LocalDate now = LocalDate.now();
        LocalDate start = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).minusDays(7);
        LocalDate end = start.plusDays(6);

        List<User> userList = userService.list();
        for (User user : userList) {
            Long userId = user.getId();

            List<HealthData> list = dataService.lambdaQuery()
                    .eq(HealthData::getUserId, userId)
                    .ge(HealthData::getRecordDate, start)
                    .le(HealthData::getRecordDate, end)
                    .orderByAsc(HealthData::getRecordDate)
                    .list();

            saveReport(userId, 1, start, end, list);
        }
    }

    @Override
    public void createMonthReport() {
        LocalDate now = LocalDate.now();
        LocalDate start = now.minusMonths(1).withDayOfMonth(1);
        LocalDate end = start.plusMonths(1).minusDays(1);

        List<User> userList = userService.list();
        for (User user : userList) {
            Long userId = user.getId();

            List<HealthData> list = dataService.lambdaQuery()
                    .eq(HealthData::getUserId, userId)
                    .ge(HealthData::getRecordDate, start)
                    .le(HealthData::getRecordDate, end)
                    .orderByAsc(HealthData::getRecordDate)
                    .list();

            saveReport(userId, 2, start, end, list);
        }
    }

    private void saveReport(Long userId, Integer type, LocalDate start, LocalDate end, List<HealthData> list) {
        LambdaQueryWrapper<HealthReport> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthReport::getUserId, userId)
                .eq(HealthReport::getReportType, type)
                .eq(HealthReport::getStartDate, start);
        HealthReport existReport = this.getOne(wrapper);

        String chartData;
        try {
            Map<String, Object> chart = new HashMap<>();
            chart.put("start", start);
            chart.put("end", end);
            chart.put("list", list);
            chartData = objectMapper.writeValueAsString(chart);
        } catch (Exception e) {
            chartData = "{}";
            e.printStackTrace();
        }

        if (existReport != null) {
            existReport.setEndDate(end);
            existReport.setAnalysisText(getAnalysis(list, type));
            existReport.setChartData(chartData);
            this.updateById(existReport);
        } else {
            HealthReport report = new HealthReport();
            report.setUserId(userId);
            report.setReportType(type);
            report.setStartDate(start);
            report.setEndDate(end);
            report.setAnalysisText(getAnalysis(list, type));
            report.setChartData(chartData);
            this.save(report);
        }
    }
    // ===================== 【唯一修改】getAnalysis 方法 =====================
    // 规则完全不变！严格程度不变！只修复BUG！
    private String getAnalysis(List<HealthData> list, int type) {
        String period = type == 1 ? "本周" : "本月";
        int totalDays = list.size();

        if (totalDays == 0) {
            return period + "暂无健康记录，建议每日坚持录入，便于跟踪健康状态。";
        }

        int sleepWarnDays = 0;
        int bpWarnDays = 0;
        int sugarWarnDays = 0;
        int bmiWarnDays = 0;

        // 1. 只查一次用户信息，避免循环内重复查询
        Long userId = list.get(0).getUserId();
        User user = userService.getById(userId);
        int age = user.getAge();

        // 2. 计算所有阈值
        double sleepThreshold = getSleepThreshold(userId);
        int sysThreshold = getBpHighThreshold(userId);
        // 舒张压阈值按医学标准修正
        int diaThreshold = (sysThreshold == 125) ? 85 : (sysThreshold == 130) ? 85 : (sysThreshold == 135) ? 90 : 90;

        // 3. 把阈值转为BigDecimal，避免比较问题
        BigDecimal sleepThresholdBD = BigDecimal.valueOf(sleepThreshold);
        BigDecimal sugarThreshold = new BigDecimal("6.1");
        BigDecimal bmiLow = new BigDecimal("18.5");
        BigDecimal bmiHigh = new BigDecimal("24.0");

        for (HealthData data : list) {
            // 睡眠（BigDecimal 安全比较）
            if (data.getSleepHour() != null) {
                if (data.getSleepHour().compareTo(sleepThresholdBD) < 0) {
                    sleepWarnDays++;
                }
            }

            // 血压（收缩压 OR 舒张压，符合医学标准）
            if (data.getSbp() != null && data.getDbp() != null) {
                boolean sysFail = data.getSbp() >= sysThreshold;
                boolean diaFail = data.getDbp() >= diaThreshold;
                if (sysFail || diaFail) {
                    bpWarnDays++;
                }
            }

            // 血糖（BigDecimal 安全比较）
            if (data.getBloodSugar() != null) {
                if (data.getBloodSugar().compareTo(sugarThreshold) >= 0) {
                    sugarWarnDays++;
                }
            }

            // BMI（原逻辑不变）
            if (data.getBmi() != null) {
                if (data.getBmi().compareTo(bmiLow) < 0 || data.getBmi().compareTo(bmiHigh) >= 0) {
                    bmiWarnDays++;
                }
            }
        }

        boolean weightAbnormal = bmiWarnDays > 0;
        boolean sleepAbnormal = false;
        boolean bpAbnormal = false;
        boolean sugarAbnormal = false;

        // 你的严格判定规则，完全不动
        if (type == 1) {
            sleepAbnormal = sleepWarnDays >= Math.max(2, (int) Math.ceil(totalDays * 0.3));
            bpAbnormal = bpWarnDays >= Math.max(2, (int) Math.ceil(totalDays * 0.3));
            sugarAbnormal = sugarWarnDays >= Math.max(2, (int) Math.ceil(totalDays * 0.2));
        } else {
            sleepAbnormal = sleepWarnDays >= Math.max(8, (int) Math.ceil(totalDays * 0.4));
            bpAbnormal = bpWarnDays >= Math.max(8, (int) Math.ceil(totalDays * 0.4));
            sugarAbnormal = sugarWarnDays >= Math.max(8, (int) Math.ceil(totalDays * 0.3));
        }

        int abnormalCount = 0;
        if (sleepAbnormal) abnormalCount++;
        if (bpAbnormal) abnormalCount++;
        if (sugarAbnormal) abnormalCount++;
        if (weightAbnormal) abnormalCount++;

        if (abnormalCount == 0) {
            return period + "健康状态优秀，各项指标稳定正常，持续保持规律作息与健康习惯！";
        } else if (abnormalCount <= 2) {
            StringBuilder sb = new StringBuilder();
            sb.append(period).append("健康状况基本平稳，以下指标需稍加关注：");
            if (sleepAbnormal) sb.append("睡眠不足；");
            if (bpAbnormal) sb.append("血压偏高；");
            if (sugarAbnormal) sb.append("血糖偏高；");
            if (weightAbnormal) sb.append("体重需控制；");
            sb.append("调整作息与饮食，很快就能恢复最佳状态。");
            return sb.toString();
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(period).append("监测到多项健康指标异常：");
            if (sleepAbnormal) sb.append("长期睡眠不足；");
            if (bpAbnormal) sb.append("血压频繁偏高；");
            if (sugarAbnormal) sb.append("血糖多次异常；");
            if (weightAbnormal) sb.append("体重状态不佳；");
            sb.append("请高度重视，制定健康计划，必要时咨询专业医生。");
            return sb.toString();
        }
    }
    // 下面的代码 100% 完全是你原版！
    private double getSleepThreshold(Long userId) {
        User user = userService.getById(userId);
        Integer age = user.getAge();
        if (age < 18) return 8.5;
        else if (age <= 25) return 7.0;
        else if (age <= 64) return 6.5;
        else return 6.0;
    }

    private int getBpHighThreshold(Long userId) {
        User user = userService.getById(userId);
        Integer age = user.getAge();
        if (age < 18) return 125;
        else if (age <= 34) return 130;
        else if (age <= 59) return 135;
        else return 145;
    }

    @Override
    public HealthReport getLatestWeekReport(Long userId) {
        HealthReport report = healthReportMapper.selectLatestByUserIdAndType(userId, 1);
        return report == null ? new HealthReport() : report;
    }

    @Override
    public HealthReport getLatestMonthReport(Long userId) {
        HealthReport report = healthReportMapper.selectLatestByUserIdAndType(userId, 2);
        return report == null ? new HealthReport() : report;
    }
}