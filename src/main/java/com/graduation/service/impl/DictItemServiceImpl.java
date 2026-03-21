package com.graduation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduation.entity.DictItem;
import com.graduation.mapper.DictItemMapper;
import com.graduation.service.DictItemService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DictItemServiceImpl extends ServiceImpl<DictItemMapper, DictItem> implements DictItemService {

    @Override
    public List<DictItem> listByTypeCode(String typeCode, boolean onlyEnabled) {
        if (typeCode == null || typeCode.isBlank()) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<DictItem> wrapper = new LambdaQueryWrapper<DictItem>()
                .eq(DictItem::getTypeCode, typeCode)
                .orderByAsc(DictItem::getSortNo, DictItem::getId);
        if (onlyEnabled) {
            wrapper.eq(DictItem::getStatus, 1);
        }
        return baseMapper.selectList(wrapper);
    }

    @Override
    public Map<String, List<DictItem>> listByTypeCodes(List<String> typeCodes, boolean onlyEnabled) {
        if (typeCodes == null || typeCodes.isEmpty()) {
            return Collections.emptyMap();
        }

        LambdaQueryWrapper<DictItem> wrapper = new LambdaQueryWrapper<DictItem>()
                .in(DictItem::getTypeCode, typeCodes)
                .orderByAsc(DictItem::getTypeCode, DictItem::getSortNo, DictItem::getId);
        if (onlyEnabled) {
            wrapper.eq(DictItem::getStatus, 1);
        }

        return baseMapper.selectList(wrapper)
                .stream()
                .collect(Collectors.groupingBy(DictItem::getTypeCode));
    }
}

