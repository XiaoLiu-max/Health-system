package com.health.controller;

import com.health.service.FriendService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/friend")
public class FriendController {

    @Resource
    private FriendService friendService;

    // 查询好友列表
    @GetMapping("/list")
    public Map<String, Object> getFriendList(Long userId) {
        Map<String, Object> map = new HashMap<>();
        try {
            map.put("code", 200);
            map.put("data", friendService.getFriendList(userId));
        } catch (Exception e) {
            map.put("code", 500);
            map.put("msg", e.getMessage());
        }
        return map;
    }

    // 双向删除好友
    @PostMapping("/delete")
    public Map<String, Object> deleteFriend(Long userId, Long friendId) {
        Map<String, Object> map = new HashMap<>();
        try {
            friendService.deleteFriend(userId, friendId);
            map.put("code", 200);
            map.put("msg", "好友删除成功");
        } catch (Exception e) {
            map.put("code", 500);
            map.put("msg", e.getMessage());
        }
        return map;
    }

    // 修改好友备注
    @PostMapping("/remark/update")
    public Map<String, Object> updateRemark(Long userId, Long friendId, String remark) {
        Map<String, Object> map = new HashMap<>();
        try {
            friendService.updateRemark(userId, friendId, remark);
            map.put("code", 200);
            map.put("msg", "备注修改成功");
        } catch (Exception e) {
            map.put("code", 500);
            map.put("msg", e.getMessage());
        }
        return map;
    }

    // 拉黑好友
    @PostMapping("/black/add")
    public Map<String, Object> blackFriend(Long userId, Long friendId) {
        Map<String, Object> map = new HashMap<>();
        try {
            friendService.blackFriend(userId, friendId);
            map.put("code", 200);
            map.put("msg", "已拉黑好友");
        } catch (Exception e) {
            map.put("code", 500);
            map.put("msg", e.getMessage());
        }
        return map;
    }

    // 解除拉黑
    @PostMapping("/black/cancel")
    public Map<String, Object> cancelBlack(Long userId, Long friendId) {
        Map<String, Object> map = new HashMap<>();
        try {
            friendService.cancelBlack(userId, friendId);
            map.put("code", 200);
            map.put("msg", "已解除拉黑");
        } catch (Exception e) {
            map.put("code", 500);
            map.put("msg", e.getMessage());
        }
        return map;
    }

    // 判断是否为好友
    @GetMapping("/isFriend")
    public Map<String, Object> isFriend(Long userId, Long friendId) {
        Map<String, Object> map = new HashMap<>();
        try {
            map.put("code", 200);
            map.put("data", friendService.isFriend(userId, friendId));
        } catch (Exception e) {
            map.put("code", 500);
            map.put("msg", e.getMessage());
        }
        return map;
    }
}