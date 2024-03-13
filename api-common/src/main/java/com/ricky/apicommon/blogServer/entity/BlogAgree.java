package com.ricky.apicommon.blogServer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author bigwhites
 * @since 2024-03-13
 */
@TableName("blog_agree")
public class BlogAgree {

    @TableId(value = "id", type = IdType.AUTO)
    public Long id;

    public Long blogId;

    public LocalDateTime createTime;

    public String uuid;

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

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return "BlogAgree{" +
        "id = " + id +
        ", blogId = " + blogId +
        ", createTime = " + createTime +
        ", uuid = " + uuid +
        "}";
    }
}
