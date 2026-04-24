package com.health.service.impl;

import com.health.service.MessageServices;
import org.springframework.stereotype.Service;

/**
 * 消息提醒实现类
 * 后续队友补全消息入库、前端推送逻辑即可，当前框架预留完毕
 */
@Service
public class MessageServiceImpl implements MessageServices {

    @Override
    public void sendHealthWarn(Long userId, String warnContent, String advice) {
        // ===================== 队友后续在这里补全代码 =====================
        // 功能：消息存入数据库、用户消息通知、前端弹窗提醒
        // 当前仅预留接口，保证你原有业务调用完全正常、不报错
        System.out.println("【健康异常提醒】用户ID："+userId);
        System.out.println("异常内容："+warnContent);
        System.out.println("健康建议："+advice);
    }
}