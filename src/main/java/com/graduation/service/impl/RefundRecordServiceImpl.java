package com.graduation.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduation.entity.RefundRecord;
import com.graduation.mapper.RefundRecordMapper;
import com.graduation.service.RefundRecordService;
import org.springframework.stereotype.Service;
@Service
public class RefundRecordServiceImpl extends ServiceImpl<RefundRecordMapper, RefundRecord> implements RefundRecordService {}

