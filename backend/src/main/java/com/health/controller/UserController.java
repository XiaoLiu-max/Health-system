//package com.health.controller;
//
//import com.health.common.Result;
//import com.health.entity.User;
//import com.health.service.UserService;
//import com.health.utils.JwtUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//
//// 这个类是接口控制器，接收前端请求
//@RestController
//// 所有接口前缀都是 /user
//@RequestMapping("/user")
//public class UserController {
//
//    // 自动注入 Service
//    @Autowired
//    private UserService userService;
//
//    // 注册接口
//    // 前端访问地址：POST /user/register
//    @PostMapping("/register")
//    public Result register(@RequestBody User user) {
//        return userService.register(user);
//    }
//
//    // 密码登录
////    @PostMapping("/login/password")
////    public Result loginByPassword(String username, String password) {
////        return userService.loginByPassword(username, password);
////    }
//
//    // 密码登录 ———— 正确写法！
//    @PostMapping("/login/password")
//    public Result loginByPassword(@RequestBody User user) {  // 这里加 @RequestBody
//        return userService.loginByPassword(user.getUsername(), user.getPassword());
//    }
//
//    // 手机号验证码登录
//    @PostMapping("/login/phone")
//    public Result loginByPhone(String phone, String code) {
//        return userService.loginByPhone(phone, code);
//    }
//
//    // 忘记密码（重置）
//    @PostMapping("/forget")
//    public Result forgetPassword(String phone, String code, String newPassword) {
//        return userService.forgetPassword(phone, code, newPassword);
//    }
//
//    @GetMapping("/test")
//    public Result test() {
//        return Result.success("登录成功！你可以访问受保护接口啦 ✅");
//    }
//
//    // 获取当前登录用户信息（必须登录）
//    @GetMapping("/info")
//    public Result info(HttpServletRequest request) {
//        // 1. 从请求头拿到 token
//        String token = request.getHeader("token");
//
//        // 2. 从 token 里拿出 用户ID
//        Long userId = JwtUtil.getUserId(token);
//
//        // 3. 根据ID查用户信息
//        User user = userService.getById(userId);
//
//        if (user == null) {
//            return Result.fail("用户不存在，账号已注销或已失效");
//        }
//
//        // 4. 返回用户信息
//        return Result.success(user);
//    }
//
//    // 修改密码（必须登录）
//    @PostMapping("/updatePassword")
//    public Result updatePassword(HttpServletRequest request,
//                                 String oldPassword,  // 旧密码
//                                 String newPassword) { // 新密码
//
//        // 1. 从token拿当前登录用户ID
//        String token = request.getHeader("token");
//        Long userId = JwtUtil.getUserId(token);
//
//        // 2. 调用service修改密码
//        userService.updatePassword(userId, oldPassword, newPassword);
//
//        return Result.success("密码修改成功");
//    }
//
//    // 退出登录（必须登录）
//    @PostMapping("/logout")
//    public Result logout(HttpServletRequest request) {
//        // 1. 从请求头拿到 token
//        String token = request.getHeader("token");
//
//        // 2. 核心逻辑：方案二，这里把 token 加入黑名单
//        //    如果你用 Redis，就写 redisTemplate.opsForValue().set(token, "logout", 过期时间, 时间单位);
//
//        // 3. 返回成功，前端做删除操作
//        return Result.success("退出登录成功");
//    }
//
//    // 更新用户资料（必须登录）
//    @PostMapping("/updateInfo")
//    public Result updateInfo(HttpServletRequest request, User user) {
//        // 1. 从 token 拿到当前登录用户ID
//        String token = request.getHeader("token");
//        Long userId = JwtUtil.getUserId(token);
//
//        // 2. 把用户ID设置进去，只允许改自己的信息
//        user.setId(userId);
//
//        // 3. 更新数据库
//        userService.updateById(user);
//
//        return Result.success("资料修改成功");
//    }
//
//    // 注销账号（永久删除当前登录用户）
//    @PostMapping("/deleteAccount")
//    public Result deleteAccount(HttpServletRequest request) {
//        // 1. 从token拿当前登录用户ID
//        String token = request.getHeader("token");
//        Long userId = JwtUtil.getUserId(token);
//
//        // 2. 永久删除自己的账号（只能删自己）
//        userService.removeById(userId);
//
//        return Result.success("账号注销成功，该账号已永久删除");
//    }
//
//}


package com.health.controller;

import com.health.common.Result;
import com.health.dto.*;
import com.health.entity.User;
import com.health.service.UserService;
import com.health.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    // 注册接口（User实体本来就匹配，保持不动）
    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        return userService.register(user);
    }

    // 密码登录 ✅ 修复完成
    @PostMapping("/login/password")
    public Result loginByPassword(@RequestBody LoginDTO loginDTO) {
        return userService.loginByPassword(loginDTO.getUsername(), loginDTO.getPassword());
    }

    // 手机号验证码登录 ✅ 修复完成
    @PostMapping("/login/phone")
    public Result loginByPhone(@RequestBody LoginPhoneDTO loginPhoneDTO) {
        return userService.loginByPhone(loginPhoneDTO.getPhone(), loginPhoneDTO.getCode());
    }

    // 忘记密码（重置）✅ 修复完成
    @PostMapping("/forget")
    public Result forgetPassword(@RequestBody ForgetDTO forgetDTO) {
        return userService.forgetPassword(forgetDTO.getPhone(), forgetDTO.getCode(), forgetDTO.getNewPassword());
    }

    @GetMapping("/test")
    public Result test() {
        return Result.success("登录成功！你可以访问受保护接口啦 ✅");
    }

    // 获取当前登录用户信息（无需改动）
    @GetMapping("/info")
    public Result info(HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = JwtUtil.getUserId(token);
        User user = userService.getById(userId);
        if (user == null) {
            return Result.fail("用户不存在，账号已注销或已失效");
        }
        return Result.success(user);
    }

    // 修改密码 ✅ 修复完成
    @PostMapping("/updatePassword")
    public Result updatePassword(HttpServletRequest request,
                                 @RequestBody UpdatePasswordDTO passwordDTO) {
        String token = request.getHeader("token");
        Long userId = JwtUtil.getUserId(token);
        userService.updatePassword(userId, passwordDTO.getOldPassword(), passwordDTO.getNewPassword());
        return Result.success("密码修改成功");
    }

    // 退出登录（无需改动）
    @PostMapping("/logout")
    public Result logout(HttpServletRequest request) {
        String token = request.getHeader("token");
        return Result.success("退出登录成功");
    }

    // 更新用户资料（无需改动）
    @PostMapping("/updateInfo")
    public Result updateInfo(HttpServletRequest request, @RequestBody User user) {
        String token = request.getHeader("token");
        Long userId = JwtUtil.getUserId(token);
        user.setId(userId);
        userService.updateById(user);
        return Result.success("资料修改成功");
    }

    // 注销账号（无需改动）
    @PostMapping("/deleteAccount")
    public Result deleteAccount(HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = JwtUtil.getUserId(token);
        userService.removeById(userId);
        return Result.success("账号注销成功，该账号已永久删除");
    }

    @GetMapping("/exist/{userId}")
    public Result isUserExist(@PathVariable Long userId) {
        boolean exist = userService.isUserExist(userId);
        return Result.success(exist);
    }

}