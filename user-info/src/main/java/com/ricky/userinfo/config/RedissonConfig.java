package com.ricky.userinfo.config;

import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {
    @Bean("getRedisson")
    public RedissonClient getRedisson() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://localhost:6379")
                .setDatabase(2)
                .setRetryInterval(5000)
                .setTimeout(13000)
                .setConnectTimeout(20000);
        return Redisson.create(config);
    }
}
