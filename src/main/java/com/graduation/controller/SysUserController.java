package com.graduation.controller;

import com.graduation.common.ApiResponse;
import com.graduation.dto.AuthCaptchaResponse;
import com.graduation.dto.AuthLoginRequest;
import com.graduation.dto.AuthLoginResponse;
import com.graduation.dto.AuthMeResponse;
import com.graduation.dto.SendSmsCodeRequest;
import com.graduation.entity.SysUser;
import com.graduation.service.AuthService;
import com.graduation.service.SysUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SysUserController {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private AuthService authService;
    @GetMapping("/sys-user")
    public ApiResponse<List<SysUser>> list() { return ApiResponse.success(sysUserService.list()); }
    @GetMapping("/sys-user/{id}")
    public ApiResponse<SysUser> getById(@PathVariable Long id) { return ApiResponse.success(sysUserService.getById(id)); }
    @PostMapping("/sys-user")
    public ApiResponse<Boolean> save(@RequestBody SysUser user) { return ApiResponse.success(sysUserService.save(user)); }
    @PutMapping("/sys-user")
    public ApiResponse<Boolean> update(@RequestBody SysUser user) { return ApiResponse.success(sysUserService.updateById(user)); }
    @DeleteMapping("/sys-user/{id}")
    public ApiResponse<Boolean> remove(@PathVariable Long id) { return ApiResponse.success(sysUserService.removeById(id)); }
    @GetMapping("/auth/captcha")
    public ApiResponse<AuthCaptchaResponse> captcha() { return ApiResponse.success(authService.generateCaptcha()); }
    @PostMapping("/auth/sms-code")
    public ApiResponse<String> sendSmsCode(@Valid @RequestBody SendSmsCodeRequest request) {
        authService.sendSmsCode(request);
        return ApiResponse.success("验证码已发送");
    }
    @PostMapping("/auth/login")
    public ApiResponse<AuthLoginResponse> login(@Valid @RequestBody AuthLoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }
    @GetMapping("/auth/me")
    public ApiResponse<AuthMeResponse> me() { return ApiResponse.success(authService.me()); }
}
