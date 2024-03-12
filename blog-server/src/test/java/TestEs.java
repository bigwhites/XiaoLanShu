import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ricky.apicommon.blogServer.DTO.BlogBasicDTO;
import com.ricky.apicommon.blogServer.entity.Blog;
import com.ricky.apicommon.blogServer.es.pojo.BlogESPojo;
import com.ricky.blogserver.BlogServerApplication;
import com.ricky.blogserver.repository.BlogEsRepository;
import com.ricky.blogserver.serviceImpl.BlogServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@SpringBootTest(classes = BlogServerApplication.class)
public class TestEs {

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    BlogEsRepository blogEsRepository;
    @Autowired
    BlogServiceImpl blogService;

    @Test
    void d() {
        System.out.println("hello wo");
        BlogESPojo blogESPojo = new BlogESPojo();
        blogESPojo.id = 1L;
        blogESPojo.title = "hello world";
        blogESPojo.publishTime = LocalDateTime.now().toString();
        blogESPojo.content = "hello world";
//        elasticsearchTemplate.index().
        elasticsearchTemplate.save(blogESPojo);
    }

    @Test
    void daoRuJiLu() {
        List<Blog> list = blogService.list();
        Set<String> ids = new TreeSet<>();
        for (var b : list) {
            ids.add(b.pubUuid);
        }
        for (var id : ids) {
            IPage<BlogBasicDTO> blogPage =
                    blogService.getBlogPage(id, 1, 10000);

            for (var blog : blogPage.getRecords()) {
                BlogESPojo blogESPojo = new BlogESPojo();
                blogESPojo.content = blog.getContent();
                blogESPojo.id = blog.getId();
                blogESPojo.title = blog.getTitle();
                blogESPojo.pubUuid = blog.getPubUuid();
                blogESPojo.publishTime = blog.getPublishTime().toString();
                blogESPojo.agreeCount = blog.getAgreeCount();
                blogESPojo.coverFileName = blog.coverFileName;
                elasticsearchTemplate.save(blogESPojo);

            }
            query();
        }
    }


    @Test
    void query() {
        Iterable<BlogESPojo> all = blogEsRepository.findAll();
        for (var blogESPojo : all) {
            System.out.println(blogESPojo.toString());
        }
    }

    @Test
    void queryPage() {
        Pageable request = PageRequest.of(0, 130);
        Page<BlogESPojo> byTitle =
                blogEsRepository
                        .findByTitleContainingOrContentContainingOrderByPublishTimeDesc("y", "y", request);
        System.out.println(byTitle.getTotalElements());
        byTitle.get().forEach(System.out::println);
    }
}
