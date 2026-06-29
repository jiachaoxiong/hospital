package com.hospital.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * 网关层 CORS 跨域配置 — 统一处理前端跨域请求
 * <p>
 * 安全策略：不允许使用 "*" 通配符 + allowCredentials(true)，
 * 必须明确指定允许的前端源地址。
 * </p>
 */
@Configuration
public class CorsConfig {

    /** 允许的前端源地址列表，通过配置或环境变量注入 */
    @Value("${cors.allowed-origins:http://localhost:5173,http://localhost:5174}")
    private List<String> allowedOrigins;

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 明确指定允许的源，不使用 "*"（与 allowCredentials=true 不兼容）
        config.setAllowedOrigins(allowedOrigins);
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);
        // 预检请求缓存时间
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }
}
