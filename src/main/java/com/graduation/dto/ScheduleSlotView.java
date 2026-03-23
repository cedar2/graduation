package com.graduation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleSlotView {
    private Long id;
    private Long doctorId;
    private Long deptId;
    private LocalDate visitDate;
    private String timeSlot;
    private String timeSlotName;
    private Integer capacity;
    private Integer remaining;
    private BigDecimal fee;
    private String status;
    private String statusName;
    private LocalDateTime openTime;
    private LocalDateTime stopTime;
}

