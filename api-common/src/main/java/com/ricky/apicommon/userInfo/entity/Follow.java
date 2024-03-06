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
 * @since 2024-03-03
 */

@TableName("follow")
public class Follow implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    public Long id;

    public String fromUid;

    public String toUid;

    @TableField(fill = FieldFill.INSERT)
    public LocalDate createDate;


    public Follow(String fromUid, String toUid) {
        this.fromUid = fromUid;
        this.toUid = toUid;
    }

    public String getFromUid() {
        return fromUid;
    }

    public void setFromUid(String fromUid) {
        this.fromUid = fromUid;
    }

    public String getToUid() {
        return toUid;
    }

    public void setToUid(String toUid) {
        this.toUid = toUid;
    }

    public LocalDate getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    @Override
    public String toString() {
        return "Follow{" +
                "uuid = " + id +
                ", fromUid = " + fromUid +
                ", toUid = " + toUid +
                ", creareDate = " + createDate +
                "}";
    }
}
