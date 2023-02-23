package com.psl.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.psl.reggie.common.R;
import com.psl.reggie.entity.Employee;
import com.psl.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
@Slf4j
@RestController
@RequestMapping("/employee")//通用请求
public class EmployeeController {
    @Autowired
    EmployeeService employeeService;
    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    //@RequestBody Employee employee将前端传的数据封装到对象中
    //HttpServletRequest request 将employee复制一份存在session中
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        R<Employee> login = employeeService.login(employee);
/*//1.将页面提交的密码进行MD5加密处理啦
        String password=employee.getPassword();
        password= DigestUtils.md5DigestAsHex(password.getBytes());
        //2.根据用户名来查询数据库，找到密码是否相同
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //wrapper.eq("实体类::查询字段", "条件值"); //相当于where条件
//        QueryWrapper queryWrapper1=new QueryWrapper();
//        queryWrapper1.eq();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = getOne(queryWrapper);

        //3、如果没有查询到则返回登录失败结果
        if(emp == null){
            return R.error("登录失败,用户名不存在");
        }

        //4、密码比对，如果不一致则返回登录失败结果
        if(!emp.getPassword().equals(password)){
            return R.error("登录失败，密码错误");
        }

        //5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if(emp.getStatus() == 0){
            return R.error("账号已禁用");
        }*/
        //6、登录成功，将员工id存入Session并返回登录成功结果
        if(login.getCode()==0)
        {
            return login;
        }else {
            request.getSession().setAttribute("employee",login.getData().getId());
        }
        return login;
    }
    //退出登录
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //清理保存的session
        request.getSession().removeAttribute("employee");

        return R.success("退出成功");
    }

    /**
     *
     * @param page 当前页
     * @param pageSize 每页最大值
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page = {},pageSize = {},name = {}" ,page,pageSize,name);
        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //添加过滤条件
        queryWrapper.like(StringUtils.isNotBlank(name),Employee::getName,name);

        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询,分页
        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工，员工信息：{}",employee.toString());

        //设置初始密码123456，需要进行md5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //设置创建时间和更新时间
        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());

        //获得当前登录用户的id
        //在实体类上id加上 @JsonFormat(shape = JsonFormat.Shape.STRING)
        //Long empId = (Long) request.getSession().getAttribute("employee");

        //设置创建人和更新人
        //employee.setCreateUser(empId);
        //employee.setUpdateUser(empId);
        log.info("新增员工，员工信息2222222：{}",employee.toString());
        employeeService.save(employee);

        return R.success("新增员工成功");
    }
    /**
     * 根据id修改员工信息
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info(employee.toString());

        //Long empId = (Long)request.getSession().getAttribute("employee");
        //employee.setUpdateTime(LocalDateTime.now());
        //employee.setUpdateUser(empId);
        employeeService.updateById(employee);

        return R.success("员工信息修改成功");
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable("id") Long id){
        log.info("根据id查询员工信息...");
        Employee employee = employeeService.getById(id);
        if(employee != null){
            return R.success(employee);
        }
        return R.error("没有查询到对应员工信息");
    }
}
