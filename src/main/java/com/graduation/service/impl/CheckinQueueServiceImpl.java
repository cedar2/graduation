package com.graduation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduation.config.BizClock;
import com.graduation.dto.CheckinRequest;
import com.graduation.dto.CheckinResponse;
import com.graduation.entity.Appointment;
import com.graduation.entity.CheckinQueue;
import com.graduation.entity.PatientProfile;
import com.graduation.entity.SysUser;
import com.graduation.mapper.AppointmentMapper;
import com.graduation.mapper.CheckinQueueMapper;
import com.graduation.mapper.PatientProfileMapper;
import com.graduation.mapper.SysUserMapper;
import com.graduation.service.CheckinQueueService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class CheckinQueueServiceImpl extends ServiceImpl<CheckinQueueMapper, CheckinQueue> implements CheckinQueueService {

    private final BizClock bizClock;
    private final AppointmentMapper appointmentMapper;
    private final SysUserMapper sysUserMapper;
    private final PatientProfileMapper patientProfileMapper;

    public CheckinQueueServiceImpl(BizClock bizClock,
                                   AppointmentMapper appointmentMapper,
                                   SysUserMapper sysUserMapper,
                                   PatientProfileMapper patientProfileMapper) {
        this.bizClock = bizClock;
        this.appointmentMapper = appointmentMapper;
        this.sysUserMapper = sysUserMapper;
        this.patientProfileMapper = patientProfileMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CheckinResponse checkin(CheckinRequest request) {
        LocalDate bizDate = bizClock.today();

        SysUser sysUser = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getPhone, request.getPhone())
                .eq(SysUser::getRole, "PATIENT"));
        if (sysUser == null) {
            throw new IllegalArgumentException("患者账号不存在");
        }

        PatientProfile patientProfile = patientProfileMapper.selectOne(new LambdaQueryWrapper<PatientProfile>()
                .eq(PatientProfile::getUserId, sysUser.getId()));
        if (patientProfile == null) {
            throw new IllegalArgumentException("患者档案不存在");
        }

        Appointment appointment = appointmentMapper.findByCheckinCodeAndVisitDate(request.getCheckinCode(), bizDate);
        if (appointment == null) {
            throw new IllegalArgumentException("签到码无效或非当天预约");
        }

        if (!patientProfile.getId().equals(appointment.getPatientId())) {
            throw new IllegalArgumentException("预约不属于当前患者");
        }

        if ("CHECKED_IN".equals(appointment.getStatus())) {
            CheckinQueue existingQueue = baseMapper.findByAppointmentId(appointment.getId());
            if (existingQueue == null) {
                throw new IllegalStateException("签到记录缺失");
            }
            return CheckinResponse.builder()
                    .appointmentId(appointment.getId())
                    .status("CHECKED_IN")
                    .queueNo(existingQueue.getQueueNo())
                    .build();
        }

        if (!"PAID".equals(appointment.getStatus())) {
            throw new IllegalArgumentException("仅已支付预约可签到");
        }

        String lockKey = String.format("queue:%d:%s:%s", appointment.getDoctorId(), bizDate, appointment.getTimeSlot());
        Integer lockAcquired = baseMapper.acquireLock(lockKey, 5);
        if (lockAcquired == null || lockAcquired != 1) {
            throw new IllegalStateException("签到人数较多，请稍后重试");
        }

        try {
            Integer maxQueueNo = baseMapper.selectMaxQueueNo(appointment.getDoctorId(), bizDate, appointment.getTimeSlot());
            int nextQueueNo = maxQueueNo == null ? 1 : maxQueueNo + 1;
            LocalDateTime now = bizClock.now();

            int updatedRows = appointmentMapper.markCheckedInIfPaid(appointment.getId(), now);
            if (updatedRows == 0) {
                Appointment latest = appointmentMapper.selectById(appointment.getId());
                if (latest != null && "CHECKED_IN".equals(latest.getStatus())) {
                    CheckinQueue existingQueue = baseMapper.findByAppointmentId(appointment.getId());
                    if (existingQueue == null) {
                        throw new IllegalStateException("签到记录缺失");
                    }
                    return CheckinResponse.builder()
                            .appointmentId(latest.getId())
                            .status("CHECKED_IN")
                            .queueNo(existingQueue.getQueueNo())
                            .build();
                }
                throw new IllegalArgumentException("当前预约状态不可签到");
            }

            CheckinQueue queue = CheckinQueue.builder()
                    .appointmentId(appointment.getId())
                    .doctorId(appointment.getDoctorId())
                    .deptId(appointment.getDeptId())
                    .visitDate(bizDate)
                    .timeSlot(appointment.getTimeSlot())
                    .queueNo(nextQueueNo)
                    .status("WAITING")
                    .checkedInAt(now)
                    .build();
            baseMapper.insert(queue);

            return CheckinResponse.builder()
                    .appointmentId(appointment.getId())
                    .status("CHECKED_IN")
                    .queueNo(nextQueueNo)
                    .build();
        } finally {
            baseMapper.releaseLock(lockKey);
        }
    }
}
