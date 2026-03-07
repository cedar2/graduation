package com.graduation.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduation.entity.QueueState;
import com.graduation.mapper.QueueStateMapper;
import com.graduation.service.QueueStateService;
import org.springframework.stereotype.Service;
@Service
public class QueueStateServiceImpl extends ServiceImpl<QueueStateMapper, QueueState> implements QueueStateService {}

