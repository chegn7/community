package com.c.community.dao;

import com.c.community.CommunityApplication;
import com.c.community.entity.Message;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.lang.management.MemoryPoolMXBean;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
class MessageMapperTest {

    @Autowired
    MessageMapper messageMapper;

    @Test
    void testAll() {
        List<Message> messages = messageMapper.selectConversationByUserId(111, 0, 20);
        for (Message message : messages) {
            System.out.println(message);
        }
        int i = messageMapper.selectConversationCountByUserId(111);
        System.out.println(i);
        for (Message message : messageMapper.selectConversationByConversationId("111_112", 0, 20)) {
            System.out.println(message);
        }
        System.out.println(messageMapper.selectConversationCountByConversationId("111_112"));

        System.out.println(messageMapper.selectUnreadMessageCountByConversationId(131, "111_131"));

    }
}