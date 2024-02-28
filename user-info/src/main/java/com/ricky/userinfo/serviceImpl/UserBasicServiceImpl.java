package com.ricky.userinfo.serviceImpl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.ricky.apicommon.XiaoLanShuException;
import com.ricky.apicommon.constant.Constant;
import com.ricky.apicommon.constant.RedisPrefix;
import com.ricky.apicommon.userInfo.entity.UserBasic;
import com.ricky.apicommon.userInfo.entity.UserDetail;
import com.ricky.apicommon.userInfo.service.IUserBasicService;
import com.ricky.apicommon.utils.JwtUtil;
import com.ricky.apicommon.utils.TokenRecord;
import com.ricky.userinfo.mapper.UserBasicMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ricky.userinfo.utils.JedisUtils;
import jakarta.annotation.Resource;
//import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 用户基本信息 服务实现类
 * </p>
 *
 * @author bigwhites
 * @since 2024-02-22
 */
@Service
@Slf4j
public class UserBasicServiceImpl extends ServiceImpl<UserBasicMapper, UserBasic> implements IUserBasicService {

    @Autowired
    JedisUtils jedisUtils;

    @Transactional
    public Map<String, Object> reg(String jsonData) {
        Jedis jedis = null;
        try {
            JSONObject jsonObj = JSONObject.parseObject(jsonData);
            String inputValidCode = jsonObj.getString("validCode");
            UserBasic userBasic = jsonObj.getObject(Constant.USER_AFTER_LOG_SIGN, UserBasic.class);

//            //验证验证码
            jedis = jedisUtils.getJedis();
            String validCode = jedis.get(userBasic.getUserEmail());
            System.out.println(inputValidCode);
            if (!Objects.equals(validCode, inputValidCode)) {
                throw new XiaoLanShuException("验证码已经过期");
            }
            System.out.println(userBasic);

            //insert 2 db
            super.save(userBasic);
            log.info(userBasic.toString());

            //删除验证码
            jedis.del(userBasic.getUserEmail());

            //生成token
            return getTokenAndSelf(userBasic);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            jedisUtils.close(jedis);
        }
    }

    public Map<String, Object> loginByPwd(String email, String pwd) {
        Jedis jedis = jedisUtils.getJedis();
        log.info(email);
        String key = RedisPrefix.LOGIN_FAIL_CNT + email;
        String value = jedis.get(key);
        Integer cnt = value == null ? null : Integer.parseInt(value);
        if (cnt != null && cnt > 3) {
            jedis.close();
            throw new XiaoLanShuException("失败超过三次，锁定两分钟!");
        }
        UserBasic userBasic = super.getOne(new LambdaQueryWrapper<UserBasic>()
                .eq(UserBasic::getUserEmail, email));
        if (userBasic == null) {
            jedis.close();
            throw new XiaoLanShuException("用户名或密码错误！");
        }
        if (StringUtils.isEmpty(userBasic.getPwd())) {
            jedis.close();
            throw new XiaoLanShuException("未设置密码!");
        }
        if (userBasic.getPwd().equals(pwd)) {  //TODO 对密码进行加密
            //签发token返回自身基本信息
            jedis.close();
            return getTokenAndSelf(userBasic);
        } else {
            jedis.incr(key);
            jedis.expire(key, 60 * 2);
            jedis.close();
            throw new XiaoLanShuException("用户名或密码错误！");
        }

    }

    private Map<String, Object> getTokenAndSelf(UserBasic userBasic) {
        TokenRecord userToken = JwtUtil.createUserToken(userBasic.getUuid(), userBasic.getUserEmail());
        Map<String, Object> res = new HashMap<>();
        res.put(Constant.USER_AFTER_LOG_SIGN, userBasic);
        userBasic.setPwd(null);
        res.put(Constant.FRONT_TOKEN_HEADER, userToken);
        return res;
    }

}
