package com.graduation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.graduation.entity.DictItem;

import java.util.List;
import java.util.Map;

public interface DictItemService extends IService<DictItem> {
    List<DictItem> listByTypeCode(String typeCode, boolean onlyEnabled);

    Map<String, List<DictItem>> listByTypeCodes(List<String> typeCodes, boolean onlyEnabled);
}

