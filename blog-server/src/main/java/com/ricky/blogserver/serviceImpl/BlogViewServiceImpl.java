package com.ricky.blogserver.serviceImpl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.ricky.apicommon.XiaoLanShuException;
import com.ricky.apicommon.blogServer.DTO.NoteCoverDTO;
import com.ricky.apicommon.blogServer.entity.BlogView;
import com.ricky.apicommon.blogServer.entity.ViewHistoryV;
import com.ricky.apicommon.blogServer.service.IBlogViewService;
import com.ricky.apicommon.userInfo.DTO.SearchUserDTO;
import com.ricky.apicommon.userInfo.service.IUserBasicService;
import com.ricky.blogserver.mapper.BlogViewMapper;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author bigwhites
 * @since 2024-03-10
 */
@Service
public class BlogViewServiceImpl extends MPJBaseServiceImpl<BlogViewMapper, BlogView> implements IBlogViewService {

    @DubboReference(check = false)
    IUserBasicService userBasicService;

    @Resource(name = "tinyPool")
    VirtualThreadTaskExecutor virtualThreadTaskExecutor;

    @Resource
    ViewHistoryVServiceImpl viewHistoryVService;

    @Resource
    BlogImageServiceImpl blogImageService;

    @Resource
    BlogAgreeServiceImpl blogAgreeService;
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

    public Page<NoteCoverDTO> getViewHistoryByPage(String viewUuid, int page, int pageSize) throws Exception {
        //检查请求的用户石存在
        checkUserExist(viewUuid);
        Page<NoteCoverDTO> resPage = new Page<>(page, pageSize);
        //在视图中分页查询
        viewHistoryVService.getBaseMapper().selectJoinPage(resPage, NoteCoverDTO.class,
                new MPJLambdaWrapper<ViewHistoryV>()
                        .selectAll(ViewHistoryV.class)
                        .eq(ViewHistoryV::getViewUuid, viewUuid));
        if (CollectionUtils.isNotEmpty(resPage.getRecords())) {
            // 查出发布用户的信息
            List<Boolean> agreeList = new ArrayList<>();
            List<String> pubIdList = new ArrayList<>();
            //并行查询： 1.查看是否点赞 2.发布用户的信息(RPC)
            Future<Void> submit = virtualThreadTaskExecutor.submit(() -> {
                resPage.getRecords().forEach(noteCoverDTO -> {
                    Boolean agree = blogAgreeService.isAgree(noteCoverDTO.blogId, noteCoverDTO.viewUuid);
                    agreeList.add(agree);;
                });
                return null;
            });
            resPage.getRecords().forEach(noteCoverDTO -> pubIdList.add(noteCoverDTO.pubUuid));

            //RPC调用较耗时，在主线程完成，完成后虚拟线程的任务也已经完成了
            List<SearchUserDTO> users = userBasicService.getNotePublisherInfo(pubIdList);
            submit.get();
            List<NoteCoverDTO> newRecords = resPage.getRecords();
            for (int i = 0; i < agreeList.size(); ++i) {
                newRecords.get(i).isAgree = agreeList.get(i);
                newRecords.get(i).pubUNickname = users.get(i).nickname;
                newRecords.get(i).pubUAvatar = users.get(i).uAvatar;
                newRecords.get(i).coverFileName = blogImageService.fillImagePath(newRecords.get(i).coverFileName
                        , newRecords.get(i).pubUuid);
            }
            resPage.setRecords(newRecords);

        }
        return resPage;
    }


}
