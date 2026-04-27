package com.health.controller;

import com.health.entity.Message;
import com.health.service.MessageServices;
import com.health.utils.UserContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Resource
    private MessageServices messageService;

    // 查询自己所有消息（自动token）
    @GetMapping("/list")
    public Map<String, Object> getMyMessage() {
        Map<String, Object> map = new HashMap<>();
        try {
            Long userId = UserContext.getUserId();
            List<Message> list = messageService.getMyMessage(userId);
            map.put("code", 200);
            map.put("data", list);
        } catch (Exception e) {
            map.put("code", 500);
            map.put("msg", e.getMessage());
        }
        return map;
    }

    // 标记已读
    @PostMapping("/read")
    public Map<String, Object> readMessage(Long msgId) {
        Map<String, Object> map = new HashMap<>();
        try {
            messageService.readMessage(msgId);
            map.put("code", 200);
            map.put("msg", "消息已读");
        } catch (Exception e) {
            map.put("code", 500);
            map.put("msg", e.getMessage());
        }
        return map;
    }

    // 好友聊天（自动token发消息）
    @PostMapping("/sendChat")
    public Map<String, Object> sendChat(Long toUid, String content) {
        Map<String, Object> map = new HashMap<>();
        try {
            Long fromUid = UserContext.getUserId();
            messageService.sendChatMessage(fromUid, toUid, content);
            map.put("code", 200);
            map.put("msg", "发送成功");
        } catch (Exception e) {
            map.put("code", 500);
            map.put("msg", e.getMessage());
        }
        return map;
    }

    // 给自己发计划/吃药/设置提醒（type=3）
    @PostMapping("/push/self/remind")
    public Map<String, Object> pushSelfRemind(String content) {
        Map<String, Object> map = new HashMap<>();
        try {
            Long userId = UserContext.getUserId();
            messageService.sendRemindMessage(userId, content);
            map.put("code", 200);
            map.put("msg", "已给自己发送提醒");
        } catch (Exception e) {
            map.put("code", 500);
            map.put("msg", e.getMessage());
        }
        return map;
    }

    // 下面这两个留给明天改
    @PostMapping("/push/partner")
    public Map<String, Object> partnerPush(Long toUid, Integer type, String content) {
        Map<String, Object> map = new HashMap<>();
        try {
            messageService.pushFromPartnerService(toUid, type, content);
            map.put("code", 200);
            map.put("msg", "推送成功");
        } catch (Exception e) {
            map.put("code", 500);
            map.put("msg", e.getMessage());
        }
        return map;
    }
}