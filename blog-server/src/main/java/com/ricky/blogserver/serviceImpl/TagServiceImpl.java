package com.ricky.blogserver.serviceImpl;

import com.github.yulichang.base.MPJBaseServiceImpl;
import com.ricky.apicommon.blogServer.entity.Tag;
import com.ricky.blogserver.mapper.TagMapper;
import com.ricky.apicommon.blogServer.service.ITagService;
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
public class TagServiceImpl extends MPJBaseServiceImpl<TagMapper, Tag> implements ITagService {

}
