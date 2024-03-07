package com.ricky.blogserver.controller;

import com.ricky.apicommon.DefalutGroup;
import com.ricky.apicommon.blogServer.DTO.UploadReqDTO;
import com.ricky.apicommon.blogServer.VO.NewBlogVO;
import com.ricky.apicommon.utils.result.R;
import com.ricky.apicommon.utils.result.ResultFactory;
import com.ricky.blogserver.serviceImpl.BlogServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

@RequestMapping("/blog")
public class BlogController {


    @Resource
    BlogServiceImpl blogService;

    /**
     * @param newBlogVO 提供新内容的信息
     * @return 返回成功后的token
     * @description 发布一条内容的请求，生成文件上传的token（redis-key），将文件
     * 写入到数据库中
     * @author Ricky01
     * @since 2024/3/7
     **/
    @PostMapping("/publish")
    public R<UploadReqDTO> publish(@RequestBody @Validated(value = {DefalutGroup.class}) NewBlogVO newBlogVO) {
        UploadReqDTO uploadReqDTO = blogService.publishBlog(newBlogVO);
        return ResultFactory.success(uploadReqDTO);
    }
}
