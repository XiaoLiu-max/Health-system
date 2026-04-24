package com.health.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.health.common.Result;
import com.health.entity.User;
import com.health.mapper.UserMapper;
import com.health.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.health.utils.JwtUtil;

/**
 * UserService 的实现类
 * 这里写所有业务逻辑的具体代码
 */
@Service // 告诉Spring，这是一个Service组件，会被自动管理
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired // 自动注入UserMapper，不用我们手动new对象
    private UserMapper userMapper;

    // 密码加密工具：用BCrypt算法，不可逆，安全
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // ====================== 1. 注册逻辑 ======================
    @Override
    public Result register(User user) {
        // ① 校验两次密码是否一致
//        if (!user.getPassword().equals(confirmPassword)) {
//            return Result.fail("两次密码不一致");
//        }

//        if (confirm_password == null || !confirm_password.equals(user.getPassword())) {
//            return Result.fail("两次输入密码不一致，请填写确认密码");
//        }

        // ② 校验手机号是否已被注册（不能重复）
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, user.getPhone()); // 构建查询条件：phone = 传入的手机号
        User existUser = userMapper.selectOne(wrapper); // 去数据库里查
        if (existUser != null) {
            return Result.fail("该手机号已被注册");
        }

        // ③ 校验性别是否合法（只能是1或2）
        if (user.getGender() == null || (user.getGender() != 1 && user.getGender() != 2)) {
            return Result.fail("性别只能选择男或女");
        }

        // ④ 校验年龄是否合法（1-120岁）
        if (user.getAge() == null || user.getAge() < 1 || user.getAge() > 120) {
            return Result.fail("年龄必须在1-120之间");
        }

        // ⑤ 密码加密（存到数据库的是加密后的字符串，不是明文）
        user.setPassword(encoder.encode(user.getPassword()));

        // ⑥ 保存用户信息到数据库
        userMapper.insert(user);
        return Result.success("注册成功！");
    }

    // ====================== 2. 密码登录逻辑 ======================
    @Override
    public Result loginByPassword(String username, String password) {
        // ① 根据用户名查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(wrapper);

        // ② 判断用户是否存在
        if (user == null) {
            return Result.fail("用户名不存在");
        }

        // ③ 校验密码（encoder.matches会自动对比明文和加密后的密码）
        if (!encoder.matches(password, user.getPassword())) {
            return Result.fail("密码错误");
        }

        // 生成 Token
        String token = JwtUtil.createToken(user.getId());

// 返回 Token
        return Result.success("登录成功", token);

//        // ④ 登录成功，返回用户信息
//        return Result.success("登录成功", user);
    }

    // ====================== 3. 手机号验证码登录 ======================
    @Override
    public Result loginByPhone(String phone, String code) {
        // ① 根据手机号查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            return Result.fail("手机号未注册");
        }

        // ② 校验验证码（这里先用固定值模拟，后面可以接短信服务）
        if (!"123456".equals(code)) {
            return Result.fail("验证码错误");
        }

        String token = JwtUtil.createToken(user.getId());

        return Result.success("登录成功", token);
    }

    // ====================== 4. 忘记密码逻辑 ======================
    @Override
    public Result forgetPassword(String phone, String code, String newPassword) {
        // ① 根据手机号查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            return Result.fail("手机号未注册");
        }

        // ② 校验验证码
        if (!"123456".equals(code)) {
            return Result.fail("验证码错误");
        }

        // ③ 新密码加密
        user.setPassword(encoder.encode(newPassword));

        // ④ 更新数据库里的密码
        userMapper.updateById(user);
        return Result.success("密码重置成功");
    }

    @Override
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        // 1. 查询当前用户
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 2. 校验旧密码是否正确
        if (!encoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("旧密码错误");
        }

        // 3. 新密码加密
        String encodePassword = encoder.encode(newPassword);
        user.setPassword(encodePassword);

        // 4. 更新到数据库
        updateById(user);
    }

    @Override
    public boolean isUserExist(Long userId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getId, userId);

        //  count = 查有没有这个用户
        long count = baseMapper.selectCount(wrapper);

        return count > 0;
    }

}