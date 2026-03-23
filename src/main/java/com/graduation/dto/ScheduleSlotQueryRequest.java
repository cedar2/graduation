package com.graduation.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record ScheduleSlotQueryRequest(
        Long deptId,
        Long doctorId,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        String timeSlot,
        String status,
        Integer minRemaining,
        Long pageNo,
        Long pageSize,
        Boolean paged
) {
}

