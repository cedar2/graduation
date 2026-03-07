package com.graduation.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduation.entity.ShiftRequest;
import com.graduation.mapper.ShiftRequestMapper;
import com.graduation.service.ShiftRequestService;
import org.springframework.stereotype.Service;
@Service
public class ShiftRequestServiceImpl extends ServiceImpl<ShiftRequestMapper, ShiftRequest> implements ShiftRequestService {}

