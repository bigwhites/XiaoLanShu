package com.ricky.blogserver.config;

import ch.qos.logback.classic.pattern.MessageConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 1. 接着我们先使用下direct exchange(直连型交换机),创建DirectRabbitConfig.java（对于队列和交换机持久化以及连接使用设置，
 * 在注释里有说明，后面的不同交换机的配置就不做同样说明了）：
 */
@Configuration
public class RabbitConfig {

    public static final String QUEUE_NAME = "DeleteFileQueue";
    public static final String EXCHANGE_NAME = "DeleteFileExchange";
    public static final String ROUTE_NAME = "DeleteRouting";

    public static final String ADD_BLOG_QUEUE = "AddBlogQueue";
    public static final String ADD_BLOG_EXCHANGE = "addBlogExchange";
    public static final String ADD_BLOG_ROUTE = "addBlogRoute";


    @Bean
    public Queue addBlogQueue() {
        // durable:是否持久化,默认是false,持久化队列：会被存储在磁盘上，当消息代理重启时仍然存在，暂存队列：当前连接有效
        // exclusive:默认也是false，只能被当前创建的连接使用，而且当连接关闭后队列即被删除。此参考优先级高于durable
        // autoDelete:是否自动删除，当没有生产者或者消费者使用此队列，该队列会自动删除。
        //   return new Queue("TestDirectQueue",true,true,false);

        //一般设置一下队列的持久化就好,其余两个就是默认false
        return new Queue(ADD_BLOG_QUEUE, true, false, false);
    }

    @Bean
    DirectExchange addBlogExchange() {
        return new DirectExchange(ADD_BLOG_EXCHANGE, true, false);
    }

    @Bean
    Binding addBlogRoute() {
        return BindingBuilder.bind(addBlogQueue()).to(addBlogExchange()).with(ADD_BLOG_ROUTE);
    }

    /**
     * 队列 起名：MailSendQueue
     */
    @Bean
    public Queue DelFileQueue() {
        // durable:是否持久化,默认是false,持久化队列：会被存储在磁盘上，当消息代理重启时仍然存在，暂存队列：当前连接有效
        // exclusive:默认也是false，只能被当前创建的连接使用，而且当连接关闭后队列即被删除。此参考优先级高于durable
        // autoDelete:是否自动删除，当没有生产者或者消费者使用此队列，该队列会自动删除。
        //   return new Queue("TestDirectQueue",true,true,false);

        //一般设置一下队列的持久化就好,其余两个就是默认false
        return new Queue(QUEUE_NAME, true, false, false);
    }

    /**
     * Direct交换机 起名：TestDirectExchange
     *
     * @return
     */
    @Bean
    DirectExchange DelfileDirectExchange() {
        return new DirectExchange(EXCHANGE_NAME, true, false);
    }

    /**
     * 绑定  将队列和交换机绑定, 并设置用于匹配键：TestDirectRouting
     *
     * @return
     */
    @Bean
    Binding bindingDirect() {
        return BindingBuilder.bind(DelFileQueue()).to(DelfileDirectExchange()).with(ROUTE_NAME);
    }


    @Bean
    DirectExchange lonelyDirectExchange() {
        return new DirectExchange("lonelyDirectExchange");
    }
}



