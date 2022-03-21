package com.c.community;

import com.alibaba.fastjson.JSONObject;
import com.c.community.dao.DiscussPostMapper;
import com.c.community.dao.elasticsearch.DiscussPostRepository;
import com.c.community.entity.DiscussPost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.mustache.SearchTemplateRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.fetch.subphase.highlight.SearchHighlightContext;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ElasticSearchTest {

    @Autowired
    @Qualifier("restHighLevelClient")
    RestHighLevelClient client;

    @Autowired
    DiscussPostMapper discussPostMapper;

    @Autowired
    DiscussPostRepository discussPostRepository;
    private HighlightBuilder.Field highlightContent;

    //
    @Test
    void testCreateIndex() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest("index1");
        CreateIndexResponse response =

                client.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(response);
    }

    @Test
    void testGetIndex() throws Exception {
        GetIndexRequest request = new GetIndexRequest("index1");
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    @Test
    void testInsert() {
        discussPostRepository.save(discussPostMapper.selectByPostId(109));
        discussPostRepository.save(discussPostMapper.selectByPostId(110));
        discussPostRepository.save(discussPostMapper.selectByPostId(111));
    }

    @Test
    void testInsertList() {
        for (int i = 101; i < 133; i++) {
            discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(i, 0, 100));
        }

    }

    @Test
    void testUpdate() {
        DiscussPost discussPost = discussPostMapper.selectByPostId(121);
        discussPost.setContent("新内容新内容修改后的内容");
        discussPostRepository.save(discussPost);
    }

    @Test
    void testDelete() {
        discussPostRepository.deleteById(121);
        discussPostRepository.deleteAll();
    }

    @Test
    void testSearch() throws IOException {

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        HighlightBuilder.Field highlightTitle =
                new HighlightBuilder.Field("title");
        highlightTitle.highlighterType("unified");
        HighlightBuilder.Field highlightContent =
                new HighlightBuilder.Field("content");
        highlightContent.highlighterType("unified");
        highlightBuilder.field(highlightContent).field(highlightTitle);
//        highlightBuilder.field("title");
//        highlightBuilder.field("content");
//        highlightBuilder.requireFieldMatch(false);
//        highlightBuilder.preTags("<span style='color:red'>");
//        highlightBuilder.postTags("</span>");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(QueryBuilders.multiMatchQuery("互联寒冬", "tile", "content"))
                .sort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .from(0).size(1)
                .highlighter(highlightBuilder);
        SearchRequest searchRequest = new SearchRequest()
                .source(searchSourceBuilder)
                .indices("discusspost");
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = searchResponse.getHits();
        List<DiscussPost> list = new LinkedList<>();
        for (SearchHit hit : hits.getHits()) {
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField highlight = highlightFields.get("content");
            Text[] fragments = highlight.fragments();
            String fragmentString = fragments[0].string();
            System.out.println(fragmentString);
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            System.out.println(sourceAsMap);
        }
        for (SearchHit hit : searchResponse.getHits()) {
            DiscussPost discussPost = JSONObject.parseObject(hit.getSourceAsString(), DiscussPost.class);
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (highlightFields != null) {
                HighlightField title = highlightFields.get("title");
                if (title != null) {
                    String s = title.getFragments()[0].toString();
                    discussPost.setTitle(s);
                }
//                HighlightField content = highlightFields.get("content");
//                if (content != null) {
//                    String s = content.getFragments()[0].toString();
//                    discussPost.setTitle(s);
//                }
            }
            list.add(discussPost);
//            System.out.println(discussPost);
        }
//        System.out.println(searchResponse.getHits().getTotalHits());
//        System.out.println(list.size());
    }

}
