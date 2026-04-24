package com.health.interceptor;

import com.health.common.Result;
import com.health.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 从请求头中获取 Token（约定前端请求头名为 token）
        String token = request.getHeader("token");

        // 2. 没有 Token，直接拦截
        if (token == null || token.isEmpty()) {
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(new ObjectMapper().writeValueAsString(Result.fail("请先登录")));
            return false;
        }

        // 3. 解析 Token，如果失败（过期/无效），也拦截
        try {
            JwtUtil.getUserId(token);
        } catch (Exception e) {
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(new ObjectMapper().writeValueAsString(Result.fail("登录已过期或无效")));
            return false;
        }

        // 4. Token 有效，放行
        return true;
    }
}