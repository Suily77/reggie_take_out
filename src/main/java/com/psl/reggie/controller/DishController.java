package com.psl.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.psl.reggie.common.R;
import com.psl.reggie.dto.DishDto;
import com.psl.reggie.dto.SetmealDto;
import com.psl.reggie.entity.Category;
import com.psl.reggie.entity.Dish;
import com.psl.reggie.entity.Setmeal;
import com.psl.reggie.service.CategoryService;
import com.psl.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @description 菜品管理
 * @author psl
 * @date 2023-02-21
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    DishService dishService;
    @Autowired
    CategoryService categoryService;

    /**
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //TODO
        Page<Dish> dishPage = new Page<Dish>(page,pageSize);
        Page<DishDto> dtoPage = new Page<>();
        Page<Dish> dishPage1 = new Page();
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //模糊查询
        queryWrapper.like(StringUtils.isNotBlank(name),Dish::getName,name);
        //排序
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //page1的地址与dishPage地址一样
        Page page1 = dishService.page(dishPage, queryWrapper);
        //复制Page
        BeanUtils.copyProperties(dishPage,dtoPage,"records");
        //制定最新的命名为records的集合
        List<DishDto> dishDtos1 = new ArrayList<>();
        List<Dish> dishs=dishPage.getRecords();
        //List不能复制,只能复制entity实体类
        BeanUtils.copyProperties(dishs,dishDtos1);
        //stream流
        List<DishDto> collect = dishs.stream().map(dish -> {
            //创建dishdto的实体类
            DishDto dishDto = new DishDto();
            Long categoryId = dish.getCategoryId();
            Category byId = categoryService.getById(categoryId);
            if(byId != null){
                dishDto.setCategoryName(byId.getName());
            }
            BeanUtils.copyProperties(dish,dishDto);
            return dishDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(collect);

        return R.success(dtoPage);
    }


//    @PostMapping("/status/{status}")
//    public R<String> updateStatus(@PathVariable("status")Integer status ,Long ids,Dish dish){
//        log.info("{}==============={}",status,ids);
//        dish.setId(ids);
//        dishService.updateById(dish);
//        return R.success("修改状态Status成功。。。");
//    }
    /**
     *
     * @param status  1.启售 2.停售
     * @param ids
     *  将status也传给Dish dish的status
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable("status")Integer status ,Long [] ids){
        log.info("{}==============={}",status,ids);
        ArrayList<Dish> dishes = new ArrayList<>();
        Dish dish = new Dish();
        for (Long id : ids) {
            dish.setId(id);
            dish.setStatus(status);
            dishService.updateById(dish);
        }
        return R.success("修改状态Status成功。。。");
//        List<Dish> dishes1 = dishes.stream().map(dish11 -> {
//            for (Long id : ids) {
//                dish.setId(id);
//                dish.setStatus(status);
//            }
//            return dish;
//        }).collect(Collectors.toList());
//        dishService.updateBatchById(dishes1);
    }
    /**
     * 根据id连表查询
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public R<DishDto> getById(@PathVariable Long id){
        //TODO
        log.info(id.toString());
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        if(dishDto != null){
            return R.success(dishDto);
        }
        return R.error("没有查询到对应菜单信息");
    }

    /**
     *
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        //TODO
        if(dishDto!=null){
            log.info("..............{}",dishDto);
            dishService.updateWithFlavor(dishDto);
            return R.success("修改成功。。。");
        }
        return R.error("修改成功。。。");
    }

    /**
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        if(dishDto!=null){
            log.info("..............{}",dishDto);
            dishService.saveWithFlavor(dishDto);
            return R.success("保存成功。。。");
        }
        return R.error("保存失败。。。");
    }
    @DeleteMapping
    public R<String> delect(Long[] ids){
        log.info(Arrays.toString(ids));
        log.info(Arrays.asList(ids).toString());
        List<Long> longs = Arrays.asList(ids);
        boolean b = dishService.delectByIds(longs);
       if(b){
           return R.success("删除成功！！！");
       }
        return R.success("删除失败！！！");

    }
    @GetMapping("/list")
    public R<List<DishDto>> list(Long categoryId){
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId,categoryId);
        List<Dish> list = dishService.list(queryWrapper);
        List<DishDto> dishDtos = list.stream().map(dish -> {
            Long id = dish.getId();
            DishDto byIdWithFlavor = dishService.getByIdWithFlavor(id);
            return byIdWithFlavor;
        }).collect(Collectors.toList());
        return R.success(dishDtos);
    }
}
