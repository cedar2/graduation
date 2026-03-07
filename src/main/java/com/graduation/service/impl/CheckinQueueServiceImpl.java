package com.graduation.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduation.entity.CheckinQueue;
import com.graduation.mapper.CheckinQueueMapper;
import com.graduation.service.CheckinQueueService;
import org.springframework.stereotype.Service;
@Service
public class CheckinQueueServiceImpl extends ServiceImpl<CheckinQueueMapper, CheckinQueue> implements CheckinQueueService {}

