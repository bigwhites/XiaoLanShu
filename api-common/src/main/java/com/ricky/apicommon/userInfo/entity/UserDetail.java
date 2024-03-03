package com.ricky.apicommon.userInfo.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDate;


/**
 * <p>
 *
 * </p>
 *
 * @author bigwhites
 * @since 2024-02-27
 */
//@Getter
//@Setter
@TableName("user_detail")
public class UserDetail implements Serializable {

    @TableId(value = "uuid", type = IdType.INPUT)
    private String uuid;

    private String uSex;

    private String uAbout;

    private String nickname;

    private LocalDate birthday;

    private String uAvatar;

    private String cover;

    private long followCount;

    private long  fansCount;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDate updateDate;

    @TableField(fill = FieldFill.INSERT)
    private LocalDate createDate;

    public long getFollowCount() {
        return followCount;
    }

    public void setFollowCount(long followCount) {
        this.followCount = followCount;
    }

    public long getFansCount() {
        return fansCount;
    }

    public void setFansCount(long fansCount) {
        this.fansCount = fansCount;
    }

    public UserDetail(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUSex() {
        return uSex;
    }

    public void setUSex(String uSex) {
        this.uSex = uSex;
    }

    public String getUAbout() {
        return uAbout;
    }

    public void setUAbout(String uAbout) {
        this.uAbout = uAbout;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getUAvatar() {
        return uAvatar;
    }

    public void setUAvatar(String uAvatar) {
        this.uAvatar = uAvatar;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public LocalDate getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDate updateDate) {
        this.updateDate = updateDate;
    }

    public LocalDate getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    @Override
    public String toString() {
        return "UserDetail{" +
                "uuid='" + uuid + '\'' +
                ", uSex='" + uSex + '\'' +
                ", uAbout='" + uAbout + '\'' +
                ", nickname='" + nickname + '\'' +
                ", birthday=" + birthday +
                ", uAvatar='" + uAvatar + '\'' +
                ", cover='" + cover + '\'' +
                ", followCount=" + followCount +
                ", fansCount=" + fansCount +
                ", updateDate=" + updateDate +
                ", createDate=" + createDate +
                '}';
    }
}
