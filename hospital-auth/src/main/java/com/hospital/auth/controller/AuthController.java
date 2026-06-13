package com.hospital.auth.controller;

import com.hospital.auth.dto.LoginDTO;
import com.hospital.auth.dto.RegisterDTO;
import com.hospital.auth.service.AuthService;
import com.hospital.common.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    public R<Map<String, String>> register(@RequestBody RegisterDTO dto) {
        return authService.register(dto.getPhone(), dto.getPassword(), dto.getName(), dto.getRole());
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public R<Map<String, String>> login(@RequestBody LoginDTO dto) {
        return authService.login(dto.getPhone(), dto.getPassword());
    }
}
