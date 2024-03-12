package com.ricky.blogserver.mqListener;

import com.alibaba.fastjson2.JSONObject;
import com.rabbitmq.client.Channel;
import com.ricky.apicommon.blogServer.DTO.UploadReqDTO;
import com.ricky.apicommon.blogServer.VO.NewBlogVO;
import com.ricky.apicommon.blogServer.entity.Blog;
import com.ricky.blogserver.config.RabbitConfig;
import com.ricky.blogserver.serviceImpl.BlogServiceImpl;
import com.ricky.blogserver.serviceImpl.BlogStatusServiceImpl;
import com.ricky.blogserver.serviceImpl.ElaSearchServiceImpl;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Component
public class BlogListener {

    private final Logger log = LoggerFactory.getLogger(BlogListener.class);

    @Resource
    private BlogServiceImpl blogService;

    @Resource
    BlogStatusServiceImpl blogStatusService;
    @Resource
    ElaSearchServiceImpl elaSearchService;

    /**
     * @param message 传入的消息
     * @description 写入新文章消息队列 <br/>
     * 将数据同时保存入ES和数据库
     * @author Ricky01
     * @since 2024/3/7
     **/
    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = RabbitConfig.ADD_BLOG_QUEUE, durable = "true", autoDelete = "false"),
            exchange = @Exchange(value = RabbitConfig.ADD_BLOG_EXCHANGE), key = "r"), ackMode = "MANUAL")
    @Transactional
    public void consumerDoAck(String message, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel)
            throws IOException {

        log.debug("receive: {}", message);
        JSONObject jsonObject = JSONObject.parseObject(message);
        NewBlogVO newBlogVo = jsonObject.getObject("newBlogVo", NewBlogVO.class);
        UploadReqDTO uploadReqDTO = jsonObject.getObject("uploadReqDTO", UploadReqDTO.class);
        Blog blog = blogService.writeBlog2DB(newBlogVo, uploadReqDTO);
        boolean b1 = blogStatusService.addDefaultValue(blog.id);
        elaSearchService.saveNewBlog2Es(blog);

        if (b1) {
            channel.basicAck(deliveryTag, false); //确认消息已处理
        } else {
            channel.basicNack(deliveryTag, false, true); //消息未处理
        }
    }
}
