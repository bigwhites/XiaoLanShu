package com.ricky.apicommon.blogServer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDate;

/**
 * <p>
 * 
 * </p>
 *
 * @author bigwhites
 * @since 2024-03-07
 */
public class Tag {

    @TableId(value = "id", type = IdType.AUTO)
   public Long id;

   public String name;

   public Long blogCnt;

   public LocalDate createDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getBlogCnt() {
        return blogCnt;
    }

    public void setBlogCnt(Long blogCnt) {
        this.blogCnt = blogCnt;
    }

    public LocalDate getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    @Override
    public String toString() {
        return "Tag{" +
        "id = " + id +
        ", name = " + name +
        ", blogCnt = " + blogCnt +
        ", createDate = " + createDate +
        "}";
    }
}
