package com.ricky.userinfo.controller;


import com.ricky.apicommon.XiaoLanShuException;
import com.ricky.apicommon.blogServer.DTO.UploadReqDTO;
import com.ricky.apicommon.blogServer.VO.FileUploadVO;
import com.ricky.apicommon.userInfo.entity.UserDetail;
import com.ricky.apicommon.utils.result.R;
import com.ricky.apicommon.utils.result.ResultFactory;
import com.ricky.userinfo.serviceImpl.UserDetailServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.http.HttpResponse;
import java.util.Map;

@RestController()
public class ImageController {
    @Resource
    UserDetailServiceImpl userDetailService;

    @PostMapping("/updateCoverAvaTar")
        //更新头像或背景
    R<UploadReqDTO> updateCoverOrAvaTar(@RequestBody FileUploadVO fileUploadVO) {
        try {
            return ResultFactory.success(userDetailService.updateCoverOrAvaTar(fileUploadVO.uuid,
                    fileUploadVO.oriFileName, fileUploadVO.type));
        } catch (XiaoLanShuException e) {
            return ResultFactory.fail(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultFactory.fail();
        }

    }

}
