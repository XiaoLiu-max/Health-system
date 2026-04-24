package com.health.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.health.entity.Friend;
import com.health.entity.FriendApply;
import com.health.entity.Message;

import com.health.mapper.FriendApplyMapper;
import com.health.mapper.FriendMapper;
import com.health.mapper.MessageMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List ;
@Service
public class FriendApplyService {

    @Resource
    private FriendApplyMapper friendApplyMapper;

    @Resource
    private FriendMapper friendMapper;

    @Resource
    private MessageMapper messageMapper;

    @Resource
    private RestTemplate restTemplate;

    // 发送好友申请
    @Transactional
    public void sendApply(Long fromUserId, Long toUserId) {
        // 1. 基础校验
        if (fromUserId == null || fromUserId <= 0) {
            throw new RuntimeException("发送者ID无效");
        }
        if (toUserId == null || toUserId <= 0) {
            throw new RuntimeException("对方ID不能为空");
        }
        if (fromUserId.equals(toUserId)) {
            throw new RuntimeException("不能添加自己");
        }

//         2. 调用队友接口检查用户是否存在
        // ===================== 队友用户接口地址 =====================
        String url = "http://localhost:8080" + "/exist/{userId}/" + toUserId;

// 发送请求调用队友
        Boolean exist;
        try {
            exist = restTemplate.getForObject(url, Boolean.class);
        } catch (Exception e) {
            throw new RuntimeException("连接队友用户服务失败，请检查队友服务是否启动");
        }

        if (exist == null || !exist) {
            throw new RuntimeException("用户不存在");
        }

        // 3. 判断是否已经是好友
        QueryWrapper<Friend> fq = new QueryWrapper<>();
        fq.eq("user_id", fromUserId).eq("friend_id", toUserId);
        if (friendMapper.selectCount(fq) > 0) {
            throw new RuntimeException("已是好友，无需重复添加");
        }

        // 4. 判断是否重复发送申请
        QueryWrapper<FriendApply> aq = new QueryWrapper<>();
        aq.eq("from_user_id", fromUserId)
                .eq("to_user_id", toUserId)
                .eq("status", 0);
        if (friendApplyMapper.selectCount(aq) > 0) {
            throw new RuntimeException("已发送过好友申请，请勿重复发送");
        }

        // 5. 插入好友申请
        FriendApply apply = new FriendApply();
        apply.setFromUserId(fromUserId);
        apply.setToUserId(toUserId);
        apply.setStatus(0);
        apply.setApplyTime(LocalDateTime.now());
        friendApplyMapper.insert(apply);

        // 6. 插入消息提醒（你已有Message，直接插入）
        Message msg = new Message();
        msg.setFromUid(fromUserId);
        msg.setToUid(toUserId);
        msg.setContent("申请添加你为好友");
        msg.setType(1);
        msg.setIsRead(0);
        msg.setCreateTime(LocalDateTime.now());
        messageMapper.insert(msg);
    }

    // 处理好友申请（同意/拒绝）
    @Transactional
    public void handleApply(Integer applyId, Integer status) {
        if (applyId == null || applyId <= 0) {
            throw new RuntimeException("申请ID无效");
        }
        if (status == null || (status != 1 && status != 2)) {
            throw new RuntimeException("状态错误：1同意 2拒绝");
        }

        FriendApply apply = friendApplyMapper.selectById(applyId);
        if (apply == null || !apply.getStatus().equals(0)) {
            throw new RuntimeException("申请不存在或已处理");
        }

        // 更新申请状态
        apply.setStatus(status);
        apply.setHandleTime(LocalDateTime.now());
        friendApplyMapper.updateById(apply);

        // 如果是同意，互相添加好友
        if (status == 1) {
            Friend f1 = new Friend();
            f1.setUserId(apply.getFromUserId());
            f1.setFriendId(apply.getToUserId());
            f1.setStatus(1);
            friendMapper.insert(f1);

            Friend f2 = new Friend();
            f2.setUserId(apply.getToUserId());
            f2.setFriendId(apply.getFromUserId());
            f2.setStatus(1);
            friendMapper.insert(f2);
        }
    }

    // 查询我收到的好友申请
    public List<FriendApply> getMyApplyList(Long userId) {
        if (userId == null || userId <= 0) {
            throw new RuntimeException("用户ID不能为空");
        }
        QueryWrapper<FriendApply> wrapper = new QueryWrapper<>();
        wrapper.eq("to_user_id", userId);
        wrapper.orderByDesc("apply_time");
        return friendApplyMapper.selectList(wrapper);
    }

    // 查询我发出的好友申请
    public List<FriendApply> getMySendList(Long userId) {
        if (userId == null || userId <= 0) {
            throw new RuntimeException("用户ID不能为空");
        }
        QueryWrapper<FriendApply> wrapper = new QueryWrapper<>();
        wrapper.eq("from_user_id", userId);
        wrapper.orderByDesc("apply_time");
        return friendApplyMapper.selectList(wrapper);
    }
}