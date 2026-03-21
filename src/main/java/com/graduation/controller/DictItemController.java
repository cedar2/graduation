package com.graduation.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.graduation.common.ApiResponse;
import com.graduation.entity.DictItem;
import com.graduation.service.DictItemService;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dict-item")
public class DictItemController {

    private final DictItemService dictItemService;

    public DictItemController(DictItemService dictItemService) {
        this.dictItemService = dictItemService;
    }

    @GetMapping
    public ApiResponse<List<DictItem>> list(@RequestParam(required = false) String typeCode,
                                            @RequestParam(required = false) Integer status,
                                            @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<DictItem> wrapper = new LambdaQueryWrapper<DictItem>()
                .orderByAsc(DictItem::getTypeCode, DictItem::getSortNo, DictItem::getId);
        if (typeCode != null && !typeCode.isBlank()) {
            wrapper.eq(DictItem::getTypeCode, typeCode);
        }
        if (status != null) {
            wrapper.eq(DictItem::getStatus, status);
        }
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(DictItem::getItemCode, keyword).or().like(DictItem::getItemName, keyword));
        }
        return ApiResponse.success(dictItemService.list(wrapper));
    }

    @GetMapping("/by-type/{typeCode}")
    public ApiResponse<List<DictItem>> listByType(@PathVariable String typeCode,
                                                   @RequestParam(defaultValue = "true") boolean onlyEnabled) {
        return ApiResponse.success(dictItemService.listByTypeCode(typeCode, onlyEnabled));
    }

    @GetMapping("/batch")
    public ApiResponse<Map<String, List<DictItem>>> listByTypes(@RequestParam String typeCodes,
                                                                @RequestParam(defaultValue = "true") boolean onlyEnabled) {
        List<String> codes = Arrays.stream(typeCodes.split(","))
                .map(String::trim)
                .filter(code -> !code.isBlank())
                .distinct()
                .collect(Collectors.toList());
        return ApiResponse.success(dictItemService.listByTypeCodes(codes, onlyEnabled));
    }

    @GetMapping("/{id}")
    public ApiResponse<DictItem> getById(@PathVariable Long id) {
        return ApiResponse.success(dictItemService.getById(id));
    }

    @PostMapping
    public ApiResponse<Boolean> save(@RequestBody DictItem dictItem) {
        return ApiResponse.success(dictItemService.save(dictItem));
    }

    @PutMapping
    public ApiResponse<Boolean> update(@RequestBody DictItem dictItem) {
        return ApiResponse.success(dictItemService.updateById(dictItem));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> remove(@PathVariable Long id) {
        return ApiResponse.success(dictItemService.removeById(id));
    }
}

