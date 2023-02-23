package com.psl.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.psl.reggie.entity.Dish;
import org.apache.ibatis.annotations.Mapper;
/**
 * @description 菜品管理
 * @author psl
 * @date 2023-02-21
 */
@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
