package com.c.community.controller;

import com.c.community.annotation.LoginRequired;
import com.c.community.entity.User;
import com.c.community.service.LikeService;
import com.c.community.util.CommunityUtil;
import com.c.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController {
    @Autowired
    HostHolder hostHolder;
    @Autowired
    LikeService likeService;

    @LoginRequired
    @RequestMapping(path = "/like", method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType, int entityId, int entityCreateUserId) {
        User user = hostHolder.getUser();
        // 点赞操作
        likeService.like(user.getId(), entityType, entityId, entityCreateUserId);
        // 点赞数量
        long likeCount = likeService.likeCount(entityType, entityId);
        // 点赞状态
        int likeStatus = likeService.findLikeStatus(user.getId(), entityType, entityId);
        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);

        return CommunityUtil.getJSONString(0, map);
    }
}
