package com.health.dto;

// 好友申请接口请求DTO
public class FriendApplyDTO {
    // 对方用户ID
    private Long toUserId;

    public Long getToUserId() {
        return toUserId;
    }

    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }
}