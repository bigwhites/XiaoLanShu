package com.ricky.apicommon.userInfo.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 *
 * </p>
 *
 * @author bigwhites
 * @since 2024-02-27
 */
@Getter
@Setter
@TableName("user_detail")
public class UserDetail implements Serializable {

    @TableId(value = "uuid",type = IdType.INPUT)
    private String uuid;

    private String uSex;

    private String uAbout;

    private String nickname;

    private LocalDate birthday;

    @TableField(fill = FieldFill.INSERT)
    private LocalDate createDate;
}
