package com.ricky.blogserver.serviceImpl;

import com.github.yulichang.base.MPJBaseServiceImpl;
import com.ricky.apicommon.blogServer.entity.BlogImage;
import com.ricky.apicommon.blogServer.service.IBlogImageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ricky.blogserver.mapper.BlogImageMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author bigwhites
 * @since 2024-03-07
 */
@Service
public class BlogImageServiceImpl extends MPJBaseServiceImpl<BlogImageMapper, BlogImage> implements IBlogImageService {

}
