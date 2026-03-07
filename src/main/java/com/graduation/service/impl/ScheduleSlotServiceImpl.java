package com.graduation.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduation.entity.ScheduleSlot;
import com.graduation.mapper.ScheduleSlotMapper;
import com.graduation.service.ScheduleSlotService;
import org.springframework.stereotype.Service;
@Service
public class ScheduleSlotServiceImpl extends ServiceImpl<ScheduleSlotMapper, ScheduleSlot> implements ScheduleSlotService {}

