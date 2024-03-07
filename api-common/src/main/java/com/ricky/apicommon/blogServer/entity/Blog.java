package com.ricky.apicommon.blogServer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author bigwhites
 * @since 2024-03-06
 */
public class Blog {

    @TableId(value = "id", type = IdType.AUTO)
    public Long id;

    public String pubUuid;

    public LocalDateTime publishTime;

    public String title;

    public String content;

    public String coverFileName;


    public String getCoverFileName() {
        return coverFileName;
    }

    public void setCoverFileName(String coverFileName) {
        this.coverFileName = coverFileName;
    }

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

    @Override
    public String toString() {
        return "Blog{" +
        "id = " + id +
        ", pubUuid = " + pubUuid +
        ", publishTime = " + publishTime +
        ", title = " + title +
        ", content = " + content +
        "}";
    }
}
