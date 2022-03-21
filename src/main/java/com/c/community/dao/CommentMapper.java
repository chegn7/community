package com.c.community.dao;

import com.c.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    /**
     * 分页要实现两个功能，一个是查到分页的数据用来显示，另一个是查到总共有多少条数据，用来显示页数
     * Entity 需要由两个属性确定，一个是id，一个是type
     * @param entityType
     * @param entityId
     * @param offset
     * @param limit
     * @return
     */
    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    int selectCommentsCountByEntity(int entityType, int entityId);

    int addComment(Comment comment);

    Comment selectCommentByEntityId(int id);


}
