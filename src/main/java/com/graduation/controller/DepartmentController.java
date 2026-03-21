package com.graduation.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.graduation.common.ApiResponse;
import com.graduation.entity.Department;
import com.graduation.service.DepartmentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/department")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public ApiResponse<List<Department>> list(@RequestParam(required = false) Integer status,
                                              @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<Department>()
                .orderByAsc(Department::getId);
        if (status != null) {
            wrapper.eq(Department::getStatus, status);
        }
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(Department::getName, keyword).or().like(Department::getCode, keyword));
        }
        return ApiResponse.success(departmentService.list(wrapper));
    }

    @GetMapping("/{id}")
    public ApiResponse<Department> getById(@PathVariable Long id) {
        return ApiResponse.success(departmentService.getById(id));
    }

    @PostMapping
    public ApiResponse<Boolean> save(@RequestBody Department department) {
        return ApiResponse.success(departmentService.save(department));
    }

    @PutMapping
    public ApiResponse<Boolean> update(@RequestBody Department department) {
        return ApiResponse.success(departmentService.updateById(department));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> remove(@PathVariable Long id) {
        return ApiResponse.success(departmentService.removeById(id));
    }
}
