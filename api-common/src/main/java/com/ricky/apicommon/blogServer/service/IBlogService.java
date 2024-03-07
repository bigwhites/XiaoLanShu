package com.ricky.apicommon.blogServer.service;

import com.github.yulichang.base.MPJBaseService;
import com.ricky.apicommon.blogServer.entity.Blog;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author bigwhites
 * @since 2024-03-06
 */
public interface IBlogService extends MPJBaseService<Blog> {
    public Long getUserBlogCntById(String uuid);
}
