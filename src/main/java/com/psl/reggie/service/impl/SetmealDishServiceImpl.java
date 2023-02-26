package com.psl.reggie.service.impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.psl.reggie.dto.SetmealDto;
import com.psl.reggie.entity.SetmealDish;
import com.psl.reggie.mapper.SetmealDishMapper;
import com.psl.reggie.mapper.SetmealMapper;
import com.psl.reggie.service.SetmealDishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {
}
