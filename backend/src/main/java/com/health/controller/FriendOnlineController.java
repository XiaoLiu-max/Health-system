package com.health.controller;

import com.health.entity.FriendOnline;
import com.health.mapper.FriendOnlineMapper;
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
    private FriendOnlineMapper friendOnlineMapper;

    // 单独查询某个用户在线信息
    @GetMapping("/info")
    public Map<String, Object> getOnlineInfo(Long userId) {
        Map<String, Object> map = new HashMap<>();
        FriendOnline online = friendOnlineMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<FriendOnline>().eq("user_id", userId)
        );
        map.put("code", 200);
        map.put("data", online);
        return map;
    }
}