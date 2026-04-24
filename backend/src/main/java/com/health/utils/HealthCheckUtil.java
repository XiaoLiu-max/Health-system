package com.health.utils;

import com.health.entity.HealthData;
import java.math.BigDecimal;

/**
 * 健康指标异常检测（全年龄段，含<18岁青少年）
 * 适配：user.age 非空
 */
public class HealthCheckUtil {

    /**
     * 检测所有健康指标是否异常
     *
     * @param data 当日健康数据
     * @param age  用户年龄（非空）
     * @return 异常信息字符串，无异常则返回空串
     */
    public static String checkAbnormal(HealthData data, Integer age) {
        StringBuilder sb = new StringBuilder();

        // ===================== BMI 通用判断 =====================
        BigDecimal bmi = data.getBmi();
        if (bmi != null) {
            if (bmi.compareTo(new BigDecimal("18.5")) < 0) {
                sb.append("体重偏瘦；");
            } else if (bmi.compareTo(new BigDecimal("24.0")) >= 0 && bmi.compareTo(new BigDecimal("28.0")) < 0) {
                sb.append("体重超重；");
            } else if (bmi.compareTo(new BigDecimal("28.0")) >= 0) {
                sb.append("肥胖；");
            }
        }

        // ===================== 血压（分年龄） =====================
        Integer sbp = data.getSbp();
        Integer dbp = data.getDbp();
        if (sbp != null && dbp != null) {
            int highWarn, lowWarn;

            if (age < 18) {
                highWarn = 125;
                lowWarn = 80;
            } else if (age <= 34) {
                highWarn = 130;
                lowWarn = 85;
            } else if (age <= 59) {
                highWarn = 135;
                lowWarn = 90;
            } else {
                highWarn = 145;
                lowWarn = 95;
            }

            if (sbp >= highWarn || dbp >= lowWarn) {
                sb.append("血压偏高；");
            }
        }

        // ===================== 体温（通用） =====================
        BigDecimal temp = data.getBodyTemp();
        if (temp != null) {
            if (temp.compareTo(new BigDecimal("37.2")) > 0) {
                sb.append("体温偏高（发热）；");
            } else if (temp.compareTo(new BigDecimal("36.0")) < 0) {
                sb.append("体温偏低；");
            }
        }

        // ===================== 空腹血糖（通用） =====================
        BigDecimal sugar = data.getBloodSugar();
        if (sugar != null && sugar.compareTo(new BigDecimal("6.1")) >= 0) {
            sb.append("血糖偏高；");
        }

        // ===================== 睡眠（分年龄） =====================
        BigDecimal sleep = data.getSleepHour();
        if (sleep != null) {
            double warn;

            if (age < 18) {
                warn = 8.5;
            } else if (age <= 25) {
                warn = 7.0;
            } else if (age <= 64) {
                warn = 6.5;
            } else {
                warn = 6.0;
            }

            if (sleep.doubleValue() < warn) {
                sb.append("睡眠时间不足；");
            }
        }

        return sb.toString();
    }
}