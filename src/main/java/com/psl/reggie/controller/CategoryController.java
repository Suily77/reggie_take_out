package com.psl.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.psl.reggie.common.R;
import com.psl.reggie.entity.Category;
import com.psl.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @description 菜品及套餐分类
 * @author psl
 * @date 2023-02-21
 */
@RestController
@RequestMapping(value = "/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page = {},pageSize = {},name = {}" ,page,pageSize,name);
        //使用mybatisplus的page分页
        Page<Category> categoryPage = new Page<>(page,pageSize);

        //创建LambaWrapperQuery
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //模糊查询，根据姓名查询
        queryWrapper.like(StringUtils.isNotBlank(name),Category::getName,name);
        //排序
        queryWrapper.orderByAsc(Category::getSort);
        categoryService.page(categoryPage,queryWrapper);
        return R.success(categoryPage);
    }

    /**
     * 新增操作
     * post请求
     * @param category
     * @return
     */
    @PostMapping
    public R<Category> save(@RequestBody Category category){

        categoryService.save(category);

        return R.success(category);
    }

    /**
     * 修改套餐
     * @param category
     * @return
     */
    @PutMapping
    public R<String> updata( @RequestBody Category category){
        log.info("要修改的信息:{}",category);
        categoryService.updateById(category);
        return R.success("修改套餐信息成功");
    }

    /**
     * 根据id删除分类
     * @param ids
     * @return
     */
//    @DeleteMapping("{ids}")
//    public R<String> delete(@PathVariable("ids") Long ids){
    @DeleteMapping
    public R<String> delete(Long ids){
        log.info("删除操作的ids:{}",ids);
        boolean remove = categoryService.remove(ids);
        if(remove){
            return R.success("分类信息删除成功。。。") ;
        }
        return R.error("分类信息删除失败。。。") ;
    }
//    @GetMapping("/list")
////    //Integer type参数也行
////    public R<List<Category>> saveDish(Category category){
////        log.info("type");
////        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
////        queryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
////        List<Category> list = new ArrayList<>();
////        list=categoryService.list(queryWrapper);
////        return R.success(list);
////    }

    /**
     * 移动端功能
     * 根据条件查询分类数据
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.eq(category.getType() != null,Category::getType,category.getType());
        //添加排序条件
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }

}