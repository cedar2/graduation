package com.graduation.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class CreateAppointmentResponse {
    private Long appointmentId;
    private String appointmentNo;
    private String status;
    private BigDecimal fee;
    private LocalDate visitDate;
    private String timeSlot;
    private String checkinCode;
}

