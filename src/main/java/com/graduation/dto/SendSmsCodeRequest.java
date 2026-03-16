package com.graduation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SendSmsCodeRequest {

    @NotBlank(message = "phone不能为空")
    @Pattern(regexp = "^1\\d{10}$", message = "phone格式不正确")
    private String phone;

    @NotBlank(message = "captchaKey不能为空")
    private String captchaKey;

    @NotBlank(message = "captchaCode不能为空")
    private String captchaCode;
}

