package com.ricky.blogserver.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.ricky.apicommon.blogServer.entity.BlogCollection;
import com.ricky.apicommon.blogServer.service.IBlogCollectionService;
import com.ricky.blogserver.mapper.BlogCollectionMapper;
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
 * @since 2024-03-14
 */
@Service
public class BlogCollectionServiceImpl extends MPJBaseServiceImpl<BlogCollectionMapper, BlogCollection> implements IBlogCollectionService {
    @Resource
    private BlogStatusServiceImpl blogStatusService;

    @Resource(name = "tinyPool")
    VirtualThreadTaskExecutor virtualThreadTaskExecutor;

    public Boolean isCollection(Long blogId, String viewUuid) {
        return super.baseMapper.exists(new LambdaQueryWrapper<BlogCollection>()
                .eq(BlogCollection::getBlogId, blogId)
                .eq(BlogCollection::getCollectUuid, viewUuid)
                .eq(BlogCollection::getStatus, '1')
        );
    }

    @Transactional
    public Boolean collectionBlogById(Long blogId, String viewUuid) {
        BlogCollection blogCollection = baseMapper.selectOne(new LambdaQueryWrapper<BlogCollection>()
                .eq(BlogCollection::getBlogId, blogId)
                .eq(BlogCollection::getCollectUuid, viewUuid)
        );
        boolean existCollectRel = blogCollection != null;  //表中是否有这条记录
        if (!existCollectRel) {  //新增一条记录

            virtualThreadTaskExecutor.submit(() -> {
                BlogCollection collection = new BlogCollection();
                collection.status = "1";
                collection.blogId = blogId;
                collection.collectUuid = viewUuid;
                collection.createTime = LocalDateTime.now();
                baseMapper.insert(collection);
                blogStatusService.updateCount(blogId, BlogStatusServiceImpl.COLLECTION_COUNT, true);
            });

            return true;
        } else if (blogCollection.status.equals("0")) {  //已经取消收藏
            virtualThreadTaskExecutor.submit(() -> {
                blogCollection.setStatus("1");
                super.baseMapper.updateById(blogCollection);
                blogCollection.createTime = LocalDateTime.now();
                blogStatusService.updateCount(blogId, BlogStatusServiceImpl.COLLECTION_COUNT, true);
            });
            return true;
        } else { //此前收藏，现在取消收藏
            virtualThreadTaskExecutor.submit(() -> {
                blogCollection.status = "0";  //采用软删除+定时批量删除的方式
                blogCollection.createTime = LocalDateTime.now();
                super.baseMapper.updateById(blogCollection);
                blogStatusService.updateCount(blogId, BlogStatusServiceImpl.COLLECTION_COUNT, false);
            });
            return false;
        }
    }
}
