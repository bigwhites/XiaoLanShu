package com.ricky.apicommon.constant;

public interface RedisPrefix {
    String LOGIN_FAIL_CNT = "LOGFAILl:";  //输入密码错误次数的记录

    String FILENAME = "FILENAME:";

    String FILEPATH = "FILEPATH:";  //根目录下的一级目录

    String FOLLOW_SET = "FOLOWSET:"; //关注集合
    String CANCEL_FOLLOW = "CANSET:"; //取关集合

    String FOLLOW_CNT = "FOWCNT:";  //关注别人的数量

    String FANS_CNT = "FANCNT:";  //关注别人的数量
}

