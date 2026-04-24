package com.health.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.health.entity.HealthData;
import java.time.LocalDate;
import java.util.List;

public interface HealthDataService extends IService<HealthData> {

    // 获取昨日数据（用于一键复用）
    HealthData getYesterdayData(Long userId);

    // 保存/更新当天数据（自动算BMI）
    void saveDayData(HealthData data, Long userId, LocalDate recordDate);

    // ========== 新增：查询用户全部历史数据（Controller调用） ==========
    List<HealthData> listUserHealthData(Long userId);

    // ========== 新增：根据时间区间查询折线图数据 ==========
    List<HealthData> getChartDataByDate(Long userId, LocalDate start, LocalDate end);

}