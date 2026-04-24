package com.health.controller;

import com.health.entity.Message;
import com.health.service.MessageService;
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
    private MessageService messageService;

    // 1. 查询当前用户全部消息
    @GetMapping("/list")
    public Map<String, Object> getMyMessage(Long userId) {
        Map<String, Object> map = new HashMap<>();
        try {
            List<Message> list = messageService.getMyMessage(userId);
            map.put("code", 200);
            map.put("data", list);
        } catch (Exception e) {
            map.put("code", 500);
            map.put("msg", e.getMessage());
        }
        return map;
    }

    // 2. 标记消息已读
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

    // 3. 好友私聊发送消息
    @PostMapping("/sendChat")
    public Map<String, Object> sendChat(Long fromUid, Long toUid, String content) {
        Map<String, Object> map = new HashMap<>();
        try {
            messageService.sendChatMessage(fromUid, toUid, content);
            map.put("code", 200);
            map.put("msg", "私聊消息发送成功");
        } catch (Exception e) {
            map.put("code", 500);
            map.put("msg", e.getMessage());
        }
        return map;
    }

    // 4. 队友专用推送接口（异常、健康报告）
    @PostMapping("/push/partner")
    public Map<String, Object> partnerPush(Long toUid, Integer type, String content) {
        Map<String, Object> map = new HashMap<>();
        try {
            messageService.pushFromPartnerService(toUid, type, content);
            map.put("code", 200);
            map.put("msg", "消息推送成功");
        } catch (Exception e) {
            map.put("code", 500);
            map.put("msg", e.getMessage());
        }
        return map;
    }
}