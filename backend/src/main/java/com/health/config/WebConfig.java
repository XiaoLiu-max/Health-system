package com.health.config;
import com.health.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**") // 拦截所有
                .excludePathPatterns(
                        "/user/login/password",   // 密码登录
                        "/user/login/phone",      // 手机号登录
                        "/user/sendCode",          // 发送验证码
                        "/user/register",         // ✅ 新增：注册
                        "/user/forget",  // ✅ 新增：忘记密码
                        "/ai/chat",
                        "/user/checkUsername"
                );
    }
}