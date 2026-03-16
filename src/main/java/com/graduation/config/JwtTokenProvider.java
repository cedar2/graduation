package com.graduation.config;

import com.graduation.common.AuthUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${app.auth.jwt-secret}")
    private String jwtSecret;

    @Value("${app.auth.jwt-expire-seconds:86400}")
    private long jwtExpireSeconds;

    public String generateToken(AuthUser authUser) {
        Instant now = Instant.now();
        Instant expireAt = now.plusSeconds(jwtExpireSeconds);
        return Jwts.builder()
                .setSubject(String.valueOf(authUser.getUserId()))
                .claim("userId", authUser.getUserId())
                .claim("role", authUser.getRole())
                .claim("phone", authUser.getPhone())
                .claim("deptId", authUser.getDeptId())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expireAt))
                .signWith(buildKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public AuthUser parseToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(buildKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        Long userId = claims.get("userId", Number.class).longValue();
        Number deptNumber = claims.get("deptId", Number.class);
        return AuthUser.builder()
                .userId(userId)
                .role(claims.get("role", String.class))
                .phone(claims.get("phone", String.class))
                .deptId(deptNumber == null ? null : deptNumber.longValue())
                .build();
    }

    public long getJwtExpireSeconds() {
        return jwtExpireSeconds;
    }

    private SecretKey buildKey() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = digest.digest(jwtSecret.getBytes(StandardCharsets.UTF_8));
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 algorithm unavailable", ex);
        }
    }
}

