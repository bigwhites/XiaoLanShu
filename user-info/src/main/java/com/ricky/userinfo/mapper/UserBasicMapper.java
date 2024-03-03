package com.ricky.userinfo.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.yulichang.base.MPJBaseMapper;
import com.ricky.apicommon.userInfo.entity.UserBasic;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户基本信息 Mapper 接口
 * </p>
 *
 * @author bigwhites
 * @since 2024-02-22
 */
@Mapper
public interface UserBasicMapper extends MPJBaseMapper<UserBasic> {

}
