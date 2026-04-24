package com.health.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.health.entity.Message;
import com.health.mapper.MessageMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    @Resource
    private MessageMapper messageMapper;

    // ====================== 1. 查询当前用户所有消息 ======================
    public List<Message> getMyMessage(Long userId) {
        QueryWrapper<Message> wrapper = new QueryWrapper<>();
        wrapper.eq("to_uid", userId);
        wrapper.orderByDesc("create_time");
        return messageMapper.selectList(wrapper);
    }

    // ====================== 2. 标记单条消息已读 ======================
    public void readMessage(Long msgId) {
        Message message = messageMapper.selectById(msgId);
        if (message != null) {
            message.setIsRead(1);
            messageMapper.updateById(message);
        }
    }

    // ====================== 3. type=1 好友申请（你好友模块调用） ======================
    public void sendFriendApplyMsg(Long fromUid, Long toUid) {
        Message msg = new Message();
        msg.setFromUid(fromUid);
        msg.setToUid(toUid);
        msg.setContent("用户" + fromUid + "申请添加你为好友");
        msg.setType(1);
        msg.setIsRead(0);
        msg.setCreateTime(LocalDateTime.now());
        messageMapper.insert(msg);
    }

    // ====================== 4. type=2 好友私聊互发消息 ======================
    public void sendChatMessage(Long fromUid, Long toUid, String content) {
        Message msg = new Message();
        msg.setFromUid(fromUid);
        msg.setToUid(toUid);
        msg.setContent(content);
        msg.setType(2);
        msg.setIsRead(0);
        msg.setCreateTime(LocalDateTime.now());
        messageMapper.insert(msg);
    }

    // ====================== 5. type=3 【你本地自定义定时提醒】补齐！ ======================
    // 你的 remind 模块时间到了，直接调用这个方法发提醒消息
    public void sendRemindMessage(Long userId, String content) {
        Message msg = new Message();
        msg.setFromUid(0L);    // 系统提醒，发送人为0
        msg.setToUid(userId);
        msg.setContent(content);
        msg.setType(3);       // 自定义提醒 type=3
        msg.setIsRead(0);
        msg.setCreateTime(LocalDateTime.now());
        messageMapper.insert(msg);
    }

    // ====================== 6. type=4、5 队友服务推送通用接口 ======================
    // 队友：异常数据 type=4 、健康报告 type=5 统一调用
    public void pushFromPartnerService(Long toUid, Integer type, String content) {
        Message msg = new Message();
        msg.setFromUid(0L);    // 队友系统推送
        msg.setToUid(toUid);
        msg.setContent(content);
        msg.setType(type);
        msg.setIsRead(0);
        msg.setCreateTime(LocalDateTime.now());
        messageMapper.insert(msg);
    }
}