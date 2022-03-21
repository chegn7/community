package com.c.community.controller;

import com.c.community.entity.DiscussPost;
import com.c.community.entity.Page;
import com.c.community.service.ElasticSearchService;
import com.c.community.service.LikeService;
import com.c.community.service.UserService;
import com.c.community.util.CommunityConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController {
    @Autowired
    private ElasticSearchService elasticSearchService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    // ?keyword=xxx
    @RequestMapping(path = "/search", method = RequestMethod.GET)
    public String search(String keyword, Page page, Model model) throws IOException {
        // 搜索帖子
        page.setLimit(5);
        int offset = (page.getCurrent() - 1) * page.getLimit();
        Map<String, Object> searchMap = elasticSearchService.searchDiscussPost(keyword, offset, page.getLimit());
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (searchMap != null && (long) searchMap.get("totalHitsValue") > 0) {
            long rows = (long) searchMap.get("totalHitsValue");
            page.setRows((int) rows);
            List<DiscussPost> posts = (List<DiscussPost>) searchMap.get("posts");
            for (int i = 0; i < posts.size(); i++) {
                Map<String, Object> map = new HashMap<>();
                DiscussPost post = posts.get(i);
                map.put("user", userService.findUser(post.getUserId()));
                map.put("likeCount", likeService.likeCount(CommunityConstant.ENTITY_POST, post.getId()));
                map.put("post", post);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        // page 的信息，rows有默认值0
        page.setPath("/search?keyword=" + keyword);
        return "/site/search";
    }
}
