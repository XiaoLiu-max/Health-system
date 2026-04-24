package com.health.dto;

import lombok.Data;

@Data
public class ForgetDTO {
    private String phone;
    private String code;
    private String newPassword;
}