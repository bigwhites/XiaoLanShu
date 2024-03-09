package com.ricky.blogserver.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.ricky.blogserver.Interceptor.MasterSlaveAutoRoutingPlugin;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@MapperScan("com.ricky.blogserver.mapper")
public class MybatisPlusConfig {




    /**
     * 添加分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.POSTGRE_SQL));
        //如果配置多个插件,切记分页最后添加
//        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());//
        // 如果有多数据源可以不配具体类型 否则都建议配上具体的DbType
        return interceptor;
    }
}