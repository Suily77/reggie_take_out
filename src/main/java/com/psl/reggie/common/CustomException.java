package com.psl.reggie.common;


public class CustomException extends RuntimeException{
    public CustomException(String message){
        //调用父类构造方法
        super(message);
    }
}
