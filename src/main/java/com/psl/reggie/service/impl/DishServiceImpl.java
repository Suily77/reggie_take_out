package com.psl.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.psl.reggie.dto.DishDto;
import com.psl.reggie.entity.Category;
import com.psl.reggie.entity.Dish;
import com.psl.reggie.entity.DishFlavor;
import com.psl.reggie.mapper.CategoryMapper;
import com.psl.reggie.mapper.DishMapper;
import com.psl.reggie.service.CategoryService;
import com.psl.reggie.service.DishFlavorService;
import com.psl.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @description 菜品管理
 * @author psl
 * @date 2023-02-21
 */
//实现接口，接口有泛型，实现类不能加泛型
@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper,Dish> implements DishService{
    @Autowired
    CategoryService categoryService;
    @Autowired
    DishFlavorService dishFlavorService;

    /**
     * 实现查询DishDto
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        DishDto dishDto = new DishDto();
        Dish dish = this.baseMapper.selectById(id);
        //3.使用BeanUtils复制到dishDto
        BeanUtils.copyProperties(dish,dishDto);
        //Dish dish2 = super.baseMapper.selectById(id);
        Long categoryId = dish.getCategoryId();
        //System.out.println(dish2);
        Category category = categoryService.getById(categoryId);
        dishDto.setCategoryName(category.getName());
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(id!=null,DishFlavor::getDishId,id).eq(DishFlavor::getIsDeleted,0);
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(list);
        log.info(String.valueOf(dishDto));
        return dishDto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateWithFlavor(DishDto dishDto) {
        log.info(dishDto.toString());
        Dish dish = new Dish();
        //复制到dish
        BeanUtils.copyProperties(dishDto,dish);
        log.info(dish.toString());

        Integer insert = this.baseMapper.updateById(dish);
        //更新或者插入flavor表
        List<DishFlavor> flavors = dishDto.getFlavors();
        //先删除所有的，再根据id修改复活，或者新增id
            int counts = dishFlavorService.count(new LambdaQueryWrapper<DishFlavor>()
                    .eq(DishFlavor::getDishId, dish.getId()).eq(DishFlavor::getIsDeleted,0));
            if(counts>0){
                dishFlavorService.update(new LambdaUpdateWrapper<DishFlavor>()
                        .set(DishFlavor::getIsDeleted,1)
                        .eq(DishFlavor::getDishId,dish.getId()));
            }

        flavors.stream().map((dishFlavor) -> {
            if(dishFlavor.getDishId()==null){
                dishFlavor.setDishId(dish.getId());
                dishFlavor.setIsDeleted(0);
                return dishFlavorService.save(dishFlavor);
            }
//            int count = dishFlavorService.count(new LambdaQueryWrapper<DishFlavor>()
//                    .eq(dishFlavor.getDishId()!=null,DishFlavor::getDishId, dishFlavor.getDishId()));
//            if(count>0){
            dishFlavor.setIsDeleted(0);
                //return dishFlavorService.update(dishFlavor,new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getId,dishFlavor.getId()));
                return dishFlavorService.updateById(dishFlavor);
//            }else {
//                log.warn("插入在这里");
////                return dishFlavorService.save(dishFlavor);
//                return false;
//            }
        }).collect(Collectors.toList());
        return ( null!=insert && insert >= 1);

    }
}
