package com.ricky.userinfo.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.ricky.apicommon.XiaoLanShuException;
import com.ricky.apicommon.blogServer.DTO.UploadReqDTO;
import com.ricky.apicommon.blogServer.service.IFileUploadService;
import com.ricky.apicommon.constant.Constant;
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
public class UserDetailServiceImpl extends ServiceImpl<UserDetailMapper, UserDetail> implements IUserDetailService {

    public final Logger log = LoggerFactory.getLogger(this.getClass());
    @Resource
    private DefaultValue defaultValue;

    @DubboReference(check = false)
    IFileUploadService fileUploadService;

    public UserDetail getUserDetail(String uuid) {
        UserDetail userDetail = super.getById(uuid);
        if (userDetail == null) {
            log.info("未找到:" + uuid);
            throw new XiaoLanShuException("无该用户");
        }
        if (userDetail.getUAbout() == null) {
            userDetail.setUAbout(defaultValue.getUserAbout());
        }
        if (StringUtils.isEmpty(userDetail.getUSex())) {
            userDetail.setUSex(defaultValue.getUserSex());
        }
        if (userDetail.getNickname() == null) {
            userDetail.setNickname(defaultValue.getUserNickname());
        }
        StringBuilder sb = new StringBuilder();
        sb.append(Constant.ROOT_PATH);
        String uAvatar = userDetail.getUAvatar();
        sb.append(defaultValue.getAvatarPrefix()).append("/");
        if (!StringUtils.isEmpty(uAvatar)) {
            sb.append(uAvatar);
        } else {
            sb.append(defaultValue.getAvatar());
        }
        userDetail.setUAvatar(sb.toString());

        StringBuilder sbb = new StringBuilder();
        sbb.append(Constant.ROOT_PATH).append(defaultValue.getCoverPrefix());
        if (!StringUtils.isEmpty(userDetail.getCover())) {
            sbb.append("/").append(userDetail.getCover());
        } else {
            sbb.append("/").append(defaultValue.getAvatar());
        }
        userDetail.setCover(sbb.toString());
        return userDetail;
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
        UserDetail userDetail = super.getBaseMapper().selectById(uuid);
        if (type.equals("cover")) {
            userDetail.setCover(reqDTO.fileName);
        } else {
            userDetail.setUAvatar(reqDTO.fileName);
        }
        super.getBaseMapper().updateById(userDetail);  //TODO 升级为异步操作
        return reqDTO;
    }
}
