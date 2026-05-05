package com.health.entity;

import com.baomidou.mybatisplus.annotation.TableName;

@TableName("friend")
public class Friend {
    private Long id;
    private Long userId;
    private Long friendId;
    private String remark;
    private Integer status;
    /**
     * 拉黑发起标记：1=我主动拉黑对方，0=对方拉黑我，null=正常
     */
    private Integer blackInitiator;
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

    public Integer getBlackInitiator() {
        return blackInitiator;
    }

    public void setBlackInitiator(Integer blackInitiator) {
        this.blackInitiator=blackInitiator;
    }
}