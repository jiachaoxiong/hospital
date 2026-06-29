package com.hospital.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** 注册请求 — 带参数校验注解 */
@Data
public class RegisterDTO {
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @NotBlank(message = "密码不能为空")
    @Size(min = 8, message = "密码需至少8位")
    private String password;

    private String name;
    private String role;

    @NotBlank(message = "验证码不能为空")
    private String code;
}
