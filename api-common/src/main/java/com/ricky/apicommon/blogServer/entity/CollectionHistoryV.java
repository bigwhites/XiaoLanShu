package com.ricky.apicommon.blogServer.entity;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * <p>
 * 收藏记录视图实体类
 * </p>
 *
 * @author bigwhites
 * @since 2024-03-14
 */
@TableName("collection_history_v")
public class CollectionHistoryV {

    private Long id;

    private Long blogId;

    private String collectUuid;

    private String title;

    private String coverFileName;

    private String pubUuid;

    private Long viewCount;

    private Integer collectionCount;

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

    @Override
    public String toString() {
        return "CollectionHistoryV{" +
        "id = " + id +
        ", blogId = " + blogId +
        ", collectUuid = " + collectUuid +
        ", title = " + title +
        ", coverFileName = " + coverFileName +
        ", pubUuid = " + pubUuid +
        ", viewCount = " + viewCount +
        ", collectionCount = " + collectionCount +
        "}";
    }
}
