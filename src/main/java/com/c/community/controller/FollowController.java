package com.c.community.controller;

import com.c.community.annotation.LoginRequired;
import com.c.community.entity.Event;
import com.c.community.entity.Page;
import com.c.community.entity.User;
import com.c.community.event.EventProducer;
import com.c.community.service.FollowService;
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

import java.util.List;
import java.util.Map;

@Controller
public class FollowController {

    @Autowired
    EventProducer producer;

    @Autowired
    FollowService followService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;

    @RequestMapping(path = "/follow", method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
    public String follow(int entityType, int entityId) {
        User user = hostHolder.getUser();
        followService.follow(user.getId(), entityType, entityId);
        Event event = new Event().setTopic(CommunityConstant.TOPIC_FOLLOW)
                .setUserId(user.getId())
                .setEntityType(CommunityConstant.ENTITY_USER)
                .setEntityId(entityId)
                .setEntityUserId(entityId)
                .setData("postId", user.getId());//此处的帖子id即为发起关注的用户的id

        producer.fireEvent(event);


        return CommunityUtil.getJSONString(0, "关注成功");
    }

    @RequestMapping(path = "/unfollow", method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
    public String unFollow(int entityType, int entityId) {
        User user = hostHolder.getUser();
        followService.unFollow(user.getId(), entityType, entityId);

        return CommunityUtil.getJSONString(0, "取关成功");
    }

    @LoginRequired
    @RequestMapping(path = "/followees/{userId}", method = RequestMethod.GET)
    public String getFollowees(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUser(userId);
        if (user == null) {
            throw new RuntimeException("查询用户不存在");
        }
        model.addAttribute("user", user);

        page.setLimit(5);
        page.setPath("/followees/" + user.getId());
        page.setRows((int) followService.findFolloweeCount(userId, CommunityConstant.ENTITY_USER));

        List<Map<String, Object>> followeeList = followService.findFolloweeList(userId, page.getOffset(), page.getLimit());
        if (followeeList != null) {
            for (Map<String, Object> followeeMap : followeeList) {
                User targetUser = (User) followeeMap.get("followeeUser");
                // 检查当前用户是否关注了targetUser
                int followStatus = checkFollowStatus(targetUser.getId());
                followeeMap.put("followStatus", followStatus);
            }
        }
        model.addAttribute("users", followeeList);
        return "/site/followee";
    }

    @LoginRequired
    @RequestMapping(path = "/followers/{userId}", method = RequestMethod.GET)
    public String getFollowers(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUser(userId);
        if (user == null) {
            throw new RuntimeException("查询用户不存在");
        }
        model.addAttribute("user", user);

        page.setLimit(5);
        page.setPath("/followers/" + userId);
        page.setRows((int) followService.findFollowerCount(CommunityConstant.ENTITY_USER, userId));

        List<Map<String, Object>> followerList = followService.findFollowerList(userId, page.getOffset(), page.getLimit());
        if (followerList != null) {
            for (Map<String, Object> followerMap : followerList) {
                User targetUser = (User) followerMap.get("followerUser");
                // 检查当前用户是否关注了targetUser
                int followStatus = checkFollowStatus(targetUser.getId());
                followerMap.put("followStatus", followStatus);
            }
        }
        model.addAttribute("users", followerList);
        return "/site/follower";
    }

    // 判断当前用户对userId用户的关注状态
    public int checkFollowStatus(int userId) {
        User loginUser = hostHolder.getUser();
        if (loginUser == null) return CommunityConstant.UNFOLLOW_STATUS;
        return followService.findFollowStatus(loginUser.getId(), CommunityConstant.ENTITY_USER, userId);
    }

}
