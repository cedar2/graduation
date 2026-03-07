package com.graduation.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduation.entity.PatientProfile;
import com.graduation.mapper.PatientProfileMapper;
import com.graduation.service.PatientProfileService;
import org.springframework.stereotype.Service;
@Service
public class PatientProfileServiceImpl extends ServiceImpl<PatientProfileMapper, PatientProfile> implements PatientProfileService {}

