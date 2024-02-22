package com.ricky.userinfo.serviceImpl;

import com.ricky.apicommon.userInfo.entity.UserBasic;
import com.ricky.apicommon.userInfo.service.IUserBasicService;
import com.ricky.userinfo.mapper.UserBasicMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户基本信息 服务实现类
 * </p>
 *
 * @author bigwhites
 * @since 2024-02-22
 */
@Service
public class UserBasicServiceImpl extends ServiceImpl<UserBasicMapper, UserBasic> implements IUserBasicService {

}
