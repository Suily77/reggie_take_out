package com.psl.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;



//@ControllerAdvice是一个通知，拦截这些类组件的异常
@RestControllerAdvice(annotations = {RestController.class, Controller.class})
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)//拦截SQL异常
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.info("============"+ex.getMessage()+"。。。。。。。。。。");
        if(ex.getMessage().contains("Duplicate entry")){
            String[] split =ex.getMessage().split(" ");
            String msg = split[2] + "已存在";
            return R.error(msg);
        }
        return R.error("未知错误");
    }
    @ExceptionHandler(CustomExpection.class)
    public R<String> exceptionHandler(CustomExpection ex) {
        //拦截异常打印到前台
        return R.error(ex.getMessage());
    }
}
