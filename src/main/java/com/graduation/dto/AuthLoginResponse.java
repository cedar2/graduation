package com.graduation.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthLoginResponse {
    private String accessToken;
    private String tokenType;
    private Long expiresIn;
    private Long userId;
    private Long patientId;
    private String role;
    private String phone;
    private Boolean newUser;
    private Boolean profileCompleted;
}

