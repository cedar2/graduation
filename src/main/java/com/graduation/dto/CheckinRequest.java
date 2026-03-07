package com.graduation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CheckinRequest {
    @NotBlank(message = "phone不能为空")
    private String phone;

    @NotBlank(message = "checkinCode不能为空")
    private String checkinCode;
}

