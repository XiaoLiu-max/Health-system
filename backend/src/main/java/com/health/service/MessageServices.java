package com.health.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.health.entity.Message;
import com.health.entity.User;
import com.health.mapper.MessageMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageServices {

    @Resource
    private MessageMapper messageMapper;

    @Resource
    private UserService userService;

    // ====================== 1. 查询当前用户所有消息 ======================
    public List<Message> getMyMessage(Long userId) {
        QueryWrapper<Message> wrapper = new QueryWrapper<>();
        // ✅ 核心逻辑：
        // 1. 私聊消息(type=2)：我发的 OR 发给我的
        // 2. 系统通知(type≠2)：只查发给我的(to_uid=userId)，不查我发出去的！
        wrapper.and(w ->
                w.eq("to_uid", userId)
                        .or(ww -> ww.eq("from_uid", userId).eq("type", 2))
        );
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

    public void sendFriendApplyMsg(Long fromUid, Long toUid, String content) {
        Message msg = new Message();
        msg.setFromUid(fromUid);
        msg.setToUid(toUid);
        msg.setContent(content); // 用传入的内容
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
    // remind 模块时间到了，直接调用这个方法发提醒消息
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




    // ====================== 给好友发：周报   ======================
    public void sendWeekReportToFriend(Long userId, Long friendId, String url) {


        User user = userService.getById(userId);
        String username = user.getUsername() != null ? user.getUsername() : "好友";

        Message msg = new Message();
        msg.setFromUid(userId);
        msg.setToUid(friendId);
        msg.setContent("【好友周报】" + username + " 生成了每周健康报告，点击查看");
        msg.setUrl(url);
        msg.setType(5);
        msg.setIsRead(0);
        msg.setCreateTime(LocalDateTime.now());
        messageMapper.insert(msg);
    }

    // ====================== 给好友发：月报 ======================
    public void sendMonthReportToFriend(Long userId, Long friendId, String url) {


        User user = userService.getById(userId);
        String username = user.getUsername() != null ? user.getUsername() : "好友";

        Message msg = new Message();
        msg.setFromUid(userId);
        msg.setToUid(friendId);
        msg.setContent("【好友月报】" + username + " 生成了每月健康报告，点击查看");
        msg.setUrl(url);
        msg.setType(5);
        msg.setIsRead(0);
        msg.setCreateTime(LocalDateTime.now());
        messageMapper.insert(msg);
    }

    // 4. 自动给好友发异常
    public void sendAbnormalToFriend(Long userId, Long friendId, String content) {

        // 获取用户真实名字
        User user = userService.getById(userId);
        String username = user.getUsername() != null ? user.getUsername() : "你的好友";

        Message msg = new Message();
        msg.setFromUid(userId);
        msg.setToUid(friendId);
        msg.setContent("【好友健康异常】" + username + " 健康数据异常：" + content);
        msg.setType(4);
        msg.setIsRead(0);
        msg.setCreateTime(LocalDateTime.now());
        messageMapper.insert(msg);
    }



    public boolean recallLatestSelfChatMsg(Long currentUserId) {
        Message msg = messageMapper.selectLatestSelfChatMsg(currentUserId);
        if (msg == null) {
            System.out.println("【撤回失败】没有找到可撤回的消息");
            return false;
        }
        msg.setIsRecall(1);
        msg.setRecallTime(java.time.LocalDateTime.now());
        int rows = messageMapper.updateById(msg);
        return rows > 0;
    }
}