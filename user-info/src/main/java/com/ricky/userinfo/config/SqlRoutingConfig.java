package com.ricky.userinfo.config;


import com.ricky.userinfo.Interceptor.MasterSlaveAutoRoutingPlugin;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class SqlRoutingConfig {
    @Autowired
    SqlSessionFactory sqlSessionFactory;

    @PostConstruct
    public void addInterceptor() {
        sqlSessionFactory.getConfiguration().addInterceptor(new MasterSlaveAutoRoutingPlugin());
    }
}
