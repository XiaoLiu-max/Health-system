package com.health.entity;

import com.baomidou.mybatisplus.annotation.TableName;

@TableName("friend")
public class Friend {
    private Long id;
    private Long userId;
    private Long friendId;
    private String remark;
    private Integer status;
    public Friend() {}

    public Friend(Long  id, Long userId, Long friendId) {
        this.id = id;
        this.userId = userId;
        this.friendId = friendId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getFriendId() {
        return friendId;
    }

    public void setFriendId(Long friendId) {
        this.friendId = friendId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemark(){
        return remark;
    }

    public void setRemark(String remark){
        this.remark=remark;
    }
}