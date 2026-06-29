package com.hospital.auth.controller;

import com.hospital.auth.dto.LoginDTO;
import com.hospital.auth.dto.RegisterDTO;
import com.hospital.auth.entity.User;
import com.hospital.auth.service.AuthService;
import com.hospital.common.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * 认证控制器 — 提供注册、登录、发送验证码的 REST API
 */
@Tag(name = "认证接口", description = "用户注册、登录、验证码")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "发送短信验证码")
    @PostMapping("/send-code")
    public R<Void> sendCode(@RequestParam String phone) {
        authService.sendCode(phone);
        return R.ok();
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public R<Map<String, String>> register(@Valid @RequestBody RegisterDTO dto) {
        return authService.register(dto.getPhone(), dto.getPassword(), dto.getName(), dto.getRole(), dto.getCode());
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public R<Map<String, String>> login(@Valid @RequestBody LoginDTO dto) {
        return authService.login(dto.getPhone(), dto.getPassword());
    }

    @Operation(summary = "登出（Token加入黑名单）")
    @PostMapping("/logout")
    public R<Void> logout(@RequestHeader("Authorization") String authHeader) {
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }
        authService.logout(token);
        return R.ok();
    }

    @Operation(summary = "根据ID获取用户信息")
    @GetMapping("/user/{id}")
    public R<User> getUserById(@PathVariable Long id) {
        User user = authService.getUserById(id);
        if (user == null) {
            return R.fail("用户不存在");
        }
        return R.ok(user);
    }
}
