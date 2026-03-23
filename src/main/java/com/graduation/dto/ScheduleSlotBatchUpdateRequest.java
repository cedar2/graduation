package com.graduation.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class ScheduleSlotBatchUpdateRequest {
    private List<Long> ids;
    private Long doctorId;
    private Long deptId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    private String timeSlot;
    private String currentStatus;

    @Min(value = 1, message = "capacity必须>=1")
    private Integer capacity;

    @DecimalMin(value = "0.0", inclusive = false, message = "fee必须>0")
    private BigDecimal fee;

    private String status;
}

