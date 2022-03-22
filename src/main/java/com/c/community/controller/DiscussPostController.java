package com.c.community.controller;

import com.c.community.annotation.LoginRequired;
import com.c.community.dao.UserMapper;
import com.c.community.entity.*;
import com.c.community.event.EventProducer;
import com.c.community.service.CommentService;
import com.c.community.service.DiscussPostService;
import com.c.community.service.LikeService;
import com.c.community.service.UserService;
import com.c.community.util.CommunityConstant;
import com.c.community.util.CommunityUtil;
import com.c.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@RequestMapping("/discuss")
@Controller

public class DiscussPostController {

    @Autowired
    DiscussPostService discussPostService;

    @Autowired
    UserService userService;

    @Autowired
    CommentService commentService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    LikeService likeService;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJSONString(403, "未登录，请登录！");
        }

        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setCreateTime(new Date());
        discussPostService.addDiscussPost(discussPost);

        // 触发发帖事件
        Event event = new Event()
                .setTopic(CommunityConstant.TOPIC_PUBLISH_POST)
                .setUserId(user.getId())
                .setEntityType(CommunityConstant.ENTITY_POST)
                .setEntityId(discussPost.getId());
        eventProducer.fireEvent(event);
        // 报错之后统一处理
        return CommunityUtil.getJSONString(0, "发布成功");
    }

    @RequestMapping(path = "/detail/{postId}", method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("postId") int postId, Model model, Page page) {
        DiscussPost discussPost = discussPostService.findPost(postId);
        model.addAttribute("post", discussPost);
        // 查作者
        User user = userService.findUser(discussPost.getUserId());
        model.addAttribute("user", user);
        // 帖子的赞的信息
        long likeCount = likeService.likeCount(CommunityConstant.ENTITY_POST, postId);
        int likeStatus = hostHolder.getUser() == null ? CommunityConstant.DEFAULT_LIKE_STATUS : likeService.findLikeStatus(hostHolder.getUser().getId(), CommunityConstant.ENTITY_POST, postId);
        model.addAttribute("likeCount", likeCount);
        model.addAttribute("likeStatus", likeStatus);

        // 帖子的评论
        // 评论的分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/" + postId);
        // 这里是对帖子的评论，comment，对评论的回复命名为reply
        page.setRows(commentService.findCommentsCountByEntity(CommunityConstant.ENTITY_POST, discussPost.getId()));
        // 获得这个帖子的所有comment
        List<Comment> commentsByEntity = commentService.findCommentsByEntity(CommunityConstant.ENTITY_POST, discussPost.getId(), page.getOffset(), page.getLimit());
        // 用一个装map的list来方便的获取变量
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentsByEntity != null) {
            for (Comment comment : commentsByEntity) {
                // 新建这个map
                Map<String, Object> commentVo = new HashMap<>();
                // 评论里包含评论的用户以及评论的内容
                commentVo.put("user", userService.findUser(comment.getUserId()));
                commentVo.put("comment", comment);
                likeCount = likeService.likeCount(CommunityConstant.ENTITY_COMMENT, comment.getId());
                commentVo.put("likeCount", likeCount);
                likeStatus = hostHolder.getUser() == null ? CommunityConstant.DEFAULT_LIKE_STATUS : likeService.findLikeStatus(hostHolder.getUser().getId(), CommunityConstant.ENTITY_COMMENT, comment.getId());
                commentVo.put("likeStatus", likeStatus);
                // 还要放楼中楼回复，和上面操作类似
                List<Comment> replysByEntity = commentService.findCommentsByEntity(
                        CommunityConstant.ENTITY_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replysByEntity != null) {
                    for (Comment reply : replysByEntity) {
                        // 回复里同样要放发表回复的用户和回复的内容
                        Map<String, Object> replyVo = new HashMap<>();
                        replyVo.put("user", userService.findUser(reply.getUserId()));
                        replyVo.put("reply", reply);
                        // 回复里多一个参数是 回复的目标用户
                        User targetUser = userService.findUser(reply.getTargetId());
                        replyVo.put("targetUser", targetUser);
                        likeCount = likeService.likeCount(CommunityConstant.ENTITY_COMMENT, reply.getId());
                        replyVo.put("likeCount", likeCount);
                        likeStatus = hostHolder.getUser() == null ? CommunityConstant.DEFAULT_LIKE_STATUS : likeService.findLikeStatus(hostHolder.getUser().getId(), CommunityConstant.ENTITY_COMMENT, reply.getId());
                        replyVo.put("likeStatus", likeStatus);
                        //封装好的map要放到list里
                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replyVolist", replyVoList);
                // 还要放这个评论有几条回复，这里可以用size是因为分页没有限制limit
//                commentVo.put("replyCount", replyVoList.size());
                commentVo.put("replyCount",
                        commentService.findCommentsCountByEntity(CommunityConstant.ENTITY_COMMENT, comment.getId()));
                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("comments", commentVoList);
        return "site/discuss-detail";
    }

    /**
     * 置顶帖子
     * @param id
     * @return
     */
    @RequestMapping(path = "/top", method = RequestMethod.POST)
    @ResponseBody
    public String setTop(int id) {
        discussPostService.updateType(id, 1);
        Event event = new Event()
                .setTopic(CommunityConstant.TOPIC_PUBLISH_POST)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(CommunityConstant.ENTITY_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);
        return CommunityUtil.getJSONString(0);
    }

    /**
     * 加精
     * @param id
     * @return
     */
    @RequestMapping(path = "/wonderful", method = RequestMethod.POST)
    @ResponseBody
    public String setWonderful(int id) {
        discussPostService.updateStatus(id, 1);
        Event event = new Event()
                .setTopic(CommunityConstant.TOPIC_PUBLISH_POST)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(CommunityConstant.ENTITY_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);
        return CommunityUtil.getJSONString(0);
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @RequestMapping(path = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public String setDelete(int id) {
        discussPostService.updateStatus(id, 2);
        Event event = new Event()
                .setTopic(CommunityConstant.TOPIC_DELETE_POST)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(CommunityConstant.ENTITY_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);
        return CommunityUtil.getJSONString(0);
    }
}
