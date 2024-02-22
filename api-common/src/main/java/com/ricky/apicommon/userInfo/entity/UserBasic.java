package com.ricky.apicommon.userInfo.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.*;

/**
 * <p>
 * 用户基本信息
 * </p>
 *
 * @author bigwhites
 * @since 2024-02-22
 */
@Getter
@Setter
@RequiredArgsConstructor
@ToString
@TableName("user_basic")
public class UserBasic implements Serializable {

    @TableId(value = "uuid",type = IdType.ASSIGN_UUID)
    private String uuid;

    private String userEmail;

    private String uSex;

    private String nickname;

    @TableLogic(delval = "1",value = "0")
    private Integer delFlag;

    @TableField(fill = FieldFill.INSERT)
    private LocalDate createDate;

    private String pwd;
}
