package com.hospital.auth.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hospital.auth.entity.User;
import com.hospital.auth.mapper.UserMapper;
import com.hospital.auth.service.AuthService;
import com.hospital.common.BusinessException;
import com.hospital.common.R;
import com.hospital.common.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现 — 注册、登录、验证码发送的核心业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final StringRedisTemplate redisTemplate;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Value("${jwt.secret:hospital-dev-secret-key-2026-min-length-32}")
    private String jwtSecret;

    private static final String PHONE_REGEX = "^1[3-9]\\d{9}$";

    @Override
    public void sendCode(String phone) {
        if (StrUtil.isBlank(phone) || !phone.matches(PHONE_REGEX)) {
            throw new BusinessException("手机号格式不正确");
        }
        String code = RandomUtil.randomNumbers(6);
        redisTemplate.opsForValue().set("sms:code:" + phone, code, 5, TimeUnit.MINUTES);
        log.info("【模拟短信】向 {} 发送验证码: {}", phone, code);
    }

    @Override
    public R<Map<String, String>> register(String phone, String password, String name, String role) {
        if (StrUtil.isBlank(phone) || !phone.matches(PHONE_REGEX)) {
            throw new BusinessException("手机号格式不正确");
        }
        if (StrUtil.isBlank(password) || password.length() < 6) {
            throw new BusinessException("密码不能少于6位");
        }
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getPhone, phone));
        if (count > 0) {
            throw new BusinessException("该手机号已注册");
        }
        User user = new User();
        user.setPhone(phone);
        user.setPassword(passwordEncoder.encode(password));
        user.setName(StrUtil.isBlank(name) ? "用户" + phone.substring(7) : name);
        user.setRole(StrUtil.isBlank(role) ? "PATIENT" : role);
        user.setStatus(1);
        userMapper.insert(user);
        log.info("用户注册成功: phone={}, role={}", phone, user.getRole());
        return R.ok();
    }

    @Override
    public R<Map<String, String>> login(String phone, String password) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getPhone, phone));
        if (user == null) {
            throw new BusinessException("手机号未注册");
        }
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException("密码错误");
        }
        String accessToken = JwtUtil.createAccessToken(user.getId(), user.getPhone(), user.getRole(), jwtSecret);
        String refreshToken = JwtUtil.createRefreshToken(user.getId(), jwtSecret);
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("accessToken", accessToken);
        tokenMap.put("refreshToken", refreshToken);
        tokenMap.put("role", user.getRole());
        tokenMap.put("name", user.getName());
        return R.ok(tokenMap);
    }
}
