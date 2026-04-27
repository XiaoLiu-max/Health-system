package com.health.service.impl;

import com.health.entity.Message;
import com.health.mapper.MessageMapper;
import com.health.service.MessageService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
public class MessageServiceImpl implements MessageService {

    @Resource
    private MessageMapper messageMapper;

    @Override
    public void sendHealthWarn(Long userId, String warnContent, String advice) {
        Message message = new Message();
        message.setFromUid(0L);
        message.setToUid(userId);
        // 没有title字段 → 合并到内容
        String fullContent = warnContent + "：" + advice;
        message.setContent(fullContent);
        message.setIsRead(0);
        message.setCreateTime(LocalDateTime.now());

        messageMapper.insert(message);
        System.out.println("✅ 系统消息发送成功：" + fullContent);
    }
}