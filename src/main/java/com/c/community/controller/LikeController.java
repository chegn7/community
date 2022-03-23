package com.c.community.controller;

import com.c.community.annotation.LoginRequired;
import com.c.community.entity.Event;
import com.c.community.entity.User;
import com.c.community.event.EventProducer;
import com.c.community.service.CommentService;
import com.c.community.service.LikeService;
import com.c.community.util.CommunityConstant;
import com.c.community.util.CommunityUtil;
import com.c.community.util.HostHolder;
import com.c.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController {
    @Autowired
    EventProducer producer;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    LikeService likeService;
    @Autowired
    CommentService commentService;
    @Autowired
    RedisTemplate redisTemplate;

    @LoginRequired
    @RequestMapping(path = "/like", method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType, int entityId, int entityCreateUserId, int postId) {
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
        // 触发点赞事件
        if (likeStatus == 1) {
            Event event = new Event().setUserId(user.getId())
                    .setTopic(CommunityConstant.TOPIC_LIKE)
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityCreateUserId)
                    .setData("postId", postId);
            producer.fireEvent(event);
        }

        if (entityType == CommunityConstant.ENTITY_POST) {
            // 将帖子存入redis缓存
            String redisKey = RedisKeyUtil.getPostScoreKey();
            redisTemplate.opsForSet().add(redisKey, postId);
        }

        return CommunityUtil.getJSONString(0, map);
    }
}
