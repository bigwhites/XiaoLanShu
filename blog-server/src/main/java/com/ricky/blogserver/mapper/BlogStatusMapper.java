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

    public final String VIEW_COUNT = "view_count";

    /**
     * @param column 要操作的列
     * @return 影响的行数
     * @description 增加某个计数器的值
     * @author Ricky01
     * @since 2024/3/10
     **/
    public Integer incrCountByColName(Long blogId, String column);

}
