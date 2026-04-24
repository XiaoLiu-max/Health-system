package com.health.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

@TableName("friend_online")
public class FriendOnline {
    private Long userId;
    private Integer onlineStatus;
    private LocalDateTime lastTime;

    // 【必须】无参构造（MyBatis-Plus反射创建对象必备）
    public FriendOnline() {}

    // 全参构造
    public FriendOnline(Long userId, Integer onlineStatus, LocalDateTime lastTime) {
        this.userId = userId;
        this.onlineStatus = onlineStatus;
        this.lastTime = lastTime;
    }

    // 【必须】getter方法，对应你业务代码里的调用
    public Long getUserId() {
        return userId;
    }

    public Integer getOnlineStatus() {
        return onlineStatus;
    }

    public LocalDateTime getLastTime() {
        return lastTime;
    }

    // setter方法
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setOnlineStatus(Integer onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public void setLastTime(LocalDateTime lastTime) {
        this.lastTime = lastTime;
    }
}