package com.graduation.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthMeResponse {
    private Long userId;
    private Long patientId;
    private String phone;
    private String role;
    private Integer status;
    private Boolean profileCompleted;
}

