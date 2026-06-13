package com.hospital.auth.dto;

import lombok.Data;

/** 登录请求 */
@Data
public class LoginDTO {
    private String phone;
    private String password;
}
