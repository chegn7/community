package com.c.community.controller;

import com.c.community.annotation.LoginRequired;
import com.c.community.entity.Comment;
import com.c.community.entity.User;
import com.c.community.service.CommentService;
import com.c.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@RequestMapping("/comment")
@Controller
public class CommentController {

    @Autowired
    CommentService commentService;

    @Autowired
    HostHolder hostHolder;

    @LoginRequired
    @RequestMapping(path = "/add/{discussPostId}", method = RequestMethod.POST)
    public String add(@PathVariable("discussPostId") int discussPostId, Comment comment) {
        User user = hostHolder.getUser();
        comment.setUserId(user.getId());
        comment.setCreateTime(new Date());
        comment.setStatus(0);
        if (comment.getTargetId() == null) comment.setTargetId(0);
        commentService.addComment(comment);
        return "redirect:/discuss/detail/" + discussPostId;
    }
}
