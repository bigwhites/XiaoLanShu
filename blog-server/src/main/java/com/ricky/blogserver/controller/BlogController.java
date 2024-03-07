package com.ricky.blogserver.controller;

import com.ricky.apicommon.DefalutGroup;
import com.ricky.apicommon.blogServer.VO.NewBlogVO;
import com.ricky.apicommon.utils.result.R;
import com.ricky.apicommon.utils.result.ResultFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//import javax.validation.Valid;

@RestController

@RequestMapping("/blog")
public class BlogController {
    @PostMapping("/publish")
    public R<Object> publish(@RequestBody @Validated(value = {DefalutGroup.class}) NewBlogVO newBlogVO) {
//        return ResultFactory.success(newBlogVO);

        return null;
    }
}
