package com.ricky.blogserver.serviceImpl;

import com.github.yulichang.base.MPJBaseServiceImpl;
import com.ricky.apicommon.blogServer.entity.ViewHistoryV;
import com.ricky.apicommon.blogServer.service.IViewHistoryVService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ricky.blogserver.mapper.ViewHistoryVMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 视图服务类
 * </p>
 *
 * @author bigwhites
 * @since 2024-03-10
 */
@Service
public class ViewHistoryVServiceImpl extends MPJBaseServiceImpl<ViewHistoryVMapper, ViewHistoryV> implements IViewHistoryVService {

}
