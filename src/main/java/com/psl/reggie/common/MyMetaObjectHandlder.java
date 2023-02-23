package com.psl.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 我的原对象处理器
 */
@Component
@Slf4j
public class MyMetaObjectHandlder implements MetaObjectHandler {
    /**
     * 插入操作，自动填充
     * 配合实体类的注解@TableField (fill=FiledFill.INSERT)
     * @param metaObject
     */

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充[insert]...");
        log.info(metaObject.toString());
        //name:是实体类字段
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("createUser", BaseContext.getCurrentId());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }
    /**
     * 更新操作，自动填充
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充[insert]...{}",BaseContext.getCurrentId());
        log.info(metaObject.toString());
        long id = Thread.currentThread().getId();
        log.info("线程id为：{}",id);
        //name:是实体类字段
        metaObject.setValue("updateTime",LocalDateTime.now());
        metaObject.setValue("updateUser",BaseContext.getCurrentId());
    }
}
