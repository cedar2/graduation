package com.graduation.controller;

import com.graduation.common.ApiResponse;
import com.graduation.dto.CheckinRequest;
import com.graduation.dto.CheckinResponse;
import com.graduation.dto.CreateAppointmentRequest;
import com.graduation.dto.CreateAppointmentResponse;
import com.graduation.service.AppointmentService;
import com.graduation.service.CheckinQueueService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class BookingController {

    private final AppointmentService appointmentService;
    private final CheckinQueueService checkinQueueService;

    public BookingController(AppointmentService appointmentService, CheckinQueueService checkinQueueService) {
        this.appointmentService = appointmentService;
        this.checkinQueueService = checkinQueueService;
    }

    @PostMapping("/appointments")
    public ApiResponse<CreateAppointmentResponse> createAppointment(@Valid @RequestBody CreateAppointmentRequest request) {
        return ApiResponse.success(appointmentService.createAppointment(request));
    }

    @PostMapping("/checkin")
    public ApiResponse<CheckinResponse> checkin(@Valid @RequestBody CheckinRequest request) {
        return ApiResponse.success(checkinQueueService.checkin(request));
    }
}

