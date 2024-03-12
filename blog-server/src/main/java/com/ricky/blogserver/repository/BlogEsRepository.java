package com.ricky.blogserver.repository;

import com.ricky.apicommon.blogServer.es.pojo.BlogESPojo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface BlogEsRepository extends ElasticsearchRepository<BlogESPojo, Long> {

    public List<BlogESPojo> findBlogESPojoByIdIs(Long id);

    public List<BlogESPojo> findAllByPubUuid(String pubUuid);

    public Page<BlogESPojo> findByTitleContainingOrContentContainingOrderByPublishTimeDesc(
            String titleKeyword, String contentKeyword, Pageable pageable);
}
