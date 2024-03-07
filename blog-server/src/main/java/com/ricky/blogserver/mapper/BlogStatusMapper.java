package com.ricky.blogserver.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.ricky.apicommon.blogServer.entity.BlogStatus;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author bigwhites
 * @since 2024-03-07
 */
@Mapper
public interface BlogStatusMapper extends MPJBaseMapper<BlogStatus> {

}
