package com.graduation.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.graduation.common.ApiResponse;
import com.graduation.entity.DictType;
import com.graduation.service.DictTypeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dict-type")
public class DictTypeController {

    private final DictTypeService dictTypeService;

    public DictTypeController(DictTypeService dictTypeService) {
        this.dictTypeService = dictTypeService;
    }

    @GetMapping
    public ApiResponse<List<DictType>> list(@RequestParam(required = false) Integer status,
                                            @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<DictType> wrapper = new LambdaQueryWrapper<DictType>()
                .orderByAsc(DictType::getTypeCode);
        if (status != null) {
            wrapper.eq(DictType::getStatus, status);
        }
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(DictType::getTypeCode, keyword).or().like(DictType::getTypeName, keyword));
        }
        return ApiResponse.success(dictTypeService.list(wrapper));
    }

    @GetMapping("/{id}")
    public ApiResponse<DictType> getById(@PathVariable Long id) {
        return ApiResponse.success(dictTypeService.getById(id));
    }

    @PostMapping
    public ApiResponse<Boolean> save(@RequestBody DictType dictType) {
        return ApiResponse.success(dictTypeService.save(dictType));
    }

    @PutMapping
    public ApiResponse<Boolean> update(@RequestBody DictType dictType) {
        return ApiResponse.success(dictTypeService.updateById(dictType));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> remove(@PathVariable Long id) {
        return ApiResponse.success(dictTypeService.removeById(id));
    }
}

