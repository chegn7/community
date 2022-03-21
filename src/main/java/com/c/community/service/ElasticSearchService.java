package com.c.community.service;

import com.alibaba.fastjson.JSONObject;
import com.c.community.dao.DiscussPostMapper;
import com.c.community.dao.elasticsearch.DiscussPostRepository;
import com.c.community.entity.DiscussPost;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ElasticSearchService {
    @Autowired
    private DiscussPostRepository discussPostRepository;

    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;


    public void saveDiscussPost(DiscussPost discussPost) {
        discussPostRepository.save(discussPost);
    }

    public void deleteDiscussPost(int id) {
        discussPostRepository.deleteById(id);
    }

    /**
     * 根据搜索的关键词在帖子标题和内容里查找，返回map
     *
     * @param keyword
     * @param offset
     * @param limit
     * @return 未搜索到返回空，Map有2个key，totalHitsValue 用于分页的总条目，posts 帖子列表
     */
    public Map<String, Object> searchDiscussPost(String keyword, int offset, int limit) throws IOException {
        if (StringUtils.isBlank(keyword)) return null;

        // 给标题和内容设置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
//        HighlightBuilder.Field highlightTitle =
//                new HighlightBuilder.Field("title");
//        highlightTitle.highlighterType("unified");
//        highlightBuilder.field(highlightTitle);
//        HighlightBuilder.Field highlightContent =
//                new HighlightBuilder.Field("content");
//        highlightContent.highlighterType("unified");
//        highlightBuilder.field(highlightContent).field(highlightTitle);
        highlightBuilder.field("title").field("content");

        // 构造搜索源
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
//                .query(QueryBuilders.termQuery("title", keyword))
                .query(QueryBuilders.multiMatchQuery(keyword, "title", "content"))
                .sort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .from(offset).size(limit)
                .highlighter(highlightBuilder);
        // 构造搜索请求
        SearchRequest searchRequest = new SearchRequest()
                .source(searchSourceBuilder)
                .indices("discusspost");
        // 获得搜索结果
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        if (searchResponse != null) {
            SearchHits hits = searchResponse.getHits();
            if (hits != null) {
                Map<String, Object> map = new HashMap<>();
                map.put("totalHitsValue", hits.getTotalHits().value);
                List<DiscussPost> posts = new ArrayList<>();
                // 处理高亮部分
                for (SearchHit hit : hits.getHits()) {
                    DiscussPost discussPost = JSONObject.parseObject(hit.getSourceAsString(), DiscussPost.class);
                    Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                    HighlightField highlightTitle = highlightFields.get("title");
                    HighlightField highlightContent = highlightFields.get("content");
                    if (highlightTitle != null) discussPost.setTitle(highlightTitle.fragments()[0].string());
                    if (highlightContent != null) discussPost.setContent(highlightContent.fragments()[0].string());
                    posts.add(discussPost);
                }
                map.put("posts", posts);
                return map;
            }
        }
        return null;
    }

//    public String getHighlight(String highlightWord, SearchHit hit) {
//        Map<String, HighlightField> highlightFields = hit.getHighlightFields();
//        if (highlightFields == null) return null;
//        HighlightField highlight = highlightFields.get(highlightWord);
//        if (highlight == null) return null;
//        Text[] fragments = highlight.fragments();
//        if (fragments == null) return null;
//        String fragmentString = fragments[0].string();
//        return fragmentString;
//    }
}
