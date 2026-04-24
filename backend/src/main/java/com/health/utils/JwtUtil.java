package com.health.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;
import java.util.Date;

/**
 * JWT 工具类
 * 功能：生成 Token、解析 Token、判断 Token 是否过期
 */
@Component // 这个注解的作用：让Spring把这个工具类当成一个Bean来管理，后面我们才能直接注入使用
public class JwtUtil {

    // ====================== 【核心配置】 ======================
    // 1. 秘钥（加密密码，自己随便写，越复杂越安全，别泄露给别人！）
    private static final String SECRET_KEY = "health_system_2026_secret_key";
    // 2. Token 过期时间（这里设置为 24 小时，单位是毫秒）
    private static final long EXPIRE_TIME = 24 * 60 * 60 * 1000;

    // ====================== 【1. 生成 Token】 ======================
    // 作用：传入用户ID，生成一段加密的Token字符串
    public static String createToken(Long userId) {
        // 获取当前时间
        Date now = new Date();
        // 计算过期时间：当前时间 + 我们设置的 24 小时
        Date expireDate = new Date(now.getTime() + EXPIRE_TIME);

        // 使用JWT的工具类生成Token
        return Jwts.builder()
                .setSubject(String.valueOf(userId)) // 把用户ID存到Token里（Subject是JWT里专门存用户信息的字段）
                .setIssuedAt(now) // 设置Token的签发时间
                .setExpiration(expireDate) // 设置Token的过期时间
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY) // 用HS256算法 + 我们的秘钥加密
                .compact(); // 把上面这些配置，压缩成一个字符串，就是我们的Token
    }

    // ====================== 【2. 解析 Token】 ======================
    // 作用：传入Token，把里面的信息解密出来
    public static Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY) // 用和生成时一样的秘钥来解密
                .parseClaimsJws(token) // 解析Token，如果Token被篡改/过期了，这里会直接报错
                .getBody(); // 拿到解密后的信息（里面包含用户ID、签发时间、过期时间）
    }

    // ====================== 【3. 从Token里获取用户ID】 ======================
    // 作用：解析Token后，直接把用户ID拿出来，方便我们后面用
    public static Long getUserId(String token) {
        Claims claims = parseToken(token);
        return Long.valueOf(claims.getSubject());
    }
}