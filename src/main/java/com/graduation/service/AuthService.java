package com.graduation.service;

import com.graduation.dto.AuthCaptchaResponse;
import com.graduation.dto.AuthLoginRequest;
import com.graduation.dto.AuthLoginResponse;
import com.graduation.dto.AuthMeResponse;
import com.graduation.dto.SendSmsCodeRequest;

public interface AuthService {
    AuthCaptchaResponse generateCaptcha();

    void sendSmsCode(SendSmsCodeRequest request);

    AuthLoginResponse login(AuthLoginRequest request);

    AuthMeResponse me();
}

