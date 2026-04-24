package com.health.task;

import com.health.service.RemindService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

@Component
public class RemindTask {

    @Resource
    private RemindService remindService;

    // 每10秒自动检查一次所有到期提醒
    @Scheduled(fixedRate = 10000)
    public void autoCheckRemind() {
        remindService.getNeedTriggerRemind();
    }
}