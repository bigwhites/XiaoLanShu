package com.ricky.blogserver.serviceImpl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ricky.apicommon.blogServer.DTO.BlogBasicDTO;
import com.ricky.apicommon.blogServer.DTO.NoteCoverDTO;
import com.ricky.apicommon.blogServer.VO.NoteUserVO;
import com.ricky.apicommon.blogServer.entity.Blog;
import com.ricky.apicommon.blogServer.es.pojo.BlogESPojo;
import com.ricky.apicommon.blogServer.service.IElaSearchService;
import com.ricky.apicommon.userInfo.service.IUserBasicService;
import com.ricky.blogserver.repository.BlogEsRepository;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ElaSearchServiceImpl implements IElaSearchService {

    @Resource
    private BlogEsRepository blogEsRepository;
    @Resource
    private IUserBasicService userBasicService;

    @Resource
    BlogImageServiceImpl blogImageService;
    @Resource
    ElasticsearchTemplate elasticsearchTemplate;

    private final Logger log = LoggerFactory.getLogger(ElaSearchServiceImpl.class);

    public IPage<NoteCoverDTO> getPageByKeyword(String keyWord, Integer page, Integer pageSize) {
        //这里mybatis-plus的分页类（从1）和Spring Data的分页类（从0）不一样，项目使用从1计数，所以要-1
        PageRequest request = PageRequest.of(page - 1, pageSize);
        Page<BlogESPojo> blogESPojos = blogEsRepository.
                findByTitleContainingOrContentContainingOrderByPublishTimeDesc(keyWord, keyWord, request);
        List<NoteCoverDTO> dtoList = new ArrayList<>();
        blogESPojos.forEach(blogESPojo -> {
            NoteCoverDTO noteCoverDTO = new NoteCoverDTO();
            noteCoverDTO.pubUuid = blogESPojo.pubUuid;
            noteCoverDTO.coverFileName = blogImageService.fillImagePath(blogESPojo.coverFileName, blogESPojo.pubUuid);
            noteCoverDTO.title = blogESPojo.title;
            noteCoverDTO.id = blogESPojo.id;
            dtoList.add(noteCoverDTO);
            NoteUserVO noteUser = userBasicService.getNoteUser(blogESPojo.pubUuid, null);//RPC
            noteCoverDTO.pubUNickname = noteUser.nickname;
            noteCoverDTO.pubUAvatar = noteUser.uAvatar;
            noteCoverDTO.isAgree = false;  //TODO  点赞还未实现
        });
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<NoteCoverDTO> resPage =

                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, pageSize);
        resPage.setRecords(dtoList);
        resPage.setTotal(blogESPojos.getTotalElements());
        return resPage;
    }


    /**
     * @param newBlog 新的笔记
     * @description 将笔记保存到es
     * @author Ricky01
     * @since 2024/3/12
     **/
    public void saveNewBlog2Es(Blog newBlog) {
        BlogESPojo pojo = new BlogESPojo();
        pojo.id = newBlog.id;
        pojo.title = newBlog.title;
        pojo.content = newBlog.content;
        pojo.coverFileName = newBlog.coverFileName;
        pojo.pubUuid = newBlog.pubUuid;
        pojo.publishTime = newBlog.publishTime.toString();
        pojo.agreeCount = 0L;
        BlogESPojo save = blogEsRepository.save(pojo);
        log.debug(save.toString());
    }

}
