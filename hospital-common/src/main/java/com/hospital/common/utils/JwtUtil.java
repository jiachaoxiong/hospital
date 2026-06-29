package com.hospital.common.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类 — 负责 Token 的生成、解析、校验
 */
public class JwtUtil {

    /** 最低密钥长度要求（256位 = 32字节），低于此长度拒绝启动 */
    private static final int MIN_SECRET_LENGTH = 32;
    private static final long ACCESS_EXPIRATION = 1000 * 60 * 60 * 24;
    private static final long REFRESH_EXPIRATION = 1000 * 60 * 60 * 24 * 7;

    /**
     * 从密钥字符串派生 HMAC-SHA 签名密钥。
     * 如果密钥长度不足 {@value #MIN_SECRET_LENGTH} 字节，抛出异常拒绝服务，
     * 防止因弱密钥导致 JWT 签名被暴力破解。
     */
    private static SecretKey getKey(String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException(
                "JWT密钥未配置！请设置环境变量 JWT_SECRET，长度至少 " + MIN_SECRET_LENGTH + " 字节");
        }
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < MIN_SECRET_LENGTH) {
            throw new IllegalStateException(
                "JWT密钥长度不足！当前 " + keyBytes.length + " 字节，需要至少 " + MIN_SECRET_LENGTH + " 字节。" +
                "请使用: openssl rand -base64 32");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public static String createAccessToken(Long userId, String phone, String role, String secret) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("phone", phone)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRATION))
                .signWith(getKey(secret))
                .compact();
    }

    public static String createRefreshToken(Long userId, String secret) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION))
                .signWith(getKey(secret))
                .compact();
    }

    public static Claims parseToken(String token, String secret) {
        return Jwts.parser()
                .verifyWith(getKey(secret))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public static boolean validateToken(String token, String secret) {
        try {
            parseToken(token, secret);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
