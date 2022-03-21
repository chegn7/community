package com.c.community.util;

import com.c.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class RedisKeyUtil {

    private static final String SPLIT = ":";

    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";

    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_FOLLOWER = "follower";

    private static final String PREFIX_KAPTCHA = "kaptcha";
    private static final String PREFIX_TICKET = "ticket";

    private static final String PREFIX_USER = "user";


    // 点赞实体的key
    // like:entity:entityType:entityId -> set(userId)
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    // 用户的被点赞量
    // like:user:userId -> int
    public static String getUserLikeKey(int userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    /**
     * 获得用户关注的实体的键名
     * followee:userId:entityType -> zset(entityId, dateTime)
     * @param userId
     * @param entityType
     * @return
     */
    public static String getFolloweeKey(int userId, int entityType) {
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    /**
     * 获得某个实体的粉丝的键名
     * follower:entityId:entityType -> zset(userId, dateTime)
     * @param entityType
     * @param entityId
     * @return
     */
    public static String getFollowerKey(int entityType, int entityId) {
        return PREFIX_FOLLOWER + SPLIT + entityId + SPLIT + entityType;
    }

    public static String getKaptcha(String owner) {
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    public static String getTicket(String ticket) {
        return PREFIX_TICKET + SPLIT + ticket;
    }

    public static String getUser(int userId) {
        return PREFIX_USER + SPLIT + userId;
    }

}