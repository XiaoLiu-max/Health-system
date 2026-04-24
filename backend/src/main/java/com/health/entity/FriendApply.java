package com.health.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;

public class FriendApply {

    // 完美对应数据库主键 apply_id
    @TableId(type = IdType.AUTO)
    private Integer applyId;

    private Long fromUserId;
    private Long toUserId;
    private Integer status;
    private LocalDateTime applyTime;
    private LocalDateTime handleTime;

    // ========== 只保留1个无参构造，删掉所有重复构造 ==========
    public FriendApply() {}

    // ========== 全参构造，仅保留1个 ==========
    public FriendApply(Integer applyId, Long fromUserId, Long toUserId,
                       Integer status, LocalDateTime applyTime, LocalDateTime handleTime) {
        this.applyId = applyId;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.status = status;
        this.applyTime = applyTime;
        this.handleTime = handleTime;
    }

    // ========== 全量getter&setter，字段完全对应 ==========
    public Integer getApplyId() {
        return applyId;
    }

    public void setApplyId(Integer applyId) {
        this.applyId = applyId;
    }

    public Long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public Long getToUserId() {
        return toUserId;
    }

    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(LocalDateTime applyTime) {
        this.applyTime = applyTime;
    }

    public LocalDateTime getHandleTime() {
        return handleTime;
    }

    public void setHandleTime(LocalDateTime handleTime) {
        this.handleTime = handleTime;
    }
}