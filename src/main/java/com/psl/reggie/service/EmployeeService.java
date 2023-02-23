package com.psl.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.psl.reggie.common.R;
import com.psl.reggie.entity.Employee;
//接口不需要加@Service
public interface EmployeeService extends IService<Employee> {
    public R<Employee> login(Employee employee);
    public R<Employee> page();
}
