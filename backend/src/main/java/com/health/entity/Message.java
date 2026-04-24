package com.health.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;


@TableName("message")
public class Message {
    private Long id;
    private Long fromUid;
    private Long toUid;
    private String content;
    private String url;
    private Integer type;
    private Integer isRead;
    private LocalDateTime createTime;
    public Message() {}

    // ====================== Getter & Setter ======================
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getFromUid() {
        return fromUid;
    }
    public void setFromUid(Long fromUid) {
        this.fromUid = fromUid;
    }

    public Long getToUid() {
        return toUid;
    }
    public void setToUid(Long toUid) {
        this.toUid = toUid;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getType() {
        return type;
    }
    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getIsRead() {
        return isRead;
    }
    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}