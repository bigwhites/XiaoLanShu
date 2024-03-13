package com.ricky.userinfo.serviceImpl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ricky.apicommon.XiaoLanShuException;
import com.ricky.apicommon.constant.RedisLockPrefix;
import com.ricky.apicommon.constant.RedisPrefix;
import com.ricky.apicommon.userInfo.DTO.SearchUserDTO;
import com.ricky.apicommon.userInfo.DTO.UserDTO;
import com.ricky.apicommon.userInfo.entity.Follow;
import com.ricky.apicommon.userInfo.entity.UserDetail;
import com.ricky.apicommon.userInfo.service.IFollowService;
import com.ricky.userinfo.config.MailSentRabbitConfig;
import com.ricky.userinfo.constant.FollowStatusEnum;
import com.ricky.userinfo.mapper.FollowMapper;
import com.ricky.userinfo.mapper.UserDetailMapper;
import jakarta.annotation.Resource;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author bigwhites
 * @since 2024-03-03
 */
@Service
public class FollowServiceImpl extends MPJBaseServiceImpl<FollowMapper, Follow> implements IFollowService {

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    RedissonClient redissonClient;

    @Resource
    UserDetailMapper userDetailMapper;

    @Resource(name = "tinyPool")
    VirtualThreadTaskExecutor executor;

    @Resource
    RabbitTemplate rabbitTemplate;

    final private Logger log = LoggerFactory.getLogger(FollowServiceImpl.class);

    final static String FAN_CNT_PATTERN = RedisPrefix.FANS_CNT + "*";
    final static String FOLLOW_CNT_PATTERN = RedisPrefix.FOLLOW_CNT + "*";
    final static String FOLLOW_SET_PATTERN = RedisPrefix.FOLLOW_SET + "*";
    final static String CANCEL_SET_PATTERN = RedisPrefix.CANCEL_FOLLOW + "*";

    /**
     * @return 更新后的信息
     * @description 从redis中检查关注信息
     * @author Ricky01
     * @params
     * @since 2024/3/3
     **/
    public UserDTO checkFoFansInRedis(UserDTO userDTO) {
        String keyFollow = RedisPrefix.FOLLOW_CNT + userDTO.uuid;
        String keyFans = RedisPrefix.FANS_CNT + userDTO.uuid;
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(keyFollow))) {
            long foCnt = Long.parseLong(
                    stringRedisTemplate.opsForValue().get(keyFollow));
            userDTO.followCount = foCnt;
        }
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(keyFans))) {
            long fansCnt = Long.parseLong(
                    stringRedisTemplate.opsForValue().get(keyFans));
            userDTO.fansCount = fansCnt;
        }
        return userDTO;
    }

    public SearchUserDTO checkFoFansInRedis(SearchUserDTO userDTO) {
        String keyFollow = RedisPrefix.FOLLOW_CNT + userDTO.uuid;
        String keyFans = RedisPrefix.FANS_CNT + userDTO.uuid;
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(keyFollow))) {
            long foCnt = Long.parseLong(Objects.requireNonNull(
                    stringRedisTemplate.opsForValue().get(keyFollow)));
            userDTO.followCount = foCnt;
        }
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(keyFans))) {
            long fansCnt = Long.parseLong(Objects.requireNonNull(
                    stringRedisTemplate.opsForValue().get(keyFans)));
            userDTO.fansCount = fansCnt;
        }
        return userDTO;
    }

    /**
     * @return <=2未关注  >2关注
     * @description 是否已经关注
     * @author Ricky01
     * @params fromId toId => from是否关注了to
     * @since 2024/3/3
     **/
    public FollowStatusEnum checkFollow(String fromId, String toId) {
        boolean hasKey = Boolean.TRUE.equals(stringRedisTemplate.opsForSet().isMember(RedisPrefix.FOLLOW_SET + toId, fromId));
        if (hasKey) {  //redis存在直接返回
            return FollowStatusEnum.FOLLOW_REDIS;
        }
        Boolean isDel = stringRedisTemplate.opsForSet().isMember(RedisPrefix.CANCEL_FOLLOW + toId, fromId);  //是否删除
        if (Boolean.TRUE.equals(isDel)) {
            return FollowStatusEnum.CANCEL_REDIS;
        }
        // query db
        Long count = super.getBaseMapper().selectCount(new LambdaQueryWrapper<Follow>()
                .eq(Follow::getFromUid, fromId)
                .eq(Follow::getToUid, toId)
        );
        if (count == 0) {
            return FollowStatusEnum.NO_DB;
        } else if (count == 1) {
            return FollowStatusEnum.FOLLOW_DB;
        }
        log.error("出现问题{}:{} count:{}", fromId, toId, count);
        throw new XiaoLanShuException("出现问题");
    }

    /**
     * @return 最新状态:  0 未关注  1 已关注
     * @description 变化关注状态 from请求关注/取消to  <br/>
     * 所有操作会先从缓存中查询,如果缓存内有相应的操作就取反
     * @author Ricky01
     * @params 用户uuid
     * @since 2024/3/3
     **/
    public boolean changeFollowStatus(String fromId, String toId) throws InterruptedException {
        String key1 = RedisPrefix.FANS_CNT + toId;
        String key2 = RedisPrefix.FOLLOW_CNT + fromId;
        RLock lockFrom = redissonClient.getLock(RedisLockPrefix.USER_FO_FANS_CNT + fromId);
        RLock lockTo = redissonClient.getLock(RedisLockPrefix.USER_FO_FANS_CNT + toId);

        //计数器读取入缓存

        lockTo.lock();
        if (Boolean.FALSE.equals(stringRedisTemplate.hasKey(key1))) {
            UserDetail userDetail = userDetailMapper.selectById(toId);  //to粉丝计数器
            stringRedisTemplate.opsForValue().setIfAbsent(key1, String.valueOf(userDetail.getFansCount()));
        }
        lockTo.unlock();


        lockFrom.lock();
        if (Boolean.FALSE.equals(stringRedisTemplate.hasKey(key2))) {
            UserDetail userDetail = userDetailMapper.selectById(fromId);
            stringRedisTemplate.opsForValue().setIfAbsent(key2, String.valueOf(userDetail.getFollowCount()));
        }
        lockFrom.unlock();

        RLock lockSet = redissonClient.getLock(RedisLockPrefix.FOLLOW_LOCK + toId);

        var followStatus = this.checkFollow(fromId, toId);  //当前是否关注
        lockSet.lock();
        if (followStatus.code <= 2) { //插入到redis (没关注)
            stringRedisTemplate.opsForSet().add(RedisPrefix.FOLLOW_SET + toId, fromId);
            if (followStatus == FollowStatusEnum.CANCEL_REDIS) { //移除缓存中的删除操作
                stringRedisTemplate.opsForSet().remove(RedisPrefix.CANCEL_FOLLOW + toId, fromId);
            }
            lockSet.unlock();
            lockTo.lock();
            stringRedisTemplate.opsForValue().increment(key1);
            lockTo.unlock();
            lockFrom.lock();
            stringRedisTemplate.opsForValue().increment(key2);
            lockFrom.unlock();

            return true;

        } else if (followStatus == FollowStatusEnum.FOLLOW_DB) {  //此前db中关注了

            stringRedisTemplate.opsForSet().add(RedisPrefix.CANCEL_FOLLOW + toId, fromId);
        } else {// (followStatus == FollowStatus.FOLLOW_REDIS) 如果数据没有写入db则从redis移除即可
            stringRedisTemplate.opsForSet().remove(RedisPrefix.FOLLOW_SET + toId, fromId);
        }

        lockTo.lock();
        stringRedisTemplate.opsForValue().decrement(key1);
        lockTo.unlock();
        lockFrom.lock();
        stringRedisTemplate.opsForValue().decrement(key2);
        lockFrom.unlock();
        lockSet.unlock();
        return false;
    }


    /**
     * @return
     * @description 将当前redis所有计数器的值更新到数据库
     * @author Ricky01
     * @params
     * @since 2024/3/5
     **/
    public void CacheCnts2DB() {

        try {
            Set<String> fansKeys = stringRedisTemplate.keys(FAN_CNT_PATTERN);
            Set<String> followKey = stringRedisTemplate.keys(FOLLOW_CNT_PATTERN);

            if (CollectionUtils.isEmpty(fansKeys) || CollectionUtils.isEmpty(followKey)) {
                return;
            }

            List<String> redisKeys = Sets.union(fansKeys, followKey).stream().toList();

            List<List<String>> partitionList = Lists.partition(redisKeys, 10);
            try (var executors = Executors.newVirtualThreadPerTaskExecutor()) {
                List<Callable<Integer>> callableList = new ArrayList<>();
                for (var list : partitionList) {
                    if (CollectionUtils.isNotEmpty(list)) {
                        callableList.add(() -> {
                            List<String> uuids = new ArrayList<>();
                            for (var redisKey : list) {
                                uuids.add(redisKey.split(":")[1]);
                            }
                            List<UserDetail> userDetails = userDetailMapper.selectBatchIds(uuids);
                            int okSize = 0;
                            for (UserDetail userDetail : userDetails) {
                                RLock lockUser = redissonClient.getLock(
                                        RedisLockPrefix.USER_FO_FANS_CNT + userDetail.getUuid());
                                //redis如果有数据就是最新的
                                String fanUKey = RedisPrefix.FANS_CNT + userDetail.getUuid();
                                String followUKey = RedisPrefix.FOLLOW_CNT + userDetail.getUuid();
                                lockUser.lock();
                                if (!lockUser.isLocked()) {
                                    return 0;
                                }
                                String strFCnt = stringRedisTemplate.opsForValue().getAndDelete(followUKey);
                                String strFansCnt = stringRedisTemplate.opsForValue().getAndDelete(fanUKey);
                                long fansCnt = userDetail.getFansCount();
                                long foCnt = userDetail.getFollowCount();
                                if (strFCnt != null) {
                                    foCnt = Long.parseLong(strFCnt);
                                }
                                if (strFansCnt != null) {
                                    fansCnt = Long.parseLong(strFansCnt);
                                }

                                userDetail.setFollowCount(foCnt);
                                userDetail.setFansCount(fansCnt);
                                okSize += userDetailMapper.updateById(userDetail);
                                lockUser.unlock();
                            }
                            return okSize;
                        });

                    }
                }
                List<Future<Integer>> cnts = executors.invokeAll(callableList);
                int i = 0;
                for (var future : cnts) {
                    i += future.get();
                }
                if (i != redisKeys.size()) {
                    log.error("有更新失败");
                }
                log.info("持久化{}个关注计数器，成功{}个", redisKeys.size(), i);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    /**
     * @description 将关注消息持久化，并给消息队列发送消息
     * @author Ricky01
     * @since 2024/3
     **/
    @Transactional
    public void cacheFollowList2DB() {
//        synchronized (this) {
        try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
            var keySet = stringRedisTemplate.keys(FOLLOW_SET_PATTERN);
            var keySet2 = stringRedisTemplate.keys(CANCEL_SET_PATTERN);
            Set<String> redisKeySet = new HashSet<>();

            Future<?> future = executor.submit(() -> {
                if (keySet != null) {
                    keySet.forEach((key) -> {
                        redisKeySet.add(key.split(":")[1]);
                    });
                }
            });
            Future<?> future1 = executor.submit(() -> {
                if (keySet2 != null) {
                    keySet2.forEach((key) -> {
                        redisKeySet.add(key.split(":")[1]);
                    });
                }
            });
            future.get();
            future1.get();

            List<List<String>> partition = Lists.partition(redisKeySet.stream().toList(), 10);
            List<Callable<Void>> runables = new ArrayList<>();

            partition.forEach((uuids -> {

                runables.add(() -> {

                    for (String uuid : uuids) {
                        log.debug("持久化uuid{}", uuid);
                        String followKey = RedisPrefix.FOLLOW_SET + uuid;
                        String cancelKey = RedisPrefix.CANCEL_FOLLOW + uuid;
                        RLock lock = redissonClient.getLock(RedisLockPrefix.FOLLOW_LOCK + uuid);
                        lock.lock();
                        Set<String> updateIds = stringRedisTemplate.opsForSet().members(followKey);
                        Set<String> delIds = stringRedisTemplate.opsForSet().members(cancelKey);
                        //save to db
                        Future<?> submit = executor.submit(() -> {
                            if (CollectionUtils.isNotEmpty(updateIds)) {
                                List<Follow> follows = new ArrayList<>();
                                for (var fromId : updateIds) {
                                    Follow follow = new Follow(fromId, uuid);
                                    follows.add(follow);
                                    rabbitTemplate.convertAndSend(MailSentRabbitConfig.EXCHANGE_FOLLOW,  //向消息中心发送消息
                                            MailSentRabbitConfig.ROUTE_FOLLOW, JSON.toJSONString(follow));
//
                                }
                                this.saveBatch(follows);
                            }
                        });
                        Future<?> submit1 = executor.submit(() -> {
                            if (CollectionUtils.isNotEmpty(delIds)) {
                                for (var fromId : delIds) {
                                    baseMapper.delete(new QueryWrapper<Follow>().eq("from_uid", fromId)
                                            .eq("to_uid", uuid));
                                }
                            }
                        });
                        submit.get();
                        submit1.get();
                        stringRedisTemplate.delete(followKey);
                        stringRedisTemplate.delete(cancelKey);
                        lock.unlock();
                    }
                    return null;
                });
            }));
            List<Future<Void>> futures = exec.invokeAll(runables);
            for (var f : futures) {
                f.get();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

