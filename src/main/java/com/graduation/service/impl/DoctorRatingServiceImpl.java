package com.graduation.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduation.entity.DoctorRating;
import com.graduation.mapper.DoctorRatingMapper;
import com.graduation.service.DoctorRatingService;
import org.springframework.stereotype.Service;
@Service
public class DoctorRatingServiceImpl extends ServiceImpl<DoctorRatingMapper, DoctorRating> implements DoctorRatingService {}

