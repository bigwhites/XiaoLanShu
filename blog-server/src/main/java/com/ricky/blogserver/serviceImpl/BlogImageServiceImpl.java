package com.ricky.blogserver.serviceImpl;

import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.ricky.apicommon.blogServer.DTO.BlogBasicDTO;
import com.ricky.apicommon.blogServer.entity.BlogImage;
import com.ricky.apicommon.blogServer.service.IBlogImageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ricky.apicommon.constant.Constant;
import com.ricky.blogserver.mapper.BlogImageMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author bigwhites
 * @since 2024-03-07
 */
@Service
@RefreshScope
public class BlogImageServiceImpl extends MPJBaseServiceImpl<BlogImageMapper, BlogImage> implements IBlogImageService {

    @Value("${filePath.blogs}")
    String blogRootPath;

    public List<String> getImageUrlsList(Long blogId, String pubUuid) {
        List<String> imageList = new ArrayList<>();
        List<BlogImage> blogImages = super.getBaseMapper().selectList(new MPJLambdaWrapper<BlogImage>().
                eq(BlogImage::getBlogId, blogId).orderByAsc(BlogImage::getSort));
        blogImages.forEach(blogImage -> {
            imageList.add(Constant.ROOT_PATH + blogRootPath + "/" + pubUuid + "/" +
                    blogImage.fileName);

        });
        return imageList;
    }

    public BlogBasicDTO fillImageList(BlogBasicDTO blogBasicDTO) {
        blogBasicDTO.imageList = getImageUrlsList(blogBasicDTO.id, blogBasicDTO.pubUuid);
        return blogBasicDTO;
    }

}
