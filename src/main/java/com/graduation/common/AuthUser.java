package com.graduation.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthUser {
    private Long userId;
    private String phone;
    private String role;
    private Long deptId;
}

