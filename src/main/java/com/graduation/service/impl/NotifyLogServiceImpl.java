package com.graduation.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduation.entity.NotifyLog;
import com.graduation.mapper.NotifyLogMapper;
import com.graduation.service.NotifyLogService;
import org.springframework.stereotype.Service;
@Service
public class NotifyLogServiceImpl extends ServiceImpl<NotifyLogMapper, NotifyLog> implements NotifyLogService {}

