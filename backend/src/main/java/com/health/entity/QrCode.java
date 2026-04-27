package com.health.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

@Data
@TableName("qr_code")
public class QrCode {
    @TableId(type = IdType.AUTO)
    private String token;
    private Long userId;
    private String extraMsg;
    private LocalDateTime expireTime;
}