package com.c.community.util;

public interface CommunityConstant {
    int ACTIVATION_SUCCESS = 0;
    int ACTIVATION_REPEAT = 1;
    int ACTIVATION_FAILURE = 2;

    /**
     * 默认账号密码保存时间,默认记12个小时
     */
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    /**
     * 勾选记住我账号密码保存时间，记7天
     */
    int IS_REMEMBERED_EXPIRED_SECONDS = DEFAULT_EXPIRED_SECONDS * 2 * 7;

    /**
     * entityType
     * 帖子 1
     * 评论/回复 2
     * 用户 3
     */
    int ENTITY_POST = 1;
    int ENTITY_COMMENT = 2;
    int ENTITY_USER = 3;

    /**
     * 消息状态
     * 0 未读
     * 1 已读
     * 2 删除
     */
    int UNREAD_STATUS = 0;
    int READ_STATUS = 1;
    int DELETED_STATUS = 2;

    /**
     * 赞 状态
     * 0 未赞
     * 1 赞
     * 2 踩
     */
    int LIKE_STATUS = 1;
    int DEFAULT_LIKE_STATUS = 0;
    int UNLIKE_STATUS = 2;

    /**
     * 关注状态
     * 0 未关注
     * 1 已关注
     * 2 拉黑
     */
    int UNFOLLOW_STATUS = 0;
    int FOLLOWING_STATUS = 1;
    int BLOCKED_STATUS = 2;

    /**
     * Event topics
     */
    String TOPIC_COMMENT = "comment";
    String TOPIC_LIKE = "like";
    String TOPIC_FOLLOW = "follow";
    String TOPIC_PUBLISH_POST = "publish";

    int SYSTEM_USER_ID = 1;

    /**
     * 权限控制相关
     */
    String AUTHORITY_USER = "user";
    String AUTHORITY_ADMIN = "admin";
    String AUTHORITY_MODERATOR = "moderator";

}
