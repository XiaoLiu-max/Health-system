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


    // 登录时自动同步：没有就插入，有就更新在线状态
    public void loginSyncOnline(Long userId) {
        FriendOnline online = getFriendOnlineInfo(userId);

        if (online == null) {
            // 没有记录 → 自动创建
            FriendOnline newOnline = new FriendOnline();
            newOnline.setUserId(userId);
            newOnline.setOnlineStatus(1);      // 1=在线
            newOnline.setLastTime(LocalDateTime.now());
            friendOnlineMapper.insert(newOnline);
        } else {
            // 有记录 → 更新为在线
            online.setOnlineStatus(1);
            online.setLastTime(LocalDateTime.now());
            friendOnlineMapper.updateById(online);
        }
    }

    // 退出登录时同步为离线
    public void logoutSyncOffline(Long userId) {
        FriendOnline online = getFriendOnlineInfo(userId);

        if (online != null) {
            online.setOnlineStatus(0); // 0=离线
            online.setLastTime(LocalDateTime.now());
            friendOnlineMapper.updateById(online);
        }
    }

}