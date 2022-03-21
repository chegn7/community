package com.c.community.service;

import com.c.community.dao.CommentMapper;
import com.c.community.dao.DiscussPostMapper;
import com.c.community.entity.Comment;
import com.c.community.util.CommunityConstant;
import com.c.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    SensitiveFilter sensitiveFilter;

    @Autowired
    DiscussPostService discussPostService;

    public List<Comment> findCommentsByEntity(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectCommentsByEntity(entityType, entityId, offset, limit);
    }

    public int findCommentsCountByEntity(int entityType, int entityId) {
        return commentMapper.selectCommentsCountByEntity(entityType, entityId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment) {
        if (comment == null) throw new IllegalArgumentException("参数不能为空");
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        int rows = commentMapper.addComment(comment);
        // 更新帖子的回复数
        if (comment.getEntityType() == CommunityConstant.ENTITY_POST) {
            int count = commentMapper.selectCommentsCountByEntity(comment.getEntityType(), comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(), count);
        }
        return rows;
    }

    public Comment findComment(int id) {
        return commentMapper.selectCommentByEntityId(id);
    }
}
