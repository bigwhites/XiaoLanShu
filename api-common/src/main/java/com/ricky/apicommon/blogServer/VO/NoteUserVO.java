package com.ricky.apicommon.blogServer.VO;

import java.io.Serializable;

/**
 * @author Ricky01
 * @description 查看博客时服务间的调用参数结果包装
 * @since 2024/3/9
 **/
public class NoteUserVO implements Serializable {
    public String nickname;
    public String userName;
    public String uAvatar;
    public long fansCount;

    //当前用户是否关注
    public Boolean isFollow;
}
