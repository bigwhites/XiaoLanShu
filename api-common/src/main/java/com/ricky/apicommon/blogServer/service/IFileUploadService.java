package com.ricky.apicommon.blogServer.service;

import com.ricky.apicommon.blogServer.DTO.UploadReqDTO;

import java.util.Map;

public interface IFileUploadService {

    UploadReqDTO uploadOneFile(String filePath, String originFileName) throws Exception;

    boolean deleteoneFile(String filePath,String fileName);
}
