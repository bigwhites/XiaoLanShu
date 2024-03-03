package com.ricky.userinfo.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.ricky.apicommon.XiaoLanShuException;
import com.ricky.apicommon.constant.RedisPrefix;
import com.ricky.apicommon.userInfo.DTO.SearchUserDTO;
import com.ricky.apicommon.userInfo.DTO.UserDTO;
import com.ricky.apicommon.userInfo.entity.Follow;
import com.ricky.apicommon.userInfo.entity.UserDetail;
import com.ricky.userinfo.constant.FollowStatusEnum;
import com.ricky.userinfo.mapper.FollowMapper;
import com.ricky.apicommon.userInfo.service.IFollowService;
import com.ricky.userinfo.mapper.UserDetailMapper;
import com.ricky.userinfo.utils.RedisUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

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
    RedisUtil redisUtil;

    @Resource
    StringRedisTemplate stringRedisTemplate;


    @Resource
    UserDetailMapper userDetailMapper;

    final private Logger log = LoggerFactory.getLogger(FollowServiceImpl.class);


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
        boolean hasKey = redisUtil.sHasKey(RedisPrefix.FOLLOW_SET + toId, fromId);
        if (hasKey) {  //redis存在直接返回
            return FollowStatusEnum.FOLLOW_REDIS;
        }
        boolean isDel = redisUtil.sHasKey(RedisPrefix.CANCEL_FOLLOW + toId, fromId);  //是否删除
        if (isDel) {
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
     **/  //TODO  粉丝计数器
    public boolean changeFollowStatus(String fromId, String toId) throws InterruptedException {
        String key1 = RedisPrefix.FANS_CNT + toId;
        String key2 = RedisPrefix.FOLLOW_CNT + fromId;
        //计数器读取入缓存
        Thread t1 = Thread.ofVirtual().start(() -> {

            if (Boolean.FALSE.equals(stringRedisTemplate.hasKey(key1))) {
                UserDetail userDetail = userDetailMapper.selectById(toId);  //to粉丝计数器
                stringRedisTemplate.opsForValue().setIfAbsent(key1, String.valueOf(userDetail.getFansCount()));
            }
        });
        Thread t2 = Thread.ofVirtual().start(() -> {  //from关注计数器

            if (Boolean.FALSE.equals(stringRedisTemplate.hasKey(key2))) {
                UserDetail userDetail = userDetailMapper.selectById(fromId);
                stringRedisTemplate.opsForValue().setIfAbsent(key2, String.valueOf(userDetail.getFollowCount()));
            }
        });

        var followStatus = this.checkFollow(fromId, toId);  //当前是否关注
        if (followStatus.code <= 2) { //插入到redis (没关注)
            redisUtil.sSet(RedisPrefix.FOLLOW_SET + toId, fromId);
            if (followStatus == FollowStatusEnum.CANCEL_REDIS) { //移除缓存中的删除操作
                redisUtil.setRemove(RedisPrefix.CANCEL_FOLLOW + toId, fromId);
            }
            t1.join();
            t2.join();
            stringRedisTemplate.opsForValue().increment(key1);
            stringRedisTemplate.opsForValue().increment(key2);
            return true;

        } else if (followStatus == FollowStatusEnum.FOLLOW_DB) {  //此前db中关注了
            redisUtil.sSet(RedisPrefix.CANCEL_FOLLOW + toId, fromId);
        } else {// (followStatus == FollowStatus.FOLLOW_REDIS) 如果数据没有写入db则从redis移除即可
            redisUtil.setRemove(RedisPrefix.FOLLOW_SET + toId, fromId);
        }
        t1.join();
        t2.join();
        stringRedisTemplate.opsForValue().decrement(key1);
        stringRedisTemplate.opsForValue().decrement(key2);
        return false;
    }

}
