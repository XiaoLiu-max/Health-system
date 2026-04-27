package com.health.controller;

import com.health.entity.FriendOnline;
import com.health.service.FriendOnlineService;
import com.health.utils.UserContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/friendOnline")
public class FriendOnlineController {

    @Resource
    private FriendOnlineService friendOnlineService;

    // 查询自己的在线状态（自动从token获取userId）
    @GetMapping("/myInfo")
    public Map<String, Object> getMyOnlineInfo() {
        Map<String, Object> map = new HashMap<>();
        try {
            Long userId = UserContext.getUserId();
            FriendOnline online = friendOnlineService.getFriendOnlineInfo(userId);
            map.put("code", 200);
            map.put("data", online);
        } catch (Exception e) {
            map.put("code", 500);
            map.put("msg", e.getMessage());
        }
        return map;
    }

    // 查询指定用户的在线状态（传userId）
    @GetMapping("/info")
    public Map<String, Object> getOnlineInfo(Long userId) {
        Map<String, Object> map = new HashMap<>();
        try {
            if (userId == null || userId <= 0) {
                map.put("code", 500);
                map.put("msg", "用户ID不能为空");
                return map;
            }
            FriendOnline online = friendOnlineService.getFriendOnlineInfo(userId);
            map.put("code", 200);
            map.put("data", online);
        } catch (Exception e) {
            map.put("code", 500);
            map.put("msg", e.getMessage());
        }
        return map;
    }

    // 登录成功后调用：自动同步在线状态（没有就创建，有就更新）
    @GetMapping("/loginSync")
    public Map<String, Object> loginSync() {
        Map<String, Object> map = new HashMap<>();
        try {
            Long userId = UserContext.getUserId();
            friendOnlineService.loginSyncOnline(userId);
            map.put("code", 200);
            map.put("msg", "登录状态同步成功");
        } catch (Exception e) {
            map.put("code", 500);
            map.put("msg", e.getMessage());
        }
        return map;
    }
}