package com.health.service;

import com.health.entity.HealthData;
import com.health.entity.Message;
import com.health.entity.Remind;
import com.health.entity.User;
import com.health.mapper.HealthDataMapper;
import com.health.mapper.MessageMapper;
import com.health.mapper.RemindMapper;
import com.health.mapper.UserMapper;
import com.health.utils.HealthCheckUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class HealthWarningTask {

    @Autowired
    private HealthDataMapper healthDataMapper;

    @Autowired
    private RemindMapper remindMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private UserMapper userMapper; // 用来查年龄

    // 每天 20:00 执行
//    @Scheduled(cron = "0 0 20 * * ?")
    @Scheduled(fixedRate = 3000)
    public void autoCheckHealthWarning() {
        LocalDate today = LocalDate.now();
        List<HealthData> dataList = healthDataMapper.selectTodayData(today);

        for (HealthData data : dataList) {
            Long userId = data.getUserId();
            User user = userMapper.selectById(userId);
            Integer age = user.getAge();

            // 调用工具类判断是否异常！！！
            String abnormalMsg = HealthCheckUtil.checkAbnormal(data, age);

            if (!abnormalMsg.isEmpty()) {
                boolean alreadySent = remindMapper.existsTodayAbnormalRemind(userId, today);
                if (!alreadySent) {
                    saveRemind(userId, abnormalMsg);
                    saveMessage(userId, abnormalMsg);
                }
            }
        }
    }

    // 存入提醒
    private void saveRemind(Long userId, String msg) {
        Remind remind = new Remind();
        remind.setUserId(userId);
        remind.setContent("【健康异常】" + msg);
        remind.setRemindTime(LocalDateTime.now());
        remind.setRepeatType(0);
        remind.setStatus(1);
        remindMapper.insert(remind);
    }

    // 存入消息
    private void saveMessage(Long userId, String msg) {
        Message message = new Message();
        message.setFromUid(0L); // 系统发送
        message.setToUid(userId);
        message.setContent("您今日健康数据异常：" + msg);
        message.setIsRead(0);
        message.setCreateTime(LocalDateTime.now());
        messageMapper.insert(message);
    }
}