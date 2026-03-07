package com.graduation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.graduation.dto.CreateAppointmentRequest;
import com.graduation.dto.CreateAppointmentResponse;
import com.graduation.entity.Appointment;

public interface AppointmentService extends IService<Appointment> {
    CreateAppointmentResponse createAppointment(CreateAppointmentRequest request);
}
