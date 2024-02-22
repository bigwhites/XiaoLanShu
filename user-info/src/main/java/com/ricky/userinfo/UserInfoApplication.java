package com.ricky.userinfo;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableDubbo
public class UserInfoApplication {

    public static void main(String[] args) {
        System.out.println("user info start!!!!!!!");
        SpringApplication.run(UserInfoApplication.class, args);
    }

}
