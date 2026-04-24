package com.health.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String phone;

//    @JsonIgnore
    private String password;

    private Integer gender;
    private Integer age;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

//    @TableField(exist = false)
////    @JsonIgnore
//    private String confirm_password;
}