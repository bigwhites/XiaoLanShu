package com.ricky.blogserver.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.ricky.apicommon.blogServer.entity.BlogStatus;
import com.ricky.blogserver.mapper.BlogStatusMapper;
import com.ricky.apicommon.blogServer.service.IBlogStatusService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author bigwhites
 * @since 2024-03-07
 */
@Service
public class BlogStatusServiceImpl extends MPJBaseServiceImpl<BlogStatusMapper, BlogStatus> implements IBlogStatusService {

    @Resource
    BlogStatusMapper blogStatusMapper;

    public static final String AGREE_COUNT = "agree_count";
    public static final String VIEW_COUNT = "view_count";
    public static final String COLLECTION_COUNT = "collection_count";

    public boolean addDefaultValue(long blog_id) {
        BlogStatus blogStatus = new BlogStatus();
        blogStatus.blogId = blog_id;
        blogStatus.agreeCount = 0L;
        blogStatus.collectionCount = 0;
        blogStatus.viewCount = 0L;
        return blogStatusMapper.insert(blogStatus) == 1;
    }

    @Transactional
    public boolean updateCount(long blog_id, String column, boolean isAdd) {
        UpdateWrapper<BlogStatus> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("blog_id", blog_id);
        updateWrapper.setSql(String.format("%s = %s %s 1",
                column, column, isAdd ? "+" : "-"));
        return blogStatusMapper.update(null, updateWrapper) == 1;
    }

}

