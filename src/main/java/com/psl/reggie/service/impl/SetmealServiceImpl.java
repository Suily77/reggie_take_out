package com.psl.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.psl.reggie.common.CustomExpection;
import com.psl.reggie.dto.DishDto;
import com.psl.reggie.dto.SetmealDto;
import com.psl.reggie.entity.Dish;
import com.psl.reggie.entity.Setmeal;
import com.psl.reggie.entity.SetmealDish;
import com.psl.reggie.mapper.SetmealMapper;
import com.psl.reggie.service.CategoryService;
import com.psl.reggie.service.DishService;
import com.psl.reggie.service.SetmealDishService;
import com.psl.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    CategoryService categoryService;
    @Autowired
    DishService dishService;
    @Autowired
    SetmealDishService setmealDishService;
    @Override
    public SetmealDto getByIdWithDish(Long id) {
      log.info("id===============:{}",id);
        SetmealDto setmealDto = new SetmealDto();
        Setmeal byId = this.getById(id);
        BeanUtils.copyProperties(byId,setmealDto);
        setmealDto.setCategoryName(categoryService.getById(byId.getCategoryId()).getName());
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> list = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(list);
        log.info("setmealDto::::::{}",setmealDto);
        return setmealDto;
    }

    /**
     * 保存功能
     * 返回值为void
     * 添加事务注解
     * @param setmealDto
     */
    @Transactional//添加注解事务
    @Override
    public void saveWithDishService(SetmealDto setmealDto) {
        //TODO
        //1.保存setmeal表，setmealDto是子类，可以向上转型
        this.save(setmealDto);
        //2.保存信息到联表
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map(setmealDish -> {
            setmealDish.setSetmealId(setmealDto.getId());
            return setmealDish;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDto.getSetmealDishes());
    }

    /**
     * 修改功能
     * @param setmealDto
     */
    @Transactional//添加注解事务
    @Override
    public void updateWithDishService(SetmealDto setmealDto) {
        //1.修改setmeal表，setmealDto是子类，可以向上转型
        this.updateById(setmealDto);
        //2.修改信息到联表，1.先删除2.再新增
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(queryWrapper);
        //新增
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map(setmealDish -> {
            setmealDish.setSetmealId(setmealDto.getId());
            return setmealDish;
        }).collect(Collectors.toList());
        log.info("ddddddddddddddddddddddd:"+setmealDishes.toString());
        log.info("ssssssssssssssss==={}", Arrays.toString(setmealDishes.toArray()));
        setmealDishService.saveBatch(setmealDishes);
    }
    @Transactional
    @Override
    public void delectWithDish(Long[] ids) {
        //TODO
        List<Long> setmealids = Arrays.asList(ids);
        //select count(*) from setmeal where id in (1,2,3) and status = 1
        //查询套餐状态，确定是否可用删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.in(Setmeal::getId,setmealids);
        queryWrapper.eq(Setmeal::getStatus,1);
        int count=this.count(queryWrapper);
        if(count>0){
            //如果不能删除，抛出一个业务异常
            throw  new CustomExpection("套餐正在售卖中，不能删除");
        }
        //如果可以删除，先删除套餐表中的数据---setmeal
        this.removeByIds(setmealids);

//        //delete from setmeal_dish where setmeal_id in (1,2,3)
//        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
//        //删除关系表中的数据----setmeal_dish
//        setmealDishService.remove(lambdaQueryWrapper);

        //删除关系表中的数据----setmeal_dish
        setmealids.stream().map(setmealid->{
            LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(SetmealDish::getSetmealId,setmealid);
            setmealDishService.remove(queryWrapper1);
            return setmealid;
        }).collect(Collectors.toList());

    }
}
