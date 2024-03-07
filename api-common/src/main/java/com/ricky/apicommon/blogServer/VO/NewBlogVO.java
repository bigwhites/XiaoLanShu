package com.ricky.apicommon.blogServer.VO;

import com.ricky.apicommon.DefalutGroup;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class NewBlogVO {


    @NotBlank(message = "发布人不能为空", groups = {DefalutGroup.class})
    public String pubUuid;

    @NotBlank(message = "标题不能为空", groups = {DefalutGroup.class})
    public String title;

    @NotBlank(message = "内容", groups = {DefalutGroup.class})
    public String content;

    public List<Long> tagId;

    @Min(value = 1, message = "至少一张图片", groups = {DefalutGroup.class})
    @Max(value = 9, message = "最多9张图片", groups = {DefalutGroup.class})
    public int imgCount;


}
