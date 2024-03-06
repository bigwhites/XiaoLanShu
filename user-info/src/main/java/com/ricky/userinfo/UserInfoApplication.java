package com.ricky.userinfo;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
//import org.apache.shardingsphere.core.config.DataSourceConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableAsync
@EnableScheduling
@EnableDubbo
public class UserInfoApplication {
    public static void main(String[] args) {
        System.out.println("user info start!!!!!!!");
        SpringApplication.run(UserInfoApplication.class, args);
    }

}
