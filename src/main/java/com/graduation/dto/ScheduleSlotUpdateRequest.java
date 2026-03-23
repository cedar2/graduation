package com.graduation.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ScheduleSlotUpdateRequest {
    @Min(value = 1, message = "capacity必须>=1")
    private Integer capacity;

    @DecimalMin(value = "0.0", inclusive = false, message = "fee必须>0")
    private BigDecimal fee;

    private String status;
}

