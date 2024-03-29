package com.ricky.userinfo.controller;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ricky.apicommon.XiaoLanShuException;
import com.ricky.apicommon.constant.Constant;
import com.ricky.apicommon.userInfo.DTO.UserDTO;
import com.ricky.apicommon.userInfo.entity.UserBasic;
import com.ricky.apicommon.userInfo.entity.UserDetail;
import com.ricky.apicommon.utils.JwtUtil;
import com.ricky.apicommon.utils.TokenRecord;
import com.ricky.apicommon.utils.result.R;
import com.ricky.apicommon.utils.result.ResultFactory;
import com.ricky.userinfo.config.MailSentRabbitConfig;
import com.ricky.userinfo.serviceImpl.UserBasicServiceImpl;
import com.ricky.userinfo.serviceImpl.UserDetailServiceImpl;
import com.ricky.userinfo.utils.JedisUtils;
import jakarta.annotation.Resource;
//import javax.annotation.Resource;
//import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;


import java.util.*;


@RestController
public class LoginController {

    @Resource
    UserBasicServiceImpl userBasicService;
    @Resource
    RabbitTemplate rabbitTemplate;

    @Autowired
    UserDetailServiceImpl userDetailService;

    private Logger log = LoggerFactory.getLogger(LoginController.class);

    @PostMapping("/sendRegEmail")
    R<String> sendRegEmail(@RequestBody UserBasic userBasic) {
        try {
            //检查email唯一
            boolean existedUser = userBasicService.getBaseMapper().exists(new LambdaQueryWrapper<UserBasic>()
                    .eq(UserBasic::getUserEmail, userBasic.getUserEmail()));
            if (existedUser) {
                return ResultFactory.fail("已经存在用户");
            }
            //测试时关闭
//            //发送注册邮件
//            rabbitTemplate.convertAndSend(MailSentRabbitConfig.EXCHANGE_NAME,
//                    MailSentRabbitConfig.ROUTE_NAME, JSON.toJSONString(userBasic));
            //返回成功
            log.debug(JSON.toJSONString(userBasic));
            return ResultFactory.ok();
        } catch (Exception e) {
            return ResultFactory.fail(e.getMessage());
        }
    }


    @PostMapping("/register")
    R<Map> reg(@RequestBody String jsonData) {
        try {
            Map<String, Object> reg = userBasicService.reg(jsonData);
            return ResultFactory.success(reg);
        } catch (Exception e) {
            if (e instanceof XiaoLanShuException) {
                if (!StringUtils.isEmpty(e.getMessage())) {
                    return ResultFactory.fail(e.getMessage());
                } else {
                    return ResultFactory.fail("验证码错误或已过期");
                }
            }
            e.printStackTrace();
            return ResultFactory.fail();
        }
    }

    @PostMapping("/loginByPwd/{uEmail}")
    public R<Map> login(@PathVariable String uEmail,
                        @RequestBody String pwd) {
        try {
            return ResultFactory.success(userBasicService.loginByPwd(uEmail, pwd));
        } catch (XiaoLanShuException e) {
            return ResultFactory.fail(e.getMessage());
        } catch (Exception e) {
            return ResultFactory.fail();
        }
    }




}
