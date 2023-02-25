package com.psl.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.psl.reggie.common.R;
import com.psl.reggie.dto.SetmealDto;
import com.psl.reggie.entity.Category;
import com.psl.reggie.entity.Setmeal;
import com.psl.reggie.service.CategoryService;
import com.psl.reggie.service.SetmealDishService;
import com.psl.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    SetmealService setmealService;
    @Autowired
    SetmealDishService setmealDishService;
    @Autowired
    CategoryService categoryService;
    /**
     * 套餐分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage=new Page<>(page,pageSize);

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //模糊查询
        queryWrapper.like(StringUtils.isNotBlank(name),Setmeal::getName,name);
        //排序
        queryWrapper.orderByAsc(Setmeal::getCreateTime);
        List<Setmeal> listsetmeal= setmealService.list(queryWrapper);
        //List<SetmealDto> list = setmealDishService.list();
        List<SetmealDto> collect = listsetmeal.stream().map(setmeal -> {
            SetmealDto setmealdto = new SetmealDto();
            Long categoryId = setmeal.getCategoryId();
            setmealdto.setCategoryName(categoryService.getById(categoryId).getName());
            //参数1.A 2.B A->B
            BeanUtils.copyProperties(setmeal, setmealdto);
            return setmealdto;
        }).collect(Collectors.toList());
        log.info(collect.toString());

        setmealPage = setmealService.page(setmealPage, queryWrapper);
        BeanUtils.copyProperties(setmealPage,setmealDtoPage);
        setmealDtoPage.setRecords(collect);
        return R.success(setmealDtoPage);
    }
    @GetMapping("/{ids}")
    public void update(@PathVariable("ids") Long id){

    }
}
