package com.ricky.userinfo.controller;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.ricky.apicommon.XiaoLanShuException;
import com.ricky.apicommon.userInfo.DTO.SearchUserDTO;
import com.ricky.apicommon.userInfo.DTO.UserDTO;
import com.ricky.apicommon.userInfo.VO.ChangeFollowVO;
import com.ricky.apicommon.userInfo.VO.UpdateUserVo;
import com.ricky.apicommon.userInfo.entity.Follow;
import com.ricky.apicommon.userInfo.entity.UserDetail;
import com.ricky.apicommon.utils.result.R;
import com.ricky.apicommon.utils.result.ResultFactory;
import com.ricky.userinfo.constant.FollowStatusEnum;
import com.ricky.userinfo.serviceImpl.FollowServiceImpl;
import com.ricky.userinfo.serviceImpl.UserBasicServiceImpl;
import com.ricky.userinfo.serviceImpl.UserDetailServiceImpl;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {
    @Resource
    UserBasicServiceImpl userBasicService;
    @Resource
    UserDetailServiceImpl userDetailService;
    @Resource
    FollowServiceImpl followService;
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/userDetail/{uId}")
    public R<UserDTO> getDetail(@PathVariable String uId) {
        try {
            var userDTO = userDetailService.getUserDetail(uId);
            return ResultFactory.success(userDTO);
        } catch (XiaoLanShuException e) {
            return ResultFactory.fail(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultFactory.fail();
        }
    }

    @GetMapping("/searchUserByTypes") //根据username或nickname搜索 =>nickname时最多搜索10个
    public R<Object> searchUserByNameOrNick(@RequestParam("uNameOrNick") String uNameOrNick,
                                            @RequestParam("uuid") String uuid) {

        try {
            List<SearchUserDTO> searchUserDTOs = userBasicService.searchByNickOrName(uNameOrNick, uuid);
            if (CollectionUtils.isEmpty(searchUserDTOs)) {  //没有搜索到结果
                return ResultFactory.success(null, "no result");
            }
            return ResultFactory.success(searchUserDTOs);

        } catch (Exception e) {
            e.printStackTrace();
            return ResultFactory.fail();
        }
    }

    @PostMapping("/changeFollowStatus")
    public R<Boolean> changeFollowStatus(@RequestBody ChangeFollowVO changeFollowVO) {
        try {
            Boolean status = followService.changeFollowStatus(changeFollowVO.fromUuid(), changeFollowVO.toUuid());
            return ResultFactory.success(status);
        } catch (XiaoLanShuException e) {
            return ResultFactory.fail(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultFactory.fail();
        }
    }

    @PostMapping("/updateUDetail")
    public R<Boolean> updateUDetail(@RequestBody UpdateUserVo userVo) {
        try {
            userDetailService.updateUser(userVo);
            return ResultFactory.success(true);
        } catch (XiaoLanShuException e) {
            return ResultFactory.fail(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultFactory.fail();
        }
    }

    @PostMapping("/isFollow")
    public R<Boolean> isFoolow(@RequestBody ChangeFollowVO followVO) {
        return ResultFactory.success(
                followService.checkFollow(
                        followVO.fromUuid(),
                        followVO.toUuid()).isFollow());
    }

    @GetMapping("/test")
    public R<String> test() {
        try {
//            followService.cacheFollowList2DB();
//            followService.save(new Follow("dddd", "ssdd"));
        } catch (Exception e) {
            return ResultFactory.fail(e.getMessage());
        }
        return ResultFactory.success("ok");
    }

}
