package com.c.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // 指定数据转换方式
        // 设置 key 序列化方式
        template.setKeySerializer(RedisSerializer.string());
        // 设置普通 value
        template.setValueSerializer(RedisSerializer.json());
        // 设置 hash key
        template.setHashKeySerializer(RedisSerializer.string());
        // 设置 hash value
        template.setHashValueSerializer(RedisSerializer.json());
        // 生效设置
        template.afterPropertiesSet();
        // 返回
        return template;
    }
}
