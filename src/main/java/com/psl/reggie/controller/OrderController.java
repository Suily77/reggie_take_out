package com.psl.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.psl.reggie.common.R;
import com.psl.reggie.entity.Orders;
import com.psl.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    OrderService orderService;
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, Integer number, String beginTime,String endTime){
        log.info("-----page:{}  pagesize:{}=========={}=========={}",page,pageSize,beginTime,endTime);
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        //like查询单号,检测是否为空
        queryWrapper.like(number!=null,Orders::getNumber,number);

        //查询下单时间
        queryWrapper.between(StringUtils.isNotBlank(beginTime)&&StringUtils.isNotBlank(endTime),Orders::getOrderTime,beginTime,endTime);

        //排序
        queryWrapper.orderByDesc(Orders::getOrderTime);
        orderService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     *
     * @param orders
     * status:订单状态 1待付款，2待派送，3已派送，4已完成，5已取消
     * @return
     */
    @PutMapping
    public R<String> sent(@RequestBody Orders orders){
        log.info(orders.toString());
        LambdaUpdateWrapper<Orders> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Orders::getStatus,orders.getStatus()).eq(Orders::getId,orders.getId());
        orderService.update(updateWrapper);
        String str;
        switch (orders.getStatus()){
            case 1:str="待付款";break;
            case 2:str="待派送";break;
            case 3: str="已派送"; break;
            case 4: str="已完成";break;
            case 5: str="已取消";break;
            default:str="派送失败。。";break;
        }
        return R.success(str);
    }
}
