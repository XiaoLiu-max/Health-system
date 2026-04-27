package com.health.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.health.common.Result;
import com.health.entity.User;

/**
 * 用户业务逻辑接口
 * 作用：定义 登录、注册、忘记密码 等功能的方法
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param user  用户信息（用户名、手机号、密码、性别、年龄）
//     * @param confirm_password 确认密码（必须和密码一致）
     * @return 统一返回结果
     */
    Result register(User user);

    /**
     * 用户名 + 密码 登录
     */
    Result loginByPassword(String username, String password);

    /**
     * 手机号 + 验证码 登录
     */
    Result loginByPhone(String phone, String code);

    /**
     * 忘记密码 → 重置密码
     */
    Result forgetPassword(String phone, String code, String newPassword);

    void updatePassword(Long userId, String oldPassword, String newPassword);

    boolean isUserExist(Long userId);

    public User getByUsername(String username);

    public User getByPhone(String phone);
}