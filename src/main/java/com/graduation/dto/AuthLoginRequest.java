package com.graduation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AuthLoginRequest {

    @NotBlank(message = "phone不能为空")
    @Pattern(regexp = "^1\\d{10}$", message = "phone格式不正确")
    private String phone;

    @NotBlank(message = "smsCode不能为空")
    @Pattern(regexp = "^\\d{6}$", message = "smsCode必须是6位数字")
    private String smsCode;
}

