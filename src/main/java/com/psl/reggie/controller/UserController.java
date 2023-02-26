package com.psl.reggie.controller;

import com.psl.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@ResponseBody
@RequestMapping("/user")
public class UserController {
    @PostMapping("/login")
    public R<String> login(@RequestBody Long phone){
        return R.success("多少度");
    }
}
