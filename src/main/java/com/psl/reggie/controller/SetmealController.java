package com.psl.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.psl.reggie.common.R;
import com.psl.reggie.dto.SetmealDto;
import com.psl.reggie.entity.Setmeal;
import com.psl.reggie.service.CategoryService;
import com.psl.reggie.service.SetmealDishService;
import com.psl.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    SetmealService setmealService;
//    @Autowired
//    SetmealDishService setmealDishService;
    @Autowired
    CategoryService categoryService;
    /**
     * 套餐分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        //TODO
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage=new Page<>(page,pageSize);

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //模糊查询
        queryWrapper.like(StringUtils.isNotBlank(name),Setmeal::getName,name);
        //排序
        queryWrapper.orderByAsc(Setmeal::getCreateTime);
        List<Setmeal> listsetmeal= setmealService.list(queryWrapper);
        //List<SetmealDto> list = setmealDishService.list();
        List<SetmealDto> collect = listsetmeal.stream().map(setmeal -> {
            SetmealDto setmealdto = new SetmealDto();
            Long categoryId = setmeal.getCategoryId();
            if(categoryService.getById(categoryId)!=null){
                setmealdto.setCategoryName(categoryService.getById(categoryId).getName());
            }
            //参数1.A 2.B A->B
            BeanUtils.copyProperties(setmeal, setmealdto);
            return setmealdto;
        }).collect(Collectors.toList());
        log.info(collect.toString());

        setmealPage = setmealService.page(setmealPage, queryWrapper);
        BeanUtils.copyProperties(setmealPage,setmealDtoPage);
        setmealDtoPage.setRecords(collect);
        return R.success(setmealDtoPage);
    }

    /**
     * 点击编辑查询出相关连表信息
     * @param id
     * @return
     */
    @GetMapping("/{ids}")
    public R<SetmealDto> getById(@PathVariable("ids") Long id){
        SetmealDto setmealDto = setmealService.getByIdWithDish(id);
        if(setmealDto != null){
            return R.success(setmealDto);
        }
        return R.error("没有查询到对应菜单信息");
    }
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        log.info(setmealDto.toString());
        setmealService.updateWithDishService(setmealDto);
        return R.success("修改成功。。。");
    }

    /**
     * 新增功能
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        //TODO
        log.info(setmealDto.toString());
        setmealService.saveWithDishService(setmealDto);
        return R.success("保存成功。。。");
    }

    /**
     * 修改状态status
     * 批量修改状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus (@PathVariable("status") int status,Long[] ids){
        List<Long> longs = Arrays.asList(ids);
        longs.stream().map(id->{
            LambdaUpdateWrapper<Setmeal> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(Setmeal::getStatus,status);
            setmealService.update(updateWrapper.eq(Setmeal::getId,id));
            return id;
        }).collect(Collectors.toList());
//        setmealService.update(updateWrapper);
        return R.success("修改状态成功！！！");
    }

    /**
     * 套餐删除
     * @param ids
     * @return R
     */
    @DeleteMapping
    public R<String> delete(Long[] ids){
        //TODO
        setmealService.delectWithDish(ids);
        return R.success("套餐数据删除成功。。。");
    }
    /**
     * 移动端操作
     * 根据条件查询套餐数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);

        return R.success(list);
    }
}
