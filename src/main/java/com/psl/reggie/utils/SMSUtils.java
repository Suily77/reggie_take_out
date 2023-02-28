package com.psl.reggie.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class SMSUtils {
    private static String accesskeyid;
    @Value("${access-key-id}")
    public String accessKeyId;
    @PostConstruct
    public void setAccesskeyid(){
        accesskeyid=this.accessKeyId;
    }
    private static String accesskeysecret;
    @Value("${access-key-secret}")
    public String accessKeySecret;
    @PostConstruct
    public void setAccessKeySecret(){
        accesskeysecret=this.accessKeySecret;
    }


    private static String signName;
    @Value("${signName}")
    public String signname;
    @PostConstruct
    public void setSignName(){
        signName=this.signname;
    }

    private static String templateCode;
    @Value("${templateCode}")
    public String templatecode;
    @PostConstruct
    public void setTemplateCode(){
        templateCode=this.templatecode;
    }
    /**
     * 发送短信
     *  signName 签名
     *  templateCode 模板
     * @param phoneNumber 手机号
     * @param param 参数,验证码
     */
    public static void sendMessage(String phoneNumber,String param){
        log.info(phoneNumber,accesskeyid,accesskeysecret,templateCode);
        System.out.println(phoneNumber+accesskeyid+accesskeysecret+templateCode);
        DefaultProfile profile = DefaultProfile.getProfile("cn-shanghai", accesskeyid, accesskeysecret);

        IAcsClient client = new DefaultAcsClient(profile);
        SendSmsRequest request = new SendSmsRequest();
        request.setPhoneNumbers(phoneNumber);//接收短信的手机号码
        request.setSignName(signName);//短信签名名称
        request.setTemplateCode(templateCode);//短信模板CODE
        request.setTemplateParam("{\"code\":\""+param+"\"}");//短信模板变量对应的实际值
        try {
            SendSmsResponse response = client.getAcsResponse(request);
            System.out.println(new Gson().toJson(response));
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            System.out.println("ErrCode:" + e.getErrCode());
            System.out.println("ErrMsg:" + e.getErrMsg());
            System.out.println("RequestId:" + e.getRequestId());
        }
    }
}
