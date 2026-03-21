package com.graduation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduation.dto.DoctorListItemResponse;
import com.graduation.entity.Department;
import com.graduation.entity.DictItem;
import com.graduation.entity.DoctorProfile;
import com.graduation.mapper.DepartmentMapper;
import com.graduation.mapper.DictItemMapper;
import com.graduation.mapper.DoctorProfileMapper;
import com.graduation.service.DoctorProfileService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DoctorProfileServiceImpl extends ServiceImpl<DoctorProfileMapper, DoctorProfile> implements DoctorProfileService {

    private final DepartmentMapper departmentMapper;
    private final DictItemMapper dictItemMapper;

    public DoctorProfileServiceImpl(DepartmentMapper departmentMapper, DictItemMapper dictItemMapper) {
        this.departmentMapper = departmentMapper;
        this.dictItemMapper = dictItemMapper;
    }

    @Override
    public List<DoctorListItemResponse> listForDisplay(Long deptId, String keyword, String status, String doctorType) {
        LambdaQueryWrapper<DoctorProfile> wrapper = new LambdaQueryWrapper<DoctorProfile>()
                .orderByDesc(DoctorProfile::getAvgRating, DoctorProfile::getRatingCount)
                .orderByAsc(DoctorProfile::getId);

        if (deptId != null) {
            wrapper.eq(DoctorProfile::getDeptId, deptId);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(DoctorProfile::getStatus, status);
        }
        if (doctorType != null && !doctorType.isBlank()) {
            wrapper.eq(DoctorProfile::getDoctorType, doctorType);
        }
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(DoctorProfile::getRealName, keyword);
        }

        List<DoctorProfile> doctors = baseMapper.selectList(wrapper);
        if (doctors.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, String> deptNameMap = buildDeptNameMap(doctors);
        Map<String, String> dictNameMap = buildDictNameMap(doctors);

        return doctors.stream()
                .map(doctor -> DoctorListItemResponse.builder()
                        .id(doctor.getId())
                        .userId(doctor.getUserId())
                        .deptId(doctor.getDeptId())
                        .deptName(deptNameMap.get(doctor.getDeptId()))
                        .realName(doctor.getRealName())
                        .title(doctor.getTitle())
                        .titleName(dictNameMap.get(doctor.getTitle()))
                        .doctorType(doctor.getDoctorType())
                        .doctorTypeName(dictNameMap.get(doctor.getDoctorType()))
                        .status(doctor.getStatus())
                        .statusName(dictNameMap.get(doctor.getStatus()))
                        .intro(doctor.getIntro())
                        .avgRating(doctor.getAvgRating())
                        .ratingCount(doctor.getRatingCount())
                        .build())
                .collect(Collectors.toList());
    }

    private Map<Long, String> buildDeptNameMap(List<DoctorProfile> doctors) {
        Set<Long> deptIds = doctors.stream()
                .map(DoctorProfile::getDeptId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (deptIds.isEmpty()) {
            return Collections.emptyMap();
        }

        return departmentMapper.selectBatchIds(deptIds).stream()
                .collect(Collectors.toMap(Department::getId, Department::getName, (a, b) -> a));
    }

    private Map<String, String> buildDictNameMap(List<DoctorProfile> doctors) {
        Set<String> codes = doctors.stream()
                .flatMap(doctor -> java.util.stream.Stream.of(doctor.getTitle(), doctor.getDoctorType(), doctor.getStatus()))
                .filter(code -> code != null && !code.isBlank())
                .collect(Collectors.toSet());

        if (codes.isEmpty()) {
            return Collections.emptyMap();
        }

        List<DictItem> items = dictItemMapper.selectList(new LambdaQueryWrapper<DictItem>()
                .in(DictItem::getItemCode, codes));

        Map<String, String> nameMap = new HashMap<>(items.size());
        for (DictItem item : items) {
            nameMap.put(item.getItemCode(), item.getItemName());
        }
        return nameMap;
    }
}
