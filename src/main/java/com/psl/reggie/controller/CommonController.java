package com.psl.reggie.controller;

import com.psl.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

/**
 * 文件上传和下载
 */
@RestController
@RequestMapping("/common")
@Slf4j
@PropertySource("classpath:application.yml")
public class CommonController {
    //获取配置文件的变量

//    @Value("${}")

    @Value("${reggie.path}")
    //java.io.FileNotFoundException: D:\img (拒绝访问。)
    private String basePath;

    /**
     * @param file 与前端保持一直的名称或者@RequestPart()
     *             接受的是表单数据，不添加@RequestBody
     * @return
     */
    @RequestMapping("/upload")

    public R<String> upload(@RequestPart("file") MultipartFile file) {
        log.info(file.toString());
        //原始文件名
        String orginfilename = file.getOriginalFilename();
        //截取.jpg .jpge
        String suffix = orginfilename.substring(orginfilename.lastIndexOf("."));
        //使用UUID重新生成文件名，防止文件名称重复造成文件覆盖
        Date date = new Date();

        //生成时间戳
        long timestamp = System.currentTimeMillis();
        String fileName = UUID.randomUUID().toString() + "-" + timestamp + suffix;
        //创建一个目录对象
        File dir = new File(basePath);
        //判断当前目录是否存在
        if (!dir.exists()) {
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

    /**
     * @param name     可以加@RequestBody，也可以不加，不加要和前端数据名称一致
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        log.info("name:" + name);
        try (ServletOutputStream outputStream = response.getOutputStream(); FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));) {
            //输入流，通过输入流读取文件内容
            //输出流，通过输出流将文件写回浏览器
            response.setContentType("image/jpeg");
            int len;
            byte[] bytes = new byte[1024];
//            len=fileInputStream.read(bytes);//条件写在外面会卡死了
//            while(len!=-1){
//                outputStream.write(bytes,0,len);
//                outputStream.flush();
//            }
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
