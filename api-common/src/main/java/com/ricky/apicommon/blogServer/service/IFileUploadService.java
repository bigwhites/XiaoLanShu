package com.ricky.apicommon.blogServer.service;

import com.ricky.apicommon.blogServer.DTO.UploadReqDTO;

public interface IFileUploadService {

    UploadReqDTO uploadOneFile(String filePath, String originFileName) throws Exception;

    boolean deleteOneFile(String filePath, String fileName);

    UploadReqDTO uploadFiles(String path, String nameSuffix, Integer fileCount);
}
