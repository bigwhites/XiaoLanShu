package com.ricky.blogserver.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.ricky.apicommon.blogServer.entity.BlogAgree;
import com.ricky.apicommon.blogServer.service.IBlogAgreeService;
import com.ricky.blogserver.mapper.BlogAgreeMapper;
import jakarta.annotation.Resource;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author bigwhites
 * @since 2024-03-13
 */
@Service
public class BlogAgreeServiceImpl extends MPJBaseServiceImpl<BlogAgreeMapper, BlogAgree>
        implements IBlogAgreeService {
    @Resource
    private BlogStatusServiceImpl blogStatusService;
    @Resource
    ElaSearchServiceImpl elaSearchService;
    @Resource(name = "tinyPool")
    VirtualThreadTaskExecutor virtualThreadTaskExecutor;

    /**
     * @return 当前是否点赞（原状态取反）
     * @params 笔记id和用户id
     * @description 用户对指定的笔记进行点赞、取消点赞
     * @author Ricky01
     * @since 2024/3/13
     **/
    @Transactional
    public Boolean agreeBlogById(Long blogId, String viewUuid) {
        //首先判断存不存在，存在就取消点赞
        BlogAgree blogAgree = baseMapper.selectOne(new LambdaQueryWrapper<BlogAgree>()
                .eq(BlogAgree::getBlogId, blogId)
                .eq(BlogAgree::getUuid, viewUuid)
        );
        boolean isAgree = blogAgree != null;
        //同时还要操作blog_status中的计数器 以及 es中的点赞数
        if (isAgree) { //取消点赞
            super.baseMapper.deleteById(blogAgree.id);
        } else {
            BlogAgree agree = new BlogAgree();
            agree.blogId = blogId;
            agree.uuid = viewUuid;
            agree.createTime = LocalDateTime.now();
            super.baseMapper.insert(agree);

        }
        virtualThreadTaskExecutor.submit(() -> {
            blogStatusService.updateCount(blogId, BlogStatusServiceImpl.AGREE_COUNT, !isAgree);
            elaSearchService.updateAgreeCount(blogId, isAgree ? -1L : 1L);
        });

        return !isAgree;
    }

    /**
     * @return 用户是否点赞该笔记
     * @params 笔记id和用户id
     * @description 查询用户是否关注指定的笔记
     * @author Ricky01
     * @since 2024/3/13
     **/
    public Boolean isAgree(Long blogId, String viewUuid) {
        return super.baseMapper.exists(new LambdaQueryWrapper<BlogAgree>()
                .eq(BlogAgree::getBlogId, blogId)
                .eq(BlogAgree::getUuid, viewUuid)
        );
    }
}
