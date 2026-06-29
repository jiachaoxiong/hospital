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

    @Value("${jwt.secret:hospital-local-dev-secret-2026-min-len-32}")
    private String jwtSecret;

    private static final String PHONE_REGEX = "^1[3-9]\\d{9}$";
    /** 密码强度：至少8位，必须同时包含字母和数字 */
    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$";

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
    public R<Map<String, String>> register(String phone, String password, String name, String role, String code) {
        // 基础校验
        if (StrUtil.isBlank(phone) || !phone.matches(PHONE_REGEX)) {
            throw new BusinessException("手机号格式不正确");
        }
        if (StrUtil.isBlank(code)) {
            throw new BusinessException("请输入短信验证码");
        }
        if (StrUtil.isBlank(password) || !password.matches(PASSWORD_REGEX)) {
            throw new BusinessException("密码需至少8位，且同时包含字母和数字");
        }

        // 短信验证码校验（防止绕过手机号验证注册）
        String cacheKey = "sms:code:" + phone;
        String cachedCode = redisTemplate.opsForValue().get(cacheKey);
        if (cachedCode == null) {
            throw new BusinessException("验证码已过期，请重新获取");
        }
        if (!cachedCode.equals(code)) {
            throw new BusinessException("验证码错误");
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

        // 注册成功后删除验证码，防止复用
        redisTemplate.delete(cacheKey);
        log.info("用户注册成功: phone={}, role={}", phone, user.getRole());
        return R.ok();
    }

    @Override
    public User getUserById(Long userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public void logout(String token) {
        if (StrUtil.isNotBlank(token)) {
            // 将Token加入Redis黑名单，过期时间与Token本身一致（24h）
            // 黑名单前缀 "token:blacklist:" 用于网关AuthFilter校验
            redisTemplate.opsForValue().set(
                    "token:blacklist:" + token, "1",
                    24, TimeUnit.HOURS);
            log.info("Token已加入黑名单");
        }
    }

    @Override
    public R<Map<String, String>> login(String phone, String password) {
        // 登录频率限制：同一手机号每分钟最多尝试 5 次（防暴力破解）
        String rateLimitKey = "login:rate:" + phone;
        String rateCount = redisTemplate.opsForValue().get(rateLimitKey);
        int attempts = rateCount != null ? Integer.parseInt(rateCount) : 0;
        if (attempts >= 5) {
            throw new BusinessException("登录尝试过于频繁，请1分钟后再试");
        }
        // 递增计数器（首次调用时设置过期时间）
        redisTemplate.opsForValue().increment(rateLimitKey);
        if (attempts == 0) {
            redisTemplate.expire(rateLimitKey, 1, TimeUnit.MINUTES);
        }

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

        // 登录成功后清除频率限制
        redisTemplate.delete(rateLimitKey);

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
