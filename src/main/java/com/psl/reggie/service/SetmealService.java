package com.psl.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.psl.reggie.dto.SetmealDto;
import com.psl.reggie.entity.Setmeal;

public interface SetmealService extends IService<Setmeal> {
    public SetmealDto getByIdWithDish(Long id);

    public void saveWithDishService(SetmealDto setmealDto);

    public void updateWithDishService(SetmealDto setmealDto);

    public void delectWithDish(Long[] ids);
}
