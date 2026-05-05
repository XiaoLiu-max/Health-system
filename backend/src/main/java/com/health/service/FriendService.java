package com.health.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.health.entity.Friend;
import com.health.entity.FriendOnline;
import com.health.mapper.FriendMapper;
import com.health.mapper.FriendOnlineMapper;
import com.health.utils.UserContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FriendService {
    @Resource
    private FriendMapper friendMapper;
    @Resource
    private FriendOnlineMapper friendOnlineMapper;

    // 好友列表：展示所有好友，包含自己的拉黑状态
    public List<Map<String, Object>> getFriendList() {
        Long userId = UserContext.getUserId();
        QueryWrapper<Friend> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        List<Friend> list = friendMapper.selectList(wrapper);
        return list.stream().map(f -> {
            Map<String, Object> map = new HashMap<>();
            map.put("friendId", f.getFriendId());
            map.put("username", "用户" + f.getFriendId());
            map.put("remark", f.getRemark());
            map.put("status", f.getStatus());
            FriendOnline online = friendOnlineMapper.selectOne(new QueryWrapper<FriendOnline>().eq("user_id", f.getFriendId()));
            map.put("onlineStatus", online != null ? online.getOnlineStatus() : 0);
            map.put("lastTime", online != null ? online.getLastTime() : null);
            map.put("unread", 0);
            return map;
        }).collect(Collectors.toList());
    }

    // 双向删除好友
    public void deleteFriend(Long friendId) {
        Long userId = UserContext.getUserId();
        friendMapper.delete(new QueryWrapper<Friend>().eq("user_id", userId).eq("friend_id", friendId));
        friendMapper.delete(new QueryWrapper<Friend>().eq("user_id", friendId).eq("friend_id", userId));
    }

    // 修改备注
    public void updateRemark(Long friendId, String remark) {
        Long userId = UserContext.getUserId();
        Friend friend = friendMapper.selectOne(new QueryWrapper<Friend>().eq("user_id", userId).eq("friend_id", friendId));
        if (friend == null) throw new RuntimeException("好友关系不存在");
        friend.setRemark(remark);
        friendMapper.updateById(friend);
    }

    // 【单向拉黑：只改自己，不改对方！】
    public void blackFriend(Long friendId) {
        Long userId = UserContext.getUserId();

        // 只修改 自己 -> 对方 这条记录
        QueryWrapper<Friend> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("user_id", userId).eq("friend_id", friendId);
        Friend self = friendMapper.selectOne(wrapper1);
        if (self == null) throw new RuntimeException("好友关系不存在");

        self.setStatus(0);
        self.setBlackInitiator(1); // 标记：我主动拉黑
        friendMapper.updateById(self);

        // ❌ 关键：不再修改对方那条！！！
    }

    // 【单向取消拉黑】
    public void cancelBlack(Long friendId) {
        Long userId = UserContext.getUserId();

        QueryWrapper<Friend> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("user_id", userId).eq("friend_id", friendId);
        Friend self = friendMapper.selectOne(wrapper1);
        if (self == null) throw new RuntimeException("好友关系不存在");

        self.setStatus(1);
        self.setBlackInitiator(null);
        friendMapper.updateById(self);

        // ❌ 不再修改对方那条
    }
    // 【终极判断：查对方是否拉黑我】
    public int getBlockType(Long friendId) {
        Long userId = UserContext.getUserId();

        // 1. 查：我有没有拉黑对方（查我自己这条）
        QueryWrapper<Friend> selfWrap = new QueryWrapper<>();
        selfWrap.eq("user_id", userId).eq("friend_id", friendId);
        Friend self = friendMapper.selectOne(selfWrap);

        // 2. 查：对方有没有拉黑我（查对方那条）
        QueryWrapper<Friend> otherWrap = new QueryWrapper<>();
        otherWrap.eq("user_id", friendId).eq("friend_id", userId);
        Friend other = friendMapper.selectOne(otherWrap);

        // ✅ 优先级：自己主动拉黑 > 被对方拉黑
        if (self != null && self.getStatus() == 0) {
            return 1; // 我拉黑对方
        }
        if (other != null && other.getStatus() == 0) {
            return 2; // 对方拉黑我
        }
        return 0; // 正常
    }
    // 判断是否好友
    public boolean isFriend(Long friendId) {
        Long userId = UserContext.getUserId();
        return friendMapper.selectCount(new QueryWrapper<Friend>().eq("user_id", userId).eq("friend_id", friendId).eq("status", 1)) > 0;
    }

    // 获取正常好友ID（推送用）
    public List<Long> getFriendIdsByUserId(Long userId) {
        return friendMapper.selectList(new QueryWrapper<Friend>().eq("user_id", userId).eq("status", 1))
                .stream().map(Friend::getFriendId).collect(Collectors.toList());
    }
}