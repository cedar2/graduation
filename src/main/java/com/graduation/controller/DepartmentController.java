package com.graduation.controller;

import com.graduation.common.ApiResponse;
import com.graduation.entity.Department;
import com.graduation.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/department")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @GetMapping
    public ApiResponse<List<Department>> list() {
        return ApiResponse.success(departmentService.list());
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

