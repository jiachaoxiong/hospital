package com.hospital.auth.service;

import com.hospital.common.R;
import java.util.Map;

/**
 * 认证服务接口
 */
public interface AuthService {
    /** 发送短信验证码 */
    void sendCode(String phone);
    /** 用户注册 */
    R<Map<String, String>> register(String phone, String password, String name, String role);
    /** 手机号+密码登录 */
    R<Map<String, String>> login(String phone, String password);
}
