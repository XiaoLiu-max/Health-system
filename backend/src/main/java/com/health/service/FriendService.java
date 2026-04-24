package com.health.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.health.entity.Friend;
import com.health.mapper.FriendMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.health.entity.FriendOnline;
import com.health.mapper.FriendOnlineMapper;

@Service
public class FriendService {

    @Resource
    private FriendMapper friendMapper;
    private FriendOnlineMapper friendOnlineMapper;

    // 1. 获取当前用户好友列表
    // 获取当前用户好友列表（带在线状态 + 最后在线时间）
    public List<Map<String, Object>> getFriendList(Long userId) {
        QueryWrapper<Friend> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("status", 1);
        List<Friend> list = friendMapper.selectList(wrapper);

        return list.stream().map(f -> {
            Map<String, Object> map = new HashMap<>();
            map.put("friendId", f.getFriendId());
            map.put("remark", f.getRemark());
            map.put("status", f.getStatus());

            // ====================== 在这里加在线状态 ======================
            FriendOnline online = friendOnlineMapper.selectOne(
                    new QueryWrapper<FriendOnline>().eq("user_id", f.getFriendId())
            );

            if (online != null) {
                map.put("onlineStatus", online.getOnlineStatus()); // 1=在线 0=离线
                map.put("lastTime", online.getLastTime());         // 最后在线时间
            } else {
                map.put("onlineStatus", 0);
                map.put("lastTime", null);
            }
            // ==============================================================

            return map;
        }).collect(Collectors.toList());
    }

    // 2. 双向删除好友
    public void deleteFriend(Long userId, Long friendId) {
        // 删除自己这边的好友记录
        QueryWrapper<Friend> w1 = new QueryWrapper<>();
        w1.eq("user_id", userId).eq("friend_id", friendId);
        friendMapper.delete(w1);

        // 删除对方那边的好友记录
        QueryWrapper<Friend> w2 = new QueryWrapper<>();
        w2.eq("user_id", friendId).eq("friend_id", userId);
        friendMapper.delete(w2);
    }

    // 3. 修改好友备注
    public void updateRemark(Long userId, Long friendId, String remark) {
        QueryWrapper<Friend> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("friend_id", friendId);
        Friend friend = friendMapper.selectOne(wrapper);
        if (friend == null) {
            throw new RuntimeException("好友关系不存在");
        }
        friend.setRemark(remark);
        friendMapper.updateById(friend);
    }

    // 4. 拉黑好友 status=0
    public void blackFriend(Long userId, Long friendId) {
        QueryWrapper<Friend> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("friend_id", friendId);
        Friend friend = friendMapper.selectOne(wrapper);
        if (friend == null) {
            throw new RuntimeException("好友关系不存在");
        }
        friend.setStatus(0);
        friendMapper.updateById(friend);
    }

    // 5. 解除拉黑 status=1
    public void cancelBlack(Long userId, Long friendId) {
        QueryWrapper<Friend> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("friend_id", friendId);
        Friend friend = friendMapper.selectOne(wrapper);
        if (friend == null) {
            throw new RuntimeException("好友关系不存在");
        }
        friend.setStatus(1);
        friendMapper.updateById(friend);
    }

    // 6. 判断两人是否为正常好友（私聊、消息权限校验用）
    public boolean isFriend(Long userId, Long friendId) {
        QueryWrapper<Friend> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("friend_id", friendId).eq("status", 1);
        return friendMapper.selectCount(wrapper) > 0;
    }
}