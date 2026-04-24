package com.health.entity;

import java.time.LocalDateTime;

public class Remind {
    // 字段
    private Long id;
    private String content;
    private LocalDateTime remindTime;
    private Integer repeatType;
    private Integer status;

    // 无参构造（必须有，Spring MVC 接收JSON需要）
    public Remind() {
    }

    // 全参构造（Service里初始化测试数据用）
    public Remind(Long id, String content, LocalDateTime remindTime, Integer repeatType, Integer status) {
        this.id = id;
        this.content = content;
        this.remindTime = remindTime;
        this.repeatType = repeatType;
        this.status = status;
    }

    // 所有字段的 Getter 和 Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getRemindTime() {
        return remindTime;
    }

    public void setRemindTime(LocalDateTime remindTime) {
        this.remindTime = remindTime;
    }

    public Integer getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(Integer repeatType) {
        this.repeatType = repeatType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}