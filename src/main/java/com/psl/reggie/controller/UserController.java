package com.psl.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.psl.reggie.common.R;
import com.psl.reggie.entity.User;
import com.psl.reggie.service.UserService;
import com.psl.reggie.utils.SMSUtils;
import com.psl.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@Controller
@ResponseBody
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    /**
     * 发送手机短信验证码
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        //获取手机号
        String phone = user.getPhone();

        if (StringUtils.isNotEmpty(phone)) {
            //生成随机的4位验证码
            String code = ValidateCodeUtils.generateValidateCode(6).toString();
            log.info("code={}", code);

            //调用阿里云提供的短信服务API完成发送短信
            SMSUtils.sendMessage(phone,code);

            //需要将生成的验证码保存到Session
            session.setAttribute(phone, code);
            log.info(session.getAttribute(phone).toString());
            return R.success("手机验证码短信发送成功...");
        }
        return R.error("短信发送失败...");
    }
     /**
     * 移动端用户登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        log.info(map.toString());
        //获取手机号
        String phone = map.get("phone").toString();

        //获取验证码
        String code = map.get("code").toString();

        Object sessionAttribute = session.getAttribute(phone);
        log.info(sessionAttribute.toString());
        //
        if(sessionAttribute!=null&&sessionAttribute.equals(code)){
            //如果能够比对成功，说明可以登录成功
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);
            //匹配不到数据，创建新用户
            if(user==null){
                //1.创建新用户
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            return R.success(user);
        }
        return R.error("登陆失败。。。");
    }
//    @PostMapping("/loginout")
//    public R<String> loginout(HttpServletRequest request){
//        request.getSession().removeAttribute("employee");
//        return R.success("退出成功！！！");
//    }

    @PostMapping("/loginout")
    public R<String> loginout(HttpSession session){
        log.info(session.toString());
        //session.removeAttribute("");
        return R.success("退出成功！！！");
    }
}
