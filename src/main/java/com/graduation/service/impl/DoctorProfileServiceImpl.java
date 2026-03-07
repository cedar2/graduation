package com.graduation.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduation.entity.DoctorProfile;
import com.graduation.mapper.DoctorProfileMapper;
import com.graduation.service.DoctorProfileService;
import org.springframework.stereotype.Service;
@Service
public class DoctorProfileServiceImpl extends ServiceImpl<DoctorProfileMapper, DoctorProfile> implements DoctorProfileService {}

