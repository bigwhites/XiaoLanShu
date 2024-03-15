package com.ricky.blogserver.serviceImpl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.ricky.apicommon.XiaoLanShuException;
import com.ricky.apicommon.blogServer.DTO.NoteCoverDTO;
import com.ricky.apicommon.blogServer.entity.CollectionHistoryV;
import com.ricky.apicommon.blogServer.service.ICollectionHistoryVService;
import com.ricky.apicommon.userInfo.DTO.SearchUserDTO;
import com.ricky.apicommon.userInfo.service.IUserBasicService;
import com.ricky.blogserver.mapper.CollectionHistoryVMapper;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author bigwhites
 * @since 2024-03-14
 */
@Service
public class CollectionHistoryVServiceImpl extends MPJBaseServiceImpl<CollectionHistoryVMapper, CollectionHistoryV> implements ICollectionHistoryVService {
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
     * @return 查询的分页对象
     * @params 查询用户的id，以及分页相关参数
     * @description 获取用户的收藏历史
     * @author Ricky01
     * @since 2024/3/14
     **/
    public Page<NoteCoverDTO> getCollectionHistoryByPage(String viewUuid, int page, int pageSize) throws Exception {
        Page<NoteCoverDTO> noteCoverDTOPage = new Page<>(page, pageSize);
        Integer ii = userBasicService.userExistByUuid(viewUuid);
        if (ii == 0) {
            throw new XiaoLanShuException("用户不存在");
        }

        this.baseMapper.selectJoinPage(noteCoverDTOPage, NoteCoverDTO.class,
                new MPJLambdaWrapper<CollectionHistoryV>()
                        .selectAll(CollectionHistoryV.class)
                        .eq(CollectionHistoryV::getCollectUuid, viewUuid)
        );
        var records = noteCoverDTOPage.getRecords();
        if (CollectionUtils.isNotEmpty(records)) { //查收藏记录那就肯定浏览过，无需查询，只需带出发布的用户即可
            List<String> pubUuids = new ArrayList<>();
            records.forEach(record -> {
                pubUuids.add(record.pubUuid);
            });
            List<SearchUserDTO> notePublisherInfo = userBasicService.getNotePublisherInfo(pubUuids);
            for (int i = 0; i < records.size(); i++) {
                records.get(i).coverFileName = blogImageService
                        .fillImagePath(records.get(i).coverFileName,
                                records.get(i).pubUuid);
                records.get(i).pubUAvatar = notePublisherInfo.get(i).uAvatar;
                records.get(i).pubUNickname = notePublisherInfo.get(i).nickname;

            }
        }
        noteCoverDTOPage.setRecords(records);
        return noteCoverDTOPage;
    }

}
