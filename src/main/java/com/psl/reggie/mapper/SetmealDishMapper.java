package com.psl.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.psl.reggie.dto.SetmealDto;
import com.psl.reggie.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SetmealDishMapper extends BaseMapper<SetmealDish> {
}
