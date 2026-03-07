package com.graduation.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckinResponse {
    private Long appointmentId;
    private String status;
    private Integer queueNo;
}

