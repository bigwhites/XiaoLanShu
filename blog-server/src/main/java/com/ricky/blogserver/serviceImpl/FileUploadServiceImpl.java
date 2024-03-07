package com.ricky.blogserver.serviceImpl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.nacos.common.utils.UuidUtils;
import com.ricky.apicommon.blogServer.DTO.FileDTO;
import com.ricky.apicommon.blogServer.DTO.UploadReqDTO;
import com.ricky.apicommon.blogServer.service.IFileUploadService;
import com.ricky.apicommon.constant.RedisPrefix;
import com.ricky.blogserver.config.RabbitConfig;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@DubboService
public class FileUploadServiceImpl implements IFileUploadService {

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    RabbitTemplate rabbitTemplate;


    /**
     * @param filePath       上传的文件的路径
     * @param originFileName 上传的原文件名
     * @return 上传的文件名、文件路径和地址的redis key
     * @description 上传单个文件的预请求
     * @author Ricky01
     * @since 2024/3/7
     **/
    @Override
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
     * @param filePath 要删除的文件路径位于哪里
     * @param fileName 文件名
     * @return 是否成功发送消息到队列
     * @description 删除静态资源文件中的单个指定文件
     * @author Ricky01
     * @since 2024/3/3
     **/
    @Override
    public boolean deleteOneFile(String filePath, String fileName) {
        FileDTO dto = new FileDTO();
        dto.fileName = fileName;
        dto.filePath = filePath;
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_NAME,
                RabbitConfig.ROUTE_NAME, JSON.toJSONString(dto));
        return true;
    }

    /**
     * @param path       要上传的文件的路径（根路径后的下一级）
     * @param nameSuffix 文件后缀 eg .png .txt
     * @param fileCount  上传的文件的数量
     * @return 返回文件的后缀以及redis列表的key
     * @description 上传多个文件
     * @author Ricky01
     * @since 2024/3/7
     **/
    @Override
    public UploadReqDTO uploadFiles(String path, String nameSuffix, Integer fileCount) {

        String redisKey = RedisPrefix.FILENAMES + UuidUtils.generateUuid();
        List<String> names = new ArrayList<>(fileCount + 1);
        names.add(path);  //文件名列表的第一个值为路径
        for (var i = 0; i < fileCount; ++i) {
            names.add(UuidUtils.generateUuid() + nameSuffix);
        }
        names.add(path);
        stringRedisTemplate.opsForList().rightPushAll(redisKey, names);
        stringRedisTemplate.expire(redisKey, 4, TimeUnit.MINUTES);
        return new UploadReqDTO(redisKey, nameSuffix, path);
    }


}
