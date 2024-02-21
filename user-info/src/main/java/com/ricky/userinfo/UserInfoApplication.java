package com.ricky.userinfo;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class UserInfoApplication {

    public static void main(String[] args) {
        System.out.println("user info start!!!!!!!");
        SpringApplication.run(UserInfoApplication.class, args);
    }

}
