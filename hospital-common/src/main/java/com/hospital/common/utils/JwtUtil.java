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

    private static final String DEFAULT_SECRET = "hospital-dev-secret-key-2026-min-length-32";
    private static final long ACCESS_EXPIRATION = 1000 * 60 * 60 * 24;
    private static final long REFRESH_EXPIRATION = 1000 * 60 * 60 * 24 * 7;

    private static SecretKey getKey(String secret) {
        byte[] keyBytes = (secret != null ? secret : DEFAULT_SECRET).getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            byte[] padded = new byte[32];
            System.arraycopy(keyBytes, 0, padded, 0, Math.min(keyBytes.length, 32));
            keyBytes = padded;
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
