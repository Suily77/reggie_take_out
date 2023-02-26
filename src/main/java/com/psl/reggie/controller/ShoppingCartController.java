package com.psl.reggie.controller;

import com.psl.reggie.common.R;
import com.psl.reggie.entity.ShoppingCart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @GetMapping("/list")
    public R<String> list(){
        return R.success("sds");
    }
}
