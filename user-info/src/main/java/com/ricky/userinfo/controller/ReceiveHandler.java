package com.ricky.userinfo.controller;


import com.ricky.userinfo.config.MailSentRabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.impl.AMQImpl;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.stereotype.Component;


//@Component
public class ReceiveHandler {
    //监听email队列
//    @RabbitListener(queues = {MailSentRabbitConfig.QUEUE_NAME})
//    public void receive_email(Object msg, Message message, Channel channel) {
//        System.out.println("QUEUE_INFORM_EMAIL msg" + msg);
//        System.out.println(message);
//    }
}