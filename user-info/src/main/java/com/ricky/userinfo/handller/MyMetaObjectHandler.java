package com.ricky.userinfo.handller;


import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
//import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

//@Log4j2
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        // 起始版本 3.3.0(推荐使用)
        this.strictInsertFill(metaObject, "createDate",LocalDate.class, LocalDate.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 起始版本 3.3.0(推荐)
        this.strictUpdateFill(metaObject, "updateDate", LocalDate.class, LocalDate.now());
    }
}
