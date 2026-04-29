package com.health.controller;

import com.health.entity.User;
import com.health.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserSearchController {

    private final UserService userService;

    public UserSearchController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 搜索用户：只返回 id + username
     */
    @GetMapping("/search")
    public Map<String, Object> searchUser(@RequestParam String keyword) {
        Map<String, Object> map = new HashMap<>();
        User user = null;

        try {
            // 按ID搜索
            Long userId = Long.parseLong(keyword);
            user = userService.getById(userId);
        } catch (Exception e) {
            // 按用户名搜索
            user = userService.lambdaQuery()
                    .eq(User::getUsername, keyword)
                    .one();
        }

        if (user == null) {
            map.put("code", 404);
            map.put("msg", "用户不存在");
            return map;
        }

        // ✅ 只保留 id + username，不返回密码、手机号等敏感信息
        Map<String, Object> resultUser = new HashMap<>();
        resultUser.put("id", user.getId());
        resultUser.put("username", user.getUsername());

        map.put("code", 200);
        map.put("data", resultUser);
        return map;
    }
}