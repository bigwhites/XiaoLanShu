package com.ricky.apicommon.blogServer.DTO;

import java.io.Serializable;

public class UploadReqDTO implements Serializable {
    public String nameKey;
    public String fileName;
    public String pathKey;

    public UploadReqDTO(String nameKey, String fileName, String pathKey) {
        this.nameKey = nameKey;
        this.fileName = fileName;
        this.pathKey = pathKey;
    }
}



