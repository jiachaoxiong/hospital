package com.hospital.auth.dto;

import lombok.Data;

/** 注册请求 */
@Data
public class RegisterDTO {
    private String phone;
    private String password;
    private String name;
    private String role;
}
