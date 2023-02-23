package com.psl.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.psl.reggie.common.R;
import com.psl.reggie.entity.Employee;
import com.psl.reggie.mapper.EmployeeMapper;
import com.psl.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
@Slf4j
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    @Override
    public R<Employee> login(Employee employee) {
        //1.将页面提交的密码进行MD5加密处理啦
        String password=employee.getPassword();
        password= DigestUtils.md5DigestAsHex(password.getBytes());
        //2.根据用户名来查询数据库，找到密码是否相同
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //wrapper.eq("实体类::查询字段", "条件值"); //相当于where条件
//        QueryWrapper queryWrapper1=new QueryWrapper();
//        queryWrapper1.eq();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = getOne(queryWrapper);
        log.info(emp.toString());
        //3、如果没有查询到则返回登录失败结果
        if(emp == null){
            return R.error("登录失败,用户不存在");
        }

        //4、密码比对，如果不一致则返回登录失败结果
        if(!emp.getPassword().equals(password)){
            return R.error("登录失败，密码错误");
        }

        //5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if(emp.getStatus() == 0){
            return R.error("账号已禁用");
        }


        return R.success(emp);
    }

    @Override
    public R<Employee> page() {
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();

        return null;
    }
}
