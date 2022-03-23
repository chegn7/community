package com.c.community.controller;

import com.c.community.annotation.LoginRequired;
import com.c.community.entity.Comment;
import com.c.community.entity.DiscussPost;
import com.c.community.entity.Event;
import com.c.community.entity.User;
import com.c.community.event.EventProducer;
import com.c.community.service.CommentService;
import com.c.community.service.DiscussPostService;
import com.c.community.util.CommunityConstant;
import com.c.community.util.HostHolder;
import com.c.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@RequestMapping("/comment")
@Controller
public class CommentController {

    @Autowired
    EventProducer producer;

    @Autowired
    CommentService commentService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    DiscussPostService discussPostService;

    @Autowired
    RedisTemplate redisTemplate;


    @LoginRequired
    @RequestMapping(path = "/add/{discussPostId}", method = RequestMethod.POST)
    public String add(@PathVariable("discussPostId") int discussPostId, Comment comment) {
        User user = hostHolder.getUser();
        comment.setUserId(user.getId());
        comment.setCreateTime(new Date());
        comment.setStatus(0);
        if (comment.getTargetId() == null) comment.setTargetId(0);
        commentService.addComment(comment);

        // 触发评论事件
        Event event = new Event().setTopic(CommunityConstant.TOPIC_COMMENT)
                .setUserId(user.getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId", discussPostId);
        if (comment.getEntityType() == CommunityConstant.ENTITY_POST) {
            DiscussPost post = discussPostService.findPost(comment.getEntityId());
            event.setEntityUserId(post.getUserId());
        } else if (comment.getEntityType() == CommunityConstant.ENTITY_COMMENT) {
            Comment targetComment = commentService.findComment(comment.getEntityId());
            event.setEntityUserId(targetComment.getUserId());
        }
        producer.fireEvent(event);

        if (comment.getEntityType() == CommunityConstant.ENTITY_POST) {
            // 触发发帖事件
            event = new Event()
                    .setTopic(CommunityConstant.TOPIC_PUBLISH_POST)
                    .setEntityType(CommunityConstant.ENTITY_POST)
                    .setEntityId(discussPostId)
                    .setUserId(comment.getUserId());
            producer.fireEvent(event);
            // 将帖子存入redis缓存
            String redisKey = RedisKeyUtil.getPostScoreKey();
            redisTemplate.opsForSet().add(redisKey, discussPostId);
        }
        return "redirect:/discuss/detail/" + discussPostId;
    }
}
