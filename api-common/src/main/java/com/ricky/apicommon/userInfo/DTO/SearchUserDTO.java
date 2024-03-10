package com.ricky.apicommon.userInfo.DTO;

import java.io.Serializable;
import java.util.List;

public class SearchUserDTO implements Serializable {
    public String userName;

    public String uuid;

    public String uAvatar;

    public String nickname;

    //关注.粉丝
    public long followCount;

    public long fansCount;

    public boolean isFollow;  //是否已经关注这个用户

}
