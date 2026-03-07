package com.graduation.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduation.entity.PaymentRecord;
import com.graduation.mapper.PaymentRecordMapper;
import com.graduation.service.PaymentRecordService;
import org.springframework.stereotype.Service;
@Service
public class PaymentRecordServiceImpl extends ServiceImpl<PaymentRecordMapper, PaymentRecord> implements PaymentRecordService {}

