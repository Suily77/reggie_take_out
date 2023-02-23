package com.psl.reggie.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.psl.reggie.entity.Category;

import java.util.Map;

/**
 * @description 菜品及套餐分类
 * @author psl
 * @date 2023-02-21
 */
public interface CategoryService extends IService<Category> {


    public boolean remove(Long ids);
}