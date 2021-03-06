package com.c.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.c.community.dao.MessageMapper;
import com.c.community.entity.Event;
import com.c.community.entity.Message;
import com.c.community.entity.Page;
import com.c.community.entity.User;
import com.c.community.service.MessageService;
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
import org.springframework.web.util.HtmlUtils;

import java.util.*;

@Controller
public class MessageController {
    @Autowired
    MessageService messageService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    UserService userService;

    // 私信列表功能
    @RequestMapping(path = "/letter/list", method = RequestMethod.GET)
    public String getLetters(Model model, Page page) {
        User user = hostHolder.getUser();
        if (user == null) return "redirect:/site/login";
        // 分页信息
        page.setPath("/letter/list");
        page.setLimit(5);
        page.setRows(messageService.findConversationsCount(user.getId()));
        List<Message> conversationsList = messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> conversationsVoList = new ArrayList<>();
        // 每个会话里面，要查到未读消息数量，消息总数量

        if (conversationsList != null) {
            for (Message conversation : conversationsList) {
                Map<String, Object> conversationsVo = new HashMap<>();
                conversationsVo.put("conversation", conversation);
                // 和这个人的未读消息数
                conversationsVo.put("unreadCount", messageService.findUnreadLettersCount(user.getId(), conversation.getConversationId()));
                // 与这个人的总消息数
                conversationsVo.put("letterCount", messageService.findLettersCount(conversation.getConversationId()));
                // 对话用户的头像
                int otherUserId = user.getId() == conversation.getFromId() ? conversation.getToId() : conversation.getFromId();
                User otherUser = userService.findUser(otherUserId);
                conversationsVo.put("otherUser", otherUser);
                conversationsVo.put("headerUrl", otherUser.getHeaderUrl());

                conversationsVoList.add(conversationsVo);
            }
        }
        model.addAttribute("conversationsVoList", conversationsVoList);
        // 总的未读消息数量
        int totalUnreadLettersCount = messageService.findUnreadLettersCount(user.getId(), null);
        model.addAttribute("totalUnreadLettersCount", totalUnreadLettersCount);
        int noticeUnreadCount = messageService.findUnreadNoticeCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);

        return "/site/letter";
    }

    // 会话详情
    @RequestMapping(path = "/letter/detail/{conversationId}", method = RequestMethod.GET)
    public String getLetterDetail(Model model, Page page, @PathVariable("conversationId") String conversationId) {
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.findLettersCount(conversationId));

        List<Message> messageServiceLetters = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> messages = new ArrayList<>();
        List<Integer> unreadList = new ArrayList<>();
        if (messageServiceLetters != null) {
            for (Message letter : messageServiceLetters) {
                Map<String, Object> map = new HashMap<>();
                User fromUser = userService.findUser(letter.getFromId());
                map.put("fromUser", fromUser);
                map.put("letter", letter);
                messages.add(map);
                if (letter.getStatus() == CommunityConstant.UNREAD_STATUS &&
                        letter.getToId() == hostHolder.getUser().getId())
                    unreadList.add(letter.getId());
            }
        }
        if (unreadList.size() > 0) messageService.readMessage(unreadList);

        model.addAttribute("messages", messages);
        User targetUser = getTargetUser(conversationId);
        model.addAttribute("targetUser", targetUser);
        return "site/letter-detail";
    }

    // send message
    @RequestMapping(path = "letter/send", method = RequestMethod.POST)
    @ResponseBody
    public String sendMessage(String toName, String content) {
        User toUser = userService.findUser(toName);
        if (toUser == null) {
            return CommunityUtil.getJSONString(1, "目标用户不存在");
        }
        User fromUser = hostHolder.getUser();
        Message message = new Message();
        message.setFromId(fromUser.getId());
        message.setToId(toUser.getId());
        String s1 = String.valueOf(fromUser.getId()), s2 = String.valueOf(toUser.getId());
        if (fromUser.getId() > toUser.getId()) {
            String temp = s1;
            s1 = s2;
            s2 = temp;
        }
        message.setConversationId(s1 + "_" + s2);
        message.setContent(content);
        message.setStatus(CommunityConstant.UNREAD_STATUS);
        message.setCreateTime(new Date());
        messageService.addMessage(message);
        return CommunityUtil.getJSONString(0);
    }

    // notice
    @RequestMapping(path = "/notice", method = RequestMethod.GET)
    public String getNoticeList(Model model) {
        User user = hostHolder.getUser();
        if (user == null) return "/site/login";
        Map<String, Object> commentVO = findNotice(user.getId(), CommunityConstant.TOPIC_COMMENT);
        Map<String, Object> likeVO = findNotice(user.getId(), CommunityConstant.TOPIC_LIKE);
        Map<String, Object> followVO = findNotice(user.getId(), CommunityConstant.TOPIC_FOLLOW);
        if (commentVO.size() > 0) model.addAttribute("commentNotice", commentVO);
        if (likeVO.size() > 0) model.addAttribute("likeNotice", likeVO);
        if (followVO.size() > 0) model.addAttribute("followNotice", followVO);

        int letterUnreadCount = messageService.findUnreadLettersCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        int noticeUnreadCount = messageService.findUnreadNoticeCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);

        return "/site/notice";
    }

    @RequestMapping(path = "/notice/detail/{topic}", method = RequestMethod.GET)
    public String getNoticeDetail(@PathVariable("topic") String topic, Model model, Page page) {
        User user = hostHolder.getUser();
        page.setLimit(5);
        page.setPath("/notice/detail/" + topic);
        page.setRows(messageService.findNoticeCount(user.getId(), topic));
        List<Message> notices = messageService.findNotices(user.getId(), topic, page.getOffset(), page.getLimit());
        List<Map<String, Object>> noticeVOList = new ArrayList<>();
        List<Integer> unreadIds = new ArrayList<>();
        if (notices != null) {
            for (Message notice : notices) {
                Map<String, Object> noticeVO = new HashMap<>();
                System.out.println(notice);
                noticeVO.put("notice", notice);
                if (notice.getStatus() == CommunityConstant.UNREAD_STATUS) unreadIds.add(notice.getId());
                // 通知内容
                String content = HtmlUtils.htmlUnescape(notice.getContent());
                Map<String , Object> data = JSONObject.parseObject(content, HashMap.class);
                noticeVO.put("user", userService.findUser((Integer) data.get("userId")));
                noticeVO.put("entityType", data.get("entityType"));
                noticeVO.put("entityId", data.get("entityId"));
                noticeVO.put("postId", data.get("postId"));
                // 通知作者
                noticeVO.put("fromUser", userService.findUser(notice.getFromId()));
                noticeVOList.add(noticeVO);
            }
        }
        model.addAttribute("notices", noticeVOList);
        // 设置已读
        if (unreadIds.size() > 0) messageService.readMessage(unreadIds);

        return "/site/notice-detail";

    }

    public Map<String, Object> findNotice(int userId, String topic) {
        Message latestNotice = messageService.findLatestNotice(userId, topic);

        Map<String, Object> map = new HashMap<>();
        if (latestNotice != null) {
            map.put("message", latestNotice);
            HashMap data = JSONObject.parseObject(HtmlUtils.htmlUnescape(latestNotice.getContent()), HashMap.class);
            map.put("user", userService.findUser((Integer) data.get("userId")));
            map.put("entityType", data.get("entityType"));
            map.put("entityId", data.get("entityId"));
            int noticeCount = messageService.findNoticeCount(userId, topic);
            int unreadNoticeCount = messageService.findUnreadNoticeCount(userId, topic);
            map.put("noticeCount", noticeCount);
            map.put("unreadNoticeCount", unreadNoticeCount);
        }
        return map;
    }


    private User getTargetUser(String conversationId) {
        User user = hostHolder.getUser();
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);
        int targetUserId = user.getId() == id0 ? id1 : id0;
        User targetUser = userService.findUser(targetUserId);
        return targetUser;
    }
}
