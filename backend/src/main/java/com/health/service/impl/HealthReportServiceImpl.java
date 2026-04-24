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

    // ========== 修复：注入 UserService（解决 userService 报错） ==========
    @Resource
    private UserService userService;

    @Resource
    private HealthReportMapper healthReportMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ===================== 生成周报（上周一 ~ 周日） =====================
    @Override
    public void createWeekReport() {
        LocalDate now = LocalDate.now();
        LocalDate start = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).minusDays(7);
        LocalDate end = start.plusDays(6);

        List<HealthData> list = dataService.lambdaQuery()
                .ge(HealthData::getRecordDate, start)
                .le(HealthData::getRecordDate, end)
                .orderByAsc(HealthData::getRecordDate)
                .list();

        saveReport(1, start, end, list);
    }

    // ===================== 生成月报（上月整月） =====================
    @Override
    public void createMonthReport() {
        LocalDate now = LocalDate.now();
        LocalDate start = now.minusMonths(1).withDayOfMonth(1);
        LocalDate end = start.plusMonths(1).minusDays(1);

        List<HealthData> list = dataService.lambdaQuery()
                .ge(HealthData::getRecordDate, start)
                .le(HealthData::getRecordDate, end)
                .orderByAsc(HealthData::getRecordDate)
                .list();

        saveReport(2, start, end, list);
    }

    // ===================== 保存报告（含智能分析 + 图表数据） =====================
    private void saveReport(Integer type, LocalDate start, LocalDate end, List<HealthData> list) {
        HealthReport report = new HealthReport();
        report.setReportType(type);
        report.setStartDate(start);
        report.setEndDate(end);
        report.setAnalysisText(getAnalysis(list, type));

        try {
            Map<String, Object> chart = new HashMap<>();
            chart.put("start", start);
            chart.put("end", end);
            chart.put("list", list);
            report.setChartData(objectMapper.writeValueAsString(chart));
        } catch (Exception e) {
            e.printStackTrace();
        }

        save(report);
    }

    // ===================== 智能健康分析文案 =====================
    // ===================== 最终科学版智能分析（按周期比例 + 最低天数） =====================
    private String getAnalysis(List<HealthData> list, int type) {
        String period = type == 1 ? "本周" : "本月";
        int totalDays = list.size(); // 周期内总天数

        if (totalDays == 0) {
            return period + "暂无健康记录，建议每日坚持录入，便于跟踪健康状态。";
        }

        // 统计各项异常次数
        int sleepWarnDays = 0;   // 睡眠不足天数
        int bpWarnDays = 0;      // 血压偏高天数
        int sugarWarnDays = 0;   // 血糖偏高天数
        int bmiWarnDays = 0;     // BMI异常天数

        for (HealthData data : list) {
            // 1. 睡眠时间不足判断（分年龄阈值，复用你之前的工具类逻辑）
            if (data.getSleepHour() != null) {
                double sleepThreshold = getSleepThreshold(data.getUserId()); // 需从user表获取年龄
                if (data.getSleepHour().doubleValue() < sleepThreshold) {
                    sleepWarnDays++;
                }
            }

            // 2. 血压偏高判断（分年龄阈值）
            if (data.getSbp() != null && data.getDbp() != null) {
                int bpHighThreshold = getBpHighThreshold(data.getUserId()); // 需从user表获取年龄
                if (data.getSbp() >= bpHighThreshold || data.getDbp() >= (bpHighThreshold - 5)) {
                    bpWarnDays++;
                }
            }

            // 3. 血糖偏高判断（通用）
            if (data.getBloodSugar() != null && data.getBloodSugar().compareTo(new BigDecimal("6.1")) >= 0) {
                sugarWarnDays++;
            }

            // 4. BMI异常判断（通用）
            if (data.getBmi() != null) {
                if (data.getBmi().compareTo(new BigDecimal("18.5")) < 0 ||
                        data.getBmi().compareTo(new BigDecimal("24.0")) >= 0) {
                    bmiWarnDays++;
                }
            }
        }

        // 计算周期比例
        double sleepRatio = (double) sleepWarnDays / totalDays;
        double bpRatio = (double) bpWarnDays / totalDays;
        double sugarRatio = (double) sugarWarnDays / totalDays;

        // 最终异常判断（按周期 + 比例 + 最低天数）
        boolean sleepAbnormal = false;
        boolean bpAbnormal = false;
        boolean sugarAbnormal = false;
        boolean weightAbnormal = bmiWarnDays > 0; // BMI 只要有1天异常即算

        if (type == 1) { // 周报（7天）
            sleepAbnormal = sleepWarnDays >= Math.max(2, Math.ceil(totalDays * 0.3));
            bpAbnormal = bpWarnDays >= Math.max(2, Math.ceil(totalDays * 0.3));
            sugarAbnormal = sugarWarnDays >= Math.max(2, Math.ceil(totalDays * 0.2));
        } else { // 月报（30天）
            sleepAbnormal = sleepWarnDays >= Math.max(8, Math.ceil(totalDays * 0.4));
            bpAbnormal = bpWarnDays >= Math.max(8, Math.ceil(totalDays * 0.4));
            sugarAbnormal = sugarWarnDays >= Math.max(8, Math.ceil(totalDays * 0.3));
        }

        // 统计最终异常项数
        int abnormalCount = 0;
        if (sleepAbnormal) abnormalCount++;
        if (bpAbnormal) abnormalCount++;
        if (sugarAbnormal) abnormalCount++;
        if (weightAbnormal) abnormalCount++;

        // 生成最终智能文案
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

    // ===================== 辅助方法：根据用户年龄获取睡眠阈值（复用你之前的逻辑） =====================
    private double getSleepThreshold(Long userId) {
        // 实际项目中，需从UserService获取用户年龄，这里模拟
        User user = userService.getById(userId);
        Integer age = user.getAge();
        if (age < 18) return 8.5;
        else if (age <= 25) return 7.0;
        else if (age <= 64) return 6.5;
        else return 6.0;
    }

    // ===================== 辅助方法：根据用户年龄获取血压高压阈值（复用你之前的逻辑） =====================
    private int getBpHighThreshold(Long userId) {
        // 实际项目中，需从UserService获取用户年龄，这里模拟
        User user = userService.getById(userId);
        Integer age = user.getAge();
        if (age < 18) return 125;
        else if (age <= 34) return 130;
        else if (age <= 59) return 135;
        else return 145;
    }

    // ========== 获取最新周报 ==========
//    @Override
//    public HealthReport getLatestWeekReport(Long userId) {
//        return this.lambdaQuery()
//                .eq(HealthReport::getUserId, userId)
//                .eq(HealthReport::getReportType, 1)
//                .orderByDesc(HealthReport::getEndDate)
//                .last("LIMIT 1")
//                .one();
//    }
//
//    // ========== 获取最新月报 ==========
//    @Override
//    public HealthReport getLatestMonthReport(Long userId) {
//        return this.lambdaQuery()
//                .eq(HealthReport::getUserId, userId)
//                .eq(HealthReport::getReportType, 2)
//                .orderByDesc(HealthReport::getEndDate)
//                .last("LIMIT 1")
//                .one();
//    }

    @Override
    public HealthReport getLatestWeekReport(Long userId) {
        HealthReport report = healthReportMapper.selectLatestByUserIdAndType(userId, 1);

        // ✅ 没有数据就返回空对象，绝不返回 null！
        return report == null ? new HealthReport() : report;
    }

    // ========== 最新月报：type=2 ==========
    @Override
    public HealthReport getLatestMonthReport(Long userId) {
        HealthReport report = healthReportMapper.selectLatestByUserIdAndType(userId, 2);

        // ✅ 没有数据就返回空对象，绝不返回 null！
        return report == null ? new HealthReport() : report;
    }

}