package com.ricky.blogserver.serviceImpl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.ricky.apicommon.XiaoLanShuException;
import com.ricky.apicommon.blogServer.DTO.BlogBasicDTO;
import com.ricky.apicommon.blogServer.DTO.UploadReqDTO;
import com.ricky.apicommon.blogServer.VO.NewBlogVO;
import com.ricky.apicommon.blogServer.entity.Blog;
import com.ricky.apicommon.blogServer.entity.BlogImage;
import com.ricky.apicommon.blogServer.entity.BlogStatus;
import com.ricky.apicommon.blogServer.service.IBlogService;
import com.ricky.apicommon.constant.Constant;
import com.ricky.apicommon.userInfo.service.IUserBasicService;
import com.ricky.blogserver.config.RabbitConfig;
import com.ricky.blogserver.mapper.BlogMapper;
import jakarta.annotation.Resource;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author bigwhites
 * @since 2024-03-06
 */
@Service
@RefreshScope
@DubboService
public class BlogServiceImpl extends MPJBaseServiceImpl<BlogMapper, Blog> implements IBlogService {

    @DubboReference(check = false)
    IUserBasicService userBasicService;
    @Resource
    FileUploadServiceImpl fileUploadService;
    @Resource
    BlogImageServiceImpl blogImageService;


    @Value("${filePath.blogs}")
    String blogRootPath;

    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    RabbitTemplate rabbitTemplate;

    /**
     * @param uuid 发布人的uuid
     * @return 用户的关注数量
     * @description 获取用户的博客数量
     * @since 2024-03-08
     */
    public Long getUserBlogCntById(String uuid) {
        return baseMapper.selectCount(new LambdaQueryWrapper<Blog>().eq(Blog::getPubUuid, uuid));
    }

    public UploadReqDTO publishBlog(NewBlogVO newBlogVO) {
        if (userBasicService.userExistByUuid(newBlogVO.pubUuid) == 0) {
            throw new XiaoLanShuException("不存在该用户");
        }

        UploadReqDTO uploadReqDTO = fileUploadService.uploadFiles(blogRootPath +
                        "\\" + newBlogVO.pubUuid,
                ".png", newBlogVO.imgCount);  //路径为.../blog/p_uuid/uuid.png

        //发送消息到mq： db中新增，tag中新增， （更新发布人的 博客数量）
        JSONObject messageBody = new JSONObject();
        messageBody.put("newBlogVo", newBlogVO);
        messageBody.put("uploadReqDTO", uploadReqDTO);
        rabbitTemplate.convertAndSend(RabbitConfig.ADD_BLOG_EXCHANGE, RabbitConfig.ADD_BLOG_ROUTE,
                messageBody.toString());
        return uploadReqDTO;
    }

    /**
     * @param newBlogVo    新博客的视图对象，包含博客的基本信息
     * @param uploadReqDTO 上传请求数据传输对象，包含图片列表等上传的相关信息
     * @return 布尔值，表示是否成功写入数据库
     * @description 将新博客写入数据库
     * @author Ricky01
     * @since 2024/3/7
     */
    @Transactional
    public Long writeBlog2DB(NewBlogVO newBlogVo, UploadReqDTO uploadReqDTO) {
        Blog blog = new Blog();
        try {
            BeanUtils.copyProperties(blog, newBlogVo); //将视图对象中的属性复制到实体对象
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        blog.pubUuid = newBlogVo.pubUuid;
        blog.content = newBlogVo.content;
        blog.title = newBlogVo.title;
        blog.setPublishTime(LocalDateTime.now());
        String redisKey = uploadReqDTO.nameKey;
        String coverFileName = stringRedisTemplate.opsForList().index(redisKey, 1);
        blog.setCoverFileName(coverFileName);
        this.save(blog);
        List<String> imgList = stringRedisTemplate.opsForList().range(redisKey, 1, newBlogVo.imgCount);
        ArrayList<BlogImage> blogImageList = new ArrayList<>();
        short cnt = 1;
        assert imgList != null;
        for (var img : imgList) {
            blogImageList.add(new BlogImage(img, blog.getId(), (int) cnt));
            ++cnt;
        }
        blogImageService.saveBatch(blogImageList);
        return blog.id;
    }


    public IPage<BlogBasicDTO> getBlogPage(String pubUuid, int pageNum, int pageSize) {

        try (var executors = Executors.newVirtualThreadPerTaskExecutor()) {


            IPage<BlogBasicDTO> blogDtos = baseMapper.selectJoinPage(new Page<>(pageNum, pageSize), BlogBasicDTO.class,
                    new MPJLambdaWrapper<Blog>()
                            .selectAll(Blog.class)
                            .select(BlogStatus::getAgreeCount)
                            .select(BlogStatus::getCollectionCount)
                            .select(BlogStatus::getViewCount)
                            .eq(Blog::getPubUuid, pubUuid)
                            .orderByDesc(Blog::getPublishTime)
                            .leftJoin(BlogStatus.class, BlogStatus::getBlogId, Blog::getId));
            if (blogDtos == null || CollectionUtils.isEmpty(blogDtos.getRecords())) {
                throw new XiaoLanShuException("没有该用户");
            }
            List<Callable<Object>> callables = new ArrayList<>();
            blogDtos.getRecords().forEach(blog -> {
                callables.add(() -> {
                    List<BlogImage> blogImages = blogImageService.getBaseMapper().selectList(new MPJLambdaWrapper<BlogImage>().
                            eq(BlogImage::getBlogId, blog.id).orderByAsc(BlogImage::getSort));
                    blog.imageList = new ArrayList<>();
                    blogImages.forEach(blogImage -> {
                        String fileName = Constant.ROOT_PATH + blogRootPath + "/" + blog.pubUuid + "/" +
                                blogImage.fileName;
                        blog.imageList.add(fileName);
                    });
                    return null;
                });
            });
            List<Future<Object>> futures = executors.invokeAll(callables);
            for (Future<Object> future : futures) {
                future.get();
            }
            return blogDtos;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
