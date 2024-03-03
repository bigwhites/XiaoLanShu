package com.ricky.userinfo.serviceImpl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.ricky.apicommon.XiaoLanShuException;
import com.ricky.apicommon.constant.Constant;
import com.ricky.apicommon.constant.RedisPrefix;
import com.ricky.apicommon.userInfo.DTO.SearchUserDTO;
import com.ricky.apicommon.userInfo.DTO.UserDTO;
import com.ricky.apicommon.userInfo.entity.UserBasic;
import com.ricky.apicommon.userInfo.entity.UserDetail;
import com.ricky.apicommon.userInfo.service.IUserBasicService;
import com.ricky.apicommon.utils.JwtUtil;
import com.ricky.apicommon.utils.TokenRecord;
import com.ricky.apicommon.utils.result.ResultFactory;
import com.ricky.userinfo.constant.DefaultValue;
import com.ricky.userinfo.mapper.UserBasicMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ricky.userinfo.mapper.UserDetailMapper;
import com.ricky.userinfo.utils.JedisUtils;
import jakarta.annotation.Resource;
//import javax.annotation.Resource;
//import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.checkerframework.checker.units.qual.A;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <p>
 * 用户基本信息 服务实现类
 * </p>
 *
 * @author bigwhites
 * @since 2024-02-22
 */
@Service
//@Slf4j
public class UserBasicServiceImpl extends MPJBaseServiceImpl<UserBasicMapper, UserBasic> implements IUserBasicService {

    @Autowired
    JedisUtils jedisUtils;

    @Resource
    UserDetailMapper userDetailMapper;

    @Resource
    UserBasicMapper userBasicMapper;

    @Resource
    private DefaultValue defaultValue;

    @Resource
    private FollowServiceImpl followService;


    final private Logger log = LoggerFactory.getLogger(UserBasicServiceImpl.class);

    @Transactional
    public Map<String, Object> reg(String jsonData) {
        Jedis jedis = null;
        try {
            JSONObject jsonObj = JSONObject.parseObject(jsonData);
            String inputValidCode = jsonObj.getString("validCode");
            UserBasic userBasic = jsonObj.getObject(Constant.USER_AFTER_LOG_SIGN, UserBasic.class);

//           //验证验证码  测试时关闭
//            jedis = jedisUtils.getJedis();
//            String validCode = jedis.get(userBasic.getUserEmail());
//            log.info(inputValidCode);
//            if (!Objects.equals(validCode, inputValidCode)) {
//                throw new XiaoLanShuException("验证码已经过期");
//            }
            //重新检查一次
            boolean existedUser = super.getBaseMapper().exists(new LambdaQueryWrapper<UserBasic>()
                    .eq(UserBasic::getUserEmail, userBasic.getUserEmail()));
            if (existedUser) {
                throw new XiaoLanShuException("存在该用户");
            }

            int cnt = 0;
            while (true) {
                // 获取当前日期
                java.time.LocalDate now = LocalDate.now();

                // 创建一个符合需求的日期格式器
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
                // 将当前日期格式化为字符串
                String formattedDate = now.format(formatter);
                String randomName = RandomStringUtils.random(6, true, true);
                userBasic.setUserName("XLS" + formattedDate + randomName);
                String pwd = jsonObj.getString("pwd");
                if (!StringUtils.isEmpty(pwd)) {
                    userBasic.setPwd(pwd);
                }
                //insert 2 db
                boolean saved = super.save(userBasic);
                if (saved) {
                    UserDetail userDetail = new UserDetail(userBasic.getUuid());
                    userDetail.setFollowCount(0);
                    userDetail.setFansCount(0);
                    userDetailMapper.insert(userDetail);
                    break;
                }
                cnt++;
                if (cnt >= 3) {
                    throw new RuntimeException();
                }
                log.info(userBasic.toString());
            }
            //删除验证码
//            jedis.del(userBasic.getUserEmail());

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

    public List<SearchUserDTO> searchByNickOrName(String uNameOrNick, String uuid) {
        uNameOrNick = uNameOrNick.replaceAll("\\s", ""); //去除所有无用字符
        List<SearchUserDTO> userDTOs = new ArrayList<>();
        if (StringUtils.isEmpty(uNameOrNick)) {
            return null;
        } else if (uNameOrNick.charAt(0) == '@') {  //根据id(username)搜索
            uNameOrNick = uNameOrNick.substring(1);  //去除@
            var uniUser = userBasicMapper.selectJoinOne(SearchUserDTO.class,
                    new MPJLambdaWrapper<UserBasic>().selectAll(UserBasic.class)
                            .select(UserDetail::getNickname)
                            .select(UserDetail::getUAvatar)
                            .select(UserDetail::getUSex)
                            .select(UserDetail::getFansCount)
                            .select(UserDetail::getFollowCount)
                            .innerJoin(UserDetail.class, UserDetail::getUuid, UserBasic::getUuid)
                            .eq(UserBasic::getUserName, uNameOrNick)
                            .ne(UserBasic::getUuid, uuid)

            );
            if (uniUser != null) {
                userDTOs.add(uniUser);
            }
        } else {   //根据nickname搜索  在uDetail中搜索
            userDTOs = userDetailMapper.selectJoinList(SearchUserDTO.class,
                    new MPJLambdaWrapper<UserDetail>().selectAll(UserDetail.class)
                            .select(UserBasic::getUserName)
                            .innerJoin(UserBasic.class, UserBasic::getUuid, UserDetail::getUuid)
                            .likeIfExists(UserDetail::getNickname, uNameOrNick)
                            .ne(UserDetail::getUuid, uuid)
                            .last("LIMIT 10")
            );
        }
        if (!CollectionUtils.isEmpty(userDTOs)) {
            for (var dto : userDTOs) {
                //检查是否关注 uuid(自己) dto.uuid别人  12未关注 34关注
                dto.isFollow = followService.checkFollow(uuid, dto.uuid).code > 2;
                dto = followService.checkFoFansInRedis(dto);
                StringBuilder sb = new StringBuilder();
                sb.append(Constant.ROOT_PATH);
                String uAvatar = dto.uAvatar;
                sb.append(defaultValue.getAvatarPrefix()).append("/");
                if (!StringUtils.isEmpty(uAvatar)) {
                    sb.append(uAvatar);
                } else {
                    sb.append(defaultValue.getAvatar());
                }
                dto.uAvatar = sb.toString();
                if (dto.nickname == null) {
                    dto.nickname = defaultValue.getUserNickname();
                }
            }
        }
        return userDTOs;
    }
}
