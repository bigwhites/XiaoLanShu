package com.ricky.apicommon.userInfo.DTO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.ricky.apicommon.userInfo.entity.UserBasic;
import com.ricky.apicommon.userInfo.entity.UserDetail;

import java.io.Serializable;
import java.time.LocalDate;

public class UserDTO implements Serializable {
    public String uuid;
    public String userName;
    public String userEmail;
    public LocalDate createDate;
    public String uSex;

    public String uAbout;

    public String nickname;

    public LocalDate birthday;

    public String uAvatar;

    public String cover;

    //关注.粉丝
    public long followCount;

    public long fansCount;

    public long blogCount; //发布的博客的数量

}
