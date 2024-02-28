package com.ricky.userinfo.serviceImpl;

import com.ricky.apicommon.XiaoLanShuException;
import com.ricky.apicommon.userInfo.entity.UserDetail;
import com.ricky.apicommon.userInfo.service.IUserDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ricky.userinfo.constant.DefaultValue;
import com.ricky.userinfo.mapper.UserDetailMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author bigwhites
 * @since 2024-02-27
 */
@Service
@Slf4j
public class UserDetailServiceImpl extends ServiceImpl<UserDetailMapper, UserDetail> implements IUserDetailService {


    public UserDetail getUserDetail(String uuid) {
        UserDetail userDetail = super.getById(uuid);
        if (userDetail == null) {
            log.info("未找到:" + uuid);
            throw new XiaoLanShuException("无该用户");
        }
        if (userDetail.getUAbout() == null) {
            userDetail.setUAbout(DefaultValue.USER_ABOUT);
        }
        if (userDetail.getUSex() == null) {
            userDetail.setUSex(DefaultValue.USER_SEX);
        }
        if (userDetail.getNickname() == null) {
            userDetail.setNickname(DefaultValue.USER_NICKNAME);
        }
        log.info("收到user:{}的detail请求",userDetail.getUuid());
        return userDetail;
    }
}
