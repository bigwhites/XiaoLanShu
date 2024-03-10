package com.ricky.apicommon.blogServer.entity;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * <p>
 * 浏览记录的试图，由于join表较多分离成为视图
 * </p>
 *
 * @author bigwhites
 * @since 2024-03-10
 */
@TableName("view_history_v")
public class ViewHistoryV {

    public Long id;

    public Long blogId;

    public String viewUuid;

    public String title;

    public String coverFileName;

    public String pubUuid;

    public Long agreeCount;

    public Long viewCount;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCoverFileName() {
        return coverFileName;
    }

    public void setCoverFileName(String coverFileName) {
        this.coverFileName = coverFileName;
    }

    public String getPubUuid() {
        return pubUuid;
    }

    public void setPubUuid(String pubUuid) {
        this.pubUuid = pubUuid;
    }

    public Long getAgreeCount() {
        return agreeCount;
    }

    public void setAgreeCount(Long agreeCount) {
        this.agreeCount = agreeCount;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    @Override
    public String toString() {
        return "ViewHistoryV{" +
                "id = " + id +
                ", blogId = " + blogId +
                ", viewUuid = " + viewUuid +
                ", title = " + title +
                ", coverFileName = " + coverFileName +
                ", pubUuid = " + pubUuid +
                ", agreeCount = " + agreeCount +
                ", viewCount = " + viewCount +
                "}";
    }
}
