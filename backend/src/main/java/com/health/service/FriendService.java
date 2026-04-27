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

    // ✅ 两个 mapper 都要加 @Resource
    @Resource
    private FriendMapper friendMapper;

    @Resource
    private FriendOnlineMapper friendOnlineMapper;

    // ============================
    // 获取当前用户好友列表（从Token取userId）
    // ============================
    public List<Map<String, Object>> getFriendList() {
        // ✅ 从 UserContext 获取当前登录用户ID
        Long userId = UserContext.getUserId();

        QueryWrapper<Friend> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("status", 1);
        List<Friend> list = friendMapper.selectList(wrapper);

        return list.stream().map(f -> {
            Map<String, Object> map = new HashMap<>();
            map.put("friendId", f.getFriendId());
            map.put("remark", f.getRemark());
            map.put("status", f.getStatus());

            // 查询在线状态
            FriendOnline online = friendOnlineMapper.selectOne(
                    new QueryWrapper<FriendOnline>().eq("user_id", f.getFriendId())
            );

            if (online != null) {
                map.put("onlineStatus", online.getOnlineStatus());
                map.put("lastTime", online.getLastTime());
            } else {
                map.put("onlineStatus", 0);
                map.put("lastTime", null);
            }

            return map;
        }).collect(Collectors.toList());
    }

    // ============================
    // 删除好友（从Token取userId）
    // ============================
    public void deleteFriend(Long friendId) {
        Long userId = UserContext.getUserId();

        QueryWrapper<Friend> w1 = new QueryWrapper<>();
        w1.eq("user_id", userId).eq("friend_id", friendId);
        friendMapper.delete(w1);

        QueryWrapper<Friend> w2 = new QueryWrapper<>();
        w2.eq("user_id", friendId).eq("friend_id", userId);
        friendMapper.delete(w2);
    }

    // ============================
    // 修改备注（从Token取userId）
    // ============================
    public void updateRemark(Long friendId, String remark) {
        Long userId = UserContext.getUserId();

        QueryWrapper<Friend> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("friend_id", friendId);
        Friend friend = friendMapper.selectOne(wrapper);

        if (friend == null) {
            throw new RuntimeException("好友关系不存在");
        }

        friend.setRemark(remark);
        friendMapper.updateById(friend);
    }

    // ============================
    // 拉黑（从Token取userId）
    // ============================
    public void blackFriend(Long friendId) {
        Long userId = UserContext.getUserId();

        QueryWrapper<Friend> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("friend_id", friendId);
        Friend friend = friendMapper.selectOne(wrapper);

        if (friend == null) {
            throw new RuntimeException("好友关系不存在");
        }

        friend.setStatus(0);
        friendMapper.updateById(friend);
    }

    // ============================
    // 取消拉黑（从Token取userId）
    // ============================
    public void cancelBlack(Long friendId) {
        Long userId = UserContext.getUserId();

        QueryWrapper<Friend> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("friend_id", friendId);
        Friend friend = friendMapper.selectOne(wrapper);

        if (friend == null) {
            throw new RuntimeException("好友关系不存在");
        }

        friend.setStatus(1);
        friendMapper.updateById(friend);
    }

    // ============================
    // 判断是否好友（从Token取userId）
    // ============================
    public boolean isFriend(Long friendId) {
        Long userId = UserContext.getUserId();

        QueryWrapper<Friend> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("friend_id", friendId).eq("status", 1);
        return friendMapper.selectCount(wrapper) > 0;
    }
}