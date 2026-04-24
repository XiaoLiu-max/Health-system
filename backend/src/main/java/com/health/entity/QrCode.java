package com.health.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("qr_code")
public class QrCode {
    private String token;
    private Long userId;
    private String extraMsg;
    private LocalDateTime expireTime;
}