package com.ricky.blogserver.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.ricky.apicommon.blogServer.entity.Blog;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author bigwhites
 * @since 2024-03-06
 */
@Mapper
public interface BlogMapper extends MPJBaseMapper<Blog> {

}
