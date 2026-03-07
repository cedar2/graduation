package com.graduation.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduation.entity.Appointment;
import com.graduation.mapper.AppointmentMapper;
import com.graduation.service.AppointmentService;
import org.springframework.stereotype.Service;
@Service
public class AppointmentServiceImpl extends ServiceImpl<AppointmentMapper, Appointment> implements AppointmentService {}

