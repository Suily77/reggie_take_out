package com.psl.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.psl.reggie.common.R;
import com.psl.reggie.dto.DishDto;
import com.psl.reggie.entity.Category;
import com.psl.reggie.entity.Dish;
import com.psl.reggie.service.CategoryService;
import com.psl.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        Page dishPage = new Page(page,pageSize);
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //模糊查询
        queryWrapper.like(StringUtils.isNotBlank(name),Dish::getName,name);
        //排序
        queryWrapper.orderByAsc(Dish::getSort);
        dishService.page(dishPage,queryWrapper);
        return R.success(dishPage);
    }

    /**
     *
     * @param status
     * @param ids
     * @param dish 将status也传给Dish dish的status
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable("status")Integer status ,Long ids,Dish dish){
        log.info("{}==============={}",status,ids);
        System.out.println(dish);
        dish.setId(ids);
        dishService.updateById(dish);
        return R.success("修改状态Status成功。。。");
    }

    /**
     * 根据id连表查询
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public R<DishDto> getById(@PathVariable Long id){
        log.info(id.toString());
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        if(dishDto != null){
            return R.success(dishDto);
        }
        return R.error("没有查询到对应菜单信息");
    }
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        if(dishDto!=null){
            log.info("..............{}",dishDto);
            dishService.updateWithFlavor(dishDto);
            return R.success("修改成功。。。");
        }
        return R.error("修改成功。。。");
    }
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        if(dishDto!=null){
            log.info("..............{}",dishDto);
            dishService.updateWithFlavor(dishDto);
            return R.success("保存成功。。。");
        }
        return R.error("保存失败。。。");
    }
}
