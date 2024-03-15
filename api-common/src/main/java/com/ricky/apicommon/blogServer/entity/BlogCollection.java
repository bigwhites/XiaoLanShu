package com.ricky.apicommon.blogServer.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author bigwhites
 * @since 2024-03-14
 */
@TableName("blog_collection")
public class BlogCollection {

    public Long id;

    public Long blogId;

    public String collectUuid;

    public LocalDateTime createTime;

    public String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBlogId() {
        return blogId;
    }

    public void setBlogId(Long blogId) {
        this.blogId = blogId;
    }

    public String getCollectUuid() {
        return collectUuid;
    }

    public void setCollectUuid(String collectUuid) {
        this.collectUuid = collectUuid;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "BlogCollection{" +
                "id = " + id +
                ", blogId = " + blogId +
                ", collectUuid = " + collectUuid +
                ", createTime = " + createTime +
                ", status = " + status +
                "}";
    }
}
