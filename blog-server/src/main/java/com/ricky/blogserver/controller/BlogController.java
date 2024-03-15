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
import com.ricky.blogserver.serviceImpl.*;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Objects;

@RestController
@RefreshScope
@RequestMapping("/blog")
public class BlogController {

    @Resource
    private BlogViewServiceImpl blogViewService;
    @Resource
    private BlogServiceImpl blogService;
    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;
    @Resource
    private BlogAgreeServiceImpl agreeService;
    @Resource
    private BlogCollectionServiceImpl blogCollectionService;
    @Resource
    private CollectionHistoryVServiceImpl collectionHistoryVService;


    @Value("${constant.blogOper.collect}")
    private Integer COLLECT;//2
    @Value("${constant.blogOper.agree}")
    private Integer AGREE; //1


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
     * @param page     当前页数，本项目中与mp保持一致，页数均从1开始
     * @param pageSize 分页大小
     * @return 分页对象
     * @description
     * @author Ricky01
     * @since 2024/3/12
     **/
    @GetMapping("/getNewByPage")
    public R<Page<NoteCoverDTO>> getNewByPage(
            @RequestParam("page") Integer page,
            @RequestParam("pageSize") Integer pageSize
    ) {
        Page<NoteCoverDTO> newBlogByPage = blogService.getNewBlogByPage(page, pageSize);
        return ResultFactory.success(newBlogByPage);
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
    @GetMapping("/getHistoryByVId/{page}/{pageSize}/{viewUuid}")
    public R<Page<NoteCoverDTO>> getViewHistoryPage(
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

    /**
     * @param viewUuid 进行点赞或收藏的用户
     * @return 操作后的点赞、收藏状态
     * @description 点赞或收藏，根据operation进行区分<br/>
     * 1为点赞，2为收藏
     * @author Ricky01
     * @since 2024/3/13
     **/
    @GetMapping("/agreeOrCollect/{viewUuid}")
    public R<Boolean> agreeOrCollect(@RequestParam("blogId") Long id,
                                     @RequestParam("operation") Integer operation,
                                     @PathVariable("viewUuid") String viewUuid) {
        if (Objects.equals(operation, AGREE)) {
            Boolean b = agreeService.agreeBlogById(id, viewUuid);
            return ResultFactory.success(b);
        } else if (Objects.equals(operation, COLLECT)) {  //2
            Boolean c = blogCollectionService.collectionBlogById(id, viewUuid);
            return ResultFactory.success(c);
        }
        return ResultFactory.fail();
    }

    /**
     * @param viewUuid 用户的uuid
     * @param page     页码
     * @param pageSize 每页大小
     * @return 分页对象
     * @description 分页查询指定用户的收藏记录
     * @author Ricky01
     * @since 2024/3/10
     **/
    @GetMapping("/collectionHistoryByVId")
    public R<Page<NoteCoverDTO>> collectionHistoryByVId(
            @RequestParam("viewUuid") String viewUuid,
            @RequestParam("page") Integer page,
            @RequestParam("pageSize") Integer pageSize
    ) throws Exception {
        Page<NoteCoverDTO> collectionHistoryByPage =
                collectionHistoryVService
                        .getCollectionHistoryByPage(viewUuid, page, pageSize);
        return ResultFactory.success(collectionHistoryByPage);
    }


}
