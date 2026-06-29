package com.hospital.gateway.filter;

import com.hospital.common.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
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
import java.util.Map;
import java.util.Set;

/**
 * 全局 JWT 鉴权 + 角色授权过滤器
 * <ul>
 *   <li>校验请求头 Bearer Token</li>
 *   <li>解析用户信息（ID、手机号、角色）向后透传</li>
 *   <li>根据请求路径 + 角色进行授权，无权限返回 403</li>
 * </ul>
 */
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    @Value("${jwt.secret:hospital-local-dev-secret-2026-min-len-32}")
    private String jwtSecret;

    @Autowired
    private ReactiveStringRedisTemplate redisTemplate;

    /** 不需要鉴权的白名单路径（登录、注册、验证码、登出、API文档） */
    private static final List<String> WHITELIST = List.of(
            "/auth/login", "/auth/register", "/auth/send-code", "/auth/logout",
            "/doc.html", "/webjars", "/v3/api-docs", "/swagger-resources"
    );

    /** 仅管理员可访问的路径前缀 */
    private static final List<String> ADMIN_ONLY = List.of(
            "/hospital/add", "/hospital/update",
            "/doctor/add", "/doctor/update",
            "/schedule/add", "/schedule/update",
            "/order/list"
    );

    /** 仅管理员可访问的路径后缀（DELETE 操作） */
    private static final List<String> ADMIN_DELETE = List.of(
            "/hospital/", "/doctor/", "/schedule/", "/order/"
    );

    /** 医生和管理员可访问的路径前缀 */
    private static final List<String> DOCTOR_OR_ADMIN = List.of(
            "/schedule/doctor/", "/booking/doctor/"
    );

    /** 需要校验请求者是医生本人的路径 */
    private static final List<String> DOCTOR_SELF = List.of(
            "/doctor/byUserId/"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod() != null
                ? exchange.getRequest().getMethod().name() : "";

        // 白名单路径直接放行
        for (String white : WHITELIST) {
            if (path.contains(white)) {
                return chain.filter(exchange);
            }
        }

        // 获取并校验 Token
        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (token == null || !token.startsWith("Bearer ")) {
            return unauthorized(exchange, "未登录或Token缺失");
        }
        token = token.substring(7);

        if (!JwtUtil.validateToken(token, jwtSecret)) {
            return unauthorized(exchange, "Token无效或已过期");
        }

        // Token 黑名单检查（登出后的Token不可复用）— 响应式 Redis 查询
        final String finalToken = token;
        return redisTemplate.hasKey("token:blacklist:" + token)
                .defaultIfEmpty(false)
                .flatMap(inBlacklist -> {
                    if (Boolean.TRUE.equals(inBlacklist)) {
                        return unauthorized(exchange, "Token已注销，请重新登录");
                    }

                    // 解析用户信息
                    Claims claims = JwtUtil.parseToken(finalToken, jwtSecret);
                    String userId = claims.getSubject();
                    String role = claims.get("role", String.class);

                    // ========== 角色授权检查 ==========

                    // 1. 检查仅管理员可访问的路径
                    for (String adminPath : ADMIN_ONLY) {
                        if (path.contains(adminPath) && !"ADMIN".equals(role)) {
                            return forbidden(exchange, "权限不足，仅管理员可操作");
                        }
                    }

                    // 1b. DELETE 操作仅管理员
                    if ("DELETE".equals(method)) {
                        for (String adminDel : ADMIN_DELETE) {
                            if (path.contains(adminDel) && !"ADMIN".equals(role)) {
                                return forbidden(exchange, "权限不足，仅管理员可删除");
                            }
                        }
                    }

                    // 2. 检查医生/管理员路径
                    for (String docPath : DOCTOR_OR_ADMIN) {
                        if (path.contains(docPath) && !"DOCTOR".equals(role) && !"ADMIN".equals(role)) {
                            return forbidden(exchange, "权限不足，仅医生或管理员可操作");
                        }
                    }

                    // 3. 患者专属操作检查
                    if ((path.contains("/booking/create") || path.contains("/order/create")
                            || path.contains("/payment/")) && !"PATIENT".equals(role)) {
                        return forbidden(exchange, "权限不足，仅患者可操作");
                    }

                    // 向后透传用户信息
                    ServerHttpRequest request = exchange.getRequest().mutate()
                            .header("X-User-Id", userId)
                            .header("X-User-Phone", claims.get("phone", String.class))
                            .header("X-User-Role", role)
                            .build();

                    return chain.filter(exchange.mutate().request(request).build());
                });
    }

    /** 返回 401 未登录 */
    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        return writeJson(exchange, HttpStatus.UNAUTHORIZED, 401, message);
    }

    /** 返回 403 无权限 */
    private Mono<Void> forbidden(ServerWebExchange exchange, String message) {
        return writeJson(exchange, HttpStatus.FORBIDDEN, 403, message);
    }

    /** 写入 JSON 响应体 */
    private Mono<Void> writeJson(ServerWebExchange exchange, HttpStatus status, int code, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"code\":" + code + ",\"message\":\"" + message + "\"}";
        DataBuffer buffer = response.bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
