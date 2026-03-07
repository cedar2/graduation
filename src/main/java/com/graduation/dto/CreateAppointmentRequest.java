package com.graduation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateAppointmentRequest {
    @NotNull(message = "patientId不能为空")
    private Long patientId;

    @NotNull(message = "scheduleId不能为空")
    private Long scheduleId;
}

