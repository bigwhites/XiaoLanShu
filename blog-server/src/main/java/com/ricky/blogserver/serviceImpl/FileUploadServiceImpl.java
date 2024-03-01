package com.ricky.blogserver.serviceImpl;

import com.ricky.apicommon.blogServer.DTO.UploadReqDTO;
import com.ricky.apicommon.blogServer.service.IFileUploadService;
import com.ricky.apicommon.constant.RedisPrefix;
import com.ricky.apicommon.utils.JwtUtil;
import com.ricky.blogserver.utils.RedisUtil;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;


@DubboService
public class FileUploadServiceImpl implements IFileUploadService {

    @Resource
    RedisUtil redisUtil;


    @Override  //上传一个文件  生成headers并把数据路径和文件名存入reids
    public UploadReqDTO uploadOneFile(String filePath, String originFileName) throws Exception {

        int i = originFileName.lastIndexOf('.'); //获取最后一个点的位置 取得文件后缀名
        if (i == -1) {
            return null;
        }
        String uuid = UUID.randomUUID().toString();
        String uuidNameKey = RedisPrefix.FILENAME + uuid;
        String uuidPathKey = RedisPrefix.FILEPATH + uuid;
        String fileName = uuid + originFileName.substring(i + 1);

        redisUtil.set(uuidNameKey, fileName, 60 * 4);
        redisUtil.set(uuidPathKey, filePath, 60 * 4);
        return new UploadReqDTO(uuidNameKey, fileName, uuidPathKey);
    }
}
