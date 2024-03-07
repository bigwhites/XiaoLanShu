package com.ricky.apicommon.blogServer.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDate;

/**
 * <p>
 *
 * </p>
 *
 * @author bigwhites
 * @since 2024-03-07
 */
@TableName("blog_image")
public class BlogImage {

    @TableId(value = "id", type = IdType.AUTO)
    public Long id;

    public String fileName;
    @TableField(fill = FieldFill.INSERT)
    public LocalDate createDate;

    public Long blogId;
    public Short sort;

    public BlogImage(String fileName, Long blogId, Short sort) {
        this.fileName = fileName;
        this.blogId = blogId;
        this.sort = sort;
    }

    public Short getSort() {
        return sort;
    }

    public void setSort(Short sort) {
        this.sort = sort;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public LocalDate getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    public Long getBlogId() {
        return blogId;
    }

    public void setBlogId(Long blogId) {
        this.blogId = blogId;
    }

    @Override
    public String toString() {
        return "BlogImage{" +
                "id = " + id +
                ", fileName = " + fileName +
                ", createDate = " + createDate +
                ", blogId = " + blogId +
                "}";
    }
}
