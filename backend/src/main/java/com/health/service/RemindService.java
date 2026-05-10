package com.health.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.health.entity.Remind;
import com.health.mapper.RemindMapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RemindService {

    @Resource
    private RemindMapper remindMapper;

    @Resource
    private MessageServices messageServices;

    public boolean addRemind(Remind remind) {
        remind.setStatus(0);
        return remindMapper.insert(remind) > 0;
    }

    public List<Remind> findAll() {
        return remindMapper.selectList(null);
    }

    public Remind getById(Long id) {
        return remindMapper.selectById(id);
    }

    public boolean updateRemind(Remind remind) {
        return remindMapper.updateById(remind) > 0;
    }

    public boolean deleteById(Long id) {
        return remindMapper.deleteById(id) > 0;
    }

    public List<Remind> getMyReminds(Long userId, Integer status) {
        LambdaQueryWrapper<Remind> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Remind::getUserId, userId);
        if (status != null) {
            wrapper.eq(Remind::getStatus, status);
        }
        wrapper.orderByAsc(Remind::getRemindTime);
        return remindMapper.selectList(wrapper);
    }

    public boolean closeRemind(Long id) {
        Remind remind = remindMapper.selectById(id);
        if (remind == null) return false;
        remind.setStatus(2);
        return remindMapper.updateById(remind) > 0;
    }

    // 重新打开提醒（把状态从 2 改回 0）
    public boolean openRemind(Long id) {
        Remind remind = remindMapper.selectById(id);
        if (remind == null) return false;
        // 只有已关闭状态才能重新打开
        if (remind.getStatus() != 2) return false;
        remind.setStatus(0);
        return remindMapper.updateById(remind) > 0;
    }

    // 系统自动触发提醒（支持重复）
    public List<Remind> getNeedTriggerRemind() {
        LambdaQueryWrapper<Remind> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Remind::getStatus, 0);
        wrapper.le(Remind::getRemindTime, LocalDateTime.now());

        List<Remind> remindList = remindMapper.selectList(wrapper);

        for (Remind remind : remindList) {
            System.out.println("🔔 自动提醒触发！用户ID：" + remind.getUserId());
            messageServices.sendRemindMessage(remind.getUserId(), remind.getContent());

            if (remind.getRepeatType() == null || remind.getRepeatType() == 0) {
                remind.setStatus(1);
                remindMapper.updateById(remind);
            } else {
                LocalDateTime nextTime = calculateNextRemindTime(remind.getRemindTime(), remind.getRepeatType());
                remind.setRemindTime(nextTime);
                remindMapper.updateById(remind);
            }
        }
        return remindList;
    }

    private LocalDateTime calculateNextRemindTime(LocalDateTime currentTime, Integer repeatType) {
        if (repeatType == 1) {
            return currentTime.plusDays(1);
        } else if (repeatType == 2) {
            return currentTime.plusWeeks(1);
        } else if (repeatType == 3) {
            return currentTime.plusMonths(1);
        } else if (repeatType == 4) {
            return currentTime.plusYears(1);
        } else {
            return currentTime;
        }
    }
}