package com.ricky.blogserver.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ricky.apicommon.DefalutGroup;
import com.ricky.apicommon.blogServer.DTO.BlogBasicDTO;
import com.ricky.apicommon.blogServer.DTO.NoteCoverDTO;
import com.ricky.apicommon.blogServer.DTO.NoteDto;
import com.ricky.apicommon.blogServer.DTO.UploadReqDTO;
import com.ricky.apicommon.blogServer.VO.NewBlogVO;
import com.ricky.apicommon.utils.result.R;
import com.ricky.apicommon.utils.result.ResultFactory;
import com.ricky.blogserver.serviceImpl.BlogServiceImpl;
import com.ricky.blogserver.serviceImpl.BlogViewServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController

@RequestMapping("/blog")
public class BlogController {

    @Resource
    BlogViewServiceImpl blogViewService;

    @Resource
    BlogServiceImpl blogService;

    /**
     * @param newBlogVO 提供新内容的信息
     * @return 返回成功后的token
     * @description 发布一条内容的请求，生成文件上传的token（redis-key），将文件
     * 写入到数据库中，并返回redis-key,前端根据该key作为token上传文件
     * @author Ricky01
     * @since 2024/3/7
     **/
    @PostMapping("/publish")
    public R<UploadReqDTO> publish(@RequestBody @Validated(value = {DefalutGroup.class}) NewBlogVO newBlogVO) {
        UploadReqDTO uploadReqDTO = blogService.publishBlog(newBlogVO);
        return ResultFactory.success(uploadReqDTO);
    }

    /**
     * @param uuid     发布人的uuid
     * @param page     页码
     * @param pageSize 每页大小
     * @return 分页对象
     * @description 根据发布人分页查询其笔记
     * @author Ricky01
     * @since 2024/3/7
     **/
    @PostMapping("/getByPid/{page}/{pageSize}/{pubUuid}")
    public R<IPage<BlogBasicDTO>> getBlogPage(
            @PathVariable("page") Integer page,
            @PathVariable("pageSize") Integer pageSize,
            @PathVariable("pubUuid") String uuid
    ) {
        return ResultFactory.success(blogService.getBlogPage(uuid, page, pageSize));
    }

    /**
     * @param uuid     发布人的uuid
     * @param page     页码
     * @param pageSize 每页大小
     * @return 分页对象
     * @description 根据发布人分页查询其笔记
     * @author Ricky01
     * @since 2024/3/10
     **/
    @GetMapping("/getByPid/{page}/{pageSize}/{viewUuid}")
    public R<Object> getViewHistoryPage(
            @PathVariable("page") Integer page,
            @PathVariable("pageSize") Integer pageSize,
            @PathVariable("viewUuid") String uuid
    ) throws Exception {
        Page<NoteCoverDTO> viewHistoryByPage =
                blogViewService.getViewHistoryByPage(uuid, page, pageSize);
        return ResultFactory.success(viewHistoryByPage);

    }


    /**
     * @param id       笔记的id
     * @param viewUuid 查看笔记人的uuid
     * @return 笔记DTO对象
     * @description 根据id获取笔记 详细信息
     * @author Ricky01
     * @since 2024/3/10
     **/
    @GetMapping("/getNoteByBId")
    public R<NoteDto> getNoteByBId(@RequestParam("blogId") Long id,
                                   @RequestParam("viewUuid") String viewUuid) throws Exception {
        return ResultFactory.success(blogService.getByBId(id, viewUuid));
    }
}
