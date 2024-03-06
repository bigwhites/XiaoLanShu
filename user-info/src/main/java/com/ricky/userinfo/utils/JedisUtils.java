package com.ricky.userinfo.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;



/**
 * @ClassName JedisUtils
 * @Description
 * @Author admin
 * @Version 1.0
 */
@Component
public class JedisUtils{
//    @Resource
    @Autowired
    private JedisPool jedisPool;

    /**
     * 获取Jedis资源
     */
    public Jedis getJedis(){
        return jedisPool.getResource();
    }
    /**
     * 释放Jedis连接
     */
    public void close(Jedis jedis){
        if(jedis!=null){
            jedis.close();
        }
    }

}