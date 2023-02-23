package com.psl.reggie.common;


public class CustomExpection extends RuntimeException{
    public CustomExpection(String message){
        //调用父类构造方法
        super(message);
    }
}
