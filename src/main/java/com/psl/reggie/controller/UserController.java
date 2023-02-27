package com.psl.reggie.controller;

import com.psl.reggie.common.R;
import com.psl.reggie.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Controller
@ResponseBody
@RequestMapping("/user")
public class UserController {
    @PostMapping("/login")
    public R<String> login(@RequestBody User user, HttpServletRequest request){
        Long value = Long.valueOf(user.getPhone());
        request.getSession().setAttribute("employee",value);
        log.info(user.getPhone());
        return R.success("多少度");
    }
}
