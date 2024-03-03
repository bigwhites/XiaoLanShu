package com.ricky.userinfo.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.ricky.apicommon.XiaoLanShuException;
import com.ricky.apicommon.blogServer.DTO.UploadReqDTO;
import com.ricky.apicommon.blogServer.service.IFileUploadService;
import com.ricky.apicommon.constant.Constant;
import com.ricky.apicommon.userInfo.DTO.UserDTO;
import com.ricky.apicommon.userInfo.VO.UpdateUserVo;
import com.ricky.apicommon.userInfo.entity.UserBasic;
import com.ricky.apicommon.userInfo.entity.UserDetail;
import com.ricky.apicommon.userInfo.service.IUserDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ricky.userinfo.constant.DefaultValue;

import com.ricky.userinfo.mapper.UserDetailMapper;
import jakarta.annotation.Resource;
//import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author bigwhites
 * @since 2024-02-27
 */
@Service
//@Slf4j
public class UserDetailServiceImpl extends MPJBaseServiceImpl<UserDetailMapper, UserDetail> implements IUserDetailService {

    public final Logger log = LoggerFactory.getLogger(this.getClass());
    @Resource
    private DefaultValue defaultValue;

    @Resource
    private UserDetailMapper userDetailMapper;

    @Resource
    private FollowServiceImpl followService;
    @DubboReference(check = false)
    IFileUploadService fileUploadService;


    //TODO 加入 cache机制
    public UserDTO getUserDetail(String uuid) {
        UserDTO userDTO = userDetailMapper.selectJoinOne(
                UserDTO.class,
                new MPJLambdaWrapper<UserDetail>().selectAll(UserDetail.class)
                        .select(UserBasic::getUserEmail)
                        .select(UserBasic::getUserName)
                        .rightJoin(UserBasic.class, UserBasic::getUuid, UserDetail::getUuid)
                        .eq(UserDetail::getUuid, uuid)

        );

        if (userDTO == null) {
            throw new XiaoLanShuException("无该用户");
        }
        log.info("userDto:{}", userDTO);
        userDTO = followService.checkFoFansInRedis(userDTO);
        if (userDTO.uAbout == null) {
            userDTO.uAbout = defaultValue.getUserAbout();
        }
        if (StringUtils.isEmpty(userDTO.uSex)) {
            userDTO.uSex = defaultValue.getUserSex();
        }
        if (userDTO.nickname == null) {
            userDTO.nickname = defaultValue.getUserNickname();
        }

        StringBuilder sb = new StringBuilder();
        sb.append(Constant.ROOT_PATH);
        String uAvatar = userDTO.uAvatar;
        sb.append(defaultValue.getAvatarPrefix()).append("/");
        if (!StringUtils.isEmpty(uAvatar)) {
            sb.append(uAvatar);
        } else {
            sb.append(defaultValue.getAvatar());
        }
        userDTO.uAvatar = sb.toString();

        StringBuilder sbb = new StringBuilder();
        sbb.append(Constant.ROOT_PATH).append(defaultValue.getCoverPrefix());
        if (!StringUtils.isEmpty(userDTO.cover)) {
            sbb.append("/").append(userDTO.cover);
        } else {
            sbb.append("/").append(defaultValue.getAvatar());
        }
        userDTO.cover = sbb.toString();
        return userDTO;
    }

    public UploadReqDTO updateCoverOrAvaTar(String uuid, String oriFileName, String type) throws Exception {

        //检验user是否有效
        boolean exists = getBaseMapper().exists(new LambdaQueryWrapper<UserDetail>().eq(UserDetail::getUuid, uuid));
        if (!exists) {
            throw new XiaoLanShuException("用户不存在");
        }
        String path = type.equals("cover") ? defaultValue.getCoverPrefix() : defaultValue.getAvatarPrefix();
        UploadReqDTO reqDTO = fileUploadService.uploadOneFile(path, oriFileName);
        if (reqDTO == null) {
            throw new XiaoLanShuException("文件格式错误");
        }
        Thread.ofVirtual().start(() -> {
            UserDetail userDetail = super.getBaseMapper().selectById(uuid);

            String oriName = null;      //删除原来的图片文件
            if (type.equals("cover")) {
                oriName = userDetail.getCover();
                userDetail.setCover(reqDTO.fileName);
            } else {
                oriName = userDetail.getUAvatar();
                userDetail.setUAvatar(reqDTO.fileName);
            }
            if (!StringUtils.isEmpty(oriName)) {
                fileUploadService.deleteoneFile(path, oriName); //rpc调用
            }
            super.getBaseMapper().updateById(userDetail);
        });

        return reqDTO;
    }


    public void updateUser(UpdateUserVo userVo) {
        boolean exists = userDetailMapper.exists(
                new LambdaQueryWrapper<UserDetail>().eq(UserDetail::getUuid, userVo.uuid));
//        UserDetail byId = userDetailMapper.selectById(userDetail.getUuid());
        if (!exists) {
            throw new XiaoLanShuException("没有该用户");
        }
        LambdaUpdateWrapper<UserDetail> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserDetail::getUuid, userVo.uuid);
        if (!StringUtils.isEmpty(userVo.nickname)) {
            wrapper.set(UserDetail::getNickname, userVo.nickname);
        }
        if (userVo.birthday != null) {
            wrapper.set(UserDetail::getBirthday,userVo.birthday);
        }
        if (!StringUtils.isEmpty(userVo.uSex)) {
            wrapper.set(UserDetail::getUSex, userVo.uSex);
        }
        if (!StringUtils.isEmpty(userVo.uAbout)) {
            wrapper.set(UserDetail::getUAbout,userVo.uAbout);
        }
        if (userDetailMapper.update(null, wrapper) != 1) {
            throw new XiaoLanShuException("更新失败");
        }
    }
}
