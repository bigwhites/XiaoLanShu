package com.ricky.apicommon.blogServer.DTO;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class BlogBasicDTO implements Serializable {
    public Long id;

    public String pubUuid;

    public LocalDateTime publishTime;

    public String title;

    public String content;

    public String coverFileName;

    public List<String> imageList;

    public Long viewCount;

    public Integer collectionCount;

    public Long agreeCount;


}
