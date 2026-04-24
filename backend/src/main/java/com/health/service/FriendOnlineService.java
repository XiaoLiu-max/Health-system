package com.health.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.health.entity.FriendOnline;
import com.health.mapper.FriendOnlineMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
public class FriendOnlineService {

    @Resource
    private FriendOnlineMapper friendOnlineMapper;

    // 根据用户id获取在线状态和最后时间
    public FriendOnline getFriendOnlineInfo(Long userId) {
        QueryWrapper<FriendOnline> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        return friendOnlineMapper.selectOne(wrapper);
    }
}