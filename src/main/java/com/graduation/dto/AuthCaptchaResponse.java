package com.graduation.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthCaptchaResponse {
    private String captchaKey;
    private String imageBase64;
    private Long expireAtEpochSecond;
}

