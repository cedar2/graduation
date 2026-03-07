package com.graduation.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduation.entity.Department;
import com.graduation.mapper.DepartmentMapper;
import com.graduation.service.DepartmentService;
import org.springframework.stereotype.Service;
@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {}

