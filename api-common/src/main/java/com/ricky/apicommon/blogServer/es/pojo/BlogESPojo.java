package com.ricky.apicommon.blogServer.es.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.Date;

@Document(indexName = "blog", createIndex = false)
public class BlogESPojo {
    @Id
    @Field(type = FieldType.Long)
    public Long id;

    @Field(type = FieldType.Text, analyzer = "ik_max_word",searchAnalyzer = "ik_max_word")
    public String title;

    @Field(type = FieldType.Keyword)
    public String pubUuid;
    @Field(type = FieldType.Text, analyzer = "ik_max_word",searchAnalyzer = "ik_max_word")
    public String content;
    @Field(type = FieldType.Keyword)
    public String publishTime;

    @Field(type = FieldType.Keyword)
    public String coverFileName;

    @Field(type = FieldType.Long)
    public Long agreeCount;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPubUuid() {
        return pubUuid;
    }

    public void setPubUuid(String pubUuid) {
        this.pubUuid = pubUuid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public Long getAgreeCount() {
        return agreeCount;
    }

    public void setAgreeCount(Long agreeCount) {
        this.agreeCount = agreeCount;
    }

    @Override
    public String toString() {
        return "BlogESPojo{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", pubUuid='" + pubUuid + '\'' +
                ", content='" + content + '\'' +
                ", publishTime=" + publishTime +
                ", agreeCount=" + agreeCount +
                '}';
    }
}
