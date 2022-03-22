package com.c.community.event;

import com.alibaba.fastjson.JSONObject;
import com.c.community.entity.DiscussPost;
import com.c.community.entity.Event;
import com.c.community.entity.Message;
import com.c.community.service.DiscussPostService;
import com.c.community.service.ElasticSearchService;
import com.c.community.service.MessageService;
import com.c.community.util.CommunityConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private MessageService messageService;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private ElasticSearchService elasticSearchService;

    @KafkaListener(topics = {CommunityConstant.TOPIC_COMMENT, CommunityConstant.TOPIC_FOLLOW, CommunityConstant.TOPIC_LIKE})
    public void handleEvent(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            LOGGER.error("消息为空");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            LOGGER.error("消息格式错误");
            return;
        }
        // 发送站内信，构造Message
        Message message = new Message();
        message.setFromId(CommunityConstant.SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());
        Map<String, Object> map = new HashMap<>();
        map.put("userId", event.getUserId());
        map.put("entityType", event.getEntityType());
        map.put("entityId", event.getEntityId());
        Map<String, Object> data = event.getData();
        if (!data.isEmpty()) {
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                map.put(entry.getKey(), entry.getValue());
            }
        }
        message.setContent(JSONObject.toJSONString(map));
        messageService.addMessage(message);
    }

    @KafkaListener(topics = {CommunityConstant.TOPIC_PUBLISH_POST})
    public void handlePublishPostEvent(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            LOGGER.error("消息为空");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            LOGGER.error("消息格式错误");
            return;
        }
        DiscussPost post = discussPostService.findPost(event.getEntityId());
        elasticSearchService.saveDiscussPost(post);

    }

    @KafkaListener(topics = {CommunityConstant.TOPIC_DELETE_POST})
    public void handleDeletePostEvent(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            LOGGER.error("消息为空");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            LOGGER.error("消息格式错误");
            return;
        }
        elasticSearchService.deleteDiscussPost(event.getEntityId());
    }

}
