package com.ricky.apicommon.userInfo.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDate;

//import lombok.*;

/**
 * <p>
 * 用户基本信息
 * </p>
 *
 * @author bigwhites
 * @since 2024-02-22
 */

@TableName("user_basic")
public class UserBasic implements Serializable {

    @TableId(value = "uuid",type = IdType.ASSIGN_UUID)
    private String uuid;

    private String userName;

    private String userEmail;

    @TableField(fill = FieldFill.INSERT)
    private LocalDate createDate;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDate updateDate;

    private String pwd;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public LocalDate getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    public LocalDate getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDate updateDate) {
        this.updateDate = updateDate;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public UserBasic(String uuid) {
        this.uuid = uuid;
    }
}
