package com.ricky.blogserver.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ricky.apicommon.blogServer.DTO.NoteCoverDTO;
import com.ricky.apicommon.utils.result.R;
import com.ricky.apicommon.utils.result.ResultFactory;
import com.ricky.blogserver.serviceImpl.ElaSearchServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search")
public class ElaSearchController {

    @Resource
    ElaSearchServiceImpl elaSearchService;

    /**
     * @param keyword 关键词，以空白分割
     * @return null
     * @description 根据关键词分页查询笔记
     * @author Ricky01
     * @since 2024/3/12
     **/
    @GetMapping("/pageByKeyword")
    public R<Object> getPageByKeyword(@RequestParam("keyword") String keyword,
                                      @RequestParam("page") Integer page,
                                      @RequestParam("pageSize") Integer pageSize) {
        keyword = keyword.replaceAll("\\s", ""); //去除空白
        IPage<NoteCoverDTO> page1 = elaSearchService.getPageByKeyword(keyword, page, pageSize);
        return ResultFactory.success(page1);
//        return ResultFactory.fail("未实现");
    }

}
