package com.health.controller;

import com.health.entity.FriendApply;
import com.health.service.FriendApplyService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/friendApply")
public class FriendApplyController {

    @Resource
    private FriendApplyService friendApplyService;

    // 发送好友申请
    @PostMapping("/send")
    public Map<String, Object> sendApply(Long fromUserId, Long toUserId) {
        Map<String, Object> map = new HashMap<>();
        try {
            friendApplyService.sendApply(fromUserId, toUserId);
            map.put("code", 200);
            map.put("msg", "申请已发送");
        } catch (Exception e) {
            map.put("code", 500);
            map.put("msg", e.getMessage());
        }
        return map;
    }

    // 处理申请
    @PostMapping("/handle")
    public Map<String, Object> handleApply(Integer applyId, Integer status) {
        Map<String, Object> map = new HashMap<>();
        try {
            friendApplyService.handleApply(applyId, status);
            map.put("code", 200);
            map.put("msg", status == 1 ? "已同意" : "已拒绝");
        } catch (Exception e) {
            map.put("code", 500);
            map.put("msg", e.getMessage());
        }
        return map;
    }

    // 查询收到的申请（独立页面自动加载）
    @GetMapping("/getMyApplyList")
    public Map<String, Object> getMyApplyList(Long userId) {
        Map<String, Object> map = new HashMap<>();
        try {
            List<FriendApply> list = friendApplyService.getMyApplyList(userId);
            map.put("code", 200);
            map.put("data", list);
        } catch (Exception e) {
            map.put("code", 500);
            map.put("msg", e.getMessage());
        }
        return map;
    }
    @GetMapping("/getMySendList")
    public Map<String, Object> getMySendList(Long userId) {
        Map<String, Object> map = new HashMap<>();
        try {
            List<FriendApply> list = friendApplyService.getMySendList(userId);
            map.put("code", 200);
            map.put("data", list);
        } catch (Exception e) {
            map.put("code", 500);
            map.put("msg", e.getMessage());
        }
        return map;
    }
}