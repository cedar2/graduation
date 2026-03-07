package com.graduation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduation.dto.CreateAppointmentRequest;
import com.graduation.dto.CreateAppointmentResponse;
import com.graduation.entity.Appointment;
import com.graduation.entity.ScheduleSlot;
import com.graduation.mapper.AppointmentMapper;
import com.graduation.mapper.ScheduleSlotMapper;
import com.graduation.service.AppointmentService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AppointmentServiceImpl extends ServiceImpl<AppointmentMapper, Appointment> implements AppointmentService {

    private static final DateTimeFormatter NO_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final int MAX_CHECKIN_CODE_RETRY = 10;

    private final ScheduleSlotMapper scheduleSlotMapper;

    public AppointmentServiceImpl(ScheduleSlotMapper scheduleSlotMapper) {
        this.scheduleSlotMapper = scheduleSlotMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CreateAppointmentResponse createAppointment(CreateAppointmentRequest request) {
        ScheduleSlot slot = scheduleSlotMapper.selectById(request.getScheduleId());
        if (slot == null) {
            throw new IllegalArgumentException("排班不存在");
        }

        int conflictCount = baseMapper.countActiveConflict(request.getPatientId(), slot.getVisitDate(), slot.getTimeSlot());
        if (conflictCount > 0) {
            throw new IllegalArgumentException("同一患者同一天同一时段已存在有效预约");
        }

        int affectedRows = scheduleSlotMapper.decrementRemainingIfAvailable(request.getScheduleId());
        if (affectedRows == 0) {
            throw new IllegalArgumentException("号源不足或已关闭");
        }

        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < MAX_CHECKIN_CODE_RETRY; i++) {
            Appointment appointment = Appointment.builder()
                    .appointmentNo(generateAppointmentNo(now))
                    .patientId(request.getPatientId())
                    .doctorId(slot.getDoctorId())
                    .deptId(slot.getDeptId())
                    .scheduleId(slot.getId())
                    .visitDate(slot.getVisitDate())
                    .timeSlot(slot.getTimeSlot())
                    .fee(slot.getFee())
                    .status("UNPAID")
                    .checkinCode(generateCheckinCode())
                    .checkinCodeGeneratedAt(now)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();

            try {
                baseMapper.insert(appointment);
                return CreateAppointmentResponse.builder()
                        .appointmentId(appointment.getId())
                        .appointmentNo(appointment.getAppointmentNo())
                        .status(appointment.getStatus())
                        .fee(appointment.getFee())
                        .visitDate(appointment.getVisitDate())
                        .timeSlot(appointment.getTimeSlot())
                        .checkinCode(appointment.getCheckinCode())
                        .build();
            } catch (DuplicateKeyException ex) {
                // 依赖数据库唯一索引保障同日 checkin_code 唯一，冲突时重试。
            }
        }

        throw new IllegalStateException("签到码生成冲突，请重试");
    }

    private String generateAppointmentNo(LocalDateTime now) {
        return "AP" + now.format(NO_FORMATTER) + ThreadLocalRandom.current().nextInt(1000, 10000);
    }

    private String generateCheckinCode() {
        int value = ThreadLocalRandom.current().nextInt(0, 1_000_000);
        return String.format("%06d", value);
    }
}
