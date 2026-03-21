package com.graduation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.graduation.dto.DoctorListItemResponse;
import com.graduation.entity.DoctorProfile;

import java.util.List;

public interface DoctorProfileService extends IService<DoctorProfile> {
    List<DoctorListItemResponse> listForDisplay(Long deptId, String keyword, String status, String doctorType);
}
