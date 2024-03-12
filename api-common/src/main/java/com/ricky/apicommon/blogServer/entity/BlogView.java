package com.ricky.apicommon.blogServer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author bigwhites
 * @since 2024-03-10
 */
@TableName("blog_view")
public class BlogView implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    public Long id;

    public Long blogId;

    public String viewUuid;


    public LocalDateTime viewTime;

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

    public String getViewUuid() {
        return viewUuid;
    }

    public void setViewUuid(String viewUuid) {
        this.viewUuid = viewUuid;
    }

    public LocalDateTime getViewTime() {
        return viewTime;
    }

    public void setViewTime(LocalDateTime viewTime) {
        this.viewTime = viewTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void fillDefault() {
        this.viewTime = LocalDateTime.now();
        this.status = "1";
    }

    @Override
    public String toString() {
        return "BlogView{" +
                "id = " + id +
                ", blogId = " + blogId +
                ", viewUuid = " + viewUuid +
                ", viewTime = " + viewTime +
                ", status = " + status +
                "}";
    }
}
