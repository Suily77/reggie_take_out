package com.psl.reggie.controller;

import com.psl.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传和下载
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {
    //获取配置文件的变量
    @Value("${reggie.path}")
    private String basePath;

    /**
     *
     * @param file 与前端保持一直的名称或者@RequestPart()
     *             接受的是表单数据，不添加@RequestBody
     * @return
     */
    @RequestMapping("/upload")

    public R<String> upload(@RequestPart("file") MultipartFile file){
        log.info(file.toString());
        //原始文件名
        String orginfilename = file.getOriginalFilename();
        //截取.jpg .jpge
        String suffix=orginfilename.substring(orginfilename.lastIndexOf("."));
        //使用UUID重新生成文件名，防止文件名称重复造成文件覆盖
        String fileName = UUID.randomUUID().toString()+suffix;
        //创建一个目录对象
        File dir = new File(basePath);
        //判断当前目录是否存在
        if(!dir.exists()){
            //目录不存在，需要创建
            dir.mkdirs();
        }
        try {
            //将临时文件转存到指定位置
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }
    @RequestMapping("/download")
    public R<String> download(){
        return R.success("下载图片完成。。。");
    }
}
