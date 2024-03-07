package com.ricky.blogserver.serviceImpl;

import com.github.yulichang.base.MPJBaseServiceImpl;
import com.ricky.apicommon.blogServer.entity.BlogStatus;
import com.ricky.blogserver.mapper.BlogStatusMapper;
import com.ricky.apicommon.blogServer.service.IBlogStatusService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
