package com.ricky.apicommon.blogServer.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDate;

/**
 * <p>
 *
 * </p>
 *
 * @author bigwhites
 * @description 帖子的状态信息
 * @since 2024-03-07
 */
@TableName("blog_status")
public class BlogStatus {

    @TableId(value = "id", type = IdType.AUTO)
    public Long id;

    /**
     * 观看次数
     */
    public Long viewCount;

    public Integer collectionCount;

    public Long agreeCount;

    public Long blogId;

    @TableField(fill = FieldFill.INSERT)
    public LocalDate createDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getBlogId() {
        return blogId;
    }

    public void setBlogId(Long blogId) {
        this.blogId = blogId;
    }

    public LocalDate getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    @Override
    public String toString() {
        return "BlogStatus{" +
                "id = " + id +
                ", viewCount = " + viewCount +
                ", collectiomCount = " + collectionCount +
                ", agreeCount = " + agreeCount +
                ", blogId = " + blogId +
                ", createDate = " + createDate +
                "}";
    }
}
