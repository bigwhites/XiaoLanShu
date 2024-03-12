package com.ricky.blogserver.serviceImpl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.ricky.apicommon.XiaoLanShuException;
import com.ricky.apicommon.blogServer.DTO.BlogBasicDTO;
import com.ricky.apicommon.blogServer.DTO.NoteCoverDTO;
import com.ricky.apicommon.blogServer.DTO.NoteDto;
import com.ricky.apicommon.blogServer.DTO.UploadReqDTO;
import com.ricky.apicommon.blogServer.VO.NewBlogVO;
import com.ricky.apicommon.blogServer.VO.NoteUserVO;
import com.ricky.apicommon.blogServer.entity.Blog;
import com.ricky.apicommon.blogServer.entity.BlogImage;
import com.ricky.apicommon.blogServer.entity.BlogStatus;
import com.ricky.apicommon.blogServer.entity.BlogView;
import com.ricky.apicommon.blogServer.service.IBlogService;
import com.ricky.apicommon.userInfo.service.IUserBasicService;
import com.ricky.blogserver.config.RabbitConfig;
import com.ricky.blogserver.mapper.BlogMapper;
import com.ricky.blogserver.mapper.BlogStatusMapper;
import jakarta.annotation.Resource;
import org.apache.commons.beanutils.BeanUtils;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

    @Resource(name = "tinyPool")
    VirtualThreadTaskExecutor virtualThreadTaskExecutor;
    @Resource
    private BlogViewServiceImpl blogViewService;

    @Resource
    private BlogStatusMapper blogStatusMapper;

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
    public Blog writeBlog2DB(NewBlogVO newBlogVo, UploadReqDTO uploadReqDTO) {
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
        return blog;
    }

    /**
     * @param uuid 用户的uuid
     * @description RPC调用检查用户是否存在
     * @author Ricky01
     * @since 2024/3/10
     **/
    public void checkUserExist(String uuid) {
        Integer i = userBasicService.userExistByUuid(uuid);
        if (i == 0) {
            throw new XiaoLanShuException("用户不存在");
        }
    }

    public IPage<BlogBasicDTO> getBlogPage(String pubUuid, int pageNum, int pageSize) {

        try (var executors = Executors.newVirtualThreadPerTaskExecutor()) {


            IPage<BlogBasicDTO> blogDtoPage = baseMapper.selectJoinPage(new Page<>(pageNum, pageSize), BlogBasicDTO.class,
                    new MPJLambdaWrapper<Blog>()
                            .selectAll(Blog.class)
                            .select(BlogStatus::getAgreeCount)
                            .select(BlogStatus::getCollectionCount)
                            .select(BlogStatus::getViewCount)
                            .eq(Blog::getPubUuid, pubUuid)
                            .orderByDesc(Blog::getPublishTime)
                            .leftJoin(BlogStatus.class, BlogStatus::getBlogId, Blog::getId));
            if (blogDtoPage == null || CollectionUtils.isEmpty(blogDtoPage.getRecords())) {
                throw new XiaoLanShuException("没有该用户");
            }
            List<Callable<Object>> callables = new ArrayList<>();
            List<BlogBasicDTO> blogDtosList = new ArrayList<>();
            Lock lock = new ReentrantLock();
            blogDtoPage.getRecords().forEach(blog -> {
                callables.add(() -> {
                    BlogBasicDTO dto = blogImageService.fillImageList(blog);
                    lock.lock();
                    blogDtosList.add(dto);  //由于多线程顺序不一致,需要排序🤫
                    lock.unlock();
                    return null;
                });
            });
            List<Future<Object>> futures = executors.invokeAll(callables);
            for (Future<Object> future : futures) {
                future.get();
            }
            blogDtosList.sort(Comparator.
                    comparing(BlogBasicDTO::getPublishTime).reversed());
            blogDtoPage.setRecords(blogDtosList);
            return blogDtoPage;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param id       博客id
     * @param viewUuid 查看者的uuid
     * @return 1
     * @description 获取博客详情
     * @author Ricky01
     * @since 2024/3/9
     **/
    public NoteDto getByBId(Long id, String viewUuid) throws Exception {
        // 1检查view 的id是否合法
        checkUserExist(viewUuid);
        NoteDto noteDto = new NoteDto();
        // 查询出博客的基础dto对象
        BlogBasicDTO blogBasicDTO = baseMapper.selectJoinOne(BlogBasicDTO.class,
                new MPJLambdaWrapper<Blog>()
                        .selectAll(Blog.class)
                        .select(BlogStatus::getAgreeCount)
                        .select(BlogStatus::getCollectionCount)
                        .select(BlogStatus::getViewCount)
                        .eq(Blog::getId, id)
                        .orderByDesc(Blog::getPublishTime)
                        .leftJoin(BlogStatus.class, BlogStatus::getBlogId, Blog::getId)
        );

        /*
         异步操作：
         1.得到所有的图片列表
         2.填充用户是否收藏、点赞过这篇记录  =》 TODO
         3.得到用户的详细信息，包括是否关注(RPC)
         4  增加浏览记录 如果已经看过则无需操作，没看过就增加一条浏览记录
         */

        Future<BlogBasicDTO> basicDTOFuture =
                virtualThreadTaskExecutor.submit(() -> blogImageService.fillImageList(blogBasicDTO));


        NoteUserVO noteUser = userBasicService.getNoteUser(blogBasicDTO.pubUuid, viewUuid); //得到用户信息
        noteDto.nickname = noteUser.nickname;
        noteDto.userName = noteUser.userName;
        noteDto.uAvatar = noteUser.uAvatar;
        noteDto.fansCount = noteUser.fansCount;
        noteDto.isFollow = noteUser.isFollow;

        BlogBasicDTO blogBasicWithImage = basicDTOFuture.get();
        noteDto.basicInfo = blogBasicWithImage;

        Future<Void> browseCntFuture = virtualThreadTaskExecutor.submit(() -> {  //该任务无需阻塞，与用户无关
            boolean exists = blogViewService.exists(
                    new LambdaQueryWrapper<BlogView>()
                            .eq(BlogView::getBlogId, blogBasicDTO.id)
                            .eq(BlogView::getViewUuid, viewUuid)
            );
            if (!exists) {  //新增一条记录，计数器加一
                blogStatusMapper.incrCountByColName(blogBasicDTO.id, BlogStatusMapper.VIEW_COUNT);
                BlogView blogView = new BlogView();
                blogView.fillDefault();
                blogView.blogId = blogBasicDTO.id;
                blogView.viewUuid = viewUuid;
                blogViewService.save(blogView);
            }
            return null;
        });

        return noteDto;

    }

    /**
     * @return 分页查询的笔记封面信息
     * @params 当前页码和单页最大数
     * @description 根据发布时间分页查询笔记
     * @author Ricky01
     * @since 2024/3/12
     **/
    public Page<NoteCoverDTO> getNewBlogByPage(Integer page, Integer pageSize) {
        Page<NoteCoverDTO> notePage = new Page<>(page, pageSize);
        Page<NoteCoverDTO> noteCoverDTOPage = baseMapper.selectJoinPage(notePage, NoteCoverDTO.class,
                new MPJLambdaWrapper<Blog>()
                        .selectAll(Blog.class)
                        .select(BlogStatus::getAgreeCount)
                        .select(BlogStatus::getCollectionCount)
                        .select(BlogStatus::getViewCount)
                        .orderByDesc(Blog::getPublishTime)
                        .leftJoin(BlogStatus.class, BlogStatus::getBlogId, Blog::getId));
        if ((noteCoverDTOPage != null) && !CollectionUtils.isEmpty(noteCoverDTOPage.getRecords())) {
            noteCoverDTOPage.getRecords().forEach(noteCoverDTO -> {
                noteCoverDTO.coverFileName = blogImageService.fillImagePath(noteCoverDTO.coverFileName, noteCoverDTO.pubUuid);

                noteCoverDTO.isAgree = false;
                //查询出博客对应的发布人的id（RPC）
                NoteUserVO noteUser = userBasicService.getNoteUser(noteCoverDTO.pubUuid, null);
                noteCoverDTO.pubUNickname = noteUser.nickname;
                noteCoverDTO.pubUAvatar = noteUser.uAvatar;

            });
        }
        return noteCoverDTOPage;

    }
}
