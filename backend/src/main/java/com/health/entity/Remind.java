package com.health.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.time.LocalDateTime;

public class Remind {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime remindTime;

    private Integer repeatType;
    private Integer status;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    public Remind() {
    }

    public Remind(Long id, String content, LocalDateTime remindTime, Integer repeatType, Integer status, Long userId) {
        this.id = id;
        this.content = content;
        this.remindTime = remindTime;
        this.repeatType = repeatType;
        this.status = status;
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

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