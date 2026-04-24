package com.health.dto;

import lombok.Data;

/**
 * 密码登录请求参数
 * 只接收登录用的字段，和数据库User实体完全分离
 */
@Data
public class LoginDTO {
    private String username;
    private String password;
}