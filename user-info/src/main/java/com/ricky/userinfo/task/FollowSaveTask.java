package com.ricky.userinfo.task;

import com.ricky.apicommon.constant.RedisLockPrefix;
import com.ricky.apicommon.constant.RedisPrefix;
import com.ricky.userinfo.config.RedissonConfig;
import com.ricky.userinfo.serviceImpl.FollowServiceImpl;
import com.ricky.userinfo.utils.RedisUtil;
import jakarta.annotation.Resource;
import org.apache.commons.logging.LogFactory;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class FollowSaveTask {

    @Resource
    FollowServiceImpl followService;

    private Logger log = LoggerFactory.getLogger(this.getClass());


    @Async("tinyPool")
    @Scheduled(fixedDelay = 1000 * 10)
    public void FollowCntsFromRedis2Db() {
        followService.CacheCnts2DB();
    }

    @Async("tinyPool")
    @Scheduled(fixedDelay = 1000 * 20)
    public void FollowFromRedis2Db() {
        followService.cacheFollowList2DB();
    }


}
