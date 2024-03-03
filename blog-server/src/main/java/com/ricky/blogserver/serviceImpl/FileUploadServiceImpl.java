package com.ricky.blogserver.serviceImpl;

import com.alibaba.fastjson2.JSON;
import com.ricky.apicommon.blogServer.DTO.FileDTO;
import com.ricky.apicommon.blogServer.DTO.UploadReqDTO;
import com.ricky.apicommon.blogServer.service.IFileUploadService;
import com.ricky.apicommon.constant.RedisPrefix;
import com.ricky.blogserver.config.RabbitConfig;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.UUID;
import java.util.concurrent.TimeUnit;


@DubboService
public class FileUploadServiceImpl implements IFileUploadService {

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    RabbitTemplate rabbitTemplate;

    @Override  //上传一个文件  生成headers并把数据路径和文件名存入reids
    public UploadReqDTO uploadOneFile(String filePath, String originFileName) throws Exception {

        int i = originFileName.lastIndexOf('.'); //获取最后一个点的位置 取得文件后缀名
        if (i == -1) {
            return null;
        }
        String uuid = UUID.randomUUID().toString();
        String uuidNameKey = RedisPrefix.FILENAME + uuid;
        String uuidPathKey = RedisPrefix.FILEPATH + uuid;
        String fileName = uuid + originFileName.substring(i);
        stringRedisTemplate.opsForValue().set(uuidNameKey, fileName, 3, TimeUnit.MINUTES);
        stringRedisTemplate.opsForValue().set(uuidPathKey, filePath, 3, TimeUnit.MINUTES);
        return new UploadReqDTO(uuidNameKey, fileName, uuidPathKey);
    }

    /**
     * @return
     * @description 删除静态资源文件中的单个指定文件
     * @author Ricky01
     * @params
     * @since 2024/3/3
     **/
    @Override
    public boolean deleteoneFile(String filePath, String fileName) {
        FileDTO dto = new FileDTO();
        dto.fileName = fileName;
        dto.filePath = filePath;
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_NAME,
                RabbitConfig.ROUTE_NAME, JSON.toJSONString(dto));
        return true;
    }
}
