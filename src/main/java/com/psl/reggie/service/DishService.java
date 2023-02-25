package com.psl.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.psl.reggie.dto.DishDto;
import com.psl.reggie.entity.Dish;
/**
 * @description 菜品管理
 * @author psl
 * @date 2023-02-21
 */
public interface DishService extends IService<Dish> {

    public DishDto getByIdWithFlavor(Long id);

    public boolean updateWithFlavor(DishDto dishDto);

    public boolean updateWithFlavor1(DishDto dishDto);

    public void saveWithFlavor(DishDto dishDto);
}
