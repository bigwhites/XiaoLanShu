package com.ricky.blogserver.serviceImpl;

import com.github.yulichang.base.MPJBaseServiceImpl;
import com.ricky.apicommon.blogServer.entity.Blog;
import com.ricky.apicommon.blogServer.service.IBlogService;
import com.ricky.blogserver.mapper.BlogMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author bigwhites
 * @since 2024-03-06
 */
@Service
public class BlogServiceImpl extends MPJBaseServiceImpl<BlogMapper, Blog> implements IBlogService {

}
