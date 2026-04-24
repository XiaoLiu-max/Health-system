//package com.health.service.impl;
//
//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.health.entity.HealthData;
//import com.health.entity.User;
//import com.health.mapper.HealthDataMapper;
//import com.health.service.HealthDataService;
//import com.health.service.MessageService;
//import com.health.service.UserService;
//import com.health.utils.HealthCheckUtil;
//import org.springframework.stereotype.Service;
//import javax.annotation.Resource;
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.List;
//
//@Service
//public class HealthDataServiceImpl extends ServiceImpl<HealthDataMapper, HealthData>
//        implements HealthDataService {
//
//    @Resource
//    private UserService userService;
//
//    // ========== 替换掉原来的Feign，换成单体消息服务 ==========
//    @Resource
//    private MessageService messageService;
//
//    // 获取昨日数据（一键复用接口逻辑完全不变）
//    @Override
//    public HealthData getYesterdayData(Long userId) {
//        LocalDate yesterday = LocalDate.now().minusDays(1);
//        return lambdaQuery()
//                .eq(HealthData::getUserId, userId)
//                .eq(HealthData::getRecordDate, yesterday)
//                .one();
//    }
//
//    // 保存当天数据全套逻辑：BMI自动计算 + 全年龄段异常检测 + 消息提醒
//    @Override
//    public void saveDayData(HealthData data, Long userId, LocalDate recordDate) {
//        data.setUserId(userId);
//        data.setRecordDate(recordDate);
//
//        // 1. 自动计算BMI（原有逻辑不变）
//        if (data.getHeight() != null && data.getWeight() != null) {
//            BigDecimal h2 = data.getHeight().multiply(data.getHeight());
//            BigDecimal bmi = data.getWeight().divide(h2, 1, BigDecimal.ROUND_HALF_UP);
//            data.setBmi(bmi);
//        }
//
//        // 2. 每日唯一存储：有则更新、无则新增（原有逻辑不变）
//        HealthData old = lambdaQuery()
//                .eq(HealthData::getUserId, userId)
//                .eq(HealthData::getRecordDate, recordDate)
//                .one();
//        if (old == null) {
//            save(data);
//        } else {
//            data.setId(old.getId());
//            updateById(data);
//        }
//
//        // 3. 全年龄段异常检测 + 单体版消息提醒（适配你user.age非空）
//        User user = userService.getById(userId);
//        String abnormalMsg = HealthCheckUtil.checkAbnormal(data, user.getAge());
//
//        // 检测到异常，调用消息服务发送提醒
//        if (!abnormalMsg.isEmpty()) {
//            String advice = "请保持规律作息、合理饮食、适度运动，定期监测自身健康指标。";
//            messageService.sendHealthWarn(userId, abnormalMsg, advice);
//        }
//    }
//
//    // ====================== 新增：查询用户所有历史健康数据 ======================
//    @Override
//    public List<HealthData> listUserHealthData(Long userId) {
//        return lambdaQuery()
//                .eq(HealthData::getUserId, userId)
//                .orderByDesc(HealthData::getRecordDate)
//                .list();
//    }
//
//    // ====================== 新增：折线图时间区间数据查询 ======================
//    @Override
//    public List<HealthData> getChartDataByDate(Long userId, LocalDate start, LocalDate end) {
//        return lambdaQuery()
//                .eq(HealthData::getUserId, userId)
//                .ge(HealthData::getRecordDate, start)
//                .le(HealthData::getRecordDate, end)
//                .orderByAsc(HealthData::getRecordDate)
//                .list();
//    }
//
//}

package com.health.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.health.entity.HealthData;
import com.health.entity.User;
import com.health.mapper.HealthDataMapper;
import com.health.service.HealthDataService;
import com.health.service.MessageService;
import com.health.service.UserService;
import com.health.utils.HealthCheckUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class HealthDataServiceImpl extends ServiceImpl<HealthDataMapper, HealthData>
        implements HealthDataService {

    @Resource
    private UserService userService;

    @Resource
    private MessageService messageService;

    @Resource
    private HealthDataMapper healthDataMapper;

    // ========== 1. 获取昨日数据 ==========
    @Override
    public HealthData getYesterdayData(Long userId) {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        HealthData data = healthDataMapper.selectByUserIdAndDate(userId, yesterday);

        // 关键：查不到数据返回空对象，不会返回null，彻底杜绝500
        return data == null ? new HealthData() : data;
    }

    // ========== 2. 保存当日健康数据 ==========
    @Override
    public void saveDayData(HealthData data, Long userId, LocalDate recordDate) {
        data.setUserId(userId);
        data.setRecordDate(recordDate);

        // 自动计算BMI，原有逻辑完全保留
        if (data.getHeight() != null && data.getWeight() != null) {
            BigDecimal h2 = data.getHeight().multiply(data.getHeight());
            BigDecimal bmi = data.getWeight().divide(h2, 1, BigDecimal.ROUND_HALF_UP);
            data.setBmi(bmi);
        }

        // 有则更新、无则新增，替换原来lambda查询
        HealthData old = healthDataMapper.selectByUserIdAndDate(userId, recordDate);
        if (old == null) {
            // 无数据，新增
            healthDataMapper.insert(data);
        } else {
            // 已有数据，更新
            data.setId(old.getId());
            healthDataMapper.updateById(data);
        }

        // 原有异常检测+消息提醒逻辑完全不动
        User user = userService.getById(userId);
        String abnormalMsg = HealthCheckUtil.checkAbnormal(data, user.getAge());
        if (!abnormalMsg.isEmpty()) {
            String advice = "请保持规律作息、合理饮食、适度运动，定期监测自身健康指标。";
            messageService.sendHealthWarn(userId, abnormalMsg, advice);
        }
    }

    // ========== 3. 查询用户全部历史数据 ==========
    @Override
    public List<HealthData> listUserHealthData(Long userId) {
        return healthDataMapper.selectAllByUserId(userId);
    }

    // ========== 4. 折线图时间区间查询 ==========
    @Override
    public List<HealthData> getChartDataByDate(Long userId, LocalDate start, LocalDate end) {
        return healthDataMapper.selectByDateRange(userId, start, end);
    }
}