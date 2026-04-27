package com.health.interceptor;

import com.health.common.Result;
import com.health.utils.JwtUtil;
import com.health.utils.UserContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI(); // 获取当前请求地址

        // ==============================================
        // 放行：查看好友在线状态接口，不需要登录
        // ==============================================
        if (uri.equals("/friendOnline/info")) {
            return true;
        }

        String token = request.getHeader("token");

        if (token == null || token.isEmpty()) {
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(new ObjectMapper().writeValueAsString(Result.fail("请先登录")));
            return false;
        }

        try {
            Long userId = JwtUtil.getUserId(token);
            UserContext.setUserId(userId);
        } catch (Exception e) {
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(new ObjectMapper().writeValueAsString(Result.fail("登录已过期")));
            return false;
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.clear();
    }
}