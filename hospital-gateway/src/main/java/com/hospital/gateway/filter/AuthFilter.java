package com.hospital.gateway.filter;

import com.hospital.common.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 全局 JWT 鉴权过滤器 — 校验请求头中的 Token，解析用户信息并向后透传
 * 白名单路径直接放行（登录、注册、Knife4j文档等）
 */
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    @Value("${jwt.secret:hospital-dev-secret-key-2026-min-length-32}")
    private String jwtSecret;

    /** 不需要鉴权的白名单路径 */
    private static final List<String> WHITELIST = List.of(
            "/auth/login", "/auth/register", "/auth/send-code",
            "/doc.html", "/webjars", "/v3/api-docs", "/swagger-resources"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // 白名单路径直接放行
        for (String white : WHITELIST) {
            if (path.contains(white)) {
                return chain.filter(exchange);
            }
        }

        // 获取 Token
        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (token == null || !token.startsWith("Bearer ")) {
            return unauthorized(exchange, "未登录或Token缺失");
        }
        token = token.substring(7);

        // 校验 Token
        if (!JwtUtil.validateToken(token, jwtSecret)) {
            return unauthorized(exchange, "Token无效或已过期");
        }

        // 解析用户信息并向后透传
        Claims claims = JwtUtil.parseToken(token, jwtSecret);
        ServerHttpRequest request = exchange.getRequest().mutate()
                .header("X-User-Id", claims.getSubject())
                .header("X-User-Phone", claims.get("phone", String.class))
                .header("X-User-Role", claims.get("role", String.class))
                .build();

        return chain.filter(exchange.mutate().request(request).build());
    }

    /** 返回 401 未授权响应 */
    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"code\":401,\"message\":\"" + message + "\"}";
        DataBuffer buffer = response.bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
