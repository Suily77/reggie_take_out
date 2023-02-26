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
import org.springframework.context.annotation.Description;
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
    @Autowired
    DishService dishService;
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
        this.updateById(dishDto);
        //清理当前菜品对应口味数据---dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        //添加当前提交过来的口味数据---dish_flavor表的insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        return dishFlavorService.saveBatch(flavors);
    }
    @Override
    @Deprecated
    @Transactional(rollbackFor = Exception.class)
    public boolean updateWithFlavor1(DishDto dishDto) {
        log.info(dishDto.toString());
        Dish dish = new Dish();
        //复制到dish
        BeanUtils.copyProperties(dishDto,dish);
        log.info(dish.toString());
        Integer insert = this.baseMapper.updateById(dish);
        //更新或者插入flavor表
        List<DishFlavor> flavors = dishDto.getFlavors();
        //先删除所有的，新增id
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

    /**
     * 新增菜品，同时保存对应的口味数据
     * @param dishDto
     */
    @Transactional
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        log.info(dishDto.toString());//DishDto(flavors=[], categoryName=null, copies=null)
        //1.保存到Dish表，创建Dish对象，因为DishDto不能直接保存
        //this.save
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDto,dish);
        dishService.save(dish);//==this.save(dishDto);==dishService.save(dishDto);
        Long id = dish.getId();
        //flavor可以新增多条数据
        List<DishFlavor> flavors = dishDto.getFlavors().stream().map(dishFlavor -> {
            dishFlavor.setDishId(id);
            return dishFlavor;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }
    @Transactional
    @Override
    public boolean delectByIds(List<Long> longs) {
        dishService.removeByIds(longs);
        longs.stream().map(dishid->{
            LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DishFlavor::getDishId,dishid);
            dishFlavorService.remove(queryWrapper);
            return dishid;
        }).collect(Collectors.toList());

        return true;
    }
}
