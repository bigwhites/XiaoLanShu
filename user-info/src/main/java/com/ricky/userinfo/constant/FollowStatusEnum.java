package com.ricky.userinfo.constant;

public enum FollowStatusEnum {


    CANCEL_REDIS(1),
    NO_DB(2),  //没有关注过
    FOLLOW_DB(3),
    FOLLOW_REDIS(4);

    public int code;

    FollowStatusEnum(int code) {
        this.code = code;
    }

    public boolean isFollow() {
        return code > 2;
    }

}
