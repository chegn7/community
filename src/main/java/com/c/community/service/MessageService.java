package com.c.community.service;

import com.c.community.dao.MessageMapper;
import com.c.community.entity.Message;
import com.c.community.util.CommunityConstant;
import com.c.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    /**
     * 根据用户的id查询消息列表
     *
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    public List<Message> findConversations(int userId, int offset, int limit) {
        return messageMapper.selectConversationByUserId(userId, offset, limit);
    }

    /**
     * 根据会话id查询会话的消息列表
     *
     * @param conversationId
     * @param offset
     * @param limit
     * @return
     */
    public List<Message> findLetters(String conversationId, int offset, int limit) {
        return messageMapper.selectConversationByConversationId(conversationId, offset, limit);
    }

    /**
     * 根据用户id查询消息数
     *
     * @param userId
     * @return
     */
    public int findConversationsCount(int userId) {
        return messageMapper.selectConversationCountByUserId(userId);
    }

    /**
     * 根据会话id查询会话消息数
     *
     * @param conversationId
     * @return
     */
    public int findLettersCount(String conversationId) {
        return messageMapper.selectConversationCountByConversationId(conversationId);
    }

    /**
     * 查询会话未读消息数
     *
     * @param userId
     * @param conversationId
     * @return
     */
    public int findUnreadLettersCount(int userId, String conversationId) {
        return messageMapper.selectUnreadMessageCountByConversationId(userId, conversationId);
    }

    public int addMessage(Message message) {
        message.setContent(sensitiveFilter.filter(HtmlUtils.htmlEscape(message.getContent())));
        return messageMapper.insertMessage(message);
    }

    public int readMessage(List<Integer> ids) {
        return messageMapper.updateMessageStatus(ids, CommunityConstant.READ_STATUS);
    }

    public int deleteMessage(int id) {
        List<Integer> ids = new ArrayList<>();
        ids.add(id);
        return messageMapper.updateMessageStatus(ids, CommunityConstant.DELETED_STATUS);
    }

    public int deleteMessage(List<Integer> ids) {
        return messageMapper.updateMessageStatus(ids, CommunityConstant.DELETED_STATUS);
    }

    public Message findLatestNotice(int userId, String topic) {
        return messageMapper.selectLatestNotice(userId, topic);
    }

    public int findNoticeCount(int userId, String topic) {
        return messageMapper.selectNoticeCount(userId, topic);
    }

    public int findUnreadNoticeCount(int userId, String topic) {
        return messageMapper.selectUnreadNoticeCount(userId, topic);
    }

    public List<Message> findNotices(int userId, String topic, int offset, int limit) {

        return messageMapper.selectNotices(userId, topic, offset, limit);
    }

}
