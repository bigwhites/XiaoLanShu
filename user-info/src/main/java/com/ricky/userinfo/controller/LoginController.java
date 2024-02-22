package com.ricky.userinfo.controller;


import com.alibaba.fastjson2.JSONObject;
import com.ricky.apicommon.userInfo.entity.UserBasic;
import com.ricky.apicommon.utils.result.R;
import com.ricky.apicommon.utils.result.ResultFactory;
import com.ricky.userinfo.config.MailSentRabbitConfig;
import com.ricky.userinfo.serviceImpl.UserBasicServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
public class LoginController {
    @Resource
    UserBasicServiceImpl userBasicService;
    @Resource
    RabbitTemplate rabbitTemplate;

    @PostMapping("/sendRegEmail")
    R<String> sendRegEmail(@RequestBody UserBasic userBasic){
        try {
            //检查email唯一

            //发送注册邮件
            rabbitTemplate.convertAndSend(MailSentRabbitConfig.EXCHANGE_NAME,
                    MailSentRabbitConfig.ROUTE_NAME,userBasic);

            //返回成功
            return ResultFactory.ok();
        }
        catch (Exception e){
            return ResultFactory.fail(e.getMessage());
        }
    }

    @PostMapping("/register")
    R<String> reg( @RequestBody String jsonData) {
        try {

            JSONObject jsonObj = JSONObject.parseObject(jsonData);
            String validCode =  jsonObj.getString("validCode");
            UserBasic userBasic = jsonObj.getObject("userBasic", UserBasic.class);

            //验证验证码

            System.out.println(userBasic);
            //insert 2 db
            userBasicService.save(userBasic);
            log.info(userBasic.toString());

            //返回主键
            return ResultFactory.success(userBasic.getUuid());
        }
        catch (Exception e){
            return ResultFactory.fail(e.getMessage());
        }
    }



}
