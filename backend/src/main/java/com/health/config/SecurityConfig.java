//package com.health.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                // 关闭 CSRF 保护
//                .csrf(csrf -> csrf.disable())
//                // 允许所有请求，不需要认证
//                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
//                // 关闭 session
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//
//        return http.build();
//    }
//}

//package com.health.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//                .csrf(csrf -> csrf.disable())
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//
//                .authorizeHttpRequests(auth -> auth
//                        .antMatchers(
//                                "/user/login/password",
//                                "/user/login/phone",
//                                "/user/sendCode",
//                                "/user/register",
//                                "/user/forget"
//                        ).permitAll()
//                        .anyRequest().authenticated()
//                );
//
//        return http.build();
//    }
//
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration config = new CorsConfiguration();
//
//        List<String> allowedOrigins = new ArrayList<>();
//        allowedOrigins.add("http://localhost:5173"); // 前端端口
//        config.setAllowedOrigins(allowedOrigins);
//
//        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//        config.setAllowedHeaders(Arrays.asList("*"));
//        config.setAllowCredentials(true);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//        return source;
//    }
//}


//package com.health.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//                .csrf(csrf -> csrf.disable())
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//
//                // 【关键】添加一个过滤器，让 Spring 认识你 header 里的 token
//                .addFilterBefore(new TokenAuthenticationFilter(), org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
//
//                .authorizeHttpRequests(auth -> auth
//                        .antMatchers(
//                                "/user/login/password",
//                                "/user/login/phone",
//                                "/user/sendCode",
//                                "/user/register",
//                                "/user/forget",
//                                "/ai/chat"
//                        ).permitAll()
//                        .anyRequest().authenticated()
//                );
//
//        return http.build();
//    }
//
//    // ==============================================
//    // 【核心】自定义过滤器：只要请求头带 token → 直接认为已登录
//    // 你现在的项目不需要复杂JWT，只要这样就能正常运行
//    // ==============================================
//    public static class TokenAuthenticationFilter extends OncePerRequestFilter {
//        @Override
//        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//            String token = request.getHeader("token");
//
//            // 只要有 token → 设置成已登录状态
//            if (token != null && !token.isEmpty()) {
//                org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(
//                        new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("user", null, new ArrayList<>())
//                );
//            }
//
//            filterChain.doFilter(request, response);
//        }
//    }
//
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration config = new CorsConfiguration();
//        List<String> allowedOrigins = new ArrayList<>();
//        allowedOrigins.add("http://localhost:5173");
//        config.setAllowedOrigins(allowedOrigins);
//        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//        config.setAllowedHeaders(Arrays.asList("*"));
//        config.setAllowCredentials(true);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//        return source;
//    }
//}




package com.health.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(new TokenAuthenticationFilter(), org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests() // 旧版写法
                .antMatchers(
                        "/user/login/password",
                        "/user/login/phone",
                        "/user/sendCode",
                        "/user/register",
                        "/user/forget",
                        "/ai/chat",
                        "/user/checkUsername"
                ).permitAll()
                .anyRequest().authenticated();

        return http.build();
    }

    // ==============================
    // 关键：AI 接口直接跳过 token 校验
    // ==============================
    public static class TokenAuthenticationFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

            String uri = request.getRequestURI();

            // ✅ AI 接口 + 预检请求 直接放行
            if (uri.startsWith("/ai/chat") || request.getMethod().equals("OPTIONS")) {
                filterChain.doFilter(request, response);
                return;
            }

            // 其他接口正常 token 校验
            String token = request.getHeader("token");
            if (token != null && !token.isEmpty()) {
                SecurityContextHolder.getContext().setAuthentication(
                        new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("user", null, new ArrayList<>())
                );
            }

            filterChain.doFilter(request, response);
        }
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        List<String> allowedOrigins = new ArrayList<>();
        allowedOrigins.add("http://localhost:5173");
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}