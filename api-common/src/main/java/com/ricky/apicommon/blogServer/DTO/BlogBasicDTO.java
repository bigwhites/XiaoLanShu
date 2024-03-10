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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPubUuid() {
        return pubUuid;
    }

    public void setPubUuid(String pubUuid) {
        this.pubUuid = pubUuid;
    }

    public LocalDateTime getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(LocalDateTime publishTime) {
        this.publishTime = publishTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCoverFileName() {
        return coverFileName;
    }

    public void setCoverFileName(String coverFileName) {
        this.coverFileName = coverFileName;
    }

    public List<String> getImageList() {
        return imageList;
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getCollectionCount() {
        return collectionCount;
    }

    public void setCollectionCount(Integer collectionCount) {
        this.collectionCount = collectionCount;
    }

    public Long getAgreeCount() {
        return agreeCount;
    }

    public void setAgreeCount(Long agreeCount) {
        this.agreeCount = agreeCount;
    }
}
