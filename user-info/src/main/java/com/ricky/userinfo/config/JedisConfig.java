package com.ricky.userinfo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

//jedis配置类
@Configuration
public class JedisConfig {

//    private Logger logger = LoggerFactory.getLogger(JedisConfig.class);

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

//    @Value("${spring.data.redis.password}")
//    private String password;
    @Value("${spring.data.redis.timeout}")
    private int timeout;

    @Value("${spring.data.redis.jedis.pool.max-active}")
    private int maxActive;

    @Value("${spring.data.redis.jedis.pool.max-idle}")
    private int maxIdle;

    @Value("${spring.data.redis.jedis.pool.min-idle}")
    private int minIdle;

    @Bean
    public JedisPool  jedisPool(){
        JedisPoolConfig jedisPoolConfig=new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMinIdle(minIdle);
        jedisPoolConfig.setMaxTotal(maxActive);

        JedisPool jedisPool=new JedisPool(jedisPoolConfig,host,port,timeout);

        System.out.println("JedisPool连接成功:"+host+"\t"+port);

        return jedisPool;
    }
}