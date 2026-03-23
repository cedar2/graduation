package com.graduation.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ScheduleSlotImportRowRequest {
    @NotNull(message = "doctorId不能为空")
    private Long doctorId;

    @NotNull(message = "visitDate不能为空")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate visitDate;

    @NotBlank(message = "timeSlot不能为空")
    private String timeSlot;

    @NotNull(message = "capacity不能为空")
    @Min(value = 1, message = "capacity必须>=1")
    private Integer capacity;

    @NotNull(message = "fee不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "fee必须>0")
    private BigDecimal fee;

    private String status;
}

