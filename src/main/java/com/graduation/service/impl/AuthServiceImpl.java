package com.graduation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.graduation.common.AuthUser;
import com.graduation.config.AuthContextHolder;
import com.graduation.config.JwtTokenProvider;
import com.graduation.dto.AuthCaptchaResponse;
import com.graduation.dto.AuthLoginRequest;
import com.graduation.dto.AuthLoginResponse;
import com.graduation.dto.AuthMeResponse;
import com.graduation.dto.SendSmsCodeRequest;
import com.graduation.entity.PatientProfile;
import com.graduation.entity.SysUser;
import com.graduation.mapper.PatientProfileMapper;
import com.graduation.mapper.SysUserMapper;
import com.graduation.service.AuthService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final DefaultKaptcha defaultKaptcha;
    private final JwtTokenProvider jwtTokenProvider;
    private final SysUserMapper sysUserMapper;
    private final PatientProfileMapper patientProfileMapper;

    private final ConcurrentHashMap<String, CaptchaItem> captchaStore = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, SmsCodeItem> smsCodeStore = new ConcurrentHashMap<>();

    @Value("${app.auth.captcha-expire-seconds:300}")
    private long captchaExpireSeconds;

    @Value("${app.auth.sms-code-expire-seconds:300}")
    private long smsCodeExpireSeconds;

    public AuthServiceImpl(DefaultKaptcha defaultKaptcha,
                           JwtTokenProvider jwtTokenProvider,
                           SysUserMapper sysUserMapper,
                           PatientProfileMapper patientProfileMapper) {
        this.defaultKaptcha = defaultKaptcha;
        this.jwtTokenProvider = jwtTokenProvider;
        this.sysUserMapper = sysUserMapper;
        this.patientProfileMapper = patientProfileMapper;
    }

    @PostConstruct
    public void init() {
        log.info("AuthService initialized: captchaTTL={}s, smsTTL={}s", captchaExpireSeconds, smsCodeExpireSeconds);
    }

    @Override
    public AuthCaptchaResponse generateCaptcha() {
        String captchaText = defaultKaptcha.createText();
        BufferedImage image = defaultKaptcha.createImage(captchaText);

        String key = UUID.randomUUID().toString().replace("-", "");
        long expireAt = Instant.now().plusSeconds(captchaExpireSeconds).getEpochSecond();
        captchaStore.put(key, new CaptchaItem(captchaText.toUpperCase(), expireAt));

        return AuthCaptchaResponse.builder()
                .captchaKey(key)
                .expireAtEpochSecond(expireAt)
                .imageBase64(toBase64(image))
                .build();
    }

    @Override
    public void sendSmsCode(SendSmsCodeRequest request) {
        CaptchaItem item = captchaStore.get(request.getCaptchaKey());
        if (item == null || item.expired()) {
            throw new IllegalArgumentException("图形验证码已过期，请刷新后重试");
        }
        if (!item.code.equalsIgnoreCase(request.getCaptchaCode())) {
            throw new IllegalArgumentException("图形验证码错误");
        }

        captchaStore.remove(request.getCaptchaKey());
        String smsCode = String.format("%06d", ThreadLocalRandom.current().nextInt(0, 1_000_000));
        long expireAt = Instant.now().plusSeconds(smsCodeExpireSeconds).getEpochSecond();
        smsCodeStore.put(request.getPhone(), new SmsCodeItem(smsCode, expireAt));

        log.info("[SMS-MOCK] phone={}, code={}", request.getPhone(), smsCode);
        if ("15770880133".equals(request.getPhone())) {
            log.info("[SMS-MOCK-PRIORITY] 演示手机号已发送验证码: phone={}, code={}", request.getPhone(), smsCode);
        }
    }

    @Override
    public AuthLoginResponse login(AuthLoginRequest request) {
        SmsCodeItem smsCodeItem = smsCodeStore.get(request.getPhone());
        if (smsCodeItem == null || smsCodeItem.expired()) {
            throw new IllegalArgumentException("短信验证码已过期，请重新获取");
        }
        if (!smsCodeItem.code.equals(request.getSmsCode())) {
            throw new IllegalArgumentException("短信验证码错误");
        }
        smsCodeStore.remove(request.getPhone());

        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getPhone, request.getPhone())
                .last("LIMIT 1"));

        boolean newUser = false;
        if (user == null) {
            user = registerPatientUser(request.getPhone());
            newUser = true;
        }

        user.setLastLoginAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        sysUserMapper.updateById(user);

        PatientProfile profile = patientProfileMapper.selectOne(new LambdaQueryWrapper<PatientProfile>()
                .eq(PatientProfile::getUserId, user.getId())
                .last("LIMIT 1"));

        AuthUser authUser = AuthUser.builder()
                .userId(user.getId())
                .phone(user.getPhone())
                .role(user.getRole())
                .deptId(user.getAdminDeptId())
                .build();

        return AuthLoginResponse.builder()
                .accessToken(jwtTokenProvider.generateToken(authUser))
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getJwtExpireSeconds())
                .userId(user.getId())
                .patientId(profile == null ? null : profile.getId())
                .role(user.getRole())
                .phone(user.getPhone())
                .newUser(newUser)
                .profileCompleted(profile != null && isProfileCompleted(profile))
                .build();
    }

    @Override
    public AuthMeResponse me() {
        AuthUser authUser = AuthContextHolder.get();
        if (authUser == null) {
            throw new IllegalStateException("未登录");
        }

        SysUser user = sysUserMapper.selectById(authUser.getUserId());
        if (user == null) {
            throw new IllegalStateException("用户不存在");
        }

        PatientProfile profile = patientProfileMapper.selectOne(new LambdaQueryWrapper<PatientProfile>()
                .eq(PatientProfile::getUserId, user.getId())
                .last("LIMIT 1"));

        return AuthMeResponse.builder()
                .userId(user.getId())
                .patientId(profile == null ? null : profile.getId())
                .phone(user.getPhone())
                .role(user.getRole())
                .status(user.getStatus())
                .profileCompleted(profile != null && isProfileCompleted(profile))
                .build();
    }

    private SysUser registerPatientUser(String phone) {
        LocalDateTime now = LocalDateTime.now();
        SysUser candidate = SysUser.builder()
                .phone(phone)
                .role("PATIENT")
                .status(1)
                .createdAt(now)
                .updatedAt(now)
                .build();

        try {
            sysUserMapper.insert(candidate);
            return candidate;
        } catch (DuplicateKeyException ex) {
            SysUser exists = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                    .eq(SysUser::getPhone, phone)
                    .last("LIMIT 1"));
            if (exists != null) {
                return exists;
            }
            throw ex;
        }
    }

    private boolean isProfileCompleted(PatientProfile profile) {
        return notBlank(profile.getRealName())
                && notBlank(profile.getGender())
                && profile.getBirthDate() != null
                && notBlank(profile.getIdCardNo());
    }

    private boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }

    private String toBase64(BufferedImage image) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", outputStream);
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (IOException ex) {
            throw new IllegalStateException("生成图形验证码失败", ex);
        }
    }

    private record CaptchaItem(String code, long expireAtEpochSecond) {
        private boolean expired() {
            return Instant.now().getEpochSecond() > expireAtEpochSecond;
        }
    }

    private record SmsCodeItem(String code, long expireAtEpochSecond) {
        private boolean expired() {
            return Instant.now().getEpochSecond() > expireAtEpochSecond;
        }
    }
}
