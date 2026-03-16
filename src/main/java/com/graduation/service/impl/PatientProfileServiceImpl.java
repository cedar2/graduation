package com.graduation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduation.dto.PatientProfileCompleteRequest;
import com.graduation.entity.PatientProfile;
import com.graduation.mapper.PatientProfileMapper;
import com.graduation.service.PatientProfileService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PatientProfileServiceImpl extends ServiceImpl<PatientProfileMapper, PatientProfile> implements PatientProfileService {

    @Override
    public PatientProfile getByUserId(Long userId) {
        return baseMapper.selectOne(new LambdaQueryWrapper<PatientProfile>()
                .eq(PatientProfile::getUserId, userId)
                .last("LIMIT 1"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PatientProfile completeProfile(Long userId, PatientProfileCompleteRequest request) {
        LocalDateTime now = LocalDateTime.now();
        PatientProfile profile = getByUserId(userId);

        if (profile == null) {
            PatientProfile candidate = PatientProfile.builder()
                    .userId(userId)
                    .realName(request.getRealName())
                    .gender(request.getGender())
                    .birthDate(request.getBirthDate())
                    .idCardNo(request.getIdCardNo())
                    .province(blankToNull(request.getProvince()))
                    .city(blankToNull(request.getCity()))
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            try {
                baseMapper.insert(candidate);
                return candidate;
            } catch (DuplicateKeyException ex) {
                profile = getByUserId(userId);
                if (profile == null) {
                    throw ex;
                }
            }
        }

        profile.setRealName(request.getRealName());
        profile.setGender(request.getGender());
        profile.setBirthDate(request.getBirthDate());
        profile.setIdCardNo(request.getIdCardNo());
        profile.setProvince(blankToNull(request.getProvince()));
        profile.setCity(blankToNull(request.getCity()));
        profile.setUpdatedAt(now);
        baseMapper.updateById(profile);
        return profile;
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
