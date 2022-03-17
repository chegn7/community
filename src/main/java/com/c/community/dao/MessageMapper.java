package com.c.community.dao;

import com.c.community.entity.Message;
import com.c.community.service.MessageService;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {

    // 查询当前用户的会话列表，每个Message只保留最新的一条content
    List<Message> selectConversationByUserId(int userId, int offset, int limit);
    // 查询当前用户的会话数量
    int selectConversationCountByUserId(int userId);
    // 根据会话id查询消息列表
    List<Message> selectConversationByConversationId(String conversationId, int offset, int limit);
    // 查询Letters数量
    int selectConversationCountByConversationId(String conversationId);
    // 查询未读消息数
    int selectUnreadMessageCountByConversationId(int userId, String conversationId);

    // 新增一条消息
    int insertMessage(Message message);

    // 修改消息的状态
    int updateMessageStatus(List<Integer> ids, int status);


}
