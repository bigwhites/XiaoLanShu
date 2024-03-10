package com.ricky.apicommon.blogServer.DTO;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Ricky01
 * @description 笔记的dto对象
 * @since 2024/3/9
 **/
public class NoteDto implements Serializable {

    public BlogBasicDTO basicInfo;
    //发布者的相关信息
    public String nickname;
    public String userName;
    public String uAvatar;
    public long fansCount;

    //当前用户是否关注、点赞、收藏这篇博客
    public boolean isFollow;
    public boolean isAgree;
    public boolean isCollection;

    public BlogBasicDTO getBasicInfo() {
        return basicInfo;
    }

    public void setBasicInfo(BlogBasicDTO basicInfo) {
        this.basicInfo = basicInfo;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUAvatar() {
        return uAvatar;
    }

    public void setUAvatar(String uAvatar) {
        this.uAvatar = uAvatar;
    }

    public long getFansCount() {
        return fansCount;
    }

    public void setFansCount(long fansCount) {
        this.fansCount = fansCount;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }

    public boolean isAgree() {
        return isAgree;
    }

    public void setAgree(boolean agree) {
        isAgree = agree;
    }

    public boolean isCollection() {
        return isCollection;
    }

    public void setCollection(boolean collection) {
        isCollection = collection;
    }
}
