package com.ricky.apicommon.userInfo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.yulichang.base.MPJBaseService;
import com.ricky.apicommon.blogServer.VO.NoteUserVO;
import com.ricky.apicommon.userInfo.DTO.SearchUserDTO;
import com.ricky.apicommon.userInfo.entity.UserBasic;

import java.util.List;

public interface IUserBasicService extends MPJBaseService<UserBasic> {
    Integer userExistByUuid(String uuid);

    NoteUserVO getNoteUser(String uuid, String viewId);

    List<SearchUserDTO> getNotePublisherInfo(List<String> uuids);

}
