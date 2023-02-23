package com.psl.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.psl.reggie.common.CustomExpection;
import com.psl.reggie.entity.Category;
import com.psl.reggie.entity.Dish;
import com.psl.reggie.entity.Setmeal;
import com.psl.reggie.mapper.CategoryMapper;
import com.psl.reggie.service.CategoryService;
import com.psl.reggie.service.DishService;
import com.psl.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    DishService dishService;
    @Autowired
    SetmealService setmealService;

    @Override
    public boolean remove(Long ids) {
        //1、判断
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<Dish>();
        //2、添加查询条件，根据分类id进行查询
        queryWrapper.eq(Dish::getCategoryId, ids);
        int count = dishService.count(queryWrapper);
        //查询当前分类是否关联了菜品，如果已经关联，抛出一个业务异常
        if (count > 0) {
            //已经关联菜品，抛出一个业务异常

//            return false;
            throw new CustomExpection("当前分类下关联了菜品，不能删除");
        }
        //查询当前分类是否关联了套餐，如果已经关联，抛出一个业务异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据分类id进行查询
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, ids);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);
        if (count2 > 0) {
            //已经关联套餐，抛出一个业务异常
            throw new CustomExpection("当前分类下关联了套餐，不能删除");
        }
//        return super.removeById(ids);
        return super.removeById(ids);
    }

}
