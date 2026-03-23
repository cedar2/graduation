package com.graduation.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class ScheduleSlotGenerateRequest {
    private List<Long> doctorIds;

    @NotNull(message = "startDate不能为空")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @NotNull(message = "endDate不能为空")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    private List<String> timeSlots;

    @Min(value = 1, message = "defaultCapacity必须>=1")
    private Integer defaultCapacity;

    @Min(value = 1, message = "amCapacity必须>=1")
    private Integer amCapacity;

    @Min(value = 1, message = "pmCapacity必须>=1")
    private Integer pmCapacity;

    @DecimalMin(value = "0.0", inclusive = false, message = "defaultFee必须>0")
    private BigDecimal defaultFee;

    @DecimalMin(value = "0.0", inclusive = false, message = "amFee必须>0")
    private BigDecimal amFee;

    @DecimalMin(value = "0.0", inclusive = false, message = "pmFee必须>0")
    private BigDecimal pmFee;

    private String status;
}

